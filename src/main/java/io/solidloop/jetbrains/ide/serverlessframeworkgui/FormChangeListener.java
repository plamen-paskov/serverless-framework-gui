package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import javax.swing.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FormChangeListener {
    private Set<JComponent> components = new HashSet<>();
    private Map<JComponent, Object> defaultValues = new HashMap<>();

    public void add(JCheckBox component) {
        defaultValues.put(component, component.isSelected());
        components.add(component);
    }

    public void reset() {
        for (JComponent component : components) {
            if (component instanceof JCheckBox) {
                ((JCheckBox) component).setSelected((boolean) defaultValues.get(component));
            }
        }
    }

    public boolean isModified() {
        for (JComponent component : components) {
            if (component instanceof JCheckBox) {
                if (((JCheckBox) component).isSelected() != (boolean) defaultValues.get(component)) {
                    return true;
                }
            }
        }

        return false;
    }
}
