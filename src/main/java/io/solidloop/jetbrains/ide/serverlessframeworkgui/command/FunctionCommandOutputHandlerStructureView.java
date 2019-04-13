package io.solidloop.jetbrains.ide.serverlessframeworkgui.command;

import com.intellij.execution.Output;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.testFramework.LightVirtualFile;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.config.Configuration;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.Function;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.FunctionInvocationResponseFileEditorManagerListener;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FunctionCommandOutputHandlerStructureView implements FunctionCommandOutputHandler {
    private FunctionInvocationResponseFileEditorManagerListener functionInvocationResponseFileEditorManagerListener;
    private Project project;
    private Configuration configuration;
    private ToolWindow structureView;

    @Override
    public void receive(final Output output, final Function function) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String ext = guessFileExt(output.getStdout());

            if (ext != null) {
                LightVirtualFile file = new LightVirtualFile(function.getName() + "." + ext, output.getStdout());

                if (configuration.isOpenFunctionInvocationResponseAsFile()) {
                    FileEditorManager.getInstance(project).openFile(file, true);

                    if (configuration.isOpenStructureView() && !structureView.isVisible()) {
                        structureView.show(null);
                    }

                    if (configuration.isCloseStructureView()) {
                        functionInvocationResponseFileEditorManagerListener.addFile(file);
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
