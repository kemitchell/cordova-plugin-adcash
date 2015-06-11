package com.adcash.cordova.plugin;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.adcash.mobileads.AdcashBannerView;
import com.adcash.mobileads.AdcashConversionTracker;
import com.adcash.mobileads.AdcashInterstitial;
import com.adcash.mobileads.AdcashView;
import com.adcash.mobileads.AdcashView.AdListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
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

    private static final String OPT_AUTO_SHOW = "autoShow";
    private static final String OPT_AD_SIZE = "adSize";
    private static final String OPT_AD_POSITION = "position";
    private static final String OPT_AD_EXTRA = "adExtra";
    private static final String OPT_ZONE_ID = "zoneId";

    /** Banner position constant for the top of the screen. */
    private static final int POSITION_TOP = 0;

    /** Banner position constant for the bottom of the screen. */
    private static final int POSITION_BOTTOM = 1;

    /** Cordova Actions. */
    private static final String ACTION_SET_OPTIONS = "setOptions";

    private static final String ACTION_CREATE_BANNER = "createBanner";
    private static final String ACTION_REMOVE_BANNER = "removeBanner";
    private static final String ACTION_LOAD_BANNER = "loadBanner";
    private static final String ACTION_SHOW_BANNER = "showBanner";
    private static final String ACTION_HIDE_BANNER = "hideBanner";

    private static final String ACTION_PREPARE_INTERSTITIAL = "prepareInterstitial";
    private static final String ACTION_SHOW_INTERSTITIAL = "showInterstitial";

    private static final String ACTION_REPORT_APP_OPEN = "reportAppOpen";


    boolean autoShow = true;
    int positionCode = POSITION_BOTTOM;
    JSONObject adExtras;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.activity = cordova.getActivity();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ACTION_PREPARE_INTERSTITIAL)) {
            JSONObject options = args.getJSONObject(0);
            String zoneId = "";
            if(options.has(OPT_ZONE_ID) && !options.isNull(OPT_ZONE_ID)) {
                if(options.get(OPT_ZONE_ID) instanceof String) {
                    zoneId = options.getString(OPT_ZONE_ID);
                } else {
                    callbackContext.error("ZoneId type is not String");
                    return true;
                }
            }else{
                callbackContext.error("No zoneId or wrong key");
                return true;
            }

            this.prepareInterstitial(zoneId, callbackContext);
            return true;
        }
        /*else if (action.equals("loadInterstitial")) {
            this.loadInterstitial(callbackContext);
            return true;
        }*/ else if(action.equals(ACTION_SHOW_INTERSTITIAL)) {
            this.showInterstitial(callbackContext);
            return true;
        } else if (action.equals(ACTION_CREATE_BANNER)) {
            JSONObject bannerOptions = args.getJSONObject(0);
            String bannerZoneId = "";
            if(bannerOptions.has(OPT_ZONE_ID) && !bannerOptions.isNull(OPT_ZONE_ID)) {
                if(bannerOptions.get(OPT_ZONE_ID) instanceof String) {
                    bannerZoneId = bannerOptions.getString(OPT_ZONE_ID);
                } else {
                    callbackContext.error("ZoneId type is not String");
                    return true;
                }
            }else{
                callbackContext.error("No zoneId or wrong key");
                return true;
            }

            this.createBanner(bannerZoneId, callbackContext);
            return true;
        } else if (action.equals(ACTION_LOAD_BANNER)) {
            this.loadBanner(callbackContext);
            return true;
        } else if (action.equals(ACTION_SHOW_BANNER)) {
            this.showBanner(callbackContext);
            return true;
        } else if (action.equals(ACTION_HIDE_BANNER)) {
            this.hideBanner(callbackContext);
            return true;
        } else if (action.equals(ACTION_REMOVE_BANNER)) {
            this.removeBanner(callbackContext);
            return true;
        } else if (action.equals(ACTION_REPORT_APP_OPEN)) {
            this.reportAppOpen(callbackContext);
            return true;
        } else if(action.equals(ACTION_SET_OPTIONS)){
            JSONObject ops = args.getJSONObject(0);
            this.executeSetOptions(ops, callbackContext);
            return true;
        }
        return false;
    }

    private void executeSetOptions(JSONObject options, CallbackContext callbackContext) {

        String msg = this.setOptions(options);
        if(!msg.equalsIgnoreCase("OK")){
            callbackContext.error(msg);
        } else {
            callbackContext.success();
        }
    }

    /**
     * apply additional settings to banner or interstitial ads
     * @param options json object containing keys and values for settings
     * @return text containing text "OK" without quotes if everything is OK or message that describes first found error
     */
    private String setOptions( JSONObject options ) {
        if(options == null) return "Options are null";


            if(options.has(OPT_AUTO_SHOW) && !options.isNull(OPT_AUTO_SHOW)) {
                try{
                    autoShow = options.getBoolean(OPT_AUTO_SHOW);
                } catch(JSONException exc) {
                    return "autoShow has to be from Boolean type";
                }
            }


        if(options.has(OPT_AD_POSITION) && !options.isNull(OPT_AD_POSITION)){
            try{
                positionCode = options.getInt(OPT_AD_POSITION);
            } catch(JSONException exc) {
                return "position has to be from Integer type";
            }
        }

        if(options.has(OPT_AD_EXTRA)){
            adExtras = options.optJSONObject(OPT_AD_EXTRA);
        }
        return "OK";
    }

    private void setDefaultOptions( ) {
        this.adExtras  = null;
        this.autoShow  = true;
        this.positionCode = POSITION_BOTTOM;
    }

    private void prepareInterstitial(final String zoneId, final CallbackContext callbackContext) {
        if (zoneId != null && zoneId.length() > 0) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    interstitial = new AdcashInterstitial(zoneId, activity);

                    if(interstitial == null) {
                        callbackContext.error("adView is null, call createInterstitial first.");
                        return;
                    }

                    interstitial.setAdListener(new AdcashView.AdListener() {

                        @Override
                        public void onAdLoaded() {
                            webView.loadUrl("javascript:cordova.fireDocumentEvent('onLoadedInterstitialAd');");
                            interstitial.showAd();
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
        } else {
            callbackContext.error("Expected valid zone id;");
        }
    }

    /**
     * Loads an ad on a background thread.
     */
    private void loadInterstitial(final CallbackContext callbackContext) {
        if(interstitial == null) {
            callbackContext.error("interstitial is null, call createInterstitial first.");
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
            callbackContext.error("interstitialAd is null, call createInterstitial first.");
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

                switch (positionCode) {
                    case POSITION_TOP:
                        adParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                        break;
                    case POSITION_BOTTOM:
                        adParams.gravity = Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL;
                        break;
                }

                activity.addContentView(adView, adParams);

                if(autoShow){
                    adView.setVisibility(View.VISIBLE);
                }else{
                    adView.setVisibility(View.GONE);
                }

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
				
                callbackContext.success();
            }
        });
    }

    private void loadBanner(final CallbackContext callbackContext) {
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

    /**
     * Show the ad if it is loaded.
     */
    private void showBanner(final CallbackContext callbackContext) {
        if(adView == null) {
            callbackContext.error("banner is null, call createBanner first.");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adView.showAd();
                callbackContext.success();
            }
        });
    }

    /**
     * Hide the ad if it is loaded.
     */
    private void hideBanner(final CallbackContext callbackContext) {
        if(adView == null) {
            callbackContext.error("banner is null, call createBanner first.");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adView.setVisibility(View.GONE);
                callbackContext.success();
            }
        });
    }

    /**
     * Show the ad if it is loaded.
     */
    private void removeBanner(final CallbackContext callbackContext) {
        if(adView == null) {
            callbackContext.error("banner is null, call createInterstitial first.");
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewParent parentView = adView.getParent();
                if (parentView != null && parentView instanceof ViewGroup) {
                    ((ViewGroup) parentView).removeView(adView);
                }
                callbackContext.success();
            }
        });
    }

    // Conversion Tracker
    private AdcashConversionTracker tracker;

    public void reportAppOpen(final CallbackContext callbackContext){
        if(tracker == null){
            this.tracker = new AdcashConversionTracker(activity);
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tracker.reportAppOpen();
                callbackContext.success();
            }
        });
    }

    public void reportConversion(final String campaign, final String payout, final String tid){
        if(tracker == null){
            this.tracker = new AdcashConversionTracker(activity);
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tracker.reportConversion(campaign, payout, tid);
            }
        });

    }
}
