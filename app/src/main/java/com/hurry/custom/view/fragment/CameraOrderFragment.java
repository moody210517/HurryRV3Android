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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gun0912.tedpicker.Config;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.utils.ImageLoaderHelper;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.view.activity.CameraOrderActivity;
import com.hurry.custom.view.activity.HomeActivity;
import org.angmarch.views.NiceSpinner;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraOrderFragment extends Fragment implements View.OnClickListener{

    Context mContext;
    public static int MAX = 5;
    @BindView(R.id.lin_container)  LinearLayout linContainer;
    @BindView(R.id.lin_item) LinearLayout  linItem;
    @BindView(R.id.txt_upload_more) TextView txtMore;
    @BindView(R.id.txt_exceed) TextView txtExceed;
    @BindView(R.id.btn_continue) Button btnContinue;
    int total = 0;

    String type = "";
    public CameraOrderFragment(String type){
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(
                R.layout.fragment_camera_order, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        Constants.page_type = "camera";
        initView();
        return view;
    }

    public void initView(){
        linItem.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int k = 0; k < Constants.cameraOrderModel.itemModels.size() ; k++){
            ItemModel itemModel = Constants.cameraOrderModel.itemModels.get(k);
            View v = layoutInflater.inflate(R.layout.row_camera_item, null);

            ImageView img = (ImageView)v.findViewById(R.id.image);
            ImageLoaderHelper.showImageFromLocal(mContext ,itemModel.image, img);
            final int finalK1 = k;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Constants.showImage(mContext, linContainer,  Constants.cameraOrderModel.itemModels.get(finalK1).image, "local");
                    }catch (Exception e){};

                }
            });
            EditText edtPackageCost = (EditText)v.findViewById(R.id.edt_cost);
            edtPackageCost.setText(itemModel.package_cost);
            edtPackageCost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Constants.cameraOrderModel.itemModels.get(finalK1).package_cost = edtPackageCost.getText().toString();
                }
            });

            TextView txtRemove = (TextView)v.findViewById(R.id.txt_remove);
            LinearLayout linRemove = (LinearLayout)v.findViewById(R.id.lin_remove);
            linRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Constants.cameraOrderModel.itemModels.size() > finalK1 ){
                        ItemModel model = Constants.cameraOrderModel.itemModels.get(finalK1);
                        Constants.cameraOrderModel.itemModels.remove(model);
                        initView();
                    }
                }
            });

            final CheckBox chkPackage = (CheckBox)v.findViewById(R.id.chk_package);
            chkPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chkPackage.isChecked()){
                        Constants.cameraOrderModel.itemModels.get(finalK1).mPackage = "1";
                    }else{
                        Constants.cameraOrderModel.itemModels.get(finalK1).mPackage = "0";
                    }
                }
            });

            final NiceSpinner spQuantity = (NiceSpinner)v.findViewById(R.id.sp_quantity);
            //spQuantity.type = 1;
            spQuantity.setPadding(20, 10,10,10);
            final NiceSpinner spWeight = (NiceSpinner)v.findViewById(R.id.sp_weight);
            //spWeight.type = 1;
            spWeight.setPadding(20, 10,10,10);

            spQuantity.attachDataSource(Constants.quantity);
            spWeight.attachDataSource(Constants.weight);

            spQuantity.setText(itemModel.quantity);
            spWeight.setText(itemModel.weight);

            if(itemModel.mPackage.equals("1")){
                chkPackage.setChecked(true);
            }

            final int finalK = k;
            spQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try{
                        Constants.cameraOrderModel.itemModels.get(finalK).quantity = Constants.quantity.get(position);
                    }catch (Exception e){};
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            spWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Constants.cameraOrderModel.itemModels.get(finalK).weight = Constants.weight.get(position);
                    Constants.cameraOrderModel.itemModels.get(finalK).weight_value = Constants.weight_value.get(position);

                    total = 0;
                    for(int k = 0; k <  Constants.cameraOrderModel.itemModels.size() ; k++){
                        total += Constants.cameraOrderModel.itemModels.get(k).weight_value;
                    }
                    if(total > 10){
                        //visibleExceed(
                        // );
                    }else{

                    }

                    txtExceed.setVisibility(View.GONE);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            linItem.addView(v);
        }

        txtMore.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        if(Constants.cameraOrderModel != null && Constants.cameraOrderModel.itemModels.size() == CameraOrderActivity.MAX){
            txtMore.setVisibility(View.GONE);
        }else{
            txtMore.setVisibility(View.VISIBLE);
        }
        total = 0;
        for(int k = 0; k <  Constants.cameraOrderModel.itemModels.size() ; k++){
            total += Constants.cameraOrderModel.itemModels.get(k).weight_value;
        }
        if(total > 10){
            //visibleExceed();
        }else{
            txtExceed.setVisibility(View.GONE);
        }
        txtExceed.setVisibility(View.GONE);
    }

    private  void visibleExceed(){
        txtExceed.setVisibility(View.VISIBLE);
        if(Constants.MODE == Constants.GUEST){
            txtExceed.setText(getResources().getString(R.string.carmera_alert_guest));
        }else{
            txtExceed.setText(getResources().getString(R.string.camera_alert));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_upload_more:
                getImages(new Config());
                break;
            case R.id.btn_continue:
                if(txtExceed.getVisibility() == View.VISIBLE && Constants.MODE == Constants.GUEST){
                    Toast.makeText(mContext, "Please Sign In", Toast.LENGTH_SHORT).show();
                }else{
                    if(Constants.cameraOrderModel.itemModels != null &&  Constants.cameraOrderModel.itemModels.size() > 0){
                        if(txtExceed.getVisibility() == View.VISIBLE){
                            ((HomeActivity)mContext).updateFragment(HomeActivity.ADDRESS_DETAILS, "exceed");
//                            Intent intent = new Intent(mContext, AddressDetailsNewActivity.class);
//                            intent.putExtra("type", "exceed");
//                            startActivity(intent);
                        }else{
                            ((HomeActivity)mContext).updateFragment(HomeActivity.ADDRESS_DETAILS, "");
//                            Intent intent = new Intent(mContext, AddressDetailsNewActivity.class);
//                            startActivity(intent);
                        }
                    }else{
                        Toast.makeText(mContext, getResources().getString(R.string.input_all), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void getImages(Config config) {

        ((HomeActivity)mContext).goToCameraPage("from_page");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){


        }
    }

}
