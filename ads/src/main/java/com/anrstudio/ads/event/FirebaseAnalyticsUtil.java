package com.anrstudio.ads.event;

import android.content.Context;
import android.os.Bundle;

import com.anrstudio.ads.config.ANRAdSdkConfig;
import com.anrstudio.ads.funtion.AdType;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

public class FirebaseAnalyticsUtil {
    private static final String TAG = "FirebaseAnalyticsUtil";

    public static void logEventWithAds(Context context, Bundle params) {
        FirebaseAnalytics.getInstance(context).logEvent("paid_ad_impression", params);
    }

    static void logPaidAdImpressionValue(Context context, Bundle bundle) {
        FirebaseAnalytics.getInstance(context).logEvent("paid_ad_impression_value", bundle);
    }

    public static void logClickAdsEvent(Context context, Bundle bundle) {

        FirebaseAnalytics.getInstance(context).logEvent("event_user_click_ads", bundle);
    }

    public static void logCurrentTotalRevenueAd(Context context, String eventName, Bundle bundle) {
        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle);
    }

    public static void logTotalRevenue001Ad(Context context, Bundle bundle) {
        FirebaseAnalytics.getInstance(context).logEvent("paid_ad_impression_value_001", bundle);
    }

    public static void logPurchase(Context context, float revenue, String currency, String idPurchase, int typeIap, String orderId, int quantity) {
        if (context == null) {
            return;
        }
        try {
            Bundle params = new Bundle();
            params.putDouble(FirebaseAnalytics.Param.VALUE, (double) revenue);
            params.putString(FirebaseAnalytics.Param.CURRENCY, currency != null && !currency.isEmpty() ? currency : "USD");
            if (orderId != null && !orderId.isEmpty()) {
                params.putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId);
            }

            Bundle item = new Bundle();
            item.putString(FirebaseAnalytics.Param.ITEM_ID, idPurchase != null ? idPurchase : "");
            item.putString(FirebaseAnalytics.Param.ITEM_NAME, idPurchase != null ? idPurchase : "");
            item.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, typeIap == 1 ? "inapp" : "subs");
            item.putLong(FirebaseAnalytics.Param.QUANTITY, quantity > 0 ? quantity : 1);
            item.putDouble(FirebaseAnalytics.Param.PRICE, (double) revenue);

            ArrayList<Bundle> items = new ArrayList<>();
            items.add(item);
            params.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items);

            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.PURCHASE, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logAdImpressionStandard(Context context, double value, String currency, String adUnitId, String network, AdType adType) {
        if (context == null) {
            return;
        }
        try {
            Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "admob");
            params.putString(FirebaseAnalytics.Param.AD_SOURCE, network != null && !network.isEmpty() ? network : "admob");
            params.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId != null ? adUnitId : "");
            if (adType != null) {
                params.putString(FirebaseAnalytics.Param.AD_FORMAT, adType.toString());
            }
            params.putDouble(FirebaseAnalytics.Param.VALUE, value);
            params.putString(FirebaseAnalytics.Param.CURRENCY, currency != null && !currency.isEmpty() ? currency : "USD");

            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logCustomEvent(Context context, String eventName, Bundle bundle) {
        if (context == null || eventName == null || eventName.isEmpty()) {
            return;
        }
        try {
            FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle != null ? bundle : new Bundle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
