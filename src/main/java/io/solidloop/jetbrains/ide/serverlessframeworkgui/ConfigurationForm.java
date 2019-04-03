package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

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
            checkboxShowJsonStructureView = new JBCheckBox("Show JSON Structure View popup when function invocation response is valid JSON");
            checkboxOpenFile = new JBCheckBox("Open function invocation response as a file");
            checkboxCloseFile = new JBCheckBox("Close function invocation response file when JSON Structure View popup is closed");

            Configuration configuration = Configuration.getInstance();
            checkboxShowJsonStructureView.setSelected(configuration.isShowJsonStructureView());
            checkboxOpenFile.setSelected(configuration.isOpenFunctionInvocationResponseFile());
            checkboxCloseFile.setSelected(configuration.isCloseFunctionInvocationResponseFile());

            toggleCheckboxes();
            checkboxShowJsonStructureView.addChangeListener(changeEvent -> toggleCheckboxes());

            formChangeListener.add(checkboxShowJsonStructureView);
            formChangeListener.add(checkboxOpenFile);
            formChangeListener.add(checkboxCloseFile);

            panel = new JPanel();
            BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
            panel.setLayout(layout);

            panel.add(checkboxShowJsonStructureView);
            panel.add(checkboxCloseFile);
            panel.add(checkboxOpenFile);
        }

        return panel;
    }

    private void toggleCheckboxes() {
        if (checkboxShowJsonStructureView.isSelected()) {
            checkboxOpenFile.setEnabled(false);
            checkboxOpenFile.setSelected(true);
        } else {
            checkboxOpenFile.setEnabled(true);
            checkboxCloseFile.setSelected(false);
        }
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
