package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.vfs.*;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Set;

@RequiredArgsConstructor
public class ServiceVirtualFileListener implements VirtualFileListener {
    @NonNull
    private Tree tree;
    @NonNull
    private ServiceFactory serviceFactory;
    @NonNull
    private ServiceNodeFactory serviceNodeFactory;
    @NonNull
    private ServicesTreeComparator comparator;

    private DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode) tree.getModel().getRoot();
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        if (event.getPropertyName().equals("name")) {
            if (ServerlessFileUtil.isServerlessFile(event.getOldValue().toString()) && !ServerlessFileUtil.isServerlessFile(event.getNewValue().toString())) {
                removeServiceNode(event.getFile());
            } else if (!ServerlessFileUtil.isServerlessFile(event.getOldValue().toString()) && ServerlessFileUtil.isServerlessFile(event.getNewValue().toString())) {
                onFileChange(event.getFile());
            }
        }
    }

    private DefaultMutableTreeNode findServiceNode(VirtualFile file) {
        return (DefaultMutableTreeNode)TreeUtil.findNodeWithObject(file, tree.getModel(), getRootNode());
    }

    @Nullable
    private Service findService(VirtualFile file) {
        DefaultMutableTreeNode serviceNode = findServiceNode(file);
        if (serviceNode != null && serviceNode.getUserObject() instanceof Service) {
            return (Service) serviceNode.getUserObject();
        }
        return null;
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        Service service = findService(event.getFile());
        if (service != null) {
            service.updateFullName();
        }
    }

    @Override
    public void contentsChanged(@NotNull final VirtualFileEvent event) {
        onFileChange(event.getFile());
    }

    @Override
    public void fileCreated(@NotNull final VirtualFileEvent event) {
        onFileChange(event.getFile());
    }

    private void removeServiceNode(VirtualFile file) {
        DefaultMutableTreeNode serviceNode = findServiceNode(file);

        if (serviceNode != null) {
            serviceNode.removeFromParent();
            tree.updateUI();
        }
    }

    private void onDirectoryDelete(VirtualFile directory) {
        Set<TreePath> treePaths = TreeUtil.treePathTraverser(tree).filter(treePath -> {
            Object userObject = ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject();
            if (userObject instanceof Service) {
                return VfsUtilCore.isAncestor(directory, ((Service) userObject).getFile(), false);
            }
            return false;
        }).toSet();

        if (treePaths.size() > 0) {
            treePaths.forEach(treePath -> ((DefaultMutableTreeNode) treePath.getLastPathComponent()).removeFromParent());
            tree.updateUI();
        }
    }

    @Override
    public void fileDeleted(@NotNull final VirtualFileEvent event) {
        VirtualFile file = event.getFile();

        if (file.isDirectory()) {
            onDirectoryDelete(file);
        } else if (ServerlessFileUtil.isServerlessFile(file)) {
            removeServiceNode(file);
        }
    }

    private void onFileChange(@NotNull final VirtualFile file) {
        if (ServerlessFileUtil.isServerlessFile(file)) {
            Service newService = serviceFactory.create(file);
            DefaultMutableTreeNode newServiceNode = serviceNodeFactory.create(newService);
            DefaultMutableTreeNode oldServiceNode = TreeUtil.findNodeWithObject(getRootNode(), newService);

            boolean isOldServiceNodeExpanded = false;
            if (oldServiceNode != null) {
                isOldServiceNodeExpanded = tree.isExpanded(new TreePath(oldServiceNode.getPath()));
                oldServiceNode.removeFromParent();
            }

            TreeUtil.insertNode(newServiceNode, getRootNode(), (DefaultTreeModel) tree.getModel(), true, comparator);

            if (isOldServiceNodeExpanded) {
                ArrayList<TreePath> treePaths = new ArrayList<>();
                treePaths.add(new TreePath(newServiceNode.getPath()));

                TreeUtil.restoreExpandedPaths(tree, treePaths);
            }

            tree.updateUI();
        }
    }
}
