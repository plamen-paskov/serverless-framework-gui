package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ServicesTreeFactory {
    private static final String SERVERLESS_EXECUTABLE = "serverless";

    @NonNull
    private CommandExecutor commandExecutor;
    @NonNull
    private CommandLineFactory commandLineFactory;

    public Tree create(DefaultMutableTreeNode rootNode) {
        Tree tree = new Tree(rootNode);
        tree.setRootVisible(false);
        TreeUtil.expandAll(tree);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {
                Tree tree = (Tree) mouseEvent.getSource();
                TreePath anchorSelectionPath = tree.getAnchorSelectionPath();
                Object userObject = ((DefaultMutableTreeNode) anchorSelectionPath.getLastPathComponent()).getUserObject();

                if (mouseEvent.getClickCount() == 2 && userObject instanceof Function) {
                    Function function = (Function) userObject;

                    execute("Invoke " + function.getName(), function.getService().getFile().getParent().getCanonicalPath(), createInvokeFunctionCommand(function), tree);
                } else if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                    createPopupMenu((DefaultMutableTreeNode) anchorSelectionPath.getLastPathComponent(), tree).show(tree, mouseEvent.getX(), mouseEvent.getY());
                }
            }
        });

        return tree;
    }

    private JBPopupMenu createPopupMenu(DefaultMutableTreeNode node, Tree tree) {
        Object userObject = node.getUserObject();

        JBPopupMenu popup = new JBPopupMenu();

        if (userObject instanceof Service) {
            Service service = (Service) userObject;
            String directory = service.getFile().getParent().getCanonicalPath();

            JBMenuItem deployMenuItem = new JBMenuItem("Deploy");
            deployMenuItem.addActionListener(actionEvent -> execute("Deploy " + service.getName(), directory, createDeployServiceCommand(), tree));
            popup.add(deployMenuItem);

            JBMenuItem removeMenuItem = new JBMenuItem("Remove");
            removeMenuItem.addActionListener(actionEvent -> execute("Remove " + service.getName(), directory, createRemoveServiceCommand(), tree));
            popup.add(removeMenuItem);
        } else if (userObject instanceof Function) {
            Function function = (Function) userObject;
            String directory = function.getService().getFile().getParent().getCanonicalPath();

            JBMenuItem deployAndInvokeMenuItem = new JBMenuItem("Deploy and Invoke");
            deployAndInvokeMenuItem.addActionListener(actionEvent -> execute("Deploy and Invoke " + function.getName(), directory, createDeployAndInvokeFunctionCommand(function), tree));
            popup.add(deployAndInvokeMenuItem);
        }

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

    private List<String> createInvokeFunctionCommand(Function function) {
        List<String> command = new ArrayList<>();
        command.add(SERVERLESS_EXECUTABLE);
        command.add("invoke");
        command.add("-f");
        command.add(function.getName());
        command.add("-r");
        command.add(function.getService().getRegion());
        command.add("-d");
        command.add("{}");

        return command;
    }

    private void execute(String terminalTitle, String directory, List<String> command, Tree tree) {
        try {
            commandExecutor.execute(terminalTitle, commandLineFactory.create(directory, command));
        } catch (ExecutionException e) {
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(e.getMessage(), MessageType.ERROR, null)
                    .setFadeoutTime(10000)
                    .createBalloon()
                    .showInCenterOf(tree);
        }
    }
}
