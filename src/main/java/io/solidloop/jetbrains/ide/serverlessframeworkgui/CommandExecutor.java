package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;

public interface CommandExecutor {
    String execute(String terminalTitle, GeneralCommandLine commandLine) throws ExecutionException;
}
