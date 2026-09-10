package com.anrstudio.ads.event;

import android.content.Context;
import android.os.Bundle;

import com.anrstudio.ads.ads.ANRAdSdk;
import com.anrstudio.ads.config.ANRAdSdkConfig;
import com.facebook.appevents.AppEventsLogger;

public class FacebookEventUtils {

    private static boolean isFacebookEnabled() {
        return ANRAdSdk.getInstance().getAdConfig() == null || ANRAdSdk.getInstance().getAdConfig().isEnableFacebook();
    }

    public static void logEventWithAds(Context context, Bundle params) {
        if (!isFacebookEnabled() || context == null) return;
        AppEventsLogger.newLogger(context).logEvent("paid_ad_impression", params);
    }

    static void logPaidAdImpressionValue(Context context, Bundle bundle) {
        if (!isFacebookEnabled() || context == null) return;
        AppEventsLogger.newLogger(context).logEvent("paid_ad_impression_value", bundle);
    }

    public static void logClickAdsEvent(Context context, Bundle bundle) {
        if (!isFacebookEnabled() || context == null) return;
        AppEventsLogger.newLogger(context).logEvent("event_user_click_ads", bundle);
    }

    public static void logCurrentTotalRevenueAd(Context context, String eventName, Bundle bundle) {
        if (!isFacebookEnabled() || context == null) return;
        AppEventsLogger.newLogger(context).logEvent(eventName, bundle);
    }

    public static void logTotalRevenue001Ad(Context context, Bundle bundle) {
        if (!isFacebookEnabled() || context == null) return;
        AppEventsLogger.newLogger(context).logEvent("paid_ad_impression_value_001", bundle);
    }
}
