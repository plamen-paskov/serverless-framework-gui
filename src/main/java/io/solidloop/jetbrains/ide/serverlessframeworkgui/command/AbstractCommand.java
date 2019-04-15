package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractCommand implements Command {
    protected CommandArguments commandArguments;

    @Override
    public void execute() throws ExecutionException {
        GeneralCommandLine generalCommandLine = commandArguments.getCommandLineFactory().create(commandArguments.getWorkingDirectory(), getCommand());
        commandArguments.getCommandExecutor().execute(generalCommandLine);
    }
}
