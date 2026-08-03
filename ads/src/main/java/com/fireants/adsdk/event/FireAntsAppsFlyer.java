package com.fireants.adsdk.event;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.appsflyer.AFAdRevenueData;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.MediationNetwork;
import com.fireants.adsdk.config.FireAntsAdSdkConfig;
import com.fireants.adsdk.util.SharePreferenceUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FireAntsAppsFlyer {
    private static final String TAG = "FireAntsAppsFlyer";
    public static final String EVENT_APP_LAUNCH = "af_app_launch";
    public static final String EVENT_OPEN_PROMOTION = "af_event_4";
    private static final String EVENT_PAID_AD_IMPRESSION = "paid_ad_impression";
    private static final String EVENT_PAID_AD_IMPRESSION_VALUE = "paid_ad_impression_value";
    private static final String KEY_AF_STATUS = "af_status";
    private static final String KEY_MEDIA_SOURCE = "media_source";

    public static boolean enableAppsFlyer = false;

    private FireAntsAppsFlyer() {
    }

    public static void init(Context context, FireAntsAdSdkConfig config) {
        if (config == null || config.getAppsFlyerConfig() == null || !config.isEnableAppsFlyer()) {
            enableAppsFlyer = false;
            return;
        }

        String appsFlyerKey = config.getAppsFlyerConfig().getAppsFlyerKey();
        if (TextUtils.isEmpty(appsFlyerKey)) {
            Log.w(TAG, "AppsFlyer is enabled but key is empty");
            enableAppsFlyer = false;
            return;
        }

        enableAppsFlyer = true;
        AppsFlyerLib.getInstance().setDebugLog(config.getAppsFlyerConfig().isEnableDebug());
        AppsFlyerLib.getInstance().init(appsFlyerKey, new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                updateOrganicState(context, conversionData);
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.w(TAG, "onConversionDataFail: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                Log.d(TAG, "onAppOpenAttribution: " + attributionData);
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.w(TAG, "onAttributionFailure: " + errorMessage);
            }
        }, context);
        AppsFlyerLib.getInstance().start(context);
        logAppLaunch(context);
    }

    public static void onTrackEvent(Context context, String eventName) {
        onTrackEvent(context, eventName, null);
    }

    public static void onTrackEvent(Context context, String eventName, Map<String, Object> eventValues) {
        if (!enableAppsFlyer || context == null || TextUtils.isEmpty(eventName)) {
            return;
        }
        AppsFlyerLib.getInstance().logEvent(context, eventName, eventValues);
    }

    public static void onTrackRevenuePurchase(Context context, float revenue, String currency, String idPurchase,
                                              int typeIap, String orderId, int quantity) {
        if (!enableAppsFlyer || context == null) {
            return;
        }
        Map<String, Object> eventValues = new HashMap<>();
        eventValues.put(AFInAppEventParameterName.REVENUE, revenue);
        eventValues.put(AFInAppEventParameterName.CURRENCY, currency);
        eventValues.put(AFInAppEventParameterName.CONTENT_ID, idPurchase);
        eventValues.put(AFInAppEventParameterName.CONTENT_TYPE, typeIap == 0 ? "inapp" : "subs");
        eventValues.put(AFInAppEventParameterName.QUANTITY, quantity);
        if (!TextUtils.isEmpty(orderId)) {
            eventValues.put(AFInAppEventParameterName.ORDER_ID, orderId);
        }
        AppsFlyerLib.getInstance().logEvent(context, AFInAppEventType.PURCHASE, eventValues);
    }

    public static void logAppLaunch(Context context) {
        onTrackEvent(context, EVENT_APP_LAUNCH);
    }

    public static void logLogin(Context context) {
        onTrackEvent(context, AFInAppEventType.LOGIN);
    }

    public static void logOpenPromotion(Context context) {
        onTrackEvent(context, EVENT_OPEN_PROMOTION);
    }

    public static void logContentView(Context context, String contentType, String contentId) {
        if (!enableAppsFlyer || context == null) {
            return;
        }
        Map<String, Object> eventValues = new HashMap<>();
        eventValues.put(AFInAppEventParameterName.CONTENT_TYPE, contentType);
        eventValues.put(AFInAppEventParameterName.CONTENT_ID, contentId);
        AppsFlyerLib.getInstance().logEvent(context, AFInAppEventType.CONTENT_VIEW, eventValues);
    }

    public static void setCustomerUserId(String customerUserId) {
        if (!enableAppsFlyer || TextUtils.isEmpty(customerUserId)) {
            return;
        }
        AppsFlyerLib.getInstance().setCustomerUserId(customerUserId);
    }

    public static void logPaidAdImpression(Context context, double revenue, String currency, String adUnitId, String network) {
        if (!enableAppsFlyer || context == null) {
            return;
        }

        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("ad_unit_id", adUnitId);
        additionalParameters.put("mediation_network", network);
        additionalParameters.put("precision_source", EVENT_PAID_AD_IMPRESSION);

        Log.d(TAG,
                "AF logAdRevenue"
                        + "\nrevenue=" + revenue
                        + "\ncurrency=" + currency
                        + "\nadUnitId=" + adUnitId
                        + "\nnetwork=" + network
                        + "\nmediation=" + MediationNetwork.GOOGLE_ADMOB
                        + "\nparams=" + additionalParameters);

        AFAdRevenueData adRevenueData = new AFAdRevenueData(
                network == null ? "admob" : network,
                MediationNetwork.GOOGLE_ADMOB,
                currency,
                revenue
        );
        AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, additionalParameters);
    }

    public static void logPaidAdImpressionValue(Context context, double revenue, String currency, int precision, String adUnitId, String network) {
        if (!enableAppsFlyer || context == null) {
            return;
        }

        Map<String, Object> eventValues = new HashMap<>();
        eventValues.put(AFInAppEventParameterName.REVENUE, revenue);
        eventValues.put(AFInAppEventParameterName.CURRENCY, currency);
        eventValues.put("precision", precision);
        eventValues.put("adunitid", adUnitId);
        eventValues.put("network", network);
        AppsFlyerLib.getInstance().logEvent(context, EVENT_PAID_AD_IMPRESSION_VALUE, eventValues);
    }

    private static void updateOrganicState(Context context, Map<String, Object> conversionData) {
        if (context == null || conversionData == null) {
            return;
        }

        Object afStatus = conversionData.get(KEY_AF_STATUS);
        Object mediaSource = conversionData.get(KEY_MEDIA_SOURCE);
        boolean organic = "Organic".equalsIgnoreCase(String.valueOf(afStatus))
                || "organic".equalsIgnoreCase(String.valueOf(mediaSource));
        SharePreferenceUtils.setIsOrganic(context, organic);
        Log.d(TAG, String.format(Locale.US, "AppsFlyer organic=%s conversionData=%s", organic, conversionData));
    }
}
