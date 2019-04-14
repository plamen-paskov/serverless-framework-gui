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

    private CommandArguments.CommandArgumentsBuilder getServerlessCommandArgumentsBuilder(String title, OutputListener outputListener) {
        return CommandArguments
                .builder()
                .commandExecutor(new TerminalCommandExecutor(project, title, outputListener))
                .commandLineFactory(commandLineFactory)
                .serverlessExecutable(SERVERLESS_EXECUTABLE);
    }

    private CommandArguments.CommandArgumentsBuilder getServerlessCommandArgumentsBuilder(String title) {
        return getServerlessCommandArgumentsBuilder(title, null);
    }

    private FunctionCommandOutputListener createOutputListener(Function function) {
        return new FunctionCommandOutputListener(project.getMessageBus(), topic, function);
    }

    @Override
    public Command createInvokeFunctionCommand(Function function) {
        CommandArguments commandArguments = getServerlessCommandArgumentsBuilder("Invoke " + function.getName(), createOutputListener(function))
                .workingDirectory(getWorkingDirectory(function))
                .build();
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
        CommandArguments commandArguments = getServerlessCommandArgumentsBuilder("Deploy and Invoke " + function.getName())
                .workingDirectory(getWorkingDirectory(function))
                .build();
        return new DeployAndInvokeFunctionCommand(commandArguments, (InvokeFunctionCommand) createInvokeFunctionCommand(function), function);
    }

    @Override
    public Command createDeployServiceCommand(Service service) {
        CommandArguments commandArguments = getServerlessCommandArgumentsBuilder("Deploy " + service.getName())
                .workingDirectory(getWorkingDirectory(service))
                .build();

        return new DeployServiceCommand(commandArguments);
    }

    @Override
    public Command createRemoveServiceCommand(Service service) {
        CommandArguments commandArguments = getServerlessCommandArgumentsBuilder("Remove " + service.getName())
                .workingDirectory(getWorkingDirectory(service))
                .build();

        return new RemoveServiceCommand(commandArguments);
    }
}
