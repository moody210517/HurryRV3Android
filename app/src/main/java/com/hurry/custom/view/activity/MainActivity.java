/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hurry.custom.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.controller.CompressProcess;
import com.hurry.custom.controller.GetBasic;
import com.hurry.custom.controller.GetCity;
import com.hurry.custom.controller.GetPhone;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.AddressModel;
import com.hurry.custom.model.CarrierModel;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.model.OrderCorporateHisModel;
import com.hurry.custom.model.OrderHisModel;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.view.activity.login.LoginActivity;
import com.hurry.custom.view.adapter.CityAdapter;
import com.hurry.custom.view.custom.CustomTypefaceSpan;
import com.hurry.custom.view.custom.IconTextTabLayout;
import com.hurry.custom.view.fragment.AboutUsFragment;
import com.hurry.custom.view.fragment.ContactUsFragment;
import com.hurry.custom.view.fragment.FeedBackFragment;
import com.hurry.custom.view.fragment.HomePersonalFragment;
import com.hurry.custom.view.fragment.OrderHistoryFragment;
import com.hurry.custom.view.fragment.PolicyFragment;
import com.hurry.custom.view.fragment.QuoteFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */

public class MainActivity extends AppCompatActivity {

    public static final int INTENT_REQUEST_GET_IMAGES = 13;
    public static final int LOGIN_ACTIVITY = 14;
    public static final int SIGN_UP_ACTIVITY = 15;
    public static final int LOGIN = 0;

    public static final int HOME_PERSONAL = 1;
    public static final int HOME_CORPERATION = 2;
    public static final int ORDER_HIS = 3;
    public static final int CANCEL_PICKUP = 4;
    public static final int SCHEDULE = 5;
    public static final int ABOUT_US = 6;
    public static final int POLICY = 7;
    public static final int FEEBACK = 8;
    public static final int PRIVACY = 9;

    public static final int PROFILE = 10;
    public static final int CAMERA_OPTION = 11;
    public static final int ITEM_OPTION = 12;
    public static final int PACKAGE_OPTION = 13;
    public static final int QUOTE = 14;
    public static final int CONTACT = 15;
    public static final int TRACKING  = 16;
    public static final int TRACKING_COR = 17;

    ActionBar ab;

    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
    };
    private static final int INITIAL_REQUEST=1337;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }
        }

        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        ab.setTitle("Welcome");
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Constants.MODE = PreferenceUtils.getMode(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
            initNavView();
        }

        DeviceUtil.hideSoftKeyboard(this);
        if(!PreferenceUtils.getFirstStart(this)){
            autoLogin();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INITIAL_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission allowed", Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        //Constants.clearData();
        //Constants.clearCorporateData(); do not add ...
        if(Constants.page_type.equals("item") || Constants.page_type.equals("package") || Constants.page_type.equals("camera")){
            return;
        }

        if (Constants.page_type.equals("reschedule")) {
            updateFragment(SCHEDULE);
            Constants.page_type = "";
        } else if (Constants.page_type.equals("cancel")) {
            updateFragment(CANCEL_PICKUP);
            Constants.page_type = "";
        } else if (Constants.page_type.equals("home")) {
            updateFragment(HOME_PERSONAL);
            Constants.page_type = "";
        } else if (Constants.page_type.equals("camera")) {
            Constants.page_type = "";
            updateFragment(CAMERA_OPTION);
        } else if (Constants.page_type.equals("item")) {
            updateFragment(ITEM_OPTION);
            Constants.page_type = "";
        } else if (Constants.page_type.equals("package")) {
            updateFragment(PACKAGE_OPTION);
            Constants.page_type = "";
        } else if (Constants.page_type.equals("quote")) {
            updateFragment(QUOTE);
            Constants.page_type = "";
        } else if (Constants.page_type.equals("aboutus")) {
            updateFragment(ABOUT_US);
            Constants.page_type = "";
        } else if (Constants.page_type.equals("contact")) {
            updateFragment(CONTACT);
            Constants.page_type = "";
        } else if (Constants.page_type.equals("privacy")) {
            updateFragment(PRIVACY);
            Constants.page_type = "";
        } else if(Constants.page_type.equals("map")){
        }else{
            if(PreferenceUtils.getLogin(this) == true){
                if(Constants.MODE == Constants.PERSONAL){
                    updateFragment(HOME_PERSONAL);
                }else if(Constants.MODE == Constants.CORPERATION){
                    updateFragment(HOME_CORPERATION);
                }else if(Constants.MODE ==  Constants.GUEST){
                    updateFragment(HOME_PERSONAL);
                }
            }else{
                updateFragment(LOGIN);
            }
        }
        if(Constants.MODE == Constants.PERSONAL){
            updatePersonalMenu();
        }else if(Constants.MODE == Constants.CORPERATION){
            updateCorporatoinMenu();
        }else if(Constants.MODE == Constants.GUEST){
            updatePersonalMenu();
        }

        if(CameraOrderActivity.cameraOrderActivity != null){
            CameraOrderActivity.cameraOrderActivity.finish();
        }
        if(ItemOrderActivity.itemOrderActivity != null){
            ItemOrderActivity.itemOrderActivity.finish();
        }

    }



    public  void initNavView(){
        if(Constants.MODE == Constants.PERSONAL){
            View hView =  navigationView.inflateHeaderView(R.layout.nav_header);
            initAdminMenu(hView);
            updatePersonalMenu();
        }else if(Constants.MODE == Constants.CORPERATION){
            View hView =  navigationView.inflateHeaderView(R.layout.nav_header);
            initAdminMenu(hView);
            updateCorporatoinMenu();

        }else if(Constants.MODE == Constants.GUEST){
            View hView =  navigationView.inflateHeaderView(R.layout.nav_header);
            initAdminMenu(hView);
            updatePersonalMenu();
        }
    }


    MenuItem menuItemUserSignOut;
    MenuItem menuItemCoperationSignOut;
    public void updatePersonalMenu(){
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.drawer_personal);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem0 = menu.getItem(0);
        MenuItem menuItem1 = menu.getItem(1);
        MenuItem menuItem2 = menu.getItem(2);
        MenuItem menuItem3 = menu.getItem(3);
        MenuItem menuItem4 = menu.getItem(4);
        MenuItem menuItem5 = menu.getItem(5);
        MenuItem menuItem6 = menu.getItem(6);
        MenuItem menuItem7 = menu.getItem(7);
        MenuItem menuItem8 = menu.getItem(8);
//        MenuItem menuItem8 = menu.getItem(8);
//        MenuItem menuItem9 = menu.getItem(9);

        if(PreferenceUtils.getLogin(MainActivity.this)){
            menuItem8.setVisible(true);
        }else{
            menuItem8.setVisible(false);
        }
        menuItemUserSignOut = menuItem8;
        applyFontToMenuItem(menuItem0);
        applyFontToMenuItem(menuItem1);
        applyFontToMenuItem(menuItem2);
        applyFontToMenuItem(menuItem3);
        applyFontToMenuItem(menuItem4);
        applyFontToMenuItem(menuItem5);
        applyFontToMenuItem(menuItem6);
        applyFontToMenuItem(menuItem7);
        applyFontToMenuItem(menuItem8);
        //applyFontToMenuItem(menuItem9);


        if(Constants.MODE == Constants.GUEST) {
            if (menuItemCoperationSignOut != null) {
                menuItemCoperationSignOut.setTitle("Sign In");
                applyFontToMenuItem(menuItemCoperationSignOut);
            }
            if (menuItemUserSignOut != null) {
                menuItemUserSignOut.setTitle("Sign In");
                applyFontToMenuItem(menuItemUserSignOut);
            }
        }
    }


    public void updateCorporatoinMenu(){
//        navigationView.getMenu().clear();
//        navigationView.inflateMenu(R.menu.drawer_corporation);
//        Menu menu = navigationView.getMenu();
//        MenuItem menuItem0 = menu.getItem(0);
//        MenuItem menuItem1 = menu.getItem(1);
//        MenuItem menuItem2 = menu.getItem(2);
//        MenuItem menuItem3 = menu.getItem(3);
//        MenuItem menuItem4 = menu.getItem(4);
//        MenuItem menuItem5 = menu.getItem(5);
//        MenuItem menuItem6 = menu.getItem(6);
//        MenuItem menuItem7 = menu.getItem(7);
//        if(PreferenceUtils.getLogin(MainActivity.this)){
//            menuItem7.setVisible(true);
//        }else{
//            menuItem7.setVisible(false);
//        }
//        menuItemCoperationSignOut = menuItem7;
//
//        applyFontToMenuItem(menuItem0);
//        applyFontToMenuItem(menuItem1);
//        applyFontToMenuItem(menuItem2);
//        applyFontToMenuItem(menuItem3);
//        applyFontToMenuItem(menuItem4);
//        applyFontToMenuItem(menuItem5);
//        applyFontToMenuItem(menuItem6);
//        applyFontToMenuItem(menuItem7);
    }

    public void initAdminMenu(View view){
//        TextView tv = (TextView)view.findViewById(R.id.txt_profile_name);
//        tv.setText(pref.getPreferences("name", "Admin"));
//        TextView txtAdmin = (TextView)view.findViewById(R.id.txt_admin);
//        txtAdmin.setText(pref.getPreferences("name", "Admin"));
//        int [] resIds = {R.id.txt_admin,
//                R.id.txt_notification,
//                R.id.txt_messages,
//                R.id.txt_calendar,
//                R.id.txt_chat,
//                R.id.txt_notice_board,
//                R.id.txt_msg_all,
//                R.id.txt_msg_teacher_staff,
//                R.id.txt_msg_parent,
//                R.id.txt_msg_stuff,
//                R.id.txt_message_board,
//                R.id.txt_enable_chat,
//                R.id.txt_logout};
//        for(int k = 0 ; k < resIds.length; k++){
//            TextView textView = (TextView)view.findViewById(resIds[k]);
//            textView.setOnClickListener(this);
//        }
    }


    public static Fragment currentFragment;
    public  void updateFragment(int which){

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.fragment);
        frameLayout.setVisibility(View.VISIBLE);
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setVisibility(View.GONE);
        IconTextTabLayout tabLayout = (IconTextTabLayout) findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);

        Fragment fragment = null;

        if(PreferenceUtils.getLogin(this) == false){
            if(currentFragment == null){
                ab.setTitle("Welcome");
                fragment = new HomePersonalFragment();
            }else{
                if(Constants.page_type.equals("signin") || Constants.page_type.equals("signup")){
                    Constants.page_type = "";
                    return ;
                }else{
                    ab.setTitle("Welcome");
                    //fragment = new LandingFragment();
                    Toast.makeText(this, "Please Sign In", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            //Toast.makeText(this, "Please Loigin", Toast.LENGTH_SHORT).show();
        }else{
            switch (which){
                case LOGIN:
                    ab.setTitle("Welcome");
                    fragment = new HomePersonalFragment();
                    break;
                case HOME_PERSONAL:
                    if(Constants.MODE == Constants.GUEST){
                        ab.setTitle("Hi Guest");
                    }else{
                        ab.setTitle( "Hi " + PreferenceUtils.getNickname(this));
                    }
                    fragment = new HomePersonalFragment();
                    break;
                case HOME_CORPERATION:
                    ab.setTitle( "Hi " + PreferenceUtils.getNickname(this));
                    fragment = new HomePersonalFragment();
                    break;
                case ORDER_HIS:
                    if(Constants.MODE != Constants.GUEST){
                        ab.setTitle("Order History");

                        Constants.orderHisModels.clear();
                        Constants.orderCorporateHisModels.clear();

                        if(Constants.MODE == Constants.PERSONAL){

                            //initOrderHistoryPersonal();
                            initOrderHistoryCor();
                            orderHistory(0);

                        }else{
                            initOrderHistoryCor();
                            orderCorHistory();
                            //setUpCorporateOrderHis(viewPager);
                            //fragment = new OrderCorporateHistoryFragment();
                        }
                        return;
                    }else{
                        Toast.makeText(this, "Please Sign In", Toast.LENGTH_SHORT).show();
                        return;
                    }
                case CANCEL_PICKUP:
                case SCHEDULE:
                    ab.setTitle("Order History");
                    Constants.orderHisModels.clear();
                    Constants.orderCorporateHisModels.clear();

                    if(Constants.MODE == Constants.PERSONAL){
                        //initOrderHistoryPersonal();
                        initOrderHistoryCor();
                        orderHistory(1);

                    }else{
                        initOrderHistoryCor();
                        orderCorHistory();
                        //setUpCorporateOrderHis(viewPager);
                        //fragment = new OrderCorporateHistoryFragment();
                    }
                    break;
                case QUOTE:
                    if(Constants.MODE != Constants.GUEST){
                        if(Constants.MODE == Constants.PERSONAL){
                            ab.setTitle("Quotes");
                            fragment = new QuoteFragment();
                        }else{
                            ab.setTitle("Quotes");
                            fragment = new QuoteFragment();
                        }
                    }else{
                        Toast.makeText(this, "Please Sign In", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                case ABOUT_US:
                    ab.setTitle("About Us");
                    fragment = new AboutUsFragment();
                    break;
                case POLICY:
                    ab.setTitle("Policy and Terms");
                    fragment = new PolicyFragment();
                    break;
                case FEEBACK:
                    ab.setTitle("Feedback");
                    fragment = new FeedBackFragment();
                    break;
                case CONTACT :
                    ab.setTitle("Contact Us");
                    fragment = new ContactUsFragment();
                    break;

                case PROFILE:
                    if(Constants.MODE != Constants.GUEST){
                        Intent intent = new Intent(this,UpdateProfileActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(this, "Please Sign In", Toast.LENGTH_SHORT).show();
                    }
                    return;
                case CAMERA_OPTION:
                    //ab.setTitle("Photo Upload");
                    //Constants.page_type = "camera";
                    //fragment = new CameraOrderFragment();
                    Intent cameraOrder = new Intent(this, CameraOrderActivity.class);
                    startActivity(cameraOrder);
                    break;
                case ITEM_OPTION:
                    //ab.setTitle("Select the Item");
                    //fragment = new ItemOrderFragment();

                    Intent itemOrder = new Intent(this, ItemOrderActivity.class);
                    itemOrder.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(itemOrder);

                    break;

            }
        }
        currentFragment = fragment;

        if(which == TRACKING){
            currentFragment = null;
        }
        //getFragmentManager().beginTransaction();

        if(fragment != null){
            final Fragment finalFragment = fragment;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                //getFragmentManager().beginTransaction()
                                ft.replace(R.id.fragment, finalFragment); // f2_container is your FrameLayout container
                                ft.setTransition(FragmentTransaction.TRANSIT_NONE); //TRANSIT_FRAGMENT_OPEN
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });
                    }catch (Exception e){};
                }
            }, 200);


            // for sign out button
            if(PreferenceUtils.getLogin(MainActivity.this)){
                if(menuItemCoperationSignOut != null )
                    menuItemCoperationSignOut.setVisible(true);
                if(menuItemUserSignOut != null)
                    menuItemUserSignOut.setVisible(true);


                if(menuItemCoperationSignOut != null ){
                    menuItemCoperationSignOut.setTitle("Sign Out");
                    applyFontToMenuItem(menuItemCoperationSignOut);
                }

                if(menuItemUserSignOut != null){
                    menuItemUserSignOut.setTitle("Sign Out");
                    applyFontToMenuItem(menuItemUserSignOut);
                }

            }else{
                if(menuItemCoperationSignOut != null )
                    menuItemCoperationSignOut.setVisible(false);
                if(menuItemUserSignOut != null)
                    menuItemUserSignOut.setVisible(false);
            }
        }
//
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      //  getMenuInflater().inflate(R.menu.sample_actions, menu);
      //  return true;

        getMenuInflater().inflate(R.menu.sample_actions, menu);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View v = findViewById(R.id.action_settings);

                if (v != null) {
                    v.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return false;
                        }
                    });
                }
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View v = findViewById(R.id.action_settings);

                if (v != null) {
                    v.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return false;
                        }
                    });
                }
            }
        });


        return true;
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        return;
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            finish();
//            return;
//        }
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce=false;
//            }
//        }, 2000);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Constants.MODE == Constants.GUEST){
            //PreferenceUtils.setLogOut(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(Constants.MODE == Constants.GUEST){
            //PreferenceUtils.setLogOut(this);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                // click home button

                DeviceUtil.hideSoftKeyboard(this);

                Constants.dateModel  = null;
                Constants.cameraOrderModel.itemModels.clear();
                Constants.page_type = "";
                Constants.clearData();

                // ------ //
                if(Constants.MODE == Constants.CORPERATION){
                    Constants.clearData();
                    updateFragment(HOME_CORPERATION);
                }else if(Constants.MODE == Constants.PERSONAL){
                    updateFragment(HOME_PERSONAL);
                }else if(Constants.MODE == Constants.GUEST){
                    updateFragment(HOME_PERSONAL);
//                    Intent login = new Intent(this, LoginActivity.class);
//                    startActivity(login);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {

                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                MainActivity.this.menuItem = menuItem;
                mHandler.postDelayed(runnable, 300);
                return true;
            }
        });
    }

    MenuItem menuItem ;
    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            switch (menuItem.getItemId()){
                case R.id.nav_profile:
                    updateFragment(PROFILE);
                    break;
                case R.id.nav_quote:
                    updateFragment(QUOTE);
                    break;

                case R.id.nav_order_history:
                    updateFragment(ORDER_HIS);
                    break;
//                case R.id.nav_reschedule:
//                    updateFragment(SCHEDULE);
//                    break;
//                case R.id.nav_cancel:
//                    updateFragment(CANCEL_PICKUP);
//                    break;
                case R.id.nav_change_location:
                    Constants.dateModel  = null;
                    Constants.cameraOrderModel.itemModels.clear();
                    Constants.page_type = "";
                    Constants.packageOrderModel = null;
                    Constants.itemOrderModel = null;
                    Constants.addressModel = null;
                    Constants.packageOrderModel = new OrderModel();
                    Constants.itemOrderModel = new OrderModel();
                    Constants.addressModel = new AddressModel();
                    showConfirmDialog();
                    break;
                case R.id.nav_about_us:
                    updateFragment(ABOUT_US);
                    break;
                case R.id.nav_contact_us:
                    updateFragment(CONTACT);
                    break;
                case R.id.nav_feedback:
                    updateFragment(FEEBACK);
                    break;
                case R.id.nav_privacy:
                    updateFragment(POLICY);
                    break;

                case R.id.nav_signout:
                    Constants.clearData();
                    PreferenceUtils.setLogOut(MainActivity.this);
                    if(Constants.MODE == Constants.PERSONAL){
                        PreferenceUtils.setEmail(MainActivity.this,"");
                        PreferenceUtils.setPassword(MainActivity.this, "");
                    }else if(Constants.MODE == Constants.CORPERATION){
                        PreferenceUtils.setCorEmail(MainActivity.this, "");
                        PreferenceUtils.setCorPassword(MainActivity.this, "");
                    }
                    updatePersonalMenu();
                    currentFragment = null;
                    Constants.MODE = Constants.PERSONAL;
                    PreferenceUtils.setMode(MainActivity.this, Constants.PERSONAL);

                    Intent login = new Intent(MainActivity.this, LoginActivity.class);
                    login.putExtra("type", "close");
                    startActivity(login);
                    finish();
                    break;
            }
        }
    };

    Dialog dialog;
    private void showConfirmDialog(){
        dialog = new Dialog(this ,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_city);
        RecyclerView recyclerView = (RecyclerView)dialog.findViewById(R.id.recyclerview);
        setupRecyclerView(recyclerView);
        // dialog.setTitle("Title...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
    private void setupRecyclerView(RecyclerView recyclerView) {
        if(Constants.cityModels!= null && Constants.cityModels.size() > 0){
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            recyclerView.setAdapter(new CityAdapter(this,
                    Constants.cityModels, recyclerView));
        }else{
            recyclerView.setAdapter(null);
        }
    }
    public void hideDialog(){
        dialog.dismiss();
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "font/verdanab.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if (requestCode == INTENT_REQUEST_GET_IMAGES ) {

                for(int k = 0; k < Constants.cameraOrderModel.itemModels.size() ; k++){
                    String image = Constants.cameraOrderModel.itemModels.get(k).image;
                    if(image == null){
                        ItemModel itemModel = Constants.cameraOrderModel.itemModels.get(k);
                        Constants.cameraOrderModel.itemModels.remove(itemModel);
                    }
                }


                final ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                new CompressProcess(MainActivity.this, image_uris).execute();



            }


            if(requestCode == LOGIN_ACTIVITY){
                if(PreferenceUtils.getLogin(this) == true){
                    new GetPhone(MainActivity.this).execute();

//                    if(Constants.MODE == Constants.PERSONAL){
//                        updateFragment(HOME_PERSONAL);
//                    }else if(Constants.MODE == Constants.CORPERATION){
//                        updateFragment(HOME_CORPERATION);
//                    }
                }
            }

            if(requestCode == SIGN_UP_ACTIVITY){
                if(PreferenceUtils.getLogin(this) == true){
                    new GetPhone(MainActivity.this).execute();

//                    if(Constants.MODE == Constants.PERSONAL){
//                        updateFragment(HOME_PERSONAL);
//                    }else if(Constants.MODE == Constants.CORPERATION){
//                        updateFragment(HOME_CORPERATION);
//                    }
                }
            }
        }
    }


    ProgressDialog mProgressDialog;
    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    public void showProgressDialog(String title) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(title);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        try{

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

        }catch (Exception e){

        }

    }


    ViewPager viewPager;
    IconTextTabLayout tabLayout;

    private  void initOrderHistoryCor(){
        FrameLayout fragment = (FrameLayout)findViewById(R.id.fragment);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragment.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);

        tabLayout = (IconTextTabLayout) findViewById(R.id.tabs);
        viewPager.removeAllViews();

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);
    }


    private void setUpPersonalOrderHis(ViewPager viewPager, int index) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new OrderHistoryFragment("1"), "Positional");
        adapter.addFragment(new OrderHistoryFragment("2"), "Message");
        adapter.addFragment(new OrderHistoryFragment("3"), "Message");
        viewPager.setAdapter(adapter);
        tabLayout.setTabsFromPagerAdapter(adapter);
        //tabLayout.setTabsFromPagerAdapter(adapter);
        viewPager.setCurrentItem(index);
    }

    private void setUpCorporateOrderHis(ViewPager viewPager) {

    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


    private void orderHistory(final int index) {
        RequestParams params = new RequestParams();
        params.put("user_id", PreferenceUtils.getUserId(MainActivity.this));

        WebClient.post(Constants.BASE_URL_ORDER + "get_orders_his", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        showProgressDialog();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        // Log.d("Error String", responseString);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        // Log.d("Error", errorResponse.toString());
                    }
                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {
                        try {
                            if (response != null) {

                                String result = response.getString("result");
                                if(result.equals("400")){
                                    setUpPersonalOrderHis(viewPager, index);
                                    return;
                                }

                                JSONArray jsonArray = response.getJSONArray("orders");
                                Constants.orderHisModels = new ArrayList<OrderHisModel>();

                                for (int k= 0; k < jsonArray.length() ; k++){

                                    JSONObject object = jsonArray.getJSONObject(k);
                                    OrderHisModel orderHisModel = new OrderHisModel();
                                    orderHisModel.orderId = object.getString("id");
                                    orderHisModel.trackId = object.getString("track");
                                    orderHisModel.payment = object.getString("payment");
                                    orderHisModel.accepted_by = object.getString("accepted_by");

                                    orderHisModel.dateModel.date = object.getString("date");
                                    orderHisModel.dateModel.time = object.getString("time");

                                    orderHisModel.serviceModel.name = object.getString("service_name");
                                    orderHisModel.serviceModel.price = object.getString("service_price");
                                    orderHisModel.serviceModel.time_in = object.getString("service_timein");

                                    orderHisModel.state = object.getString("state");
                                    orderHisModel.is_quote_request = object.getString("is_quote_request");
                                    orderHisModel.price = object.getString("price");
                                    orderHisModel.paymentId = object.getString("transaction_id");

                                    try{
                                        JSONArray addressArray = object.getJSONArray("address");
                                        for(int i = 0; i < addressArray.length(); i++){
                                            JSONObject addressObj = addressArray.getJSONObject(i);
                                            orderHisModel.addressModel.sourceAddress = addressObj.getString("s_address");
                                            orderHisModel.addressModel.sourceArea =  addressObj.getString("s_area");
                                            orderHisModel.addressModel.sourceCity = addressObj.getString("s_city");
                                            orderHisModel.addressModel.sourceState = addressObj.getString("s_state");
                                            orderHisModel.addressModel.sourcePinCode = addressObj.getString("s_pincode");
                                            try{
                                                String res = addressObj.getString("s_phone");
                                                orderHisModel.addressModel.sourcePhonoe = res;
                                                String[] resArray = res.split(":");
                                                if(resArray.length == 2){
                                                    orderHisModel.addressModel.sourcePhonoe = resArray[0];
                                                    orderHisModel.addressModel.senderName = resArray[1];
                                                }
                                            }catch (Exception e){};
                                            //orderHisModel.addressModel.sourcePhonoe = addressObj.getString("s_phone");
                                            orderHisModel.addressModel.sourceLandMark = addressObj.getString("s_landmark");
                                            orderHisModel.addressModel.sourceInstruction = addressObj.getString("s_instruction");
                                            orderHisModel.addressModel.sourceLat = addressObj.getDouble("s_lat");
                                            orderHisModel.addressModel.sourceLng = addressObj.getDouble("s_lng");

                                            orderHisModel.addressModel.desAddress = addressObj.getString("d_address");
                                            orderHisModel.addressModel.desArea = addressObj.getString("d_area");
                                            orderHisModel.addressModel.desCity = addressObj.getString("d_city");
                                            orderHisModel.addressModel.desState = addressObj.getString("d_state");
                                            orderHisModel.addressModel.desPinCode = addressObj.getString("d_pincode");
                                            orderHisModel.addressModel.desLandMark = addressObj.getString("d_landmark");
                                            orderHisModel.addressModel.desInstruction = addressObj.getString("d_instruction");
                                            orderHisModel.addressModel.desLat = addressObj.getDouble("d_lat");
                                            orderHisModel.addressModel.desLng = addressObj.getDouble("d_lng");
                                            orderHisModel.addressModel.desPhone = addressObj.getString("d_phone");
                                            orderHisModel.addressModel.desName = addressObj.getString("d_name");
                                        }

                                    }catch (Exception e){};


                                    try{
                                        JSONArray productArray = object.getJSONArray("products");
                                        orderHisModel.orderModel = new OrderModel();
                                        orderHisModel.orderModel.itemModels = new ArrayList<ItemModel>();

                                        for(int j = 0; j < productArray.length(); j++){
                                            JSONObject productObj = productArray.getJSONObject(j);
                                            int product_type = productObj.getInt("product_type");

                                            if(product_type == Constants.CAMERA_OPTION){
                                                orderHisModel.orderModel.product_type = product_type;

                                                ItemModel model = new ItemModel();
                                                model.image = productObj.getString("image");
                                                model.quantity  = productObj.getString("quantity");
                                                model.weight = productObj.getString("weight");

                                                orderHisModel.orderModel.itemModels.add(model);

                                            }else if(product_type == Constants.ITEM_OPTION){
                                                orderHisModel.orderModel.product_type = product_type;
                                                ItemModel model = new ItemModel();
                                                model.title = productObj.getString("title");

                                                model.quantity  = productObj.getString("quantity");
                                                model.weight = productObj.getString("weight");
                                                try{
                                                    String[] dim = productObj.getString("dimension").split("X");
                                                    model.dimension1 = dim[0];
                                                    model.dimension2 = dim[1];
                                                    model.dimension3 = dim[2];
                                                }catch (Exception e){};
                                                orderHisModel.orderModel.itemModels.add(model);

                                            }else if(product_type == Constants.PACKAGE_OPTION){
                                                orderHisModel.orderModel.product_type = product_type;
                                                ItemModel model = new ItemModel();
                                                model.title = productObj.getString("title");
                                                model.quantity  = productObj.getString("quantity");
                                                model.weight = productObj.getString("weight");
                                                orderHisModel.orderModel.itemModels.add(model);
                                            }
                                        }
                                    }catch (Exception e){
                                        String err  = e.toString();
                                    }
                                    Constants.orderHisModels.add(orderHisModel);
                                }
                                setUpPersonalOrderHis(viewPager, index);

                            }else if(response.getString("result").equals("400")){
                                Toast.makeText(MainActivity.this , getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    };
                    public void onFinish() {
                        hideProgressDialog();
                    }
                    ;
                });
    }



    private void orderCorHistory() {
        Constants.orderCorporateHisModels.clear();
        RequestParams params = new RequestParams();
        params.put("user_id", PreferenceUtils.getCorporateUserId(MainActivity.this));
        WebClient.post(Constants.BASE_URL_ORDER + "get_corporate_orders_his", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        showProgressDialog();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        // Log.d("Error String", responseString);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        // Log.d("Error", errorResponse.toString());
                    }
                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {
                        try {
                            if (response != null) {

                                String result = response.getString("result");
                                if(result.equals("400")){
                                    setUpCorporateOrderHis(viewPager);
                                    return;
                                }

                                JSONArray jsonArray = response.getJSONArray("orders");
                                Constants.orderCorporateHisModels.clear();

                                for (int k= 0; k < jsonArray.length() ; k++){

                                    OrderCorporateHisModel orderCorporateHisModel = new OrderCorporateHisModel();
                                    try{

                                        JSONObject object = jsonArray.getJSONObject(k);

                                        orderCorporateHisModel.orderId = object.getString("id");
                                        orderCorporateHisModel.trackId = object.getString("track");
                                        orderCorporateHisModel.accepted_by = object.getString("accepted_by");

                                        orderCorporateHisModel.deliver = object.getString("description");
                                        orderCorporateHisModel.loadType = object.getString("loadtype");

                                        orderCorporateHisModel.dateModel.date = object.getString("date");
                                        orderCorporateHisModel.dateModel.time = object.getString("time");

                                        orderCorporateHisModel.serviceModel.name = object.getString("service_name");
                                        orderCorporateHisModel.serviceModel.price = object.getString("service_price");
                                        orderCorporateHisModel.serviceModel.time_in = object.getString("service_timein");

                                        orderCorporateHisModel.state = object.getString("state");
                                        orderCorporateHisModel.payment = object.getString("payment");
                                        orderCorporateHisModel.receiver_signature = object.getString("receiver_signature");

                                        JSONArray addressArray = object.getJSONArray("addresses");

                                        for(int i = 0; i < addressArray.length(); i++){
                                            JSONObject addressObj = addressArray.getJSONObject(i);
                                            orderCorporateHisModel.addressModel.sourceAddress = addressObj.getString("s_address");
                                            orderCorporateHisModel.addressModel.sourceCity = addressObj.getString("s_city");
                                            orderCorporateHisModel.addressModel.sourceState = addressObj.getString("s_state");
                                            orderCorporateHisModel.addressModel.sourcePinCode = addressObj.getString("s_pincode");
                                            try{
                                                String res = addressObj.getString("s_phone");
                                                orderCorporateHisModel.addressModel.sourcePhonoe = res;
                                                String[] resArray = res.split(":");
                                                if(resArray.length == 2){
                                                    orderCorporateHisModel.addressModel.sourcePhonoe = resArray[0];
                                                    orderCorporateHisModel.addressModel.senderName = resArray[1];
                                                }
                                            }catch (Exception e){};

                                            orderCorporateHisModel.addressModel.sourceLandMark = addressObj.getString("s_landmark");
                                            orderCorporateHisModel.addressModel.sourceInstruction = addressObj.getString("s_instruction");

                                            orderCorporateHisModel.addressModel.desAddress = addressObj.getString("d_address");
                                            orderCorporateHisModel.addressModel.desCity = addressObj.getString("d_city");
                                            orderCorporateHisModel.addressModel.desState = addressObj.getString("d_state");

                                            orderCorporateHisModel.addressModel.desPinCode = addressObj.getString("d_pincode");
                                            orderCorporateHisModel.addressModel.desLandMark = addressObj.getString("d_landmark");
                                            orderCorporateHisModel.addressModel.desInstruction = addressObj.getString("d_instruction");
                                            orderCorporateHisModel.addressModel.desPhone = addressObj.getString("d_phone");
                                            orderCorporateHisModel.addressModel.desName = addressObj.getString("d_name");
                                        }

                                        JSONArray productArray = object.getJSONArray("carrier");
                                        orderCorporateHisModel.carrierModel = new CarrierModel();
                                        for(int j = 0; j < productArray.length(); j++){
                                            JSONObject productObj = productArray.getJSONObject(j);
                                            orderCorporateHisModel.carrierModel.id = productObj.getString("id");
                                            orderCorporateHisModel.carrierModel.order_id = productObj.getString("order_id");
                                            orderCorporateHisModel.carrierModel.freight = productObj.getString("freight");
                                            orderCorporateHisModel.carrierModel.load_type = productObj.getString("load_type");
                                            orderCorporateHisModel.carrierModel.consignment = productObj.getString("consignment");
                                            orderCorporateHisModel.carrierModel.date = productObj.getString("date");
                                            orderCorporateHisModel.carrierModel.time = productObj.getString("time");
                                            orderCorporateHisModel.carrierModel.vehicle = productObj.getString("vehicle");
                                            orderCorporateHisModel.carrierModel.driver_id = productObj.getString("driver_id");
                                            orderCorporateHisModel.carrierModel.driver_name = productObj.getString("driver_name");
                                            orderCorporateHisModel.carrierModel.signature = productObj.getString("signature");
                                        }

                                    }catch (Exception e){};
                                    Constants.orderCorporateHisModels.add(orderCorporateHisModel);
                                }
                                setUpCorporateOrderHis(viewPager);

                            }else if(response.getString("result").equals("400")){
                                Toast.makeText(MainActivity.this , getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    };
                    public void onFinish() {
                        hideProgressDialog();
                    }
                    ;
                });
    }

    private void autoLogin() {
        showProgressDialog(getString(R.string.wait_your_profile));
        new GetBasic(this, "").execute();
    }
    public void getCities(){
        new GetCity(this, "").execute();
    }

}
