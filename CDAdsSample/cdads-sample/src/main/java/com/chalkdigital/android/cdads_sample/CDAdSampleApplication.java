package com.chalkdigital.android.cdads_sample;

import android.app.Application;
import android.util.Log;

import com.chalkdigital.BuildConfig;
import com.chalkdigital.ads.CDAds;
import com.chalkdigital.ads.CDAdsInitialisationParams;
import com.chalkdigital.common.CDAdsUtils;
import com.chalkdigital.interstitial.ads.CDAdInterstitial;

import java.util.logging.Level;


/**
 * Created by arungupta on 11/01/18.
 */

public class CDAdSampleApplication extends Application {
    public static CDAdInterstitial mCDAdInterstitial;
    @Override
    public void onCreate() {
        super.onCreate();
        CDAdsUtils.setLogLevel(Level.ALL);
        CDAdsUtils.initialize(this);
        CDAdsUtils.setGeoIpLocationEnabled(false);
        CDAdsUtils.setLogLevel(BuildConfig.DEBUG?Level.ALL:Level.OFF);
        CDAdsInitialisationParams cdAdsInitialisationParams = new CDAdsInitialisationParams(this.getApplicationContext());
        cdAdsInitialisationParams.setPartnerKey("ENTER YOUR PARTNER KEY HERE");
        CDAds cdAds = CDAds.initialiseWithParams(cdAdsInitialisationParams, this);
        cdAds.setEnableTracking(true);
        cdAds.start();
    }
}
