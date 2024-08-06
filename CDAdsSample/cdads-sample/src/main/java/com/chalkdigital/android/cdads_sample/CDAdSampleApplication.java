package com.chalkdigital.android.cdads_sample;

import android.app.Application;
import android.util.Log;

import com.chalkdigital.ads.BuildConfig;
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
//        CDAdsUtils.setGDPR(true);
//        CDAdsUtils.setConsent(true);
        CDAdsUtils.setLogLevel(Level.ALL);
//        CDAdsUtils.UseBeacon = false;
        CDAdsUtils.initialize(this);
        CDAdsUtils.setGeoIpLocationEnabled(false);
        CDAdsInitialisationParams cdAdsInitialisationParams = new CDAdsInitialisationParams(this.getApplicationContext());
        cdAdsInitialisationParams.setPartnerKey("7d95de69cfc7cc03c3a05b4fde9662b8");
        cdAdsInitialisationParams.setLogLevel(BuildConfig.DEBUG? Log.VERBOSE:Log.ASSERT);
        CDAds.initialiseWithParams(cdAdsInitialisationParams, this).start();
    }
}
