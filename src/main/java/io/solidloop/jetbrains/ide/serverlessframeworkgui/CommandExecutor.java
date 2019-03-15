package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;

public interface CommandExecutor {
    void execute(Function function, GeneralCommandLine commandLine) throws ExecutionException;
}
