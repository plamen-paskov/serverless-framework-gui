package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.vfs.VirtualFile;
import lombok.Data;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

@Data
public class Service implements Comparable<Service> {
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
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Service))
            return false;
        if (obj == this)
            return true;
        return Objects.equals(file.getCanonicalPath(), ((Service) obj).getFile().getCanonicalPath());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(file.getCanonicalPath()).
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
