package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.Output;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.testFramework.LightVirtualFile;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.config.PluginSettings;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.function.Function;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.function.FunctionInvocationResponseFileCloseListener;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FunctionCommandOutputHandlerStructureView implements FunctionCommandOutputHandler {
    private FunctionInvocationResponseFileCloseListener functionInvocationResponseFileCloseListener;
    private Project project;
    private PluginSettings pluginSettings;
    private ToolWindow structureView;

    @Override
    public void receive(final Output output, final Function function) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String ext = guessFileExt(output.getStdout());

            if (ext != null) {
                LightVirtualFile file = new LightVirtualFile(function.getName() + "." + ext, output.getStdout());

                if (pluginSettings.isOpenFunctionInvocationResponseAsFile()) {
                    FileEditorManager.getInstance(project).openFile(file, true);

                    if (pluginSettings.isOpenStructureView() && !structureView.isVisible()) {
                        structureView.show(null);
                    }

                    if (pluginSettings.isCloseStructureView()) {
                        functionInvocationResponseFileCloseListener.addFile(file);
                    }
                }
            }
        });
    }

    private String guessFileExt(String content) {
        content = content.trim();
        if (content.startsWith("{") && content.endsWith("}")) {
            return "json";
        } else if (content.startsWith("<") && content.endsWith(">")) {
            return "xml";
        }

        return null;
    }
}
