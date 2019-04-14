package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommandArguments {
    private String serverlessExecutable;
    private String workingDirectory;
    private CommandExecutor commandExecutor;
    private CommandLineFactory commandLineFactory;
}
