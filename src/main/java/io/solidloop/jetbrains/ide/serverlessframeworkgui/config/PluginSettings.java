package io.solidloop.jetbrains.ide.serverlessframeworkgui.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.ProjectUtil;
import lombok.Data;

@Data
@State(name = "PluginSettings", storages = @Storage("serverless-framework-gui/plugin-settings.xml"))
public class PluginSettings implements PersistentStateComponent<PluginSettings> {
    private boolean openStructureView = true;
    private boolean closeStructureView = true;
    private boolean openFunctionInvocationResponseAsFile = true;
    private boolean deployAndInvokeInsteadInvoke = false;

    public static PluginSettings getInstance() {
        return ServiceManager.getService(ProjectUtil.guessCurrentProject(null), PluginSettings.class);
    }

    @Override
    public PluginSettings getState() {
        return this;
    }

    @Override
    public void loadState(final PluginSettings state) {
        setOpenStructureView(state.isOpenStructureView());
        setCloseStructureView(state.isCloseStructureView());
        setOpenFunctionInvocationResponseAsFile(state.isOpenFunctionInvocationResponseAsFile());
        setDeployAndInvokeInsteadInvoke(state.isDeployAndInvokeInsteadInvoke());
    }
}
