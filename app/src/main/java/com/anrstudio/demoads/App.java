package com.anrstudio.demoads;

import com.anrstudio.ads.admob.Admob;
import com.anrstudio.ads.admob.AppOpenManager;
import com.anrstudio.ads.ads.ANRAdSdk;
import com.anrstudio.ads.application.AdsMultiDexApplication;
import com.anrstudio.ads.billing.AppPurchase;
import com.anrstudio.ads.config.AdjustConfig;
import com.anrstudio.ads.config.AppsFlyerConfig;
import com.anrstudio.ads.config.ANRAdSdkConfig;

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
        String environment = BuildConfig.DEBUG ? ANRAdSdkConfig.ENVIRONMENT_DEVELOP : ANRAdSdkConfig.ENVIRONMENT_PRODUCTION;
        mANRAdSdkConfig = new ANRAdSdkConfig(this, environment);

        AdjustConfig adjustConfig = new AdjustConfig(true,getString(R.string.adjust_token));
        mANRAdSdkConfig.setAdjustConfig(adjustConfig);
        AppsFlyerConfig appsFlyerConfig = new AppsFlyerConfig(true, getString(R.string.appsflyer_key), BuildConfig.DEBUG);
        mANRAdSdkConfig.setAppsFlyerConfig(appsFlyerConfig);
        mANRAdSdkConfig.setFacebookClientToken(getString(R.string.facebook_client_token));
        mANRAdSdkConfig.setAdjustTokenTiktok(getString(R.string.tiktok_token));

        mANRAdSdkConfig.setIdAdResume("");
        mANRAdSdkConfig.setShowAdDebugDialog(BuildConfig.DEBUG);

        ANRAdSdk.getInstance().init(this, mANRAdSdkConfig);
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
