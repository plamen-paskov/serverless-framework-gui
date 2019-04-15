package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.ExecutionException;

import java.util.List;

public interface Command {
    List<String> getCommand();

    void execute() throws ExecutionException;
}
