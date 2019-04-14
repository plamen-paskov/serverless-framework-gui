package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.OutputListener;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.function.Function;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.service.Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultCommandFactory implements CommandFactory {
    private static final String SERVERLESS_EXECUTABLE = "serverless";

    @NonNull
    private Project project;
    @NonNull
    private CommandLineFactory commandLineFactory;
    @NonNull
    private Topic<FunctionCommandOutputHandler> topic;

    private FunctionCommandOutputListener createOutputListener(Function function) {
        return new FunctionCommandOutputListener(project.getMessageBus(), topic, function);
    }

    private CommandArguments createCommandArguments(String title, String workingDirectory, OutputListener outputListener) {
        return CommandArguments
                .builder()
                .commandExecutor(new TerminalCommandExecutor(project, title, outputListener))
                .commandLineFactory(commandLineFactory)
                .serverlessExecutable(SERVERLESS_EXECUTABLE)
                .workingDirectory(workingDirectory)
                .build();
    }

    @Override
    public Command createInvokeFunctionCommand(Function function) {
        CommandArguments commandArguments = createCommandArguments("Invoke " + function.getName(), getWorkingDirectory(function), createOutputListener(function));
        return new InvokeFunctionCommand(commandArguments, function);
    }

    private String getWorkingDirectory(Service service) {
        return service.getFile().getParent().getCanonicalPath();
    }

    private String getWorkingDirectory(Function function) {
        return getWorkingDirectory(function.getService());
    }

    @Override
    public Command createDeployAndInvokeFunctionCommand(Function function) {
        CommandArguments commandArguments = createCommandArguments("Deploy and Invoke " + function.getName(), getWorkingDirectory(function), null);
        return new DeployAndInvokeFunctionCommand(commandArguments, (InvokeFunctionCommand) createInvokeFunctionCommand(function), function);
    }

    @Override
    public Command createDeployServiceCommand(Service service) {
        CommandArguments commandArguments = createCommandArguments("Deploy " + service.getName(), getWorkingDirectory(service), null);
        return new DeployServiceCommand(commandArguments);
    }

    @Override
    public Command createRemoveServiceCommand(Service service) {
        CommandArguments commandArguments = createCommandArguments("Remove " + service.getName(), getWorkingDirectory(service), null);
        return new RemoveServiceCommand(commandArguments);
    }
}
