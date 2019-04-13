package io.solidloop.jetbrains.ide.serverlessframeworkgui.service;

import com.intellij.openapi.vfs.VirtualFile;
import lombok.Data;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Objects;
import java.util.Set;

@Data
public class Service {
    private VirtualFile file;
    private String name;
    private String stage;
    private String region;
    private Set<String> functions;

    @Override
    public String toString() {
        return name;
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
}
