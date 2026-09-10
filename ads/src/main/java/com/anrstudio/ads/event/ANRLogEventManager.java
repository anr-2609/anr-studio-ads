package com.anrstudio.ads.event;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.applovin.mediation.MaxAd;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.anrstudio.ads.ads.ANRAdSdk;
import com.anrstudio.ads.funtion.AdType;
import com.anrstudio.ads.util.AppUtil;
import com.anrstudio.ads.util.SharePreferenceUtils;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdValue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class ANRLogEventManager {

    private static final String TAG = "ANRLogEventManager";

    private static boolean isFacebookEnabled() {
        return ANRAdSdk.getInstance().getAdConfig() == null || ANRAdSdk.getInstance().getAdConfig().isEnableFacebook();
    }

    private static boolean isFirebasePurchaseTrackingEnabled() {
        return ANRAdSdk.getInstance().getAdConfig() != null && ANRAdSdk.getInstance().getAdConfig().isEnableFirebasePurchaseTracking();
    }

    private static boolean isFirebaseAdImpressionTrackingEnabled() {
        return ANRAdSdk.getInstance().getAdConfig() != null && ANRAdSdk.getInstance().getAdConfig().isEnableFirebaseAdImpressionTracking();
    }

    private static boolean isFirebaseCustomEventTrackingEnabled() {
        return ANRAdSdk.getInstance().getAdConfig() != null && ANRAdSdk.getInstance().getAdConfig().isEnableFirebaseCustomEventTracking();
    }

    private static boolean isLegacyAdRevenueEventsEnabled() {
        return ANRAdSdk.getInstance().getAdConfig() == null || ANRAdSdk.getInstance().getAdConfig().isEnableLegacyAdRevenueEvents();
    }

    public static void logPaidAdImpression(Context context, AdValue adValue, String adUnitId, String mediationAdapterClassName) {
        logPaidAdImpression(context, adValue, adUnitId, mediationAdapterClassName, null);
    }

    public static void logPaidAdImpression(Context context, AdValue adValue, String adUnitId, String mediationAdapterClassName, AdType adType) {
        logEventWithAds(context, (float) adValue.getValueMicros(), adValue.getPrecisionType(), adUnitId, mediationAdapterClassName);
        ANRAdjust.pushTrackEventAdmob(adValue);
        ANRAppsFlyer.logPaidAdImpression(context,
                adValue.getValueMicros() / 1000000.0,
                adValue.getCurrencyCode(),
                adUnitId,
                mediationAdapterClassName,
                adType);
        float value = adValue.getValueMicros() * 1.0f / 1000000;
        if (isFacebookEnabled() && context != null) {
            AppEventsLogger.newLogger(context).logPurchase(BigDecimal.valueOf(value), Currency.getInstance("USD"));
        }
        if (isFirebaseAdImpressionTrackingEnabled()) {
            FirebaseAnalyticsUtil.logAdImpressionStandard(context,
                    adValue.getValueMicros() / 1000000.0,
                    adValue.getCurrencyCode(),
                    adUnitId,
                    mediationAdapterClassName,
                    adType);
        }
    }

    public static void logPaidAdImpression(Context context, MaxAd maxAd, AdType adType) {
        ANRAppsFlyer.logPaidAdImpression(maxAd, adType);
        if (isFirebaseAdImpressionTrackingEnabled() && maxAd != null) {
            FirebaseAnalyticsUtil.logAdImpressionStandard(context,
                    maxAd.getRevenue(),
                    "USD",
                    maxAd.getAdUnitId(),
                    maxAd.getNetworkName(),
                    adType);
        }
    }

    public static void logPaidAdjustWithToken(AdValue adValue, String adUnitId, String token) {
        AdjustEvent adjustEvent = new AdjustEvent(token);
        float value = adValue.getValueMicros() * 1.0f / 1000000;
        adjustEvent.setRevenue(value, "USD");
        adjustEvent.setOrderId(adUnitId);
        Adjust.trackEvent(adjustEvent);
    }

    private static void logEventWithAds(Context context, float revenue, int precision, String adUnitId, String network) {
        if (!isLegacyAdRevenueEventsEnabled()) {
            return;
        }
        Bundle params = new Bundle(); // Log ad value in micros.
        params.putDouble("valuemicros", revenue);
        params.putString("currency", "USD");
        params.putInt("precision", precision);
        params.putString("adunitid", adUnitId);
        params.putString("network", network);

        logPaidAdImpressionValue(context, revenue / 1000000.0, precision, adUnitId, network);
        FirebaseAnalyticsUtil.logEventWithAds(context, params);
        FacebookEventUtils.logEventWithAds(context, params);
        SharePreferenceUtils.updateCurrentTotalRevenueAd(context, (float) revenue);
        logCurrentTotalRevenueAd(context, "event_current_total_revenue_ad");

        AppUtil.currentTotalRevenue001Ad += revenue;
        SharePreferenceUtils.updateCurrentTotalRevenue001Ad(context, AppUtil.currentTotalRevenue001Ad);
        logTotalRevenue001Ad(context);

        logTotalRevenueAdIn3DaysIfNeed(context);
        logTotalRevenueAdIn7DaysIfNeed(context);
    }

    private static void logPaidAdImpressionValue(Context context, double value, int precision, String adunitid, String network) {
        Bundle params = new Bundle();
        params.putDouble("value", value);
        params.putString("currency", "USD");
        params.putInt("precision", precision);
        params.putString("adunitid", adunitid);
        params.putString("network", network);


        ANRAdjust.logPaidAdImpressionValue(value);
        FirebaseAnalyticsUtil.logPaidAdImpressionValue(context, params);

        FacebookEventUtils.logPaidAdImpressionValue(context, params);
    }

    public static void logClickAdsEvent(Context context, String adUnitId) {
        Log.d(TAG, String.format(
                "User click ad for ad unit %s.",
                adUnitId));
        Bundle bundle = new Bundle();
        bundle.putString("ad_unit_id", adUnitId);

        FirebaseAnalyticsUtil.logClickAdsEvent(context, bundle);
        FacebookEventUtils.logClickAdsEvent(context, bundle);
    }

    public static void logCurrentTotalRevenueAd(Context context, String eventName) {
        float currentTotalRevenue = SharePreferenceUtils.getCurrentTotalRevenueAd(context);
        Bundle bundle = new Bundle();
        bundle.putFloat("value", currentTotalRevenue);

        FirebaseAnalyticsUtil.logCurrentTotalRevenueAd(context, eventName, bundle);
        FacebookEventUtils.logCurrentTotalRevenueAd(context, eventName, bundle);
    }

    public static void logTotalRevenue001Ad(Context context) {
        float revenue = AppUtil.currentTotalRevenue001Ad;
        if (revenue / 1000000 >= 0.01) {
            AppUtil.currentTotalRevenue001Ad = 0;
            SharePreferenceUtils.updateCurrentTotalRevenue001Ad(context, 0);
            Bundle bundle = new Bundle();
            bundle.putFloat("value", revenue / 1000000);
            FirebaseAnalyticsUtil.logTotalRevenue001Ad(context, bundle);
            FacebookEventUtils.logTotalRevenue001Ad(context, bundle);
        }
    }

    public static void logTotalRevenueAdIn3DaysIfNeed(Context context) {
        long installTime = SharePreferenceUtils.getInstallTime(context);
        if (!SharePreferenceUtils.isPushRevenue3Day(context)
                && (System.currentTimeMillis() - installTime >= 3L * 24 * 60 * 60 * 1000)) {
            Log.d(TAG, "logTotalRevenueAdAt3DaysIfNeed: ");
            logCurrentTotalRevenueAd(context, "event_total_revenue_ad_in_3_days");
            SharePreferenceUtils.setPushedRevenue3Day(context);
        }
    }

    public static void logTotalRevenueAdIn7DaysIfNeed(Context context) {
        long installTime = SharePreferenceUtils.getInstallTime(context);
        if (!SharePreferenceUtils.isPushRevenue7Day(context)
                && (System.currentTimeMillis() - installTime >= 7L * 24 * 60 * 60 * 1000)) {
            Log.d(TAG, "logTotalRevenueAdAt7DaysIfNeed: ");
            logCurrentTotalRevenueAd(context, "event_total_revenue_ad_in_7_days");
            SharePreferenceUtils.setPushedRevenue7Day(context);
        }
    }


    public static void setEventNamePurchaseAdjust(String eventNamePurchase) {
        ANRAdjust.setEventNamePurchase(eventNamePurchase);
    }

    public static void trackAdRevenue(String id) {
        ANRAdjust.trackAdRevenue(id);
    }

    public static void onTrackEvent(String eventName) {
        ANRAdjust.onTrackEvent(eventName);
        ANRAppsFlyer.onTrackEvent(ANRAdSdk.getInstance().getAdConfig().getApplication(), eventName);
        if (isFirebaseCustomEventTrackingEnabled()) {
            Context app = ANRAdSdk.getInstance().getAdConfig() != null ? ANRAdSdk.getInstance().getAdConfig().getApplication() : null;
            if (app != null) {
                FirebaseAnalyticsUtil.logCustomEvent(app, eventName, new Bundle());
            }
        }
    }

    public static void onTrackEvent(String eventName, String id) {
        ANRAdjust.onTrackEvent(eventName, id);
        Map<String, Object> eventValues = new HashMap<>();
        eventValues.put("callback_id", id);
        ANRAppsFlyer.onTrackEvent(ANRAdSdk.getInstance().getAdConfig().getApplication(), eventName, eventValues);
        if (isFirebaseCustomEventTrackingEnabled()) {
            Context app = ANRAdSdk.getInstance().getAdConfig() != null ? ANRAdSdk.getInstance().getAdConfig().getApplication() : null;
            if (app != null) {
                Bundle bundle = new Bundle();
                bundle.putString("callback_id", id);
                FirebaseAnalyticsUtil.logCustomEvent(app, eventName, bundle);
            }
        }
    }

    public static void onTrackRevenue(String eventName, float revenue, String currency) {
        ANRAdjust.onTrackRevenue(eventName, revenue, currency);
    }

    public static void onTrackRevenuePurchase(float revenue, String currency, String idPurchase, int typeIAP,
                                              String orderId, int quantity) {
        ANRAdjust.onTrackRevenuePurchase(revenue, currency);
        ANRAppsFlyer.onTrackRevenuePurchase(ANRAdSdk.getInstance().getAdConfig().getApplication(),
                revenue, currency, idPurchase, typeIAP, orderId, quantity);
        if (isFirebasePurchaseTrackingEnabled()) {
            Context app = ANRAdSdk.getInstance().getAdConfig() != null ? ANRAdSdk.getInstance().getAdConfig().getApplication() : null;
            if (app != null) {
                FirebaseAnalyticsUtil.logPurchase(app, revenue, currency, idPurchase, typeIAP, orderId, quantity);
            }
        }
    }

    public static void logAppsFlyerLogin() {
        ANRAppsFlyer.logLogin(ANRAdSdk.getInstance().getAdConfig().getApplication());
    }

    public static void logAppsFlyerAddToCart(String contentId) {
        ANRAppsFlyer.logAddToCart(ANRAdSdk.getInstance().getAdConfig().getApplication(), contentId);
    }

    public static void logAppsFlyerContentView(String contentType, String contentId) {
        ANRAppsFlyer.logContentView(ANRAdSdk.getInstance().getAdConfig().getApplication(), contentType, contentId);
    }

    public static void logAppsFlyerOpenPromotion() {
        ANRAppsFlyer.logOpenPromotion(ANRAdSdk.getInstance().getAdConfig().getApplication());
    }

    public static void setAppsFlyerCustomerUserId(String customerUserId) {
        ANRAppsFlyer.setCustomerUserId(customerUserId);
    }

    public static void updateAppsFlyerServerUninstallToken(String uninstallToken) {
        ANRAppsFlyer.updateServerUninstallToken(ANRAdSdk.getInstance().getAdConfig().getApplication(), uninstallToken);
    }

    public static void pushTrackEventAdmob(AdValue adValue) {
        ANRAdjust.pushTrackEventAdmob(adValue);
    }
}
