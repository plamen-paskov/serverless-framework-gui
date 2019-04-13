package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import java.util.ArrayList;
import java.util.List;

public class RemoveServiceCommand extends AbstractCommand {
    public RemoveServiceCommand(ServerlessCommandArguments serverlessCommandArguments) {
        super(serverlessCommandArguments);
    }

    @Override
    public List<String> getCommand() {
        List<String> command = new ArrayList<>();
        command.add(serverlessCommandArguments.getServerlessExecutable());
        command.add("remove");

        return command;
    }
}
