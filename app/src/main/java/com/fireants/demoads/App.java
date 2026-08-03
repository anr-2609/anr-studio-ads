package com.fireants.demoads;

import com.fireants.adsdk.admob.Admob;
import com.fireants.adsdk.admob.AppOpenManager;
import com.fireants.adsdk.ads.FireAntsAdSdk;
import com.fireants.adsdk.application.AdsMultiDexApplication;
import com.fireants.adsdk.billing.AppPurchase;
import com.fireants.adsdk.config.AdjustConfig;
import com.fireants.adsdk.config.AppsFlyerConfig;
import com.fireants.adsdk.config.FireAntsAdSdkConfig;

import java.util.ArrayList;
import java.util.List;

public class App extends AdsMultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        initAds();
        initBilling();
    }

    private void initAds() {
        String environment = BuildConfig.DEBUG ? FireAntsAdSdkConfig.ENVIRONMENT_DEVELOP : FireAntsAdSdkConfig.ENVIRONMENT_PRODUCTION;
        mFireAntsAdSdkConfig = new FireAntsAdSdkConfig(this, environment);

        AdjustConfig adjustConfig = new AdjustConfig(true,getString(R.string.adjust_token));
        mFireAntsAdSdkConfig.setAdjustConfig(adjustConfig);
        AppsFlyerConfig appsFlyerConfig = new AppsFlyerConfig(true, getString(R.string.appsflyer_key), BuildConfig.DEBUG);
        mFireAntsAdSdkConfig.setAppsFlyerConfig(appsFlyerConfig);
        mFireAntsAdSdkConfig.setFacebookClientToken(getString(R.string.facebook_client_token));
        mFireAntsAdSdkConfig.setAdjustTokenTiktok(getString(R.string.tiktok_token));

        mFireAntsAdSdkConfig.setIdAdResume("");
        mFireAntsAdSdkConfig.setShowAdDebugDialog(BuildConfig.DEBUG);

        FireAntsAdSdk.getInstance().init(this, mFireAntsAdSdkConfig);
        Admob.getInstance().setDisableAdResumeWhenClickAds(true);
        Admob.getInstance().setOpenActivityAfterShowInterAds(true);
        AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity.class);
    }

    private void initBilling(){
        List<String> listIAP = new ArrayList<>();
        listIAP.add("android.test.purchased");
        List<String> listSub = new ArrayList<>();
        AppPurchase.getInstance().initBilling(this, listIAP, listSub);
    }
}
