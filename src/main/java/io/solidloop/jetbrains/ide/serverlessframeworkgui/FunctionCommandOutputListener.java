package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.OutputListener;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class FunctionCommandOutputListener extends OutputListener {
    private MessageBus messageBus;
    private Topic<FunctionCommandOutputHandler> topic;
    private Function function;

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        super.processTerminated(event);
        messageBus.syncPublisher(topic).receive(getOutput(), function);
    }
}
