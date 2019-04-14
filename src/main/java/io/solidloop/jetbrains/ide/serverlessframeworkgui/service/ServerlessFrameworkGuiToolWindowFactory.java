package io.solidloop.jetbrains.ide.serverlessframeworkgui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Ordering;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.FunctionInvocationResponseFileEditorManagerListener;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.command.*;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.config.Config;
import io.solidloop.jetbrains.ide.serverlessframeworkgui.config.PluginSettings;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ServerlessFrameworkGuiToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        try {
            Content content = contentFactory.createContent(createWindowContent(project), null, false);
            toolWindow.getContentManager().addContent(content);
        } catch (IOException e) {
            reportException(e);
        }
    }

    private JBScrollPane createWindowContent(Project project) throws IOException {
        Config config = getConfig();
        copyExecScriptToTmpIfNeeded(config);

        Topic<FunctionCommandOutputHandler> functionCommandResponseTopic = Topic.create("Function command response", FunctionCommandOutputHandler.class);
        DefaultCommandFactory commandFactory = new DefaultCommandFactory(project, new ExecScriptCommandLineFactory(config.getExecScriptFilesystemPath()));
        ServiceFactory serviceFactory = new ServiceFactory(new ObjectMapper(new YAMLFactory()));
        ServiceRepository serviceRepository = new ServiceRepository(serviceFactory, project);
        ServiceTreeNodeFactory serviceTreeNodeFactory = new ServiceTreeNodeFactory();

        Tree tree = createTree(project, serviceTreeNodeFactory, commandFactory, serviceRepository);

        PluginSettings pluginSettings = PluginSettings.getInstance();
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        ToolWindow structureView = ToolWindowManager.getInstance(project).getToolWindow("Structure");
        FunctionInvocationResponseFileEditorManagerListener functionInvocationResponseFileEditorManagerListener = new FunctionInvocationResponseFileEditorManagerListener(structureView, pluginSettings);
        messageBusConnection.subscribe(functionCommandResponseTopic, new FunctionCommandOutputHandlerStructureView(functionInvocationResponseFileEditorManagerListener, project, pluginSettings, structureView));
        messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, functionInvocationResponseFileEditorManagerListener);
        VirtualFileManager.getInstance().addVirtualFileListener(new ServiceVirtualFileListener(tree, serviceFactory, serviceTreeNodeFactory, Ordering.allEqual(), project));

        return new JBScrollPane(tree);
    }

    private Tree createTree(Project project, ServiceTreeNodeFactory serviceTreeNodeFactory, CommandFactory commandFactory, ServiceRepository serviceRepository) {
        ServiceTreeFactory serviceTreeFactory = new ServiceTreeFactory(project, serviceTreeNodeFactory, commandFactory, new ServiceTreeContextMenuFactory(project, commandFactory));
        return serviceTreeFactory.create(serviceRepository.getAll());
    }

    private void copyExecScriptToTmpIfNeeded(Config config) throws IOException {
        File file = new File(config.getExecScriptFilesystemPath());
        if (!file.exists()) {
            URL execScript = getClass().getResource(config.getExecScriptJarPath());
            if (execScript == null) {
                throw new IOException("The exec script was not found in jar");
            }

            FileUtils.copyURLToFile(execScript, file);
            if (!file.setExecutable(true)) {
                reportException(new Exception("Cannot set executable permissions on " + file.getAbsolutePath()));
            }
        }
    }

    private Config getConfig() throws IOException {
        JavaPropsMapper propertiesMapper = new JavaPropsMapper();
        return propertiesMapper.readValue(getClass().getResource("/config.properties"), Config.class);
    }

    private void reportException(Exception e) {
        PluginManager.getLogger().error(e);
    }
}
