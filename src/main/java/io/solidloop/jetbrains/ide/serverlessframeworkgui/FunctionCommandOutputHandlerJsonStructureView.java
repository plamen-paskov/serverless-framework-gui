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
public class FunctionCommandOutputHandlerJsonStructureView implements FunctionCommandOutputHandler {
    private Project project;
    private ObjectMapper objectMapper;
    private Configuration configuration;

    @Override
    public void receive(final Output output, final Function function) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String data = output.getStdout();

            LightVirtualFile file = new LightVirtualFile(function.getName() + ".json", output.getStdout());
            FileEditor selectedEditor = FileEditorManager.getInstance(project).getSelectedEditor(file);

            if (configuration.isOpenFunctionInvocationResponseFile()) {
                FileEditorManager.getInstance(project).openFile(file, true);
            }

            if (isValidJson(data) && configuration.isShowJsonStructureView()) {
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

                        if (configuration.isOpenFunctionInvocationResponseFile() && configuration.isCloseFunctionInvocationResponseFile()) {
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
