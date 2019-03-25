package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConfigurationForm implements Configurable {
    private FormChangeListener formChangeListener = new FormChangeListener();
    private JPanel panel;
    private JBCheckBox checkboxShowJsonStructureView;
    private JBCheckBox checkboxOpenFile;
    private JBCheckBox checkboxCloseFile;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Serverless Framework GUI";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (panel == null) {
            checkboxOpenFile = new JBCheckBox("Open function invocation response as a file");
            checkboxShowJsonStructureView = new JBCheckBox("Show JSON Structure View popup when function invocation response is valid JSON");
            checkboxCloseFile = new JBCheckBox("Close function invocation response file when JSON Structure View popup is closed");

            toggleCheckboxes();
            checkboxShowJsonStructureView.addChangeListener(changeEvent -> toggleCheckboxes());

            Configuration configuration = Configuration.getInstance();
            checkboxShowJsonStructureView.setSelected(configuration.isShowJsonStructureView());
            checkboxCloseFile.setSelected(configuration.isCloseFunctionInvocationResponseFile());
            checkboxOpenFile.setSelected(configuration.isOpenFunctionInvocationResponseFile());

            formChangeListener.add(checkboxShowJsonStructureView);
            formChangeListener.add(checkboxCloseFile);
            formChangeListener.add(checkboxOpenFile);

            panel = new JPanel();
            GroupLayout layout = new GroupLayout(panel);
            panel.setLayout(layout);

            layout.setVerticalGroup(
                    layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(checkboxShowJsonStructureView))
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(checkboxCloseFile))
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(checkboxOpenFile)));

            layout.setHorizontalGroup(
                    layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(checkboxShowJsonStructureView)
                                    .addComponent(checkboxCloseFile)
                                    .addComponent(checkboxOpenFile)));
        }

        return panel;
    }

    private void toggleCheckboxes() {
        checkboxCloseFile.setEnabled(checkboxShowJsonStructureView.isSelected());
    }

    @Override
    public boolean isModified() {
        return formChangeListener.isModified();
    }

    @Override
    public void apply() {
        Configuration configuration = Configuration.getInstance();
        configuration.setShowJsonStructureView(checkboxShowJsonStructureView.isSelected());
        configuration.setCloseFunctionInvocationResponseFile(checkboxCloseFile.isSelected());
        configuration.setOpenFunctionInvocationResponseFile(checkboxOpenFile.isSelected());

        formChangeListener.add(checkboxShowJsonStructureView);
        formChangeListener.add(checkboxCloseFile);
        formChangeListener.add(checkboxOpenFile);
    }

    @Override
    public void reset() {
        formChangeListener.reset();
    }

    @Override
    public void disposeUIResources() {
        checkboxShowJsonStructureView = null;
        checkboxOpenFile = null;
        checkboxCloseFile = null;
        panel = null;
    }
}
