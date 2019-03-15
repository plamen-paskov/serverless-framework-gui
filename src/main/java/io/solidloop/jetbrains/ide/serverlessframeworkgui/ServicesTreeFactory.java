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

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ServicesTreeFactory {
    private CommandExecutor commandExecutor;
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

                    try {
                        GeneralCommandLine deployAndInvokeCommandLine = commandLineFactory.create(function.getService().getFile().getParent().getCanonicalPath(), createDeployAndInvokeFunctionCommand(function));
                        commandExecutor.execute("Invoke " + function.getName(), deployAndInvokeCommandLine);
                    } catch (ExecutionException e) {
                        JBPopupFactory.getInstance()
                                .createHtmlTextBalloonBuilder(e.getMessage(), MessageType.ERROR, null)
                                .setFadeoutTime(10000)
                                .createBalloon()
                                .showInCenterOf(tree);
                    }
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
            deployMenuItem.addActionListener(new MenuItemListener(directory, createDeployServiceCommand(), "Deploy " + service.getName(), commandExecutor, commandLineFactory, tree));
            popup.add(deployMenuItem);

            JBMenuItem removeMenuItem = new JBMenuItem("Remove");
            removeMenuItem.addActionListener(new MenuItemListener(directory, createRemoveServiceCommand(), "Remove " + service.getName(), commandExecutor, commandLineFactory, tree));
            popup.add(removeMenuItem);
        } else if (userObject instanceof Function) {
            Function function = (Function) userObject;
            String directory = function.getService().getFile().getParent().getCanonicalPath();

            JBMenuItem deployAndInvokeMenuItem = new JBMenuItem("Deploy and Invoke");
            deployAndInvokeMenuItem.addActionListener(new MenuItemListener(directory, createDeployAndInvokeFunctionCommand(function), "Deploy and Invoke " + function.getName(), commandExecutor, commandLineFactory, tree));
            popup.add(deployAndInvokeMenuItem);
        }

        return popup;
    }

    private List<String> createDeployServiceCommand() {
        List<String> command = new ArrayList<>();
        command.add("serverless");
        command.add("deploy");

        return command;
    }

    private List<String> createRemoveServiceCommand() {
        List<String> command = new ArrayList<>();
        command.add("serverless");
        command.add("remove");

        return command;
    }

    private List<String> createDeployAndInvokeFunctionCommand(Function function) {
        List<String> command = new ArrayList<>();
        command.add("serverless");
        command.add("deploy");
        command.add("function");
        command.add("-f");
        command.add(function.getName());
        command.add("&&");
        command.add("serverless");
        command.add("invoke");
        command.add("-f");
        command.add(function.getName());
        command.add("-r");
        command.add(function.getService().getRegion());
        command.add("-d");
        command.add("{}");

        return command;
    }
}
