package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PtyCommandLine;
import lombok.AllArgsConstructor;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ExecScriptCommandLineFactory implements CommandLineFactory {
    private String execScriptPath;

    @Override
    public GeneralCommandLine create(String directory, List<String> command) {
        List<String> cmd = new ArrayList<>();
        cmd.add(execScriptPath);
        cmd.add(new GeneralCommandLine(command).getCommandLineString());

        return new PtyCommandLine()
                .withCharset(Charset.forName("UTF-8"))
                .withWorkDirectory(directory)
                .withExePath("/bin/bash")
                .withParameters(cmd);
    }
}
