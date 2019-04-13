package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.OutputListener;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ServerlessCommandArguments {
    private String serverlessExecutable;
    private String workingDirectory;
    private String terminalTitle;
    private CommandExecutor commandExecutor;
    private CommandLineFactory commandLineFactory;
    private OutputListener outputListener;
}
