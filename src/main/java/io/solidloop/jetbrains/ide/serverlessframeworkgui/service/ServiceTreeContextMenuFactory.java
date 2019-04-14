package io.solidloop.jetbrains.ide.serverlessframeworkgui.service;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.command.Command;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.command.CommandFactory;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.event.ActionEvent;

@RequiredArgsConstructor
public class ServiceTreeContextMenuFactory {
    @NonNull
    private Project project;
    @NonNull
    private CommandFactory commandFactory;

    public JBPopupMenu createContextMenu(Object userObject) {
        return userObject instanceof Service ? createContextMenu((Service) userObject) : createContextMenu((Function) userObject);
    }

    public JBPopupMenu createContextMenu(Service service) {
        JBPopupMenu popup = new JBPopupMenu();

        JBMenuItem openFileMenuItem = new JBMenuItem("Open file");
        openFileMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                FileEditorManager.getInstance(project).openFile(service.getFile(), true);
            }
        });
        popup.add(openFileMenuItem);

        JBMenuItem deployMenuItem = new JBMenuItem("Deploy");
        deployMenuItem.addActionListener(actionEvent -> execute(commandFactory.createDeployServiceCommand(service)));
        popup.add(deployMenuItem);

        JBMenuItem removeMenuItem = new JBMenuItem("Remove");
        removeMenuItem.addActionListener(actionEvent -> execute(commandFactory.createRemoveServiceCommand(service)));
        popup.add(removeMenuItem);

        return popup;
    }

    public JBPopupMenu createContextMenu(Function function) {
        JBPopupMenu popup = new JBPopupMenu();

        JBMenuItem deployAndInvokeMenuItem = new JBMenuItem("Deploy and Invoke");
        deployAndInvokeMenuItem.addActionListener(actionEvent -> execute(commandFactory.createDeployAndInvokeFunctionCommand(function)));
        popup.add(deployAndInvokeMenuItem);

        return popup;
    }

    private void execute(Command command) {
        try {
            command.execute();
        } catch (ExecutionException ignored) {
        }
    }
}
