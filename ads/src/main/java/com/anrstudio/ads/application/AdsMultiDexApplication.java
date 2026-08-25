package com.anrstudio.ads.application;

import androidx.multidex.MultiDexApplication;

import com.anrstudio.ads.R;
import com.anrstudio.ads.config.ANRAdSdkConfig;
import com.anrstudio.ads.util.AppUtil;
import com.anrstudio.ads.util.SharePreferenceUtils;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

public abstract class AdsMultiDexApplication extends MultiDexApplication {

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
