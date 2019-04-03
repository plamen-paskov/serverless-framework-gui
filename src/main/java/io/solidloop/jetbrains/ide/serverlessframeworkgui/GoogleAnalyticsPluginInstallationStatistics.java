package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.brsanthu.googleanalytics.request.EventHit;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GoogleAnalyticsPluginInstallationStatistics extends AbstractPluginStateListener<EventHit> {
    private GoogleAnalyticsEventFactory googleAnalyticsEventFactory;

    @Override
    protected EventHit getInstallEvent(final IdeaPluginDescriptor pluginDescriptor) {
        return createEvent(pluginDescriptor)
                .eventAction("install")
                .eventLabel("Install plugin");
    }

    @Override
    protected EventHit getUpdateEvent(final IdeaPluginDescriptor pluginDescriptor, final IdeaPluginDescriptor installedPluginDescriptor) {
        return createEvent(pluginDescriptor)
                .eventAction("upgrade")
                .eventLabel("Upgrade plugin")
                .customMetric(GoogleAnalyticsCustomMetrics.PLUGIN_PREVIOUS_VERSION.getIndex(), installedPluginDescriptor.getVersion());
    }

    @Override
    protected EventHit getUninstallEvent(final IdeaPluginDescriptor pluginDescriptor) {
        return createEvent(pluginDescriptor)
                .eventAction("uninstall")
                .eventLabel("Uninstall plugin");
    }

    @Override
    protected void send(final EventHit event) {
        event.sendAsync();
    }

    private EventHit createEvent(final IdeaPluginDescriptor pluginDescriptor) {
        return googleAnalyticsEventFactory.create(pluginDescriptor);
    }
}
