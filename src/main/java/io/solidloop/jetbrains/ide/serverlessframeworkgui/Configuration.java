package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.ProjectUtil;
import lombok.Data;

@Data
@State(name = "Configuration", storages = @Storage("serverless-framework-gui/configuration.xml"))
public class Configuration implements PersistentStateComponent<Configuration> {
    private boolean openStructureView = true;
    private boolean closeStructureView = true;
    private boolean openFunctionInvocationResponseAsFile = true;

    public static Configuration getInstance() {
        return ServiceManager.getService(ProjectUtil.guessCurrentProject(null), Configuration.class);
    }

    @Override
    public Configuration getState() {
        return this;
    }

    @Override
    public void loadState(final Configuration state) {
        setOpenStructureView(state.isOpenStructureView());
        setCloseStructureView(state.isCloseStructureView());
        setOpenFunctionInvocationResponseAsFile(state.isOpenFunctionInvocationResponseAsFile());
    }
}
