package io.solidloop.jetbrains.ide.serverlessframeworkgui.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.AbstractServerlessVirtualFileListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Comparator;

public class ServerlessVirtualFileListener extends AbstractServerlessVirtualFileListener {
    private Tree tree;
    private ServiceTreeNodeFactory serviceTreeNodeFactory;
    private Comparator<Object> comparator;

    public ServerlessVirtualFileListener(Tree tree, ServiceFactory serviceFactory, ServiceTreeNodeFactory serviceTreeNodeFactory, Comparator<Object> comparator, Project project) {
        super(project, serviceFactory);

        this.tree = tree;
        this.serviceTreeNodeFactory = serviceTreeNodeFactory;
        this.comparator = comparator;
    }

    @Override
    protected void onCreateOrUpdate(Service service) {
        // @// TODO: 12.04.19 handle create and update separately
        DefaultMutableTreeNode newServiceNode = serviceTreeNodeFactory.createOrUpdate(service);

        if (findTreeNode(service) == null) {
            TreeUtil.insertNode(newServiceNode, getRootNode(), (DefaultTreeModel) tree.getModel(), true, comparator);
        }

        tree.updateUI();
    }

    @Override
    protected void onDelete(VirtualFile file) {
        DefaultMutableTreeNode serviceNode = findTreeNode(file);

        if (serviceNode != null) {
            serviceNode.removeFromParent();
            tree.updateUI();
        }
    }

    @Override
    protected void onMoveInAnotherDirectory(VirtualFile file) {
        tree.updateUI();
    }

    private DefaultMutableTreeNode findTreeNode(Object object) {
        return (DefaultMutableTreeNode) TreeUtil.findNodeWithObject(object, tree.getModel(), getRootNode());
    }

    private DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode) tree.getModel().getRoot();
    }
}
