package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.Output;

public interface CommandExecutionOutputHandler {
    void receive(Function function, Output output, boolean openFile, boolean closeFile);
}
