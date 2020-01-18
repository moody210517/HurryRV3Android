/*
 * Copyright (c) 2016. Ted Park. All Rights Reserved
 */

package com.gun0912.tedpicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.google.android.material.tabs.TabLayout;
import com.gun0912.tedpicker.custom.adapter.SpacesItemDecoration;
import com.gun0912.tedpicker.util.Util;
import java.util.ArrayList;

public class ImagePickerActivity extends AppCompatActivity implements CameraHostProvider {

    /**
     * Returns the parcelled image uris in the intent with this extra.
     */
    public static final String EXTRA_IMAGE_URIS = "image_uris";
    public static CwacCameraFragment.MyCameraHost mMyCameraHost;
    // initialize with default config.
    private static Config mConfig = new Config();
    /**
     * Key to persist the list when saving the state of the activity.
     */

    public ArrayList<Uri> mSelectedImages;
    protected Toolbar toolbar;
    View view_root;
    TextView mSelectedImageEmptyMessage;
    View view_selected_photos_container;
    RecyclerView rc_selected_photos;
    TextView tv_selected_title;
    ViewPager mViewPager;
    TabLayout tabLayout;
    PagerAdapter_Picker adapter;
    Adapter_SelectedPhoto adapter_selectedPhoto;


    public static Config getConfig() {
        return mConfig;
    }

    public static void setConfig(Config config) {

        if (config == null) {
            throw new NullPointerException("Config cannot be passed null. Not setting config will use default values.");
        }
        mConfig = config;
    }

    @Override
    public CameraHost getCameraHost() {
        return mMyCameraHost;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFromSavedInstanceState(savedInstanceState);
        setContentView(R.layout.picker_activity_main_pp);
        initView();
        setTitle("Photo Upload"); //mConfig.getToolbarTitleRes()
        setupTabs();
        setSelectedPhotoRecyclerView();
//        showConfirmDialog();
        showAlertdialog();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        TextView txtUpload = (TextView)findViewById(R.id.txt_upload);
        txtUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePicture();
            }
        });

        Config config = new Config();
        config.setSelectionLimit(getLimit());
        if(isTablet(this)){
            config.setCameraHeight(R.dimen.camera_tablet);
        }else{
            config.setCameraHeight(R.dimen.camera_phone);
        }
        setConfig(config);
    }

    private int getLimit(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            return bundle.getInt("limit");
        }
        return 5;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        view_root = findViewById(R.id.view_root);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tv_selected_title = (TextView) findViewById(R.id.tv_selected_title);

        rc_selected_photos = (RecyclerView) findViewById(R.id.rc_selected_photos);
        mSelectedImageEmptyMessage = (TextView) findViewById(R.id.selected_photos_empty);

        view_selected_photos_container = findViewById(R.id.view_selected_photos_container);
        view_selected_photos_container.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view_selected_photos_container.getViewTreeObserver().removeOnPreDrawListener(this);

                int selected_bottom_size = (int) getResources().getDimension(mConfig.getSelectedBottomHeight());

                ViewGroup.LayoutParams params = view_selected_photos_container.getLayoutParams();
                params.height = selected_bottom_size;
                view_selected_photos_container.setLayoutParams(params);
                return true;
            }
        });


        if (mConfig.getSelectedBottomColor() > 0) {
            tv_selected_title.setBackgroundColor(ContextCompat.getColor(this, mConfig.getSelectedBottomColor()));
            mSelectedImageEmptyMessage.setTextColor(ContextCompat.getColor(this, mConfig.getSelectedBottomColor()));
        }


    }

    private void setupFromSavedInstanceState(Bundle savedInstanceState) {


        if (savedInstanceState != null) {
            mSelectedImages = savedInstanceState.getParcelableArrayList(EXTRA_IMAGE_URIS);
        } else {
            mSelectedImages = getIntent().getParcelableArrayListExtra(EXTRA_IMAGE_URIS);
        }


        if (mSelectedImages == null) {
            mSelectedImages = new ArrayList<>();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mSelectedImages != null) {
            outState.putParcelableArrayList(EXTRA_IMAGE_URIS, mSelectedImages);
        }

    }

    private void setupTabs() {
        adapter = new PagerAdapter_Picker(this, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);


        if (mConfig.getTabBackgroundColor() > 0)
            tabLayout.setBackgroundColor(ContextCompat.getColor(this, mConfig.getTabBackgroundColor()));

        if (mConfig.getTabSelectionIndicatorColor() > 0)
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, mConfig.getTabSelectionIndicatorColor()));

    }

    private void setSelectedPhotoRecyclerView() {


        LinearLayoutManager mLayoutManager_Linear = new LinearLayoutManager(this);
        mLayoutManager_Linear.setOrientation(LinearLayoutManager.HORIZONTAL);

        rc_selected_photos.setLayoutManager(mLayoutManager_Linear);
        rc_selected_photos.addItemDecoration(new SpacesItemDecoration(Util.dpToPx(this, 5), SpacesItemDecoration.TYPE_VERTICAL));
        rc_selected_photos.setHasFixedSize(true);

        int closeImageRes = mConfig.getSelectedCloseImage();

        adapter_selectedPhoto = new Adapter_SelectedPhoto(this, closeImageRes);
        adapter_selectedPhoto.updateItems(mSelectedImages);
        rc_selected_photos.setAdapter(adapter_selectedPhoto);


        if (mSelectedImages.size() >= 1) {
            mSelectedImageEmptyMessage.setVisibility(View.GONE);
        }

    }


    public GalleryFragment getGalleryFragment() {

        if (adapter == null || adapter.getCount() < 2)
            return null;

        return (GalleryFragment) adapter.getItem(1);

    }

    public void addImage(final Uri uri) {


        if (mSelectedImages.size() == mConfig.getSelectionLimit()) {
            String text = String.format(getResources().getString(R.string.max_count_msg), mConfig.getSelectionLimit());
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            return;
        }

        for(int k = 0; k < mSelectedImages.size() ; k++){
            if(mSelectedImages.get(k).equals(uri)){
                Toast.makeText(this, "Select Other One", Toast.LENGTH_SHORT).show();
                return;
            }
        }
//
//        File file = new File(uri.getPath());
//        File compressedImageFile = null;
//
//        long fileSizeInBytes = file.length();
//        long fileSizeInKB = fileSizeInBytes / 1024;
//        if(fileSizeInKB < 500){
//            compressedImageFile = file;
//        }else if(fileSizeInKB < 1000) {
//            try {
//                compressedImageFile = new Compressor(this)
//                        .setQuality(85)
//                        .setCompressFormat(Bitmap.CompressFormat.PNG)
//                        .compressToFile(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }else{
//            try {
//                compressedImageFile = new Compressor(this)
//                        .setQuality(75)
//                        .setCompressFormat(Bitmap.CompressFormat.PNG)
//                        .compressToFile(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        mSelectedImages.add(uri);
        adapter_selectedPhoto.updateItems(mSelectedImages);

        if (mSelectedImages.size() >= 1) {
            mSelectedImageEmptyMessage.setVisibility(View.GONE);
        }

        rc_selected_photos.smoothScrollToPosition(adapter_selectedPhoto.getItemCount()-1);
    }

    public void removeImage(Uri uri) {

        mSelectedImages.remove(uri);
        adapter_selectedPhoto.updateItems(mSelectedImages);

        if (mSelectedImages.size() == 0) {
            mSelectedImageEmptyMessage.setVisibility(View.VISIBLE);
        }
        GalleryFragment.mGalleryAdapter.notifyDataSetChanged();
    }

    public boolean containsImage(Uri uri) {
        return mSelectedImages.contains(uri);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_confirm, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_done) {
            updatePicture();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePicture() {

        if (mSelectedImages.size() < mConfig.getSelectionMin()) {
            String text = String.format(getResources().getString(R.string.min_count_msg), mConfig.getSelectionMin());
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_IMAGE_URIS, mSelectedImages);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }



    private void showConfirmDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_forgot);
        // dialog.setTitle("Title...");
        TextView txtTitle = (TextView)dialog.findViewById(R.id.txt_title);
        TextView txtContent  = (TextView)dialog.findViewById(R.id.txt_content);

        txtTitle.setText("Notification");
        txtContent.setText(getResources().getString(R.string.camera_alarm));

        Button btnOk = (Button)dialog.findViewById(R.id.btn_ok);
        btnOk.setText("I Agree");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private  void showAlertdialog(){
        try{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            //builder1.setMessage("Write your message here.");
            builder1.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = (View)inflater.inflate(R.layout.dialog_forgot, null);
            builder1.setView(view);
            final AlertDialog alert11 = builder1.create();


            TextView txtTitle = (TextView)view.findViewById(R.id.txt_title);
            TextView txtContent  = (TextView)view.findViewById(R.id.txt_content);

            txtTitle.setText("Notification");
            txtContent.setText(getResources().getString(R.string.camera_alarm));

            Button btnOk = (Button)view.findViewById(R.id.btn_ok);
            btnOk.setText("I Agree");
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert11.hide();
                }
            });
            alert11.getWindow().setLayout(600, 600);
            alert11.show();
        }catch (Exception e){

        }


    }

}
