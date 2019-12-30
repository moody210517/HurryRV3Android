package com.hurry.custom;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hurry.custom.payumoney.AppEnvironment;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;



/**
 * Created by Hirak on 28-03-2015.
 */

@TargetApi(Build.VERSION_CODES.DONUT)
//@ReportsCrashes(
//        mailTo = "kingstarboy@outlook.com", // my email here
//        mode = ReportingInteractionMode.TOAST,
//        resToastText = R.string.crash)

@AcraCore(buildConfigClass = BuildConfig.class)
public class MyApplication extends Application {
    AppEnvironment appEnvironment;
    @Override
    public void onCreate() {

        super.onCreate();
        ACRA.init(this);

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        appEnvironment = AppEnvironment.PRODUCTION;

        //facebook event
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        logAdImpressionEvent("banner");

        // google tag manager
        final FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle params = new Bundle();
        params.putString("name", getString(R.string.app_name));
        params.putString("description", getString(R.string.app_name));
        mFirebaseAnalytics.logEvent("share_image", params);
    }

    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }


    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */

    public void logAdImpressionEvent (String adType) {

        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_AD_TYPE, adType);
        params.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, "Description");
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "Free");
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent(AppEventsConstants.EVENT_NAME_AD_IMPRESSION, params);

    }

}





