package com.anrstudio.demoads;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anrstudio.ads.ads.ANRAdSdk;
import com.anrstudio.ads.ads.wrapper.ApInterstitialAd;
import com.anrstudio.ads.ads.wrapper.ApInterstitialPriority2Ad;
import com.anrstudio.ads.ads.wrapper.ApNativeAd;
import com.anrstudio.ads.billing.AppPurchase;
import com.anrstudio.ads.funtion.AdCallback;
import com.anrstudio.ads.funtion.AdType;
import com.anrstudio.ads.funtion.PurchaseListener;
import com.anrstudio.ads.funtion.RewardCallback;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;

public class MainActivity extends AppCompatActivity {
    private ApInterstitialPriority2Ad mInterstitialPriority2Ad;
    private Button btnLoad, btnShow, btnIap, btnLoadReward, btnShowReward;
    private FrameLayout frAds, frBanner;
    private ShimmerFrameLayout shimmerAds;
    private ApNativeAd mApNativeAd;
    private RewardedAd rewardedAds;
    private boolean isEarn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnLoad = findViewById(R.id.btnLoad);
        btnShow = findViewById(R.id.btnShow);
        btnIap = findViewById(R.id.btnIap);
        btnLoadReward = findViewById(R.id.btnLoadReward);
        btnShowReward = findViewById(R.id.btnShowReward);
        frAds = findViewById(R.id.fr_ads);
        frBanner = findViewById(R.id.frBanner);
        shimmerAds = findViewById(R.id.shimmer_native);


        // Interstitial Priority2 Ads
        mInterstitialPriority2Ad = new ApInterstitialPriority2Ad(BuildConfig.ad_interstitial_splash, BuildConfig.ad_interstitial_splash);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ANRAdSdk.getInstance().loadPriority2InterstitialAds(MainActivity.this, mInterstitialPriority2Ad, new AdCallback() {
                    @Override
                    public void onApInterstitialLoad(@Nullable ApInterstitialAd apInterstitialAd) {
                        super.onApInterstitialLoad(apInterstitialAd);
                        Toast.makeText(MainActivity.this, "Ads Ready", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btnShow.setOnClickListener(v -> ANRAdSdk.getInstance().forceShowInterstitialPriority2(MainActivity.this, mInterstitialPriority2Ad, new AdCallback() {
        }, true));

        // Banner Ads
        /*ANRAdSdk.getInstance().loadBanner(this, BuildConfig.ad_banner);*/
        /*ANRAdSdk.getInstance().loadCollapsibleBanner(this, BuildConfig.ad_banner, AppConstant.CollapsibleGravity.BOTTOM, new AdCallback());*/

        // Native Ads: Load And Show
        /*ANRAdSdk.getInstance().loadNativeAd(this, BuildConfig.ad_native, R.layout.native_large, frAds, shimmerAds, new AdCallback() {
            @Override
            public void onAdFailedToLoad(@Nullable LoadAdError i) {
                super.onAdFailedToLoad(i);
                frAds.removeAllViews();
            }

            @Override
            public void onAdFailedToShow(@Nullable AdError adError) {
                super.onAdFailedToShow(adError);
                frAds.removeAllViews();
            }
        });*/


        ANRAdSdk.getInstance().loadNativeAd(this, BuildConfig.ad_native, R.layout.native_large, frAds, shimmerAds, "#FF0000", 40, 20, new AdCallback() {
        });

        // Native Ads: Load
        /*ANRAdSdk.getInstance().loadNativeAdResultCallback(this, BuildConfig.ad_native, R.layout.native_large, new AdCallback() {
            @Override
            public void onNativeAdLoaded(@NonNull ApNativeAd nativeAd) {
                super.onNativeAdLoaded(nativeAd);

                mApNativeAd = nativeAd;
            }

            @Override
            public void onAdFailedToLoad(@Nullable LoadAdError i) {
                super.onAdFailedToLoad(i);

                mApNativeAd = null;
            }

            @Override
            public void onAdFailedToShow(@Nullable AdError adError) {
                super.onAdFailedToShow(adError);

                mApNativeAd = null;
            }
        });*/

        // Native Ads: Show
        /*if (mApNativeAd != null) {
            ANRAdSdk.getInstance().populateNativeAdView(this, mApNativeAd, frAds, shimmerAds);
        }*/

        // In-App Purchase
        AppPurchase.getInstance().setPurchaseListener(new PurchaseListener() {
            @Override
            public void onProductPurchased(String productId, String transactionDetails) {
                Toast.makeText(MainActivity.this, "onProductPurchased", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void displayErrorMessage(String errorMsg) {
                Toast.makeText(MainActivity.this, "displayErrorMessage", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserCancelBilling() {
                Toast.makeText(MainActivity.this, "onUserCancelBilling", Toast.LENGTH_SHORT).show();
            }
        });

        btnIap.setOnClickListener(v -> AppPurchase.getInstance().purchase(MainActivity.this, "android.test.purchased"));

        // Reward Ads
        btnLoadReward.setOnClickListener(v -> {
            ANRAdSdk.getInstance().initRewardAds(this, BuildConfig.ad_reward, new AdCallback() {
                @Override
                public void onRewardAdLoaded(RewardedAd rewardedAd) {
                    super.onRewardAdLoaded(rewardedAd);
                    rewardedAds = rewardedAd;
                    Toast.makeText(MainActivity.this, "Ads Ready", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnShowReward.setOnClickListener(v -> {
            isEarn = false;
            ANRAdSdk.getInstance().showRewardAds(MainActivity.this, rewardedAds, new RewardCallback() {
                @Override
                public void onUserEarnedReward(RewardItem var1) {
                    isEarn = true;
                }

                @Override
                public void onRewardedAdClosed() {
                    if (isEarn) {
                        // action intent
                    }
                }

                @Override
                public void onRewardedAdFailedToShow(int codeError) {

                }

                @Override
                public void onAdClicked() {

                }

                @Override
                public void onAdClicked(String adUnitId, String mediationAdapterClassName, AdType adType) {

                }

                @Override
                public void onAdImpression() {

                }

                @Override
                public void onAdLogRev(AdValue adValue, String adUnitId, String mediationAdapterClassName, AdType adType) {

                }
            });
        });


        /*MobileAds.openAdInspector(this, new OnAdInspectorClosedListener() {
            @Override
            public void onAdInspectorClosed(@Nullable AdInspectorError adInspectorError) {
                Log.d("TAG", "onAdInspectorClosed: " + adInspectorError);
            }
        });*/
    }
}
