package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.Output;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.function.Function;

public interface FunctionCommandOutputHandler {
    void receive(Output output, Function function);
}
