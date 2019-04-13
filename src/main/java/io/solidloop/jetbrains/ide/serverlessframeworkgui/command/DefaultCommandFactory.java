package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.Function;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.service.Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultCommandFactory implements CommandFactory {
    private static final String SERVERLESS_EXECUTABLE = "serverless";
    private Topic<FunctionCommandOutputHandler> functionCommandOutputHandlerTopic;

    @NonNull
    private Project project;
    @NonNull
    private CommandLineFactory commandLineFactory;

    private ServerlessCommandArguments.ServerlessCommandArgumentsBuilder getServerlessCommandArgumentsBuilder() {
        return ServerlessCommandArguments
                .builder()
                .commandExecutor(new TerminalCommandExecutor(project))
                .commandLineFactory(commandLineFactory)
                .serverlessExecutable(SERVERLESS_EXECUTABLE);
    }

    private Topic<FunctionCommandOutputHandler> getFunctionCommandOutputHandlerTopic() {
        if (functionCommandOutputHandlerTopic == null) {
            functionCommandOutputHandlerTopic = Topic.create("Function command response", FunctionCommandOutputHandler.class);
        }
        return functionCommandOutputHandlerTopic;
    }

    private FunctionCommandOutputListener createOutputListener(Function function) {
        return new FunctionCommandOutputListener(project.getMessageBus(), getFunctionCommandOutputHandlerTopic(), function);
    }

    @Override
    public Command createInvokeFunctionCommand(Function function) {
        ServerlessCommandArguments serverlessCommandArguments = getServerlessCommandArgumentsBuilder()
                .terminalTitle("Invoke " + function.getName())
                .workingDirectory(getWorkingDirectory(function))
                .outputListener(createOutputListener(function))
                .build();
        return new InvokeFunctionCommand(serverlessCommandArguments, function);
    }

    private String getWorkingDirectory(Service service) {
        return service.getFile().getParent().getCanonicalPath();
    }

    private String getWorkingDirectory(Function function) {
        return getWorkingDirectory(function.getService());
    }

    @Override
    public Command createDeployAndInvokeFunctionCommand(Function function) {
        ServerlessCommandArguments serverlessCommandArguments = getServerlessCommandArgumentsBuilder()
                .terminalTitle("Deploy and Invoke " + function.getName())
                .workingDirectory(getWorkingDirectory(function))
                .build();
        return new DeployAndInvokeFunctionCommand(serverlessCommandArguments, (InvokeFunctionCommand) createInvokeFunctionCommand(function), function);
    }

    @Override
    public Command createDeployServiceCommand(Service service) {
        ServerlessCommandArguments serverlessCommandArguments = getServerlessCommandArgumentsBuilder()
                .terminalTitle("Deploy " + service.getName())
                .workingDirectory(getWorkingDirectory(service))
                .build();

        return new DeployServiceCommand(serverlessCommandArguments);
    }

    @Override
    public Command createRemoveServiceCommand(Service service) {
        ServerlessCommandArguments serverlessCommandArguments = getServerlessCommandArgumentsBuilder()
                .terminalTitle("Remove " + service.getName())
                .workingDirectory(getWorkingDirectory(service))
                .build();

        return new RemoveServiceCommand(serverlessCommandArguments);
    }
}
