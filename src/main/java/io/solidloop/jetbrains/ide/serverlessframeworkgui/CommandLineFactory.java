package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.execution.configurations.GeneralCommandLine;

import java.util.List;

public interface CommandLineFactory {
    GeneralCommandLine create(String directory, List<String> command);
}
