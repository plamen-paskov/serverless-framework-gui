package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class ServiceRepository {
    private ServiceFactory serviceFactory;

    public Set<Service> getAll() {
        Set<Service> services = new HashSet<>();

        FileTypeIndex.getFiles(YAMLFileType.YML, new GlobalSearchScope() {
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
                if (file.getExtension() != null) {
                    return file.getName().substring(0, file.getName().length() - file.getExtension().length() - 1).equals("serverless");
                }

                return false;
            }
        }).forEach(file -> {
            try {
                services.add(serviceFactory.create(file));
            } catch (ServiceException e) {
            }
        });

        return services;
    }
}
