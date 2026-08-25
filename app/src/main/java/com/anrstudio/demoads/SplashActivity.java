package com.anrstudio.demoads;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anrstudio.ads.ads.ANRAdSdk;
import com.anrstudio.ads.funtion.AdCallback;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d("TAG", "onCreate: " + ANRAdSdk.getInstance().getOrganic());

        ANRAdSdk.getInstance().loadInterSplashPriority2SameTime(this, BuildConfig.ad_interstitial_splash, BuildConfig.ad_interstitial_splash, 25000, 5000, new AdCallback() {
            @Override
            public void onAdSplashHigh1Ready() {
                super.onAdSplashHigh1Ready();
                Log.d("XXXXXX", "onAdSplashHigh1Ready");
                ANRAdSdk.getInstance().onShowSplashPriority2(SplashActivity.this, new AdCallback() {
                    @Override
                    public void onNextAction() {
                        super.onNextAction();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onAdSplashNormalReady() {
                super.onAdSplashNormalReady();
                Log.d("XXXXXX", "onAdSplashNormalReady");
                ANRAdSdk.getInstance().onShowSplashPriority2(SplashActivity.this, new AdCallback() {
                    @Override
                    public void onNextAction() {
                        super.onNextAction();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onNextAction() {
                super.onNextAction();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
