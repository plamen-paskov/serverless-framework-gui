package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.Output;

public interface FunctionCommandOutputHandler {
    void receive(Output output, Function function);
}
