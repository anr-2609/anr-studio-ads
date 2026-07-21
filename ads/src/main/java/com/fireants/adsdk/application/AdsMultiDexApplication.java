package com.fireants.adsdk.application;

import androidx.multidex.MultiDexApplication;

import com.fireants.adsdk.R;
import com.fireants.adsdk.config.FireAntsAdSdkConfig;
import com.fireants.adsdk.util.AppUtil;
import com.fireants.adsdk.util.SharePreferenceUtils;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

public abstract class AdsMultiDexApplication extends MultiDexApplication {

    protected FireAntsAdSdkConfig mFireAntsAdSdkConfig;
    protected List<String> listTestDevice;

    @Override
    public void onCreate() {
        super.onCreate();
        listTestDevice = new ArrayList<String>();
        mFireAntsAdSdkConfig = new FireAntsAdSdkConfig(this);
        if (SharePreferenceUtils.getInstallTime(this) == 0) {
            SharePreferenceUtils.setInstallTime(this);
        }
        AppUtil.currentTotalRevenue001Ad = SharePreferenceUtils.getCurrentTotalRevenue001Ad(this);
    }


}
