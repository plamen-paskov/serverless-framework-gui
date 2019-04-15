package io.solidloop.jetbrains.ide.serverlessframeworkgui.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class ServiceRepository {
    private ServiceFactory serviceFactory;
    private Project project;

    public Set<Service> getAll() {
        Set<Service> services = new HashSet<>();

        ProjectFileIndex.SERVICE.getInstance(project).iterateContent(fileOrDir -> {
            if (FileUtil.isServerlessFile(fileOrDir)) {
                services.add(serviceFactory.create(fileOrDir));
            }

            return true;
        });

        return services;
    }
}
