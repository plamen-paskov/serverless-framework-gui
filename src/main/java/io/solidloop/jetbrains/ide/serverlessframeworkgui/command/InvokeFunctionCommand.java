package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import io.solidloop.jetbrains.ide.serverlessframeworkgui.Function;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.service.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InvokeFunctionCommand extends AbstractCommand {
    private Function function;

    public InvokeFunctionCommand(CommandArguments commandArguments, Function function) {
        super(commandArguments);
        this.function = function;
    }

    @Override
    public List<String> getCommand() {
        Service service = function.getService();

        List<String> command = new ArrayList<>();
        command.add(commandArguments.getServerlessExecutable());
        command.add("invoke");
        command.add("-f");
        command.add(function.getName());

        if (service.getRegion() != null) {
            command.add("-r");
            command.add(function.getService().getRegion());
        }

        String dataFile = getDataFile(function);
        File file = new File(dataFile);
        if (!file.exists()) {
            command.add("-d");
            command.add("{}");
        } else {
            command.add("-p");
            command.add(dataFile);
        }

        return command;
    }

    private String getDataFile(Function function) {
        return function.getService().getFile().getParent().getCanonicalPath() + "/serverless-framework-gui/" + function.getService().getName() + "/" + function.getName() + ".json";
    }
}
