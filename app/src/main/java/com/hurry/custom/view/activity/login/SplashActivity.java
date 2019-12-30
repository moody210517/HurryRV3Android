package com.hurry.custom.view.activity.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.controller.GetBasic;
import com.hurry.custom.controller.GetCity;
import com.hurry.custom.controller.GetPhone;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.MainActivity;

import java.util.Locale;

/**
 * Created by Administrator on 3/18/2017.
 */

public class SplashActivity extends Activity  implements  SurfaceHolder.Callback{


    private MediaPlayer mMediaPlayer = null;
    SurfaceView mSurfaceView=null;
    private SurfaceHolder mFirstSurface;
    private Uri mVideoUri;
    private SurfaceHolder mActiveSurface;

    Handler mHandler= new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };

    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            PreferenceUtils.setFirstStart(SplashActivity.this, false);
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        DeviceUtil.hideStatusbar(this);


        Constants.initWeight();
        init();

        Resources res = getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();

        Locale locale = new Locale("us");//Locale.getDefault().getDisplayLanguage()
        Locale.setDefault(locale); // this is needed to change even the map's language
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLayoutDirection(conf.locale);
        }
        res.updateConfiguration(conf, dm);
        //return res;
        getBaseContext().getResources().updateConfiguration(conf,
                getBaseContext().getResources().getDisplayMetrics());
        String language = Locale.getDefault().getLanguage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(this, language);
        }
        updateResourcesLegacy(this, language);


        if(PreferenceUtils.getLogin(SplashActivity.this)){

            if(PreferenceUtils.getCityId(SplashActivity.this) == -1){
                PreferenceUtils.setFirstStart(SplashActivity.this, false);
                Intent intent = new Intent(SplashActivity.this, LocationActivity.class);
                startActivity(intent);
                finish();
            }else{

                PreferenceUtils.setFirstStart(SplashActivity.this, false);
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }

        }else{
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initPlayer();
                }
            });
            loadBasicData();
            mHandler.postDelayed(runnable, 4000);
        }
    }

    private void autoLogin() {
        new GetBasic(this, "auto").execute();
    }

    public void getCities(){
        new GetCity(this, "").execute();
    }



    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }


    public void init(){
        Constants.MODE = PreferenceUtils.getMode(this);
    }

    private  void initPlayer(){
        mMediaPlayer = new MediaPlayer();
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mSurfaceView.setBackgroundColor(Color.TRANSPARENT);
        mSurfaceView.getHolder().lockCanvas();
        mSurfaceView.getHolder().addCallback(this);
        //if(DeviceUtil.isTablet(this)){
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            mSurfaceView.getHolder().setFixedSize(width, height/2);
        //}
        mVideoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.new_white);
    }



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mFirstSurface = surfaceHolder;
        if (mVideoUri != null) {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(),
                    mVideoUri, mFirstSurface);
            mActiveSurface = mFirstSurface;
            mMediaPlayer.setLooping(false);
            mMediaPlayer.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void callNext(){
        try{
            PreferenceUtils.setUserId(SplashActivity.this, "0");
            new GetPhone(SplashActivity.this).execute();
        }catch (Exception e){
        }
    }

    @Override


    public void onResume(){
        super.onResume();
        final FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        Bundle params = new Bundle();
        params.putString("app_name", getString(R.string.app_name));
        params.putString("event_name", getString(R.string.app_name));
        mFirebaseAnalytics.logEvent("app_name", params);
    }

    private void loadBasicData() {
        //new GetBasic(this).execute();
        callNext();
    }


}
