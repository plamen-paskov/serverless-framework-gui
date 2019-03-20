package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.execution.Output;
import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.json.structureView.JsonStructureViewBuilderFactory;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import lombok.AllArgsConstructor;

import java.awt.*;
import java.io.IOException;

@AllArgsConstructor
public class CommandExecutionOutputHandlerJsonStructureView implements CommandExecutionOutputHandler {
    private Project project;
    private ObjectMapper objectMapper;

    @Override
    public void receive(final Function function, final Output output, final boolean openFile, final boolean closeFile) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String data = output.getStdout();

            if (isValidJson(data)) {
                LightVirtualFile file = new LightVirtualFile(function.getName() + ".json", output.getStdout());
                FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor(file);

                if (openFile) {
                    FileEditorManager.getInstance(project).openFile(file, true);
                }

                PsiFile jsonPsiFile = PsiManager.getInstance(project).findFile(file);

                if (jsonPsiFile != null) {
                    StructureViewBuilder structureViewBuilder = new JsonStructureViewBuilderFactory().getStructureViewBuilder(jsonPsiFile);
                    if (structureViewBuilder != null) {
                        StructureView structureView = structureViewBuilder.createStructureView(selectedEditor, project);

                        ComponentPopupBuilder componentPopupBuilder = JBPopupFactory.getInstance()
                                .createComponentPopupBuilder(structureView.getComponent(), structureView.getComponent())
                                .setMovable(true)
                                .setResizable(true)
                                .setTitle(function.getName());

                        if (openFile && closeFile) {
                            componentPopupBuilder.setCancelCallback(() -> {
                                FileEditorManager.getInstance(project).closeFile(file);
                                return true;
                            });
                        }

                        componentPopupBuilder
                                .setMinSize(new Dimension(600, 300))
                                .createPopup()
                                .showInFocusCenter();
                    }
                }
            }
        });
    }

    private boolean isValidJson(String data) {
        try {
            objectMapper.readTree(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
