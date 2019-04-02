package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.GoogleAnalyticsConfig;
import com.brsanthu.googleanalytics.request.EventHit;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleAnalyticsEventFactory {
    @NonNull
    private String trackingId;
    @NonNull
    private User user;

    private GoogleAnalytics googleAnalytics;

    private GoogleAnalytics getGoogleAnalytics() {
        if (googleAnalytics == null) {
            googleAnalytics = GoogleAnalytics.builder()
                    .withConfig(new GoogleAnalyticsConfig().setBatchingEnabled(false))
                    .withTrackingId(trackingId)
                    .build();
        }

        return googleAnalytics;
    }

    public EventHit create(IdeaPluginDescriptor pluginDescriptor) {
        return getGoogleAnalytics()
                .event()
                .applicationId(pluginDescriptor.getName())
                .applicationVersion(pluginDescriptor.getVersion())
                .clientId(user.getId())
                .eventCategory("General");
    }
}
