package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.treeStructure.Tree;
import lombok.AllArgsConstructor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

@AllArgsConstructor
public class MenuItemListener implements ActionListener {
    private String directory;
    private List<String> command;
    private String terminalTitle;
    private CommandExecutor commandExecutor;
    private CommandLineFactory commandLineFactory;
    private Tree tree;

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
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
