package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginStateListener;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPluginStateListener<E> implements PluginStateListener {
    abstract protected E getInstallEvent(IdeaPluginDescriptor pluginDescriptor);
    abstract protected E getUpdateEvent(IdeaPluginDescriptor pluginDescriptor, IdeaPluginDescriptor installedPluginDescriptor);
    abstract protected E getUninstallEvent(IdeaPluginDescriptor pluginDescriptor);
    abstract protected void send(E event);

    @Override
    public void install(@NotNull final IdeaPluginDescriptor pluginDescriptor) {
        IdeaPluginDescriptor installedPlugin = PluginManager.getPlugin(pluginDescriptor.getPluginId());
        E event = installedPlugin != null ? getUpdateEvent(pluginDescriptor, installedPlugin) : getInstallEvent(pluginDescriptor);
        send(event);
    }

    @Override
    public void uninstall(@NotNull final IdeaPluginDescriptor pluginDescriptor) {
        send(getUninstallEvent(pluginDescriptor));
    }
}
