package com.anrstudio.ads.config;

public class AppsFlyerConfig {
    private boolean enableAppsFlyer = false;
    private String appsFlyerKey = "";
    private boolean enableDebug = false;
    private String eventAdImpression = "";

    public AppsFlyerConfig(boolean enableAppsFlyer) {
        this.enableAppsFlyer = enableAppsFlyer;
    }

    public AppsFlyerConfig(boolean enableAppsFlyer, String appsFlyerKey) {
        this.enableAppsFlyer = enableAppsFlyer;
        this.appsFlyerKey = appsFlyerKey;
    }

    public AppsFlyerConfig(boolean enableAppsFlyer, String appsFlyerKey, boolean enableDebug) {
        this.enableAppsFlyer = enableAppsFlyer;
        this.appsFlyerKey = appsFlyerKey;
        this.enableDebug = enableDebug;
    }

    public boolean isEnableAppsFlyer() {
        return enableAppsFlyer;
    }

    public void setEnableAppsFlyer(boolean enableAppsFlyer) {
        this.enableAppsFlyer = enableAppsFlyer;
    }

    public String getAppsFlyerKey() {
        return appsFlyerKey;
    }

    public void setAppsFlyerKey(String appsFlyerKey) {
        this.appsFlyerKey = appsFlyerKey;
    }

    public boolean isEnableDebug() {
        return enableDebug;
    }

    public void setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
    }

    public String getEventAdImpression() {
        return eventAdImpression;
    }

    public void setEventAdImpression(String eventAdImpression) {
        this.eventAdImpression = eventAdImpression;
    }
}
