package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractCommand implements Command {
    protected ServerlessCommandArguments serverlessCommandArguments;

    @Override
    public void execute() throws ExecutionException {
        GeneralCommandLine generalCommandLine = serverlessCommandArguments.getCommandLineFactory().create(serverlessCommandArguments.getWorkingDirectory(), getCommand());
        serverlessCommandArguments.getCommandExecutor().execute(serverlessCommandArguments.getTerminalTitle(), generalCommandLine, serverlessCommandArguments.getOutputListener());
    }
}
