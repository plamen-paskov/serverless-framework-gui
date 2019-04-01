package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.ProjectUtil;

@State(name = "Configuration", storages = @Storage("serverless-framework-gui/config.xml"))
public class Configuration implements PersistentStateComponent<Configuration.OptionSet> {
    private OptionSet optionSet = new OptionSet();

    public static Configuration getInstance() {
        return ServiceManager.getService(ProjectUtil.guessCurrentProject(null), Configuration.class);
    }

    @Override
    public OptionSet getState() {
        return optionSet;
    }

    @Override
    public void loadState(final OptionSet state) {
        optionSet = state;
    }

    public boolean isOpenFunctionInvocationResponseFile() {
        return optionSet.openFile;
    }

    public void setOpenFunctionInvocationResponseFile(boolean openFile) {
        optionSet.openFile = openFile;
    }

    public boolean isCloseFunctionInvocationResponseFile() {
        return optionSet.closeFile;
    }

    public void setCloseFunctionInvocationResponseFile(boolean closeFile) {
        optionSet.closeFile = closeFile;
    }

    public boolean isShowJsonStructureView() {
        return optionSet.showJsonStructureView;
    }

    public void setShowJsonStructureView(boolean showJsonStructureView) {
        optionSet.showJsonStructureView = showJsonStructureView;
    }

    public static final class OptionSet {
        public boolean showJsonStructureView = true;
        public boolean openFile = true;
        public boolean closeFile = true;
    }
}
