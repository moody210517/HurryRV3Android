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
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.view.activity.CameraOrderActivity;
import com.hurry.custom.view.dialog.ChooseTypeDialog;


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hurry.custom.view.activity.HomeActivity.INTENT_REQUEST_GET_IMAGES;

public class HomeFragment extends BaseFragment implements  View.OnClickListener{


    Context mContext;
    @BindView(R.id.rl_station) RelativeLayout rlStation;
    @BindView(R.id.rl_city) RelativeLayout rlCity;
    @BindView(R.id.rl_international) RelativeLayout rlInternational;
    @BindView(R.id.img_local) ImageView imgLocal;
    @BindView(R.id.txt_location_name) TextView txtLocationName;

    private static final int INITIAL_REQUEST=1337;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         view = (View) inflater.inflate(
                R.layout.fragment_home, container, false);
        position = 2;
        mContext = getActivity();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        initView();
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
                        Intent intent = new Intent(getActivity(), CameraOrderActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {
            }
        }
    }

    private void initView(){
        rlStation.setOnClickListener(this);
        rlCity.setOnClickListener(this);
        rlInternational.setOnClickListener(this);

        if(!PreferenceUtils.getCityName(mContext).isEmpty() && PreferenceUtils.getCityId(mContext) != -1){
            try{
                String sub[] = PreferenceUtils.getCityName(mContext).split("-");
                txtLocationName.setText(sub[0]);
            }catch (Exception e){};
        }

        Glide.with(mContext)
                .load(Constants.PHOTO_URL + "employer/" +  Constants.cityModels.get(PreferenceUtils.getCityId(mContext)).image )
                .dontAnimate()
                .centerCrop()
                .error(com.gun0912.tedpicker.R.drawable.no_image)
                .into(imgLocal);
    }

    public void updateImage(){

        if(mContext != null && imgLocal != null){

            if(!PreferenceUtils.getCityName(mContext).isEmpty() && PreferenceUtils.getCityId(mContext) != -1){
                try{
                    String sub[] = PreferenceUtils.getCityName(mContext).split("-");
                    txtLocationName.setText(sub[0]);
                }catch (Exception e){};
            }

            Glide.with(mContext)
                    .load(Constants.PHOTO_URL + "employer/" +  Constants.cityModels.get(PreferenceUtils.getCityId(mContext)).image )
                    .diskCacheStrategy(DiskCacheStrategy.NONE )
                    .dontAnimate()
                    .centerCrop()
                    .error(com.gun0912.tedpicker.R.drawable.no_image)
                    .into(imgLocal);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_station:
                if(PreferenceUtils.getCityId(mContext) != -1){
                    Constants.DELIVERY_STATUS = Constants.OUT_STATION;
                    ChooseTypeDialog.show(getActivity(), "station");
                }
                break;

            case R.id.rl_city:
                if(PreferenceUtils.getCityId(mContext) != -1){
                    Constants.DELIVERY_STATUS = Constants.SAME_CITY;
                    ChooseTypeDialog.show(getActivity(), "city");
                }
                break;

            case R.id.rl_international:
                if(PreferenceUtils.getCityId(mContext) != -1){
                    Constants.DELIVERY_STATUS = Constants.INTERNATIONAL;
                    ChooseTypeDialog.show(getActivity(), "international");
                }
                break;
        }

    }

}
