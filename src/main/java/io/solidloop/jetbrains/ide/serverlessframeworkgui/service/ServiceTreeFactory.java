package io.solidloop.jetbrains.ide.serverlessframeworkgui.service;

import com.intellij.execution.ExecutionException;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.TreeContextMenuFactory;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.Function;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.command.CommandFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.SystemIndependent;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

@RequiredArgsConstructor
public class ServiceTreeFactory {
    @NonNull
    private Project project;
    @NonNull
    private ServiceNodeFactory serviceNodeFactory;
    @NonNull
    private CommandFactory commandFactory;
    @NonNull
    private TreeContextMenuFactory contextMenuFactory;

    public Tree create(Set<Service> services) {
        Tree tree = new Tree(createRootNode(services));
        tree.setRootVisible(false);
        tree.addMouseListener(createMouseListener());
        tree.setCellRenderer(new TreeCellRenderer());
        TreeUtil.expandAll(tree);
        return tree;
    }

    private DefaultMutableTreeNode createRootNode(Set<Service> services) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        services.forEach(service -> rootNode.add(serviceNodeFactory.createOrUpdate(service)));
        return rootNode;
    }

    // @// TODO: 12.04.19 inject mouse listener
    private MouseAdapter createMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {
                Tree tree = (Tree) mouseEvent.getSource();
                TreePath selectedTreePath = tree.getAnchorSelectionPath();

                if (selectedTreePath != null) {
                    Object userObject = ((DefaultMutableTreeNode) selectedTreePath.getLastPathComponent()).getUserObject();

                    if (SwingUtilities.isLeftMouseButton(mouseEvent) && mouseEvent.getClickCount() == 2 && userObject instanceof Function) {
                        try {
                            commandFactory.createDeployAndInvokeFunctionCommand((Function) userObject).execute();
                        } catch (ExecutionException e) {
                            JBPopupFactory.getInstance()
                                    .createHtmlTextBalloonBuilder(e.getMessage(), MessageType.ERROR, null)
                                    .setFadeoutTime(10000)
                                    .createBalloon()
                                    .showInCenterOf(tree);
                        }
                    } else if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                        contextMenuFactory
                                .createContextMenu(userObject)
                                .show(tree, mouseEvent.getX(), mouseEvent.getY());
                    }
                }
            }
        };
    }


    private class TreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, createNodeDisplayName(value, sel), sel, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.getUserObject() instanceof Service) {
                    setIcon(AllIcons.FileTypes.Diagram);
                } else if (node.getUserObject() instanceof Function) {
                    setIcon(AllIcons.Gutter.ImplementingFunctionalInterface);
                }
            }

            return this;
        }

        private String createNodeDisplayName(Object nodeObject, boolean selected) {
            if (nodeObject instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) nodeObject).getUserObject();

                if (userObject instanceof Service) {
                    return createNodeDisplayName((Service) userObject, selected);
                } else if (userObject instanceof Function) {
                    return createNodeDisplayName((Function) userObject);
                }
            }

            return nodeObject.toString();
        }

        private String createNodeDisplayName(Function function) {
            return function.getName();
        }

        // @// TODO: 12.04.19 optimize getting relative path
        private String createNodeDisplayName(Service service, boolean selected) {
            StringBuilder name = new StringBuilder();
            name.append("<html>");

            if (service.getName() != null) {
                name.append("<b>");
                name.append(service.getName());
                name.append("</b>");
                name.append("&nbsp;&nbsp;");
            }

            String color = selected ? "white" : "gray";

            name.append("<font color=\"").append(color).append("\">");
            name.append(getRelativePath(service.getFile()));
            name.append("</font>");
            name.append("</html>");

            return name.toString();
        }

        private String getRelativePath(VirtualFile file) {
            String canonicalPath = file.getCanonicalPath();
            @SystemIndependent String projectBasePath = project.getBasePath();
            if (canonicalPath != null && projectBasePath != null && canonicalPath.startsWith(projectBasePath)) {
                return canonicalPath.replace(projectBasePath + "/", "");
            }

            return canonicalPath;
        }
    }
}
