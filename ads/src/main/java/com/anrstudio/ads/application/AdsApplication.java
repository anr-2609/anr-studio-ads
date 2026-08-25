package com.anrstudio.ads.application;

import android.app.Application;

import com.anrstudio.ads.config.ANRAdSdkConfig;
import com.anrstudio.ads.util.AppUtil;
import com.anrstudio.ads.util.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class AdsApplication extends Application {

    protected ANRAdSdkConfig mANRAdSdkConfig;
    protected List<String> listTestDevice;

    @Override
    public void onCreate() {
        super.onCreate();
        listTestDevice = new ArrayList<String>();
        mANRAdSdkConfig = new ANRAdSdkConfig(this);
        if (SharePreferenceUtils.getInstallTime(this) == 0) {
            SharePreferenceUtils.setInstallTime(this);
        }
        AppUtil.currentTotalRevenue001Ad = SharePreferenceUtils.getCurrentTotalRevenue001Ad(this);
    }

}
