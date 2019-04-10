package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.*;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
public class Service implements Comparable<Service> {
    @NonNull
    private Project project;

    private String fullName;

    @Getter
    @Setter
    private VirtualFile file;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String stage;

    @Getter
    @Setter
    private String region;

    @Getter
    @Setter
    private Set<String> functions;

    public void updateFullName() {
        fullName = " [ " + VfsUtilCore.getRelativePath(file, project.getBaseDir()) + " ]";
        if (name != null) {
            fullName = name + fullName;
        }
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (!(otherObject instanceof Service) && !(otherObject instanceof VirtualFile)) {
            return false;
        } else if (otherObject == this) {
            return true;
        }

        VirtualFile otherObjectFile = otherObject instanceof Service ? ((Service) otherObject).getFile() : (VirtualFile) otherObject;
        return Objects.equals(file, otherObjectFile);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(file).
                toHashCode();
    }

    @Override
    public int compareTo(@NotNull final Service service) {
        if (file.getCanonicalPath() == null) {
            return 1;
        }
        if (service.getFile().getCanonicalPath() == null) {
            return -1;
        }
        return file.getCanonicalPath().compareTo(service.getFile().getCanonicalPath());
    }
}
