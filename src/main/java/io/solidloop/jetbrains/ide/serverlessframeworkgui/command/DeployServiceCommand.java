package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import java.util.ArrayList;
import java.util.List;

public class DeployServiceCommand extends AbstractCommand {
    public DeployServiceCommand(CommandArguments commandArguments) {
        super(commandArguments);
    }

    @Override
    public List<String> getCommand() {
        List<String> command = new ArrayList<>();
        command.add(commandArguments.getServerlessExecutable());
        command.add("deploy");

        return command;
    }
}
