package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.OutputListener;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.project.Project;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CommandExecutionOutputListener extends OutputListener {
    private Project project;
    private Function function;

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        super.processTerminated(event);

        project.getMessageBus()
                .syncPublisher(CommandTopic.COMMAND_EXECUTION_RESPONSE_TOPIC)
                .receive(function, getOutput(), true, false);
    }
}
