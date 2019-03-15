package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import lombok.AllArgsConstructor;

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
                if (mouseEvent.getClickCount() == 2) {
                    Tree tree = (Tree) mouseEvent.getSource();
                    TreePath anchorSelectionPath = tree.getAnchorSelectionPath();

                    if (anchorSelectionPath.getParentPath() != null) {
                        Object selectedNode = ((DefaultMutableTreeNode) anchorSelectionPath.getLastPathComponent()).getUserObject();
                        if (selectedNode instanceof Function) {
                            Function function = (Function) selectedNode;

                            try {
                                commandExecutor.execute(function, createInvokeCommand(function));
                            } catch (ExecutionException e) {
                                JBPopupFactory.getInstance()
                                        .createHtmlTextBalloonBuilder(e.getMessage(), MessageType.ERROR, null)
                                        .setFadeoutTime(10000)
                                        .createBalloon()
                                        .showInCenterOf(tree);
                            }
                        }
                    }
                }
            }
        });

        return tree;
    }

    private GeneralCommandLine createInvokeCommand(Function function) {
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

        return commandLineFactory.create(function.getService().getFile().getParent().getCanonicalPath(), command);
    }
}
