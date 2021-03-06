package io.solidloop.jetbrains.ide.serverlessframeworkgui.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;

@RequiredArgsConstructor
public abstract class AbstractServiceVirtualFileListener implements VirtualFileListener {
    @NonNull
    protected Project project;
    @NonNull
    protected ServiceFactory serviceFactory;

    private Queue<VirtualFile> beforeDeletionFiles;

    abstract protected void onCreateOrUpdate(Service service);

    abstract protected void onDelete(VirtualFile file);

    abstract protected void onMoveInAnotherDirectory(VirtualFile file);


    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        VirtualFile file = event.getFile();

        if (event.getPropertyName().equals("name") && inProject(file)) {
            String oldName = event.getOldValue().toString();
            String newName = event.getNewValue().toString();

            if (isRenameFromServerlessToNonServerless(oldName, newName)) {
                onDelete(file);
            } else if (isRenameFromNonServerlessToServerless(oldName, newName)) {
                onCreateOrUpdate(serviceFactory.create(file));
            }
        }
    }

    @Override
    public void fileCreated(@NotNull final VirtualFileEvent event) {
        if (processEvent(event)) {
            onCreateOrUpdate(serviceFactory.create(event.getFile()));
        }
    }

    @Override
    public void contentsChanged(@NotNull final VirtualFileEvent event) {
        fileCreated(event);
    }

    @Override
    public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
        VirtualFile file = event.getFile();

        if (file.isDirectory()) {
            if (beforeDeletionFiles == null) {
                beforeDeletionFiles = new LinkedList<>();
            }

            getProjectFileIndex().iterateContentUnderDirectory(file, fileOrDir -> {
                if (FileUtil.isServerlessFile(fileOrDir)) {
                    beforeDeletionFiles.add(fileOrDir);
                }

                return true;
            });
        }
    }

    @Override
    public void fileDeleted(@NotNull final VirtualFileEvent event) {
        VirtualFile file = event.getFile();

        if (file.isDirectory() && beforeDeletionFiles != null) {
            VirtualFile currentFile;
            while ((currentFile = beforeDeletionFiles.poll()) != null) {
                onDelete(currentFile);
            }
        } else if (FileUtil.isServerlessFile(file)) {
            onDelete(event.getFile());
        }
    }


    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        VirtualFile file = event.getFile();

        if (areDifferentProjects(event.getOldParent(), event.getNewParent()) && event.getRequestor() instanceof PsiManager) {
            if (project.equals(((PsiManager) event.getRequestor()).getProject())) {
                onDelete(file);
            } else {
                onCreateOrUpdate(serviceFactory.create(file));
            }
        } else {
            onMoveInAnotherDirectory(file);
        }
    }

    private boolean areDifferentProjects(VirtualFile project1, VirtualFile project2) {
        boolean project1InProject = inProject(project1);
        boolean project2InProject = inProject(project2);

        return (project1InProject && !project2InProject) || (!project1InProject && project2InProject);
    }

    private boolean processEvent(@NotNull final VirtualFileEvent event) {
        VirtualFile file = event.getFile();
        return FileUtil.isServerlessFile(file) && inProject(file);
    }

    private boolean isRenameFromServerlessToNonServerless(String oldName, String newName) {
        return FileUtil.isServerlessFile(oldName) && !FileUtil.isServerlessFile(newName);
    }

    private boolean isRenameFromNonServerlessToServerless(String oldName, String newName) {
        return !FileUtil.isServerlessFile(oldName) && FileUtil.isServerlessFile(newName);
    }

    private ProjectFileIndex getProjectFileIndex() {
        return ProjectFileIndex.getInstance(project);
    }

    private boolean inProject(VirtualFile file) {
        if (project.isDisposed()) {
            return false;
        }
        return getProjectFileIndex().isInContent(file);
    }
}
