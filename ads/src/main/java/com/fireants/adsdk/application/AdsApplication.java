package com.fireants.adsdk.application;

import android.app.Application;

import com.fireants.adsdk.config.FireAntsAdSdkConfig;
import com.fireants.adsdk.util.AppUtil;
import com.fireants.adsdk.util.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class AdsApplication extends Application {

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
