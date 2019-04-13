package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import io.solidloop.jetbrains.ide.serverlessframeworkgui.Function;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.service.Service;

public interface CommandFactory {
    Command createInvokeFunctionCommand(Function function);

    Command createDeployAndInvokeFunctionCommand(Function function);

    Command createDeployServiceCommand(Service service);

    Command createRemoveServiceCommand(Service service);
}
