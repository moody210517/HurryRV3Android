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

package com.hurry.custom.view.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.view.activity.CameraOrderActivity;
import com.hurry.custom.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class HomePersonalFragment extends Fragment implements  SurfaceHolder.Callback,  View.OnClickListener{

    LinearLayout linCamera;
    Button btnCamera;
    Button btnItem;
    Button btnPackage;
    //NiceSpinner spPackage;

    LinearLayout rlCamera;
    LinearLayout rlItem;
    RelativeLayout rlPackage;

    Context  mContext;
    MediaPlayer mMediaPlayer;
    Uri mVideoUri;

    EditText edtCake;
    TextView txtOrder, txtSend;
    LinearLayout linOrder, linSend;

    private SurfaceHolder mFirstSurface;
    private static final String[] INITIAL_PERMS={
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final int INITIAL_REQUEST=1337;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = (View) inflater.inflate(
                R.layout.fragment_home_personal, container, false);
        mContext = getActivity();

        PreferenceUtils.setQuote(mContext, false);
        initView(view);

        DeviceUtil.setupUICloseKeyBoardOnClick(getActivity(), view.findViewById(R.id.lin_container));
        DeviceUtil.hideSoftKeyboard(getActivity());
        return view;
    }

    ProgressBar progressBar;
    private void  initProgressBar(View view){
        progressBar = (ProgressBar)view.findViewById(R.id.progressbar);
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_preparen));

    }
    int totalTime = 20;
    private class SeekBarTimer extends CountDownTimer {

        public SeekBarTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            progressBar.setMax((int)totalTime);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            progressBar.setProgress(totalTime - (int)(millisUntilFinished/1000 - 1));
        }

        @Override
        public void onFinish() {

        }
    }


    private void initView(View view){

        TextView txtOption1 = (TextView)view.findViewById(R.id.txt_option1);
        TextView txtOption2 = (TextView)view.findViewById(R.id.txt_option2);
        DeviceUtil.setVerdana(mContext, txtOption1);
        DeviceUtil.setVerdana(mContext, txtOption2);

        initProgressBar(view);
        SeekBarTimer seekbarTimer = new SeekBarTimer(
                (totalTime) * 1000, 1000);
        seekbarTimer.start();

        linCamera = (LinearLayout) view.findViewById(R.id.lin_camera);
        btnCamera = (Button)view.findViewById(R.id.btn_camera);
        btnItem = (Button)view.findViewById(R.id.btn_item);
        btnPackage = (Button)view.findViewById(R.id.btn_package);
        edtCake = (EditText)view.findViewById(R.id.edt_cake);
        edtCake.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edtCake.getText().toString().length()> 0 && !edtCake.getText().toString().trim().isEmpty()){
                    btnPackage.setVisibility(View.VISIBLE);
                }else{
                    btnPackage.setVisibility(View.GONE);
                }
            }
        });

        edtCake.requestFocus();
        txtSend = (TextView)view.findViewById(R.id.txt_send);
        txtOrder= (TextView)view.findViewById(R.id.txt_order);
        linSend  = (LinearLayout)view.findViewById(R.id.lin_send);
        linSend.setFocusable(true);
        linOrder = (LinearLayout)view.findViewById(R.id.lin_order);
        txtSend.setOnClickListener(this);
        txtOrder.setOnClickListener(this);

        //spPackage = (NiceSpinner)view.findViewById(R.id.sp_package);
        List<String> packageLists =  new ArrayList<>();
        packageLists.add("Small Package");
        packageLists.add("Medium Package");
        //spPackage.attachDataSource(packageLists);
        btnCamera.setOnClickListener(this);
        btnItem.setOnClickListener(this);
        btnPackage.setOnClickListener(this);

        rlCamera =  (LinearLayout)view.findViewById(R.id.rl_camera);
        rlItem =  (LinearLayout)view.findViewById(R.id.rl_item);
        rlPackage =  (RelativeLayout)view.findViewById(R.id.rl_package);
        view.findViewById(R.id.lin_webview).setOnClickListener(this);
        rlCamera.setOnClickListener(this);
        rlItem.setOnClickListener(this);
        rlPackage.setOnClickListener(this);

        WebView mWebView = (WebView) view.findViewById(R.id.webview);
        mWebView.setOnClickListener(this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        String summary = "<html><body><MARQUEE><font size='3'>"+" Documents &nbsp&nbsp&nbsp&nbsp&nbsp    Flowers  &nbsp&nbsp&nbsp&nbsp&nbsp   Small Wooden Book Shelf  &nbsp&nbsp&nbsp&nbsp&nbsp   LED TV without box   &nbsp&nbsp&nbsp&nbsp&nbsp  Large Suitcase  &nbsp&nbsp&nbsp&nbsp&nbsp "+"</font></MARQUEE></body></html>";
        mWebView.loadData(summary, "text/html", "utf-8");
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Constants.ORDER_TYPE = Constants.ITEM_OPTION;
                ((MainActivity)mContext).updateFragment(MainActivity.ITEM_OPTION);
                return false;
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_camera:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED ) {
                        Constants.ORDER_TYPE = Constants.CAMERA_OPTION;
                        getImages(new Config());
                    } else {
                        requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
                    }
                }

                break;
            case R.id.btn_item:
                Constants.ORDER_TYPE = Constants.ITEM_OPTION;
                ((MainActivity)mContext).updateFragment(MainActivity.ITEM_OPTION);
                break;

            case R.id.rl_camera:

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED ) {
                        Constants.ORDER_TYPE = Constants.CAMERA_OPTION;
                        getImages(new Config());
                    } else {
                        requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
                    }
                }
                break;
            case R.id.lin_webview:
            case R.id.webview:
            case R.id.rl_item:
                Constants.ORDER_TYPE = Constants.ITEM_OPTION;
                ((MainActivity)mContext).updateFragment(MainActivity.ITEM_OPTION);
                break;
            case R.id.rl_package:
                Constants.ORDER_TYPE = Constants.PACKAGE_OPTION;
                ((MainActivity)mContext).updateFragment(MainActivity.PACKAGE_OPTION);
                break;
            case R.id.txt_send:
                if(linSend.getVisibility() == View.VISIBLE){
                    linSend.setVisibility(View.GONE);
                }else{
                    linSend.setVisibility(View.VISIBLE);
                }
                linOrder.setVisibility(View.VISIBLE);
                break;
            case R.id.txt_order:
                if(linOrder.getVisibility() == View.VISIBLE){
                    linOrder.setVisibility(View.GONE);
                    edtCake.setEnabled(true);
                    edtCake.setFocusable(true);
                }else{

                    linOrder.setVisibility(View.VISIBLE);
                }
                linSend.setVisibility(View.VISIBLE);
                break;
        }
    }


    private void getImages(Config config) {
        config.setSelectionLimit(CameraOrderActivity.MAX - Constants.cameraOrderModel.itemModels.size());
        if(DeviceUtil.isTablet(mContext)){
            config.setCameraHeight(R.dimen.camera_tablet);
        }else{
            config.setCameraHeight(R.dimen.camera_phone);
        }
        //ImagePickerActivity.setConfig(config);
        Intent intent = new Intent(mContext, ImagePickerActivity.class);
        intent.putExtra("limit", CameraOrderActivity.MAX - Constants.cameraOrderModel.itemModels.size());
        ((MainActivity)mContext).startActivityForResult(intent, MainActivity.INTENT_REQUEST_GET_IMAGES);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mFirstSurface = surfaceHolder;
        if (mVideoUri != null) {
            mMediaPlayer = MediaPlayer.create(mContext,
                    mVideoUri, mFirstSurface);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INITIAL_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED ) {
                        Constants.ORDER_TYPE = Constants.CAMERA_OPTION;
                        getImages(new Config());
                    }
                } else {
                    Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
