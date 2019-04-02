package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.GoogleAnalyticsConfig;
import com.brsanthu.googleanalytics.request.EventHit;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.application.ApplicationInfo;
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
                .eventCategory("General")
                .customMetric(GoogleAnalyticsCustomMetrics.PRODUCT_NAME.getIndex(), ApplicationInfo.getInstance().getVersionName())
                .customMetric(GoogleAnalyticsCustomMetrics.PRODUCT_VERSION.getIndex(), ApplicationInfo.getInstance().getFullVersion())
                .customMetric(GoogleAnalyticsCustomMetrics.PRODUCT_BUILD_NUMBER.getIndex(), ApplicationInfo.getInstance().getBuild().asString())
                .customMetric(GoogleAnalyticsCustomMetrics.OS.getIndex(), System.getProperty("os.name"));
    }
}
