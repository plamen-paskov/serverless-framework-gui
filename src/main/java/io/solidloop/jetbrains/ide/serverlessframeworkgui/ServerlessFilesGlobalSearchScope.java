package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class ServerlessFilesGlobalSearchScope extends GlobalSearchScope {
    private VirtualFile directory;

    @Override
    public int compare(@NotNull final VirtualFile file1, @NotNull final VirtualFile file2) {
        return 0;
    }

    @Override
    public boolean isSearchInModuleContent(@NotNull final Module aModule) {
        return false;
    }

    @Override
    public boolean isSearchInLibraries() {
        return false;
    }

    @Override
    public boolean contains(@NotNull final VirtualFile file) {
        VirtualFile parent = file.getParent();
        return parent != null && in(parent);
    }

    private boolean in(@NotNull VirtualFile parent) {
        return VfsUtilCore.isAncestor(directory, parent, false);
    }
}
