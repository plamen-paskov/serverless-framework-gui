package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessListener;

public interface CommandExecutor {
    void execute(String terminalTitle, GeneralCommandLine commandLine, ProcessListener processListener) throws ExecutionException;
}
