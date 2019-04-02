package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GoogleAnalyticsCustomMetrics {
    PRODUCT_NAME(1),
    PRODUCT_VERSION(2),
    PRODUCT_BUILD_NUMBER(3),
    OS(4),
    PLUGIN_PREVIOUS_VERSION(5);

    @Getter
    private final int index;
}
