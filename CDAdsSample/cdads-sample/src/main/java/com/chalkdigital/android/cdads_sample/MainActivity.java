package com.chalkdigital.android.cdads_sample;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import com.chalkdigital.ads.CDAdErrorCode;
import com.chalkdigital.common.CDAdRequest;
import com.chalkdigital.common.CDAdSize;
import com.chalkdigital.common.logging.CDAdLog;
import com.chalkdigital.interstitial.ads.CDAdInterstitial;
import com.chalkdigital.ads.CDAdView;
import com.chalkdigital.interstitial.ads.CDAdVideoInterstitial;
import com.chalkdigital.nativeads.CDAdNative;
import com.chalkdigital.nativeads.CDAdVideoNativeAdRenderer;
import com.chalkdigital.nativeads.BaseNativeAd;
import com.chalkdigital.nativeads.MediaViewBinder;
import com.chalkdigital.nativeads.NativeAd;
import com.chalkdigital.nativeads.NativeErrorCode;
import com.chalkdigital.nativeads.VideoConfiguration;

import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements CDAdView.CDAdViewListener, CDAdInterstitial.InterstitialAdListener, CDAdVideoInterstitial.InterstitialVideoAdListener, CDAdNative.CDAdNativeAdListener {
    private CDAdView mSmallBannerView;
    private CDAdView mLargeBannerView;
    private CDAdVideoInterstitial mCDAdVideoInterstitial;
    private ProgressBar mProgressBar;
    private SharedPreferences sharedPreferences;
    private Menu mMenu;
    private AlertDialog alert11;
    private MediaViewBinder mediaViewBinder;
    private CDAdNative mCDAdNative;
    private NativeAd mNativeAd;
    private ConstraintLayout mConstraintLayout;

    private static final String Notification_Action = "Notification_Action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String partnerId = "";
        String bundleId = getApplicationContext().getPackageName();
        String cat = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        sharedPreferences = getSharedPreferences(SettingsActivity.sharedPreferencesFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.getBoolean("isSettingsSaved", false)){
            try {
                partnerId = getResources().getText(getResources().getIdentifier("CDADS_PARTNER_ID", "string", getPackageName())).toString();
                editor.putString("partnerId", partnerId);
            } catch (Exception e) {

            }
            try {
                bundleId = getResources().getText(getResources().getIdentifier("CDADS_BUNDLE_ID", "string", getPackageName())).toString();
                editor.putString("bundleId", bundleId);
            } catch (Exception e) {

            }
            try {
                cat = getResources().getText(getResources().getIdentifier("CDADS_CAT", "string", getPackageName())).toString();
                editor.putString("cat", cat);
            } catch (Exception e) {

            }

        }else if(sharedPreferences.getString("partnerId", "").equals("")){
            try {
                partnerId = getResources().getText(getResources().getIdentifier("CDADS_PARTNER_ID", "string", getPackageName())).toString();
                editor.putString("partnerId", partnerId);
            } catch (Exception e) {

            }
        }

        else if(sharedPreferences.getString("bundleId", "").equals("")){
            try {
                bundleId = getResources().getText(getResources().getIdentifier("CDADS_BUNDLE_ID", "string", getPackageName())).toString();
                editor.putString("bundleId", bundleId);
            } catch (Exception e) {

            }
        }

        else if(sharedPreferences.getString("cat", "").equals("")){
            try {
                cat = getResources().getText(getResources().getIdentifier("CDADS_CAT", "string", getPackageName())).toString();
                editor.putString("cat", cat);
            } catch (Exception e) {

            }
        }

        editor.putString("placementId", "");
        editor.putBoolean("isSettingsSaved", true);
        editor.commit();
        setContentView(R.layout.activity_main);
        mSmallBannerView = findViewById(R.id.smallBannerView);
        mLargeBannerView = findViewById(R.id.largeBannerView);
        mProgressBar = findViewById(R.id.progressBar2);
        mSmallBannerView.setCDAdViewListener(this);
        mLargeBannerView.setCDAdViewListener(this);
        mSmallBannerView.setLocationAutoUpdateEnabled(true);
        mLargeBannerView.setLocationAutoUpdateEnabled(true);
        mLargeBannerView.setAdAutoRefreshEnabled(true);     //Default value is false
        mSmallBannerView.setAdAutoRefreshEnabled(true);
        mLargeBannerView.setTesting(false);
        mSmallBannerView.setTesting(false);
        mLargeBannerView.setBundleIdentifier(sharedPreferences.getString("bundleId", ""));
        mLargeBannerView.setCategory(sharedPreferences.getString("cat", ""));
        mSmallBannerView.setBundleIdentifier(sharedPreferences.getString("bundleId", ""));
        mSmallBannerView.setCategory(sharedPreferences.getString("cat", ""));





    }

    public void loadInterstitialVideo(View v){
        clearAllViews();
        if (mCDAdVideoInterstitial == null){
            mCDAdVideoInterstitial = new CDAdVideoInterstitial(this);
            mCDAdVideoInterstitial.setLocationAutoUpdateEnabled(true);
            mCDAdVideoInterstitial.setInterstitialVideoAdListener(this);
            mCDAdVideoInterstitial.setTesting(false);
            mCDAdVideoInterstitial.setBundleIdentifier(sharedPreferences.getString("bundleId", ""));
            mCDAdVideoInterstitial.setCategory(sharedPreferences.getString("cat", ""));
        }
        HashMap<String, String > map = new HashMap<>();
        map.put("gender", "male");
        map.put("age", "25");
        map.put("income", "100000");
        map.put("language", "en");
        VideoConfiguration videoConfiguration = new VideoConfiguration.Builder().build(this);
        mCDAdVideoInterstitial.requestInterstitialVideo(this,videoConfiguration, map, sharedPreferences.getString("partnerId", ""), sharedPreferences.getString("placementId", ""));
    }

    public void loadNativeVideo(View v){
        mProgressBar.setVisibility(View.VISIBLE);
        dismissIfAlertDialogVisible();
        clearAllViews();
        CDAdVideoNativeAdRenderer cdAdVideoNativeAdRenderer = new CDAdVideoNativeAdRenderer(mediaViewBinder);
        mCDAdNative = new CDAdNative(this, this);
        mCDAdNative.registerAdRenderer(cdAdVideoNativeAdRenderer);
        CDAdRequest cdAdRequest = (new CDAdRequest.Builder()).build(this);
        cdAdRequest.bundleId = sharedPreferences.getString("bundleId", "");
        cdAdRequest.partnerId = sharedPreferences.getString("partnerId", "");
        cdAdRequest.placementId = sharedPreferences.getString("placementId", "");
        cdAdRequest.cat = sharedPreferences.getString("cat", "");
        cdAdRequest.testing = true;
        mCDAdNative.makeRequest(cdAdRequest);
    }

    public void loadInterstitial(View v){
        clearAllViews();
        if (CDAdSampleApplication.mCDAdInterstitial == null){
            CDAdSampleApplication.mCDAdInterstitial = new CDAdInterstitial(this);
            CDAdSampleApplication.mCDAdInterstitial.setLocationAutoUpdateEnabled(true);
            CDAdSampleApplication.mCDAdInterstitial.setInterstitialAdListener(this);
            CDAdSampleApplication.mCDAdInterstitial.setBundleIdentifier(sharedPreferences.getString("bundleId", ""));
            CDAdSampleApplication.mCDAdInterstitial.setCategory(sharedPreferences.getString("cat", ""));
            CDAdSampleApplication.mCDAdInterstitial.setTesting(false);
            CDAdSampleApplication.mCDAdInterstitial.setCdAdSize(CDAdSize.CDAdSizeConstant.CDAdSize320X480);
        }
        HashMap<String, String > map = new HashMap<>();
        map.put("gender", "male");
        map.put("age", "25");
        map.put("income", "100000");
        map.put("language", "en");
        CDAdSampleApplication.mCDAdInterstitial.requestNewAd(this,map, sharedPreferences.getString("partnerId", ""), sharedPreferences.getString("placementId", ""));

    }

    private void clearAllViews(){
        if (mNativeAd!=null){
            mNativeAd.clear(mConstraintLayout);
            mConstraintLayout.setVisibility(View.INVISIBLE);
        }
        mLargeBannerView.setVisibility(View.INVISIBLE);
        mLargeBannerView.setAdAutoRefreshEnabled(false);
        mSmallBannerView.setVisibility(View.INVISIBLE);
        mSmallBannerView.setAdAutoRefreshEnabled(false);

        if (CDAdSampleApplication.mCDAdInterstitial!=null){
            CDAdSampleApplication.mCDAdInterstitial.destroy();
            CDAdSampleApplication.mCDAdInterstitial = null;
        }

        if (mCDAdVideoInterstitial!=null){
            mCDAdVideoInterstitial.destroy();
            mCDAdVideoInterstitial = null;
        }
    }


    public void loadSmallBanner(View v){
        clearAllViews();
        HashMap<String, String > map = new HashMap<>();
        map.put("gender", "male");
        map.put("age", "25");
        map.put("income", "100000");
        map.put("language", "en");
        mSmallBannerView.setAdAutoRefreshEnabled(true);
        mSmallBannerView.requestNewAd(map, sharedPreferences.getString("partnerId", ""), sharedPreferences.getString("placementId", ""));

    }

    public void loadLargeBanner(View v){
        clearAllViews();
        HashMap<String, String > map = new HashMap<>();
        map.put("gender", "male");
        map.put("age", "25");
        map.put("income", "100000");
        map.put("language", "en");
        mLargeBannerView.setAdAutoRefreshEnabled(true);
        mLargeBannerView.requestNewAd(map, sharedPreferences.getString("partnerId", ""), sharedPreferences.getString("placementId", ""));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.settings).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInterstitialAdRequest(CDAdInterstitial cdAdInterstitial) {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInterstitialLoaded(final CDAdInterstitial cdAdInterstitial) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (isAppIsInBackground(MainActivity.this)){
            Intent intent = new Intent(MainActivity.this, DisplayService.class);
            intent.setAction(Notification_Action);
            PendingIntent contentIntent = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder b = new NotificationCompat.Builder(MainActivity.this);

            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Offer Available")
                    .setContentText("Please open to see offer details.")
                    .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                    .setContentIntent(contentIntent)
                    .build();


            NotificationManager notificationManager = (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, b.build());
        }
        else{
            dismissIfAlertDialogVisible();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setTitle("Offer Available");
            builder1.setMessage("Please open to see offer details.");
            builder1.setCancelable(false);

            builder1.setPositiveButton(
                    "Open",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            if (cdAdInterstitial!=null && cdAdInterstitial.isReady()) {
                                cdAdInterstitial.show();
                            }
                        }
                    });

            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }


    }

    @Override
    public void onInterstitialFailed(CDAdInterstitial cdAdInterstitial, CDAdErrorCode cdAdErrorCode) {
        mProgressBar.setVisibility(View.INVISIBLE);
        showAlertDialogWithMessage("Failed to load Interstitial Ad");
    }

    @Override
    public void onInterstitialShown(CDAdInterstitial cdAdInterstitial) {
        CDAdLog.i("onInterstitialShown");
    }

    @Override
    public void onInterstitialClicked(CDAdInterstitial cdAdInterstitial) {
        CDAdLog.i("onInterstitialClicked");
    }

    @Override
    public void onInterstitialDismissed(CDAdInterstitial cdAdInterstitial) {
        CDAdLog.i("onInterstitialDismissed");
    }

    @Override
    public void onInterstitialVideoAdRequest(final CDAdVideoInterstitial videoInterstitial) {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInterstitialVideoLoaded(final CDAdVideoInterstitial videoInterstitial) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (videoInterstitial.isReady()) {
            videoInterstitial.show();
            dismissIfAlertDialogVisible();
        }
    }

    @Override
    public void onInterstitialVideoFailed(final CDAdVideoInterstitial videoInterstitial, final CDAdErrorCode errorCode) {
        mProgressBar.setVisibility(View.INVISIBLE);
        showAlertDialogWithMessage("Failed to load Interstitial Video Ad");
    }

    @Override
    public void onInterstitialVideoShown(final CDAdVideoInterstitial videoInterstitial) {
        CDAdLog.i("onInterstitialVideoShown");
    }

    @Override
    public void onInterstitialVideoClicked(final CDAdVideoInterstitial videoInterstitial) {
        CDAdLog.i("onInterstitialVideoClicked");
    }

    @Override
    public void onInterstitialVideoDismissed(final CDAdVideoInterstitial videoInterstitial) {
        CDAdLog.i("onInterstitialVideoDismissed");
    }

    @Override
    public void onInterstitialVideoEnded(final CDAdVideoInterstitial videoInterstitial) {
        CDAdLog.d("onInterstitialVideoEnded");
    }

    @Override
    public void onBannerAdRequest(CDAdView cdAdView) {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBannerLoaded(CDAdView cdAdView) {
        mProgressBar.setVisibility(View.INVISIBLE);
        cdAdView.setVisibility(View.VISIBLE);
        dismissIfAlertDialogVisible();
    }

    @Override
    public void onBannerFailed(CDAdView cdAdView, CDAdErrorCode cdAdErrorCode) {
        mProgressBar.setVisibility(View.INVISIBLE);
        showAlertDialogWithMessage("Failed to receive "+(cdAdView==mLargeBannerView?"300x250":"320x50")+" Ad");
    }

    @Override
    public void onBannerClicked(CDAdView cdAdView) {

    }

    @Override
    public void onBannerExpanded(CDAdView cdAdView) {

    }

    @Override
    public void onBannerCollapsed(CDAdView cdAdView) {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mSmallBannerView!=null){
            mSmallBannerView.destroy();
            mSmallBannerView = null;
        }
        if (mLargeBannerView!=null){
            mLargeBannerView.destroy();
            mLargeBannerView = null;
        }

        if (!CDAdSampleApplication.mCDAdInterstitial.isReady()) {
            CDAdSampleApplication.mCDAdInterstitial.destroy();
            CDAdSampleApplication.mCDAdInterstitial = null;
        }
        super.onDestroy();
    }

    private void dismissIfAlertDialogVisible(){
        if (alert11 != null && alert11.isShowing()){
            alert11.cancel();
        }
    }

    private void showAlertDialogWithMessage(String message){
        dismissIfAlertDialogVisible();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setNegativeButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onNativeLoad(final NativeAd nativeAd) {
        mProgressBar.setVisibility(View.INVISIBLE);
        dismissIfAlertDialogVisible();
        View adView = nativeAd.createAdView(MainActivity.this, mConstraintLayout);
        mNativeAd = nativeAd;
        nativeAd.prepare(adView);
        nativeAd.renderAdView(adView);
        mConstraintLayout.addView(adView);
        mConstraintLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNativeFail(final NativeErrorCode errorCode) {
        mProgressBar.setVisibility(View.INVISIBLE);
        showAlertDialogWithMessage("Failed to load Native Video Ad");
    }

    @Override
    public void onNativeVideoEnded(final BaseNativeAd nativeAd) {
        CDAdLog.d("onNativeVideoEnded");
    }

    @Override
    public void onNativeClicked(final BaseNativeAd nativeAd) {
        CDAdLog.d("onNativeVideoClicked");
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
