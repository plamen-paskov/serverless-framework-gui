package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class ServiceRepository {
    private Project project;
    private ServiceFactory serviceFactory;

    public Set<Service> getAll() {
        Set<Service> services = new HashSet<>();

        ProjectFileIndex.getInstance(project).iterateContent(file -> {
            if (!file.isDirectory() && ServerlessFileUtil.isServerlessFile(file)) {
                try {
                    services.add(serviceFactory.create(file));
                } catch (IOException e) {
                    NotificationUtil.displayError("Parse error", file.getCanonicalPath() + " is invalid and cannot be parsed");
                }
            }

            return true;
        });

        return services;
    }

    public Set<Service> filterBy(VirtualFile dir) {
        Set<Service> filteredServices = new HashSet<>();
        getAll().iterator().forEachRemaining(service -> {
            if (!fileResidesInDir(service.getFile(), dir)) {
                filteredServices.add(service);
            }
        });

        return filteredServices;
    }

    private boolean fileResidesInDir(VirtualFile file, VirtualFile dir) {
        String filePath = file.getCanonicalPath();
        return !(filePath != null && filePath.startsWith(dir.getCanonicalPath() + "/"));
    }
}
