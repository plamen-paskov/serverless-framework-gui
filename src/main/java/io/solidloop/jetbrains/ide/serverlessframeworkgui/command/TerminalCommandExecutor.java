package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.terminal.TerminalExecutionConsole;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class TerminalCommandExecutor implements CommandExecutor {
    @NonNull
    private Project project;
    @NonNull
    private String title;
    @NonNull
    private ProcessListener processListener;

    @Override
    public void execute(GeneralCommandLine commandLine) throws ExecutionException {
        ProcessHandler processHandler = createProcessHandler(commandLine);
        TerminalExecutionConsole consoleView = new TerminalExecutionConsole(project, processHandler);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(consoleView.getComponent());

        processHandler.startNotify();

        RunContentDescriptor contentDescriptor = new RunContentDescriptor(consoleView, processHandler, panel, title);
        ExecutionManager.getInstance(project).getContentManager().showRunContent(DefaultRunExecutor.getRunExecutorInstance(), contentDescriptor);
    }

    private ProcessHandler createProcessHandler(GeneralCommandLine commandLine) throws ExecutionException {
        ProcessHandler processHandler = new OSProcessHandler(commandLine);

        if (processListener != null) {
            processHandler.addProcessListener(processListener);
        }

        return processHandler;
    }
}
