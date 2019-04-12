package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.OutputListener;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.Topic;
import com.intellij.util.ui.tree.TreeUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.SystemIndependent;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class ServiceTreeFactory {
    private static final String SERVERLESS_EXECUTABLE = "serverless";

    @NonNull
    private ServiceNodeFactory serviceNodeFactory;
    @NonNull
    private CommandExecutor commandExecutor;
    @NonNull
    private CommandLineFactory commandLineFactory;
    @NonNull
    private Project project;
    @NonNull
    private Topic<FunctionCommandOutputHandler> topic;

    private Tree tree;

    public Tree create(Set<Service> services) {
        tree = new Tree(createRootNode(services));
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
                        execute((Function) userObject);
                    } else if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                        createContextMenu(userObject).show(tree, mouseEvent.getX(), mouseEvent.getY());
                    }
                }
            }
        };
    }

    private void execute(Function function) {
        execute("Invoke " + function.getName(), function.getService().getFile().getParent().getCanonicalPath(), createInvokeFunctionCommand(function), createInvokeFunctionOutputListener(function));
    }

    private FunctionCommandOutputListener createInvokeFunctionOutputListener(Function function) {
        return new FunctionCommandOutputListener(project.getMessageBus(), topic, function);
    }

    private JBPopupMenu createContextMenu(Object userObject) {
        return userObject instanceof Service ? createContextMenu((Service) userObject) : createContextMenu((Function) userObject);
    }

    private JBPopupMenu createContextMenu(Service service) {
        JBPopupMenu popup = new JBPopupMenu();

        String directory = service.getFile().getParent().getCanonicalPath();

        JBMenuItem openFile = new JBMenuItem("Open file");
        openFile.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                FileEditorManager.getInstance(project).openFile(service.getFile(), true);
            }
        });
        popup.add(openFile);

        JBMenuItem deployMenuItem = new JBMenuItem("Deploy");
        deployMenuItem.addActionListener(actionEvent -> execute("Deploy " + service.getName(), directory, createDeployServiceCommand()));
        popup.add(deployMenuItem);

        JBMenuItem removeMenuItem = new JBMenuItem("Remove");
        removeMenuItem.addActionListener(actionEvent -> execute("Remove " + service.getName(), directory, createRemoveServiceCommand()));
        popup.add(removeMenuItem);

        return popup;
    }

    private JBPopupMenu createContextMenu(Function function) {
        JBPopupMenu popup = new JBPopupMenu();

        JBMenuItem deployAndInvokeMenuItem = new JBMenuItem("Deploy and Invoke");
        deployAndInvokeMenuItem.addActionListener(actionEvent -> execute("Deploy and Invoke " + function.getName(), function.getService().getFile().getParent().getCanonicalPath(), createDeployAndInvokeFunctionCommand(function)));
        popup.add(deployAndInvokeMenuItem);

        return popup;
    }

    private List<String> createDeployServiceCommand() {
        List<String> command = new ArrayList<>();
        command.add(SERVERLESS_EXECUTABLE);
        command.add("deploy");

        return command;
    }

    private List<String> createRemoveServiceCommand() {
        List<String> command = new ArrayList<>();
        command.add(SERVERLESS_EXECUTABLE);
        command.add("remove");

        return command;
    }

    private List<String> createDeployAndInvokeFunctionCommand(Function function) {
        List<String> command = new ArrayList<>();
        command.add(SERVERLESS_EXECUTABLE);
        command.add("deploy");
        command.add("function");
        command.add("-f");
        command.add(function.getName());
        command.add("&&");
        command.addAll(createInvokeFunctionCommand(function));

        return command;
    }

    private String getDataFile(Function function) {
        return function.getService().getFile().getParent().getCanonicalPath() + "/serverless-framework-gui/" + function.getService().getName() + "/" + function.getName() + ".json";
    }

    private List<String> createInvokeFunctionCommand(Function function) {
        Service service = function.getService();

        List<String> command = new ArrayList<>();
        command.add(SERVERLESS_EXECUTABLE);
        command.add("invoke");
        command.add("-f");
        command.add(function.getName());

        if (service.getRegion() != null) {
            command.add("-r");
            command.add(function.getService().getRegion());
        }

        String dataFile = getDataFile(function);
        File file = new File(dataFile);
        if (!file.exists()) {
            command.add("-d");
            command.add("{}");
        } else {
            command.add("-p");
            command.add(dataFile);
        }

        return command;
    }

    private void execute(String terminalTitle, String directory, List<String> command) {
        execute(terminalTitle, directory, command, null);
    }

    private void execute(String terminalTitle, String directory, List<String> command, OutputListener outputListener) {
        try {
            commandExecutor.execute(terminalTitle, commandLineFactory.create(directory, command), outputListener);
        } catch (ExecutionException e) {
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(e.getMessage(), MessageType.ERROR, null)
                    .setFadeoutTime(10000)
                    .createBalloon()
                    .showInCenterOf(tree);
        }
    }

    private class TreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, createNodeDisplayName(value), sel, expanded, leaf, row, hasFocus);

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

        private String createNodeDisplayName(Object nodeObject) {
            if (nodeObject instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) nodeObject).getUserObject();

                if (userObject instanceof Service) {
                    return createNodeDisplayName((Service) userObject);
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
        private String createNodeDisplayName(Service service) {
            StringBuilder name = new StringBuilder();
            name.append("<html>");

            if (service.getName() != null) {
                name.append("<b>");
                name.append(service.getName());
                name.append("</b>");
                name.append("&nbsp;&nbsp;");
            }

            name.append("<font color=\"gray\">");
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
