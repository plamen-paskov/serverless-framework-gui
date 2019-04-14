package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import io.solidloop.jetbrains.ide.serverlessframeworkgui.Function;

import java.util.ArrayList;
import java.util.List;

public class DeployAndInvokeFunctionCommand extends AbstractCommand {
    private InvokeFunctionCommand invokeFunctionCommand;
    private Function function;

    public DeployAndInvokeFunctionCommand(CommandArguments commandArguments, InvokeFunctionCommand invokeFunctionCommand, Function function) {
        super(commandArguments);
        this.invokeFunctionCommand = invokeFunctionCommand;
        this.function = function;
    }

    @Override
    public List<String> getCommand() {
        List<String> command = new ArrayList<>();
        command.add(commandArguments.getServerlessExecutable());
        command.add("deploy");
        command.add("function");
        command.add("-f");
        command.add(function.getName());
        command.add("&&");
        command.addAll(invokeFunctionCommand.getCommand());

        return command;
    }
}
