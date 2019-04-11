package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ServicesToolWindowFactory implements ToolWindowFactory {
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
        ServiceFactory serviceFactory = new ServiceFactory(new ObjectMapper(new YAMLFactory()));
        ServiceRepository serviceRepository = new ServiceRepository(serviceFactory, project);
        ServiceNodeFactory serviceNodeFactory = new ServiceNodeFactory();
        ServicesTreeComparator servicesTreeComparator = new ServicesTreeComparator();
        ServicesTreeRootNodeFactory servicesTreeRootNodeFactory = new ServicesTreeRootNodeFactory(serviceNodeFactory, servicesTreeComparator);
        DefaultMutableTreeNode rootNode = servicesTreeRootNodeFactory.create(serviceRepository.getAll());
        Tree servicesTree = new ServicesTreeFactory(new TerminalCommandExecutor(project), new ExecScriptCommandLineFactory(config.getExecScriptFilesystemPath()), project, functionCommandResponseTopic).create(rootNode);

        Configuration configuration = Configuration.getInstance();
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        FunctionInvocationResponseFileEditorManagerListener functionInvocationResponseFileEditorManagerListener = new FunctionInvocationResponseFileEditorManagerListener(ToolWindowUtil.getStructureView(project), configuration);
        messageBusConnection.subscribe(functionCommandResponseTopic, new FunctionCommandOutputHandlerStructureView(functionInvocationResponseFileEditorManagerListener, project, configuration));
        messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, functionInvocationResponseFileEditorManagerListener);
        VirtualFileManager.getInstance().addVirtualFileListener(new ServerlessVirtualFileListener(servicesTree, serviceFactory, serviceNodeFactory, servicesTreeComparator, project));

        return new JBScrollPane(servicesTree);
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
