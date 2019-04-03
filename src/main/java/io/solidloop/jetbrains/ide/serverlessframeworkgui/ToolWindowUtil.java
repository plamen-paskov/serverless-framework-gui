package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

public class ToolWindowUtil {
    public static ToolWindow getStructureView(Project project) {
        return ToolWindowManager.getInstance(project).getToolWindow("Structure");
    }
}
