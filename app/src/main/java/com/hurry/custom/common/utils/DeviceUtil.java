package com.hurry.custom.common.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hurry.custom.R;
import com.hurry.custom.view.activity.MainActivity;

import java.io.UnsupportedEncodingException;


/**
 * Created by Administrator on 6/2/2016.
 */
public class DeviceUtil {
    /**
     * Set focus true to all the child views of a specified view
     * @param context
     * @param view
     */
    public static void initializeUI(final Context context, View view) {
        try {
            if (!(view instanceof EditText)) {
                view.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        closeKeyboard(context);
                        return false;
                    }

                });
                view.setFocusableInTouchMode(true);
                view.setFocusable(true);
                view.setClickable(true);
            }
            else {
            }

            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    initializeUI(context, innerView);
                }
            }
            else {
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * description: Show or Hide the soft keyborad as focused onto a EditText parameter
     * @param context
     * @param edt
     * @param visible
     */
    public static void setKeyboard(final Context context, final EditText edt, final boolean visible) {
        final Runnable showIMERunnable = new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(edt, 0);
                }
            }
        };

        final android.os.Handler handler = new android.os.Handler();

        if (visible) {
            handler.post(showIMERunnable);
        }
        else {
            handler.removeCallbacks(showIMERunnable);
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);
            }
        }
    }

    /**
     * description:  Close the soft keyboard in a activity
     * @param context
     */
    public static void closeKeyboard(Context context) {
        View view = ((MainActivity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Open Wifi setting
     * @param context
     */
    public static void startSettingWifi(Context context) {
        try {
            Intent intentWifi = new Intent(Settings.ACTION_WIFI_SETTINGS);
            context.startActivity(intentWifi);
        }
        catch (Exception e) {
        }
    }

    /**
     * Open Device setting
     * @param context
     */
    public static void startSettingDevice(Context context) {
        try {
            ((Activity) context).startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
        }
        catch (Exception e) {
        }
    }

    /**
     * Open NFC setting
     * @param context
     */
    public static void startSettingNFC(Context context) {
        try {
            Intent intentWifi = new Intent(Settings.ACTION_NFC_SETTINGS);
            context.startActivity(intentWifi);
        }
        catch (Exception e) {
        }
    }

    /**
     *
     * @param context
     */
    public static void startSettingWireless(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            context.startActivity(intent);
        }
        catch (Exception e) {

        }
    }

    /**
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        try {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return ((netInfo != null) && netInfo.isConnected());
        } catch (Exception e) {
            return false;
        }

    }

    /**
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        try {
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return ((netInfo != null) && netInfo.isConnected());
        } catch (Exception e) {
            return false;
        }
    }

    public static void  hideStatusbar(Activity context){
        View decorView = context.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static  void setStatusBarColor(Activity context){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(context.getResources().getColor(R.color.teal_back));
        }
    }

    public static void hideSoftKeyboard(Activity activity) {

        if(activity.getCurrentFocus() != null){
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);

            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        }

    }


    public static void showSoftKeyboard(Activity activity) {

        if(activity.getCurrentFocus() != null){
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);

            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        }

    }



    public static  void setupUICloseKeyBoardOnClick(final Activity activity, View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUICloseKeyBoardOnClick(activity, innerView);
            }
        }
    }


    public static void setVerdana(Context context, TextView textView){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/ahronbd.ttf");
        textView.setTypeface(typeface);
    }

    public static void setOrbitron(Context context, TextView textView){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/ahronbd.ttf");
        textView.setTypeface(typeface);
    }

    public static void setOpenSanBold(Context context, TextView textView){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/OpenSansBold.ttf");
        textView.setTypeface(typeface);
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static int pixcelToDp( float myPixels){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        int dp = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, myPixels, displaymetrics );
        return dp;
    }
    public static float dipToPixelsDensity(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (dipValue * scale + 0.5f);
        return pixels;
    }

    public static float convertPxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }



    public static void expand(View summary) {
        //set Visible
        summary.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        summary.measure(widthSpec, summary.getMeasuredHeight());
        ValueAnimator mAnimator = slideAnimator(0, summary.getMeasuredHeight(), summary);
        mAnimator.start();
    }


    public static void expandGrid(View summary, int count) {
        //set Visible
        summary.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        summary.measure(widthSpec, summary.getMeasuredHeight());
        ValueAnimator mAnimator = slideAnimator(0, summary.getMeasuredHeight() * count, summary);
        mAnimator.start();
    }

    public static void collapse(final View summary) {
        int finalHeight = summary.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, summary);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                summary.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }
    public static ValueAnimator slideAnimator(int start, int end, final View summary) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = summary.getLayoutParams();
                layoutParams.height = value;
                summary.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }


 public static String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    
    public static  String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String encrypt(String text){

        // Sending side
        byte[] data = new byte[0];
        try {
            data = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String ret = Base64.encodeToString(data, Base64.DEFAULT);
        ret = ret.trim();
        return  ret;
    }



    public   static String decrypt(String base64){
        // Receiving side
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }





    public static boolean isPlugged(Context context) {
        boolean isPlugged= false;
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }
        return isPlugged;
    }


}
