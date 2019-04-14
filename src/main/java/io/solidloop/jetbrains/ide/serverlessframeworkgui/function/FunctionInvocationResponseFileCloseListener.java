package io.solidloop.jetbrains.ide.serverlessframeworkgui.function;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.config.PluginSettings;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class FunctionInvocationResponseFileCloseListener implements FileEditorManagerListener {
    @NonNull
    private ToolWindow structureView;

    @NonNull
    private PluginSettings pluginSettings;

    private Set<VirtualFile> files = new HashSet<>();

    public void addFile(VirtualFile file) {
        files.add(file);
    }

    @Override
    public void fileClosed(@NotNull final FileEditorManager source, @NotNull final VirtualFile file) {
        if (pluginSettings.isCloseStructureView() && files.contains(file)) {
            files.remove(file);

            if (files.size() == 0) {
                structureView.hide(null);
            }
        }
    }
}
