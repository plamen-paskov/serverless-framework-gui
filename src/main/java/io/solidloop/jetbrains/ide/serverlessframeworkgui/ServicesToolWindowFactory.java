package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.intellij.ide.plugins.PluginInstaller;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.Topic;
import com.intellij.util.ui.JBUI;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ServicesToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        try {
            Content content = ContentFactory.SERVICE.getInstance().createContent(createWindowContent(project, toolWindow), null, false);
            toolWindow.getContentManager().addContent(content);
        } catch (IOException e) {
            reportException(e);
        }
    }

    private JComponent createWindowContent(Project project, ToolWindow toolWindow) throws IOException {
        User user = createClientState(project);
        return user.isTcAccepted() ? init(project, user) : createTc(getAcceptTcActionListener(project, user, toolWindow));
    }

    private ActionListener getAcceptTcActionListener(Project project, User user, ToolWindow toolWindow) {
        return actionEvent -> {
            try {
                user.setId(UUID.randomUUID().toString());
                user.setTcAccepted(true);

                ContentManager contentManager = toolWindow.getContentManager();
                contentManager.removeContent(contentManager.getSelectedContent(), true);
                Content content = ContentFactory.SERVICE.getInstance().createContent(init(project, user), null, false);
                contentManager.addContent(content);
            } catch (IOException e) {
                reportException(e);
            }
        };
    }

    private JPanel createTc(ActionListener acceptActionListener) throws IOException {
        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        jEditorPane.setContentType("text/html");
        jEditorPane.setText(readTcFile());
        jEditorPane.setEditable(false);
        jEditorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(e.getURL().toURI());
                    } catch (Exception e1) {
                        reportException(e1);
                    }
                }
            }
        });

        JScrollPane tc = new JBScrollPane(jEditorPane);
        SwingUtilities.invokeLater(() -> tc.getVerticalScrollBar().setValue(0));
        tc.setAlignmentX(Component.CENTER_ALIGNMENT);
        tc.setBorder(JBUI.Borders.empty(10));

        JButton accept = new JButton("I AGREE");
        accept.addActionListener(acceptActionListener);
        accept.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        panel.setBorder(JBUI.Borders.empty(10));

        panel.add(tc);
        panel.add(accept);

        return panel;
    }

    private JBScrollPane init(Project project, User user) throws IOException {
        Config config = getConfig();
        copyExecScriptToTmpIfNeeded(config);

        Topic<FunctionCommandOutputHandler> functionCommandResponseTopic = Topic.create("Function command response", FunctionCommandOutputHandler.class);
        ServiceFactory serviceFactory = new ServiceFactory(new ObjectMapper(new YAMLFactory()));
        ServiceRepository serviceRepository = new ServiceRepository(project, serviceFactory);
        ServiceNodeFactory serviceNodeFactory = new ServiceNodeFactory();
        ServicesTreeComparator servicesTreeComparator = new ServicesTreeComparator();
        ServicesTreeRootNodeFactory servicesTreeRootNodeFactory = new ServicesTreeRootNodeFactory(serviceNodeFactory, servicesTreeComparator);
        DefaultMutableTreeNode rootNode = servicesTreeRootNodeFactory.create(serviceRepository.getAll());
        Tree servicesTree = new ServicesTreeFactory(new TerminalCommandExecutor(project), new ExecScriptCommandLineFactory(config.getExecScriptFilesystemPath()), project, functionCommandResponseTopic).create(rootNode);

        project.getMessageBus().connect().subscribe(functionCommandResponseTopic, new FunctionCommandOutputHandlerJsonStructureView(project, new ObjectMapper(), Configuration.getInstance()));
        VirtualFileManager.getInstance().addVirtualFileListener(new ServiceVirtualFileListener(servicesTree, serviceRepository, serviceFactory, serviceNodeFactory, servicesTreeComparator));
        PluginInstaller.addStateListener(new GoogleAnalyticsPluginStats(new GoogleAnalyticsEventFactory(config.getGoogleAnalyticsTrackingId(), user)));

        return new JBScrollPane(servicesTree);
    }

    private User createClientState(Project project) {
        return ServiceManager.getService(project, User.class);
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

    private String readTcFile() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/tc.html");

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        return textBuilder.toString();
    }
}
