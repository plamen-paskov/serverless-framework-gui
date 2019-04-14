package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;

public interface CommandExecutor {
    void execute(GeneralCommandLine commandLine) throws ExecutionException;
}
