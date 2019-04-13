package io.solidloop.jetbrains.ide.serverlessframeworkgui.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConfigurationForm implements Configurable {
    private Configuration configuration = Configuration.getInstance();
    private FormChangeListener formChangeListener = new FormChangeListener();
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

            openFunctionInvocationResponseAsFile.setSelected(configuration.isOpenFunctionInvocationResponseAsFile());
            openStructureView.setSelected(configuration.isOpenStructureView());
            closeStructureView.setSelected(configuration.isCloseStructureView());

            toggle();
            openFunctionInvocationResponseAsFile.addActionListener(changeEvent -> toggle());

            formChangeListener.add(openFunctionInvocationResponseAsFile);
            formChangeListener.add(openStructureView);
            formChangeListener.add(closeStructureView);

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
        return formChangeListener.isModified();
    }

    @Override
    public void apply() {
        configuration.setOpenFunctionInvocationResponseAsFile(openFunctionInvocationResponseAsFile.isSelected());
        configuration.setOpenStructureView(openStructureView.isSelected());
        configuration.setCloseStructureView(closeStructureView.isSelected());

        formChangeListener.add(openFunctionInvocationResponseAsFile);
        formChangeListener.add(openStructureView);
        formChangeListener.add(closeStructureView);
    }

    @Override
    public void reset() {
        formChangeListener.reset();
    }

    @Override
    public void disposeUIResources() {
        openFunctionInvocationResponseAsFile = null;
        openStructureView = null;
        closeStructureView = null;
        panel = null;
    }
}
