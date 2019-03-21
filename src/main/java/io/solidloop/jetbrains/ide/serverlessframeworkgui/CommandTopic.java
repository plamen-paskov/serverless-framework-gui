package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.util.messages.Topic;

public class CommandTopic {
    public static final Topic<FunctionCommandOutputHandler> FUNCTION_COMMAND_RESPONSE_TOPIC = Topic.create("Function command response", FunctionCommandOutputHandler.class);
}
