package com.adcash.cordova.plugin;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import com.adcash.mobileads.AdcashInterstitial;
import com.adcash.mobileads.AdcashView;
import com.adcash.mobileads.AdcashBannerView;
import com.adcash.mobileads.AdcashView.AdListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class AdcashSDK extends CordovaPlugin {

    private Activity activity;
    private AdcashInterstitial interstitial;
    private AdcashBannerView adView;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.activity = cordova.getActivity();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("createInterstitial")) {
            String zoneId = args.getString(0);
            this.createInterstitial(zoneId, callbackContext);
            return true;
        }
        else if (action.equals("loadInterstitial")) {
            this.loadInterstitial(callbackContext);
            return true;
        } else if(action.equals("showInterstitial")) {
            this.showInterstitial(callbackContext);
            return true;
        } else if (action.equals("createBanner")) {
            String zoneId = args.getString(0);
            this.createBanner(zoneId, callbackContext);
            return true;
        } else if (action.equals("loadBanner")) {
            this.loadAd(callbackContext);
            return true;
        }
        return false;
    }

    private void createInterstitial(final String zoneId, final CallbackContext callbackContext) {
        if (zoneId != null && zoneId.length() > 0) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    interstitial = new AdcashInterstitial(zoneId, activity);
                    callbackContext.success();
                }
            });
        } else {
            callbackContext.error("Expected valid zone id;");
        }
    }

    /**
     * Loads an ad on a background thread.
     */
    private void loadInterstitial(final CallbackContext callbackContext) {
        if(interstitial == null) {
            callbackContext.error("adView is null, call createInterstitial first.");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitial.setAdListener(new AdcashView.AdListener() {

                    @Override
                    public void onAdLoaded() {
                        webView.loadUrl("javascript:cordova.fireDocumentEvent('onLoadedInterstitialAd');");
                        callbackContext.success();
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        webView.loadUrl(String.format(
                                "javascript:cordova.fireDocumentEvent('onAdFailedToLoad', { 'error': %d });",
                                errorCode));
                        callbackContext.error(errorCode);
                    }

                    @Override
                    public void onAdOpened() {
                        webView.loadUrl("javascript:cordova.fireDocumentEvent('onOpenedInterstitialAd');");
                    }

                    @Override
                    public void onAdClosed() {
                        webView.loadUrl("javascript:cordova.fireDocumentEvent('onClosedInterstitialAd');");
                        interstitial = null;
                    }

                    @Override
                    public void onAdLeftApplication() {
                        webView.loadUrl("javascript:cordova.fireDocumentEvent('onAdLeftApplication');");
                    }
                });
                interstitial.loadAd();
            }
        });
    }

    /**
     * Show the ad if it is loaded.
     */
    private void showInterstitial(final CallbackContext callbackContext) {
        if(interstitial == null) {
            callbackContext.error("interstitialAd is null, call createInterstitialView first.");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitial.showAd();
                callbackContext.success();
            }
        });
    }

    private void createBanner(final String zoneId, final CallbackContext callbackContext) {
        final int positionCode = 1;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adView = new AdcashBannerView(activity, null);// AttributeSet
                // Setting the background color works around an issue where the
                // first ad isn't visible.
                adView.setBackgroundColor(Color.TRANSPARENT);
                adView.setAdUnitId(zoneId);
                // adView.setAdSize(adSize);


                FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

                final int one = 1;
                final int zero = 0;
                switch (positionCode) {
                    case zero:
                        adParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                        break;
                    case one:
                        adParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                        break;
                }

                activity.addContentView(adView, adParams);
                callbackContext.success();
            }
        });
    }

    private void loadAd(final CallbackContext callbackContext) {
        if(adView == null) {
            callbackContext.error("adView is null, call createBanner to create BannerView first");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        webView.loadUrl("javascript:cordova.fireDocumentEvent('onAdLoaded');");
                        callbackContext.success();
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        webView.loadUrl(String.format("javascript:cordova.fireDocumentEvent('onAdFailedToLoad', { 'error': %d });", errorCode));
                        callbackContext.error(errorCode);
                    }

                    @Override
                    public void onAdOpened() {
                        webView.loadUrl("javascript:cordova.fireDocumentEvent('onAdOpened');");
                    }

                    @Override
                    public void onAdClosed() {
                        webView.loadUrl("javascript:cordova.fireDocumentEvent('onAdClosed');");
                    }

                    @Override
                    public void onAdLeftApplication() {
                        webView.loadUrl("javascript:cordova.fireDocumentEvent('onAdLeftApplication');");
                    }
                });
                adView.loadAd();
            }
        });
    }
}
