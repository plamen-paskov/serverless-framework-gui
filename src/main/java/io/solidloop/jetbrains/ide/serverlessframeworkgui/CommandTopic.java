package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.util.messages.Topic;

public class CommandTopic {
    public static final Topic<CommandExecutionOutputHandler> COMMAND_EXECUTION_RESPONSE_TOPIC = Topic.create("Command execution", CommandExecutionOutputHandler.class);
}
