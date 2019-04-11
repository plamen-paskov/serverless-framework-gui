package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.List;

public class ServerlessFileUtil {
    private static final List<String> configFileNames = new ArrayList<>();

    static {
        configFileNames.add("serverless.yaml");
        configFileNames.add("serverless.yml");
    }

    public static boolean isServerlessFile(VirtualFile file) {
        return !file.isDirectory() && isServerlessFile(file.getName());
    }

    public static boolean isServerlessFile(String fileName) {
        return configFileNames.contains(fileName);
    }
}
