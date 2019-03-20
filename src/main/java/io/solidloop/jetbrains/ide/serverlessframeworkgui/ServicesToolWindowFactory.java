package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

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

        ServiceFactory serviceFactory = new ServiceFactory(new ObjectMapper(new YAMLFactory()));
        ServiceRepository serviceRepository = new ServiceRepository(project, serviceFactory);
        ServiceNodeFactory serviceNodeFactory = new ServiceNodeFactory();
        ServicesTreeComparator servicesTreeComparator = new ServicesTreeComparator();
        ServicesTreeRootNodeFactory servicesTreeRootNodeFactory = new ServicesTreeRootNodeFactory(serviceNodeFactory, servicesTreeComparator);
        DefaultMutableTreeNode rootNode = servicesTreeRootNodeFactory.create(serviceRepository.getAll());
        Tree servicesTree = new ServicesTreeFactory(new TerminalCommandExecutor(project), new ExecScriptCommandLineFactory(config.getExecScriptFilesystemPath()), project).create(rootNode);

        project.getMessageBus().connect().subscribe(CommandTopic.COMMAND_EXECUTION_RESPONSE_TOPIC, new CommandExecutionOutputHandlerJsonStructureView(project, new ObjectMapper()));

        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener() {
            private Service upcomingServiceFileDeletion;

            @Override
            public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
                if (event.getPropertyName().equals("name")) {
                    if (ServerlessFileUtil.isServerlessFile(event.getOldValue().toString()) && !ServerlessFileUtil.isServerlessFile(event.getNewValue().toString())) {
                        try {
                            findAndRemoveServiceNodeByService(serviceFactory.create(event.getFile()));
                        } catch (IOException e) {
                            reportException(e);
                        }
                    } else if (!ServerlessFileUtil.isServerlessFile(event.getOldValue().toString()) && ServerlessFileUtil.isServerlessFile(event.getNewValue().toString())) {
                        onFileChange(event.getFile());
                    }
                }
            }

            @Override
            public void contentsChanged(@NotNull final VirtualFileEvent event) {
                onFileChange(event.getFile());
            }

            @Override
            public void fileCreated(@NotNull final VirtualFileEvent event) {
                onFileChange(event.getFile());
            }

            private void findAndRemoveServiceNodeByService(Service service) {
                DefaultMutableTreeNode oldServiceNode = TreeUtil.findNodeWithObject(rootNode, service);
                if (oldServiceNode != null) {
                    oldServiceNode.removeFromParent();
                    servicesTree.updateUI();
                }
            }

            @Override
            public void fileDeleted(@NotNull final VirtualFileEvent event) {
                if (upcomingServiceFileDeletion != null) {
                    findAndRemoveServiceNodeByService(upcomingServiceFileDeletion);
                    upcomingServiceFileDeletion = null;
                }
            }

            @Override
            public void beforeFileDeletion(@NotNull final VirtualFileEvent event) {
                if (event.getFile().isDirectory()) {
                    serviceRepository.filterBy(event.getFile()).forEach(this::findAndRemoveServiceNodeByService);
                } else if (ServerlessFileUtil.isServerlessFile(event.getFile())) {
                    try {
                        upcomingServiceFileDeletion = serviceFactory.create(event.getFile());
                    } catch (IOException e) {
                        reportException(e);
                    }
                }
            }

            private void onFileChange(@NotNull final VirtualFile file) {
                if (ServerlessFileUtil.isServerlessFile(file)) {
                    Service newService;
                    try {
                        newService = serviceFactory.create(file);
                    } catch (IOException e) {
                        reportException(e);
                        return;
                    }


                    DefaultMutableTreeNode newServiceNode = serviceNodeFactory.create(newService);
                    DefaultMutableTreeNode oldServiceNode = TreeUtil.findNodeWithObject(rootNode, newService);

                    boolean isOldServiceNodeExpanded = false;
                    if (oldServiceNode != null) {
                        isOldServiceNodeExpanded = servicesTree.isExpanded(new TreePath(oldServiceNode.getPath()));
                        oldServiceNode.removeFromParent();
                    }

                    TreeUtil.insertNode(newServiceNode, rootNode, (DefaultTreeModel) servicesTree.getModel(), true, servicesTreeComparator);

                    if (isOldServiceNodeExpanded) {
                        ArrayList<TreePath> treePaths = new ArrayList<>();
                        treePaths.add(new TreePath(newServiceNode.getPath()));

                        TreeUtil.restoreExpandedPaths(servicesTree, treePaths);
                    }

                    servicesTree.updateUI();
                }
            }
        });

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
