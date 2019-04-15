package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PtyCommandLine;
import lombok.AllArgsConstructor;

import java.nio.charset.Charset;
import java.util.List;

@AllArgsConstructor
public class ExecScriptCommandLineFactory implements CommandLineFactory {
    private String execScriptPath;

    @Override
    public GeneralCommandLine create(String directory, List<String> command) {
        return new PtyCommandLine()
                .withCharset(Charset.forName("UTF-8"))
                .withWorkDirectory(directory)
                .withExePath(execScriptPath)
                .withParameters(new GeneralCommandLine(command).getCommandLineString());
    }
}
