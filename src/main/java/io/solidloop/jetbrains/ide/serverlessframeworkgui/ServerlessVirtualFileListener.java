package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ServerlessVirtualFileListener extends AbstractServerlessVirtualFileListener {
    private Tree tree;
    private ServiceNodeFactory serviceNodeFactory;
    private ServicesTreeComparator comparator;

    public ServerlessVirtualFileListener(Tree tree, ServiceFactory serviceFactory, ServiceNodeFactory serviceNodeFactory, ServicesTreeComparator comparator, Project project) {
        super(project, serviceFactory);

        this.tree = tree;
        this.serviceNodeFactory = serviceNodeFactory;
        this.comparator = comparator;
    }

    @Override
    void onCreateOrUpdate(Service service) {
        DefaultMutableTreeNode newServiceNode = serviceNodeFactory.createOrUpdate(service);

        if (findTreeNode(service) == null) {
            TreeUtil.insertNode(newServiceNode, getRootNode(), (DefaultTreeModel) tree.getModel(), true, comparator);
        }

        tree.updateUI();
    }

    @Override
    void onDelete(VirtualFile file) {
        DefaultMutableTreeNode serviceNode = findTreeNode(file);

        if (serviceNode != null) {
            serviceNode.removeFromParent();
            tree.updateUI();
        }
    }

    private DefaultMutableTreeNode findTreeNode(Object object) {
        return (DefaultMutableTreeNode) TreeUtil.findNodeWithObject(object, tree.getModel(), getRootNode());
    }

    private DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode) tree.getModel().getRoot();
    }
}
