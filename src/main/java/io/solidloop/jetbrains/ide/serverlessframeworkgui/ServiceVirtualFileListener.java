package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
public class ServiceVirtualFileListener implements VirtualFileListener {
    @NonNull
    private Tree tree;
    @NonNull
    private ServiceRepository serviceRepository;
    @NonNull
    private ServiceFactory serviceFactory;
    @NonNull
    private ServiceNodeFactory serviceNodeFactory;
    @NonNull
    private ServicesTreeComparator comparator;

    private Service upcomingServiceFileDeletion;

    private DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode) tree.getModel().getRoot();
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        if (event.getPropertyName().equals("name")) {
            if (ServerlessFileUtil.isServerlessFile(event.getOldValue().toString()) && !ServerlessFileUtil.isServerlessFile(event.getNewValue().toString())) {
                try {
                    findAndRemoveServiceNodeByService(serviceFactory.create(event.getFile()));
                } catch (IOException e) {
                    reportException(e);
                }
            } else if (!ServerlessFileUtil.isServerlessFile(event.getOldValue().toString()) && ServerlessFileUtil.isServerlessFile(event.getNewValue().toString())) {
                onFileChange(event.getFile());
            }
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

    private void findAndRemoveServiceNodeByService(Service service) {
        DefaultMutableTreeNode oldServiceNode = TreeUtil.findNodeWithObject(getRootNode(), service);
        if (oldServiceNode != null) {
            oldServiceNode.removeFromParent();
            tree.updateUI();
        }
    }

    @Override
    public void fileDeleted(@NotNull final VirtualFileEvent event) {
        if (upcomingServiceFileDeletion != null) {
            findAndRemoveServiceNodeByService(upcomingServiceFileDeletion);
            upcomingServiceFileDeletion = null;
        }
    }

    @Override
    public void beforeFileDeletion(@NotNull final VirtualFileEvent event) {
        if (event.getFile().isDirectory()) {
            serviceRepository.filterBy(event.getFile()).forEach(this::findAndRemoveServiceNodeByService);
        } else if (ServerlessFileUtil.isServerlessFile(event.getFile())) {
            try {
                upcomingServiceFileDeletion = serviceFactory.create(event.getFile());
            } catch (IOException e) {
                reportException(e);
            }
        }
    }

    private void onFileChange(@NotNull final VirtualFile file) {
        if (ServerlessFileUtil.isServerlessFile(file)) {
            Service newService;
            try {
                newService = serviceFactory.create(file);
            } catch (IOException e) {
                reportException(e);
                return;
            }


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

    private void reportException(Exception e) {
        PluginManager.getLogger().error(e);
    }
}
