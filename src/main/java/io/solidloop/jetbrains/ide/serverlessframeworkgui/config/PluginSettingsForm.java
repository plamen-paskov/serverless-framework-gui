package io.solidloop.jetbrains.ide.serverlessframeworkgui.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PluginSettingsForm implements Configurable {
    private PluginSettings pluginSettings = PluginSettings.getInstance();
    private FormModificationHandler formModificationHandler = new FormModificationHandler();
    private JPanel panel;
    private JBCheckBox openStructureView;
    private JBCheckBox closeStructureView;
    private JBCheckBox openFunctionInvocationResponseAsFile;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Serverless Framework GUI";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (panel == null) {
            openFunctionInvocationResponseAsFile = new JBCheckBox("Open function invocation response as a file");
            openStructureView = new JBCheckBox("Open Structure View when function invocation response file is opened");
            closeStructureView = new JBCheckBox("Close Structure View when all function invocation response files are closed");

            openFunctionInvocationResponseAsFile.setSelected(pluginSettings.isOpenFunctionInvocationResponseAsFile());
            openStructureView.setSelected(pluginSettings.isOpenStructureView());
            closeStructureView.setSelected(pluginSettings.isCloseStructureView());

            toggle();
            openFunctionInvocationResponseAsFile.addActionListener(changeEvent -> toggle());

            formModificationHandler.add(openFunctionInvocationResponseAsFile);
            formModificationHandler.add(openStructureView);
            formModificationHandler.add(closeStructureView);

            panel = new JPanel();
            BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
            panel.setLayout(layout);

            panel.add(openFunctionInvocationResponseAsFile);
            panel.add(openStructureView);
            panel.add(closeStructureView);
        }

        return panel;
    }

    private void toggle() {
        if (openFunctionInvocationResponseAsFile.isSelected()) {
            openStructureView.setEnabled(true);
            closeStructureView.setEnabled(true);
        } else {
            openStructureView.setEnabled(false);
            closeStructureView.setEnabled(false);
        }
    }

    @Override
    public boolean isModified() {
        return formModificationHandler.isModified();
    }

    @Override
    public void apply() {
        pluginSettings.setOpenFunctionInvocationResponseAsFile(openFunctionInvocationResponseAsFile.isSelected());
        pluginSettings.setOpenStructureView(openStructureView.isSelected());
        pluginSettings.setCloseStructureView(closeStructureView.isSelected());

        formModificationHandler.add(openFunctionInvocationResponseAsFile);
        formModificationHandler.add(openStructureView);
        formModificationHandler.add(closeStructureView);
    }

    @Override
    public void reset() {
        formModificationHandler.reset();
    }

    @Override
    public void disposeUIResources() {
        openFunctionInvocationResponseAsFile = null;
        openStructureView = null;
        closeStructureView = null;
        panel = null;
    }
}
