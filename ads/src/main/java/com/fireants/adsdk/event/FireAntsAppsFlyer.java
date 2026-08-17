package com.fireants.adsdk.event;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.applovin.mediation.MaxAd;
import com.appsflyer.AFAdRevenueData;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.MediationNetwork;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.fireants.adsdk.config.FireAntsAdSdkConfig;
import com.fireants.adsdk.funtion.AdType;
import com.fireants.adsdk.util.SharePreferenceUtils;

import java.util.HashMap;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

public class FireAntsAppsFlyer {
    private static final String TAG = "FireAntsAppsFlyer";
    public static final String EVENT_ADD_TO_CART = AFInAppEventType.ADD_TO_CART;
    public static final String EVENT_OPEN_PROMOTION = "af_event_4";
    private static final String EVENT_PAID_AD_IMPRESSION_VALUE = "paid_ad_impression_value";
    private static final String AF_STATUS = "af_status";
    private static final String AF_STATUS_ORGANIC = "Organic";
    private static final String AF_STATUS_NON_ORGANIC = "Non-organic";

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
        AppsFlyerLib.getInstance().init(appsFlyerKey, createConversionListener(context), context);
        AppsFlyerLib.getInstance().start(context);
    }

    private static AppsFlyerConversionListener createConversionListener(Context context) {
        return new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                if (context == null) {
                    return;
                }

                if (conversionData == null) {
                    Log.w(TAG, "AppsFlyer conversion data is null, keep previous attribution");
                    return;
                }

                if (SharePreferenceUtils.isAttributionSaved(context)) {
                    Log.d(TAG, "AppsFlyer attribution already saved, ignore cached callback");
                    return;
                }

                String status = String.valueOf(conversionData.get(AF_STATUS));

                if (AF_STATUS_ORGANIC.equalsIgnoreCase(status)) {
                    SharePreferenceUtils.saveAttribution(context, true);
                    Log.d(TAG, "AppsFlyer attribution saved: status=" + status + ", organic=true");
                } else if (AF_STATUS_NON_ORGANIC.equalsIgnoreCase(status)) {
                    SharePreferenceUtils.saveAttribution(context, false);
                    Log.d(TAG, "AppsFlyer attribution saved: status=" + status + ", organic=false");
                } else {
                    Log.w(TAG, "AppsFlyer attribution status missing or unknown: " + status + ", keep previous attribution");
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.w(TAG, "AppsFlyer conversion data failed: " + errorMessage + ", keep previous attribution");
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                Log.d(TAG, "AppsFlyer app open attribution: " + attributionData);
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.w(TAG, "AppsFlyer attribution failed: " + errorMessage);
            }
        };
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
        eventValues.put(AFInAppEventParameterName.CONTENT_TYPE, typeIap == 1 ? "inapp" : "subs");
        AppsFlyerLib.getInstance().logEvent(context, AFInAppEventType.PURCHASE, eventValues, createRequestListener(AFInAppEventType.PURCHASE, idPurchase));
    }

    public static void logLogin(Context context) {
        onTrackEvent(context, AFInAppEventType.LOGIN);
    }

    public static void logAddToCart(Context context, String contentId) {
        if (!enableAppsFlyer || context == null || TextUtils.isEmpty(contentId)) {
            return;
        }

        Map<String, Object> eventValues = new HashMap<>();
        eventValues.put(AFInAppEventParameterName.CONTENT_ID, contentId);
        AppsFlyerLib.getInstance().logEvent(context, EVENT_ADD_TO_CART, eventValues, createRequestListener(EVENT_ADD_TO_CART, contentId));
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

    public static void updateServerUninstallToken(Context context, String uninstallToken) {
        if (!enableAppsFlyer || context == null || TextUtils.isEmpty(uninstallToken)) {
            return;
        }

        AppsFlyerLib.getInstance().updateServerUninstallToken(context, uninstallToken);
    }

    public static void logPaidAdImpression(Context context, double revenue, String currency, String adUnitId, String network) {
        logPaidAdImpression(context, revenue, currency, adUnitId, network, null);
    }

    public static void logPaidAdImpression(Context context, double revenue, String currency, String adUnitId, String network, AdType adType) {
        if (!enableAppsFlyer || context == null) {
            return;
        }

        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("ad_unit", adUnitId);
        additionalParameters.put("ad_type", adType == null ? null : adType.toString());

        AFAdRevenueData adRevenueData = new AFAdRevenueData(
                network == null ? "admob" : network,
                MediationNetwork.GOOGLE_ADMOB,
                currency,
                revenue
        );

        AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, additionalParameters);
    }

    public static void logPaidAdImpression(MaxAd maxAd, AdType adType) {
        if (!enableAppsFlyer || maxAd == null) {
            return;
        }

        double revenue = maxAd.getRevenue();
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("ad_unit", maxAd.getAdUnitId());
        additionalParameters.put("ad_type", adType == null ? null : adType.toString());

        AFAdRevenueData adRevenueData = new AFAdRevenueData(
                "applovinmax",
                MediationNetwork.APPLOVIN_MAX,
                Currency.getInstance(Locale.US).toString(),
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

    private static AppsFlyerRequestListener createRequestListener(String eventName, String referenceValue) {
        return new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, eventName + " success: " + referenceValue);
            }

            @Override
            public void onError(int code, String description) {
                Log.w(TAG, eventName + " error=" + code + " message=" + description + " reference=" + referenceValue);
            }
        };
    }
}