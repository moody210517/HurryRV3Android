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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.hurry.custom.MyApplication;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.common.utils.ImageLoaderHelper;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.payumoney.AppEnvironment;
import com.hurry.custom.payumoney.AppPreference;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.OrderConfirmNewActivity;
import com.hurry.custom.view.activity.map.TouchMapActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import org.angmarch.views.NiceSpinner;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewFragment extends Fragment implements View.OnClickListener{
    Context mContext;

    @BindView(R.id.lin_container)
    LinearLayout linContainer;
    @BindView(R.id.edt_sender) TextView txtSenderName;
    @BindView(R.id.edt_address) TextView edtPickAddress;

    @BindView(R.id.edt_phone) TextView edtPickPhone;
    @BindView(R.id.edt_landmark) TextView edtPickLandMark;
    @BindView(R.id.edt_instruction) TextView edtPickInstrunction;

    @BindView(R.id.edt_address_des) TextView edtDesAddress;
    @BindView(R.id.edt_landmark_des) TextView edtDesLandmark;
    @BindView(R.id.edt_instruction_des) TextView edtDesInstruction;
    @BindView(R.id.edt_phone_des) TextView edtDesPhone;
    @BindView(R.id.edt_des_name) TextView edtDesName;

    @BindView(R.id.lin_item) LinearLayout linItem;
    @BindView(R.id.lin_column) LinearLayout linColumn;
    @BindView(R.id.lin_order_detail) LinearLayout linOrderDetails;

    @BindView(R.id.lin_details) LinearLayout linDetails;

    OrderModel orderModel ;
    @BindView(R.id.txt_date) TextView txtDate;
    @BindView(R.id.txt_service) TextView txtService;


    @BindView(R.id.btn_edit_product) Button btnProductEdit;
    @BindView(R.id.btn_edit_address) Button btnAddressEdit;
    @BindView(R.id.btn_edit_date) Button btnDateEdit;
    @BindView(R.id.btn_edit_service) Button btnServiceEdit;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_cash_on_pickup) TextView txtCash;
    @BindView(R.id.txt_pay_using_card) TextView txtPayUsingCard;
    @BindView(R.id.txt_cod) TextView txtCod;
    @BindView(R.id.txt_net_banking) TextView txtNetBanking;

    @BindView(R.id.rd_cash)
    RadioButton rdCash;
    @BindView(R.id.rd_cod) RadioButton rdCod;
    @BindView(R.id.rd_card) RadioButton rdCard;
    @BindView(R.id.rd_net_banking) RadioButton rdNetBanking;
    @BindView(R.id.btn_place_order) Button btnPlaceOrder;
    @BindView(R.id.btn_apply) Button btnApply;
    @BindView(R.id.sp_type)
    NiceSpinner spType;
    @BindView(R.id.img_sender) ImageView imgSender;
    @BindView(R.id.edt_coupon) EditText edtCoupon;
    @BindView(R.id.txt_price) TextView txtPrice;


    ImageView imgBottom;


    private AppPreference mAppPreference;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    private boolean isShow = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(
                R.layout.activity_review, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        mAppPreference = new AppPreference();

        initView();

        if(Constants.MODE != Constants.CORPERATION){
            showItemLists();
        }else{
            linOrderDetails.setVisibility(View.GONE);
            linColumn.setVisibility(View.GONE);
            linItem.setVisibility(View.GONE);
            linDetails.setVisibility(View.VISIBLE);
            TextView txtDeliver = (TextView)view.findViewById(R.id.txt_deliver);
            TextView txtLoadType = (TextView)view.findViewById(R.id.txt_load_type);
            txtDeliver.setText(Constants.delivery);
            txtLoadType.setText(Constants.loadType);
        }

        showAddressDetails();
        txtDate.setText("Pick up on: " + Constants.dateModel.date  + "  " + Constants.dateModel.time);
        txtService.setText("Service Level: " + Constants.serviceModel.name + ", " + Constants.getPrice(mContext, Constants.serviceModel.price) + ", " + Constants.serviceModel.time_in);

        return view;
    }


    private  void initView(){


        imgSender.setOnClickListener(this);
        txtCash.setOnClickListener(this);
        txtCod.setOnClickListener(this);
        txtPayUsingCard.setOnClickListener(this);
        txtNetBanking.setOnClickListener(this);
        btnPlaceOrder.setOnClickListener(this);
        btnApply.setOnClickListener(this);

        spType.setPadding(20, 10,10,10);

        btnProductEdit.setOnClickListener(this);
        btnAddressEdit.setOnClickListener(this);
        btnDateEdit.setOnClickListener(this);
        btnServiceEdit.setOnClickListener(this);
        // hide the edit button in the case of quote order.
        if(PreferenceUtils.getQuote(mContext)){
            btnProductEdit.setVisibility(View.GONE);
            btnAddressEdit.setVisibility(View.GONE);
        }


        if(PreferenceUtils.getQuote(mContext)){
            mPayment = "Pay using Card";
            spType.attachDataSource(Constants.quotePaymentWay);
        }else{
            if(Constants.DELIVERY_STATUS == Constants.SAME_CITY){
                spType.attachDataSource(Constants.paymentWay);
            }else{
                spType.attachDataSource(Constants.paymentWay1);
            }
        }

        spType.setSelectedIndex(0);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(PreferenceUtils.getQuote(mContext)){
                    if(position == 0){
                        //linPayment.setVisibility(View.VISIBLE);
                        btnPlaceOrder.setText("Pay now");
                        Constants.paymentType = "Pay using Card";
                        mPayment = "Pay using Card";
                    }else if(position == 1 ){
                        //linPayment.setVisibility(View.GONE);
                        btnPlaceOrder.setText("Pay now");
                        Constants.paymentType = "Pay using Bank";
                        mPayment ="Pay using Bank";
                    }

                }else{
                    if(position == 0){
                        //linPayment.setVisibility(View.VISIBLE);
                        btnPlaceOrder.setText("Pickup my order");
                        Constants.paymentType = "Cash On Pick up";
                        mPayment = "Cash On Pick up";

                    }else if(position == 1 ){
                        //linPayment.setVisibility(View.GONE);
                        btnPlaceOrder.setText("Pay now");
                        Constants.paymentType = "Pay using Card";
                        mPayment = "Pay using Card";

                    }else if(position == 2){
                        btnPlaceOrder.setText("Pay now");
                        Constants.paymentType = "Pay using Bank";
                        mPayment ="Pay using Bank";
                    }else if(position == 3){
                        btnPlaceOrder.setText("Pay now");
                        Constants.paymentType = "COD";
                        mPayment ="COD";
                    }
                }
                paymentType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if(Constants.serviceModel.price != null)
            txtPrice.setText(Constants.serviceModel.price);
    }

    private  void showItemLists(){
        linItem.removeAllViews();
        if(Constants.ORDER_TYPE == Constants.CAMERA_OPTION){
            orderModel = Constants.cameraOrderModel;
            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewCol = inflater.inflate(R.layout.column_camera, null);
            viewCol.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            imgBottom = (ImageView)viewCol.findViewById(R.id.img_bottom);
            imgBottom.setOnClickListener(this);
            if(isShow){
                imgBottom.setImageResource(R.mipmap.down);
            }else{
                imgBottom.setImageResource(R.mipmap.up);
            }
            linColumn.addView(viewCol);

            if(orderModel != null){
                for(int k = 0;  k < orderModel.itemModels.size(); k++){
                    View view = inflater.inflate(R.layout.row_review_camera, null);
                    ImageView img = (ImageView) view.findViewById(R.id.image);
                    TextView txtQuantity = (TextView)view.findViewById(R.id.txt_quantity);
                    TextView txtWeight = (TextView)view.findViewById(R.id.txt_weight);

                    if(PreferenceUtils.getQuote(mContext)){
                        ImageLoaderHelper.showImage(mContext, orderModel.itemModels.get(k).image, img);
                    }else{
                        ImageLoaderHelper.showImageFromLocal(mContext, orderModel.itemModels.get(k).image, img);
                    }

                    final int finalK = k;
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(PreferenceUtils.getQuote(mContext)){
                                Constants.showImage(mContext, linContainer, orderModel.itemModels.get(finalK).image, "url");
                            }else{
                                Constants.showImage(mContext, linContainer, orderModel.itemModels.get(finalK).image, "local");
                                //Constants.showImageFromLocal(mContext, orderModel.itemModels.get(finalK).image);
                            }
                        }
                    });
                    txtQuantity.setText(orderModel.itemModels.get(k).quantity);
                    txtWeight.setText(orderModel.itemModels.get(k).weight);


                    linItem.addView(view);
                    View line = inflater.inflate(R.layout.divider, null);
                    if(k < orderModel.itemModels.size() -1){
                        linItem.addView(line);
                    }

                    if(!isShow){
                        break;
                    }

                }
            }

        }else if(Constants.ORDER_TYPE == Constants.ITEM_OPTION){
            orderModel = Constants.itemOrderModel;

            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewCol = inflater.inflate(R.layout.column_item, null);
            viewCol.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            imgBottom = (ImageView)viewCol.findViewById(R.id.img_bottom);
            imgBottom.setOnClickListener(this);
            if(isShow){
                imgBottom.setImageResource(R.mipmap.up);
            }else{
                imgBottom.setImageResource(R.mipmap.down);
            }
            linColumn.addView(viewCol);
            if(orderModel != null){
                for(int k = 0;  k < orderModel.itemModels.size(); k++){

                    View view = inflater.inflate(R.layout.row_review_item, null);
                    LinearLayout linTotal = (LinearLayout)view.findViewById(R.id.lin_total);
                    TextView txtItem = (TextView)view.findViewById(R.id.txt_item);
                    TextView txtDimension = (TextView)view.findViewById(R.id.txt_dimension);
                    TextView txtQuantity = (TextView)view.findViewById(R.id.txt_quantity);
                    TextView txtWeight = (TextView)view.findViewById(R.id.txt_weight);
                    view.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    txtItem.setText(orderModel.itemModels.get(k).title);
                    txtDimension.setText(orderModel.itemModels.get(k).dimension1 + "X" + orderModel.itemModels.get(k).dimension2 + "X" + orderModel.itemModels.get(k).dimension3);
                    txtQuantity.setText(orderModel.itemModels.get(k).quantity);
                    txtWeight.setText(orderModel.itemModels.get(k).weight);
                    linItem.addView(view);

                    View line = inflater.inflate(R.layout.divider, null);
                    if(k < orderModel.itemModels.size() -1){
                        linItem.addView(line);
                    }

                    if(!isShow){
                        break;
                    }
                }
            }

        }else if(Constants.ORDER_TYPE == Constants.PACKAGE_OPTION){
            orderModel = Constants.packageOrderModel;
            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewCol = inflater.inflate(R.layout.row_review_package, null);
            viewCol.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linColumn.addView(viewCol);

            if(orderModel != null){
                for(int k = 0;  k < orderModel.itemModels.size(); k++){

                    View view = inflater.inflate(R.layout.row_review_package, null);
                    TextView txtItem = (TextView)view.findViewById(R.id.txt_item);
                    TextView txtQuantity = (TextView)view.findViewById(R.id.txt_quantity);
                    TextView txtWeight = (TextView)view.findViewById(R.id.txt_weight);

                    txtItem.setText(orderModel.itemModels.get(k).title);
                    txtQuantity.setText(orderModel.itemModels.get(k).quantity);
                    txtWeight.setText(orderModel.itemModels.get(k).weight);
                    linItem.addView(view);

                    View line = inflater.inflate(R.layout.divider, null);
                    if(k < orderModel.itemModels.size() -1){
                        linItem.addView(line);
                    }
                    if(!isShow){
                        break;
                    }
                }
            }
        }
    }



    private  void showAddressDetails(){
        if(Constants.addressModel.desAddress != null){
            txtSenderName.setText(Constants.addressModel.senderName);
            edtPickAddress.setText(Constants.addressModel.sourceAddress);

            edtPickPhone.setText(Constants.addressModel.sourcePhonoe);
            edtPickLandMark.setText( Constants.addressModel.sourceLandMark); //"House No, Flat No:\n" +

            edtPickInstrunction.setText(Constants.addressModel.sourceInstruction); //"Landmark, Instruction to Associate:\n" +
            if(Constants.addressModel.sourceInstruction.isEmpty()){
                edtPickInstrunction.setVisibility(View.GONE);
            }

            edtDesAddress.setText(Constants.addressModel.desAddress);
            edtDesLandmark.setText(Constants.addressModel.desLandMark); //"House No, Flat No:\n" +
            edtDesInstruction.setText(Constants.addressModel.desInstruction); //"Landmark, Instruction to Associate:\n" +
            if(Constants.addressModel.desInstruction.isEmpty()){
                edtDesInstruction.setVisibility(View.GONE);
            }
            edtDesPhone.setText(Constants.addressModel.desPhone);
            edtDesName.setText(Constants.addressModel.desName);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_bottom:
                if(isShow){
                    isShow = false;
                    showItemLists();
                }else{
                    isShow = true;
                    showItemLists();
                }
                break;
            case R.id.btn_apply:
                String coupon = edtCoupon.getText().toString();
                if(coupon.isEmpty()){
                    Toast.makeText(mContext, getString(R.string.input_all), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, getString(R.string.invalid_coupon), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_cash_on_pickup:
                checked(1);

                break;
            case R.id.txt_cod:
                checked(2);

                break;
            case R.id.txt_pay_using_card:
                checked(3);
                break;
            case R.id.txt_net_banking:
                checked(4);
                break;

            case R.id.btn_place_order:
                if(paymentType != -1){
                    Constants.paymentType = mPayment;
                    if(PreferenceUtils.getQuote(mContext)){
                        //btnPlaceOrder.setEnabled(false);
                        launchPayUMoneyFlow();
                    }else{
                        if(paymentType == 0 || paymentType == 3){
                            showOrdreConfirm(true);
                        }else{
                            //btnPlaceOrder.setEnabled(false);
                            launchPayUMoneyFlow();
                        }
                    }
                }else{
                    Toast.makeText(mContext, getString(R.string.choose_payment), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.img_sender:
                ((HomeActivity)mContext).updateFragment(HomeActivity.DATE_TIME,"review");
                break;

        }
    }

    String mPayment = "Cash On Pick up";
    int paymentType = 0;
    private void checked(int index){
        rdCash.setChecked(false);
        rdCod.setChecked(false);
        rdCard.setChecked(false);
        rdNetBanking.setChecked(false);
        switch (index){
            case 1:
                rdCash.setChecked(true);
                paymentType = 1;
                mPayment = "Cash On Pick up";
                break;

            case 2:
                rdCod.setChecked(true);
                mPayment ="COD";
                paymentType = 2;
                break;

            case 3:
                rdCard.setChecked(true);
                mPayment = "Pay using Card";
                paymentType = 3;
                break;

            case 4:
                rdNetBanking.setChecked(true);
                paymentType = 4;
                mPayment ="Pay using Bank";
                break;
        }
    }

    private  void showOrdreConfirm(boolean flag){
        if(PreferenceUtils.getQuote(mContext)){
            if(Constants.MODE == Constants.PERSONAL){
                PreferenceUtils.setOrderId(mContext, Constants.quote_order_id);
                update_order(Constants.quote_order_id, flag);
            }else{
                PreferenceUtils.setOrderId(mContext, Constants.quote_order_id);
                update_corporate_order(Constants.quote_order_id, flag);
            }
        }else{
            order(flag);
        }
    }



    private void update_order(final String order_id, boolean flag) {
        if(flag){
            ((HomeActivity)mContext).showProgressDialog();
        }

        RequestParams params = new RequestParams();
        params.put("id", order_id);
        params.put("user_id", PreferenceUtils.getUserId(mContext));
        params.put("date", Constants.dateModel.date);
        params.put("time", Constants.dateModel.time);

        params.put("service_name", Constants.serviceModel.name);
        params.put("service_price", Constants.serviceModel.price);
        params.put("service_timein", Constants.serviceModel.time_in);

        params.put("weight", String.valueOf(Constants.getTotalWeight()));
        params.put("payment", mPayment);

        // order address
        JSONArray addressArray = new JSONArray();
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("s_address", Constants.addressModel.sourceAddress);
        jsonMap.put("s_area", Constants.addressModel.sourceArea);
        jsonMap.put("s_city", Constants.addressModel.sourceCity);
        jsonMap.put("s_state", Constants.addressModel.sourceState);
        jsonMap.put("s_pincode", Constants.addressModel.sourcePinCode);
        jsonMap.put("s_phone", Constants.addressModel.sourcePhonoe + ":" + Constants.addressModel.senderName);
        jsonMap.put("s_landmark", Constants.addressModel.sourceLandMark);
        jsonMap.put("s_instruction", Constants.addressModel.sourceInstruction);
        jsonMap.put("s_lat", String.valueOf(Constants.addressModel.sourceLat));
        jsonMap.put("s_lng", String.valueOf(Constants.addressModel.sourceLng));

        jsonMap.put("d_address", Constants.addressModel.desAddress);
        jsonMap.put("d_area", Constants.addressModel.desArea);
        jsonMap.put("d_city", Constants.addressModel.desCity);
        jsonMap.put("d_state", Constants.addressModel.desState);
        jsonMap.put("d_pincode", Constants.addressModel.desPinCode);
        jsonMap.put("d_landmark", Constants.addressModel.desLandMark);
        jsonMap.put("d_instruction", Constants.addressModel.desInstruction);
        jsonMap.put("d_lat", String.valueOf(Constants.addressModel.desLat));
        jsonMap.put("d_lng", String.valueOf(Constants.addressModel.desLng));
        jsonMap.put("d_phone", Constants.addressModel.desPhone);
        jsonMap.put("d_name", Constants.addressModel.desName);

        JSONObject addressJson = new JSONObject(jsonMap);
        addressArray.put(addressJson);
        params.put("orderaddress", addressArray.toString());

        JSONArray productsArrays = new JSONArray();
        if(Constants.ORDER_TYPE == Constants.CAMERA_OPTION){

            try{
                for(int k = 0 ; k < Constants.cameraOrderModel.itemModels.size(); k++){
                    File file =  new File(Constants.cameraOrderModel.itemModels.get(k).image);
                    params.put("file" + String.valueOf(k), file);
                }
            }catch (Exception e){

            }
            for(int i = 0; i < Constants.cameraOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("image", Constants.cameraOrderModel.itemModels.get(i).image);
                cameraMap.put("quantity", Constants.cameraOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.cameraOrderModel.itemModels.get(i).weight);
                cameraMap.put("weight_value", String.valueOf(Constants.cameraOrderModel.itemModels.get(i).weight_value));
                cameraMap.put("product_type", String.valueOf(Constants.CAMERA_OPTION));
                cameraMap.put("package", Constants.cameraOrderModel.itemModels.get(i).mPackage);
                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);
            }

        }else if(Constants.ORDER_TYPE == Constants.ITEM_OPTION){
            for(int i = 0; i < Constants.itemOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("title", Constants.itemOrderModel.itemModels.get(i).title);
                cameraMap.put("dimension", Constants.getDimetion(i));
                cameraMap.put("quantity", Constants.itemOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.itemOrderModel.itemModels.get(i).weight);
                cameraMap.put("product_type", String.valueOf(Constants.ITEM_OPTION));
                cameraMap.put("package", Constants.itemOrderModel.itemModels.get(i).mPackage);
                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);
            }
        }else if(Constants.ORDER_TYPE == Constants.PACKAGE_OPTION){
            for(int i = 0; i < Constants.packageOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("title", Constants.packageOrderModel.itemModels.get(i).title);
                cameraMap.put("quantity", Constants.packageOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.packageOrderModel.itemModels.get(i).weight);
                cameraMap.put("product_type", String.valueOf(Constants.PACKAGE_OPTION));
                cameraMap.put("package", Constants.packageOrderModel.itemModels.get(i).mPackage);
                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);
            }
        }
        params.put("products", productsArrays.toString());

        WebClient.post(Constants.BASE_URL_ORDER + "update_order", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {

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
                                String results = response.getString("result");
                                if(results.equals("200")){
                                    PreferenceUtils.setOrderId(mContext, order_id);


                                    ((HomeActivity)mContext).updateFragment(HomeActivity.ORDER_CONFIRM, "");
//                                    Intent intent  = new Intent(mContext, OrderConfirmActivity.class);
//                                    startActivity(intent);
                                }
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(mContext, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    };
                    public void onFinish() {
                        ((HomeActivity)mContext).hideProgressDialog();
                    }
                    ;
                });
    }


    private void update_corporate_order(final String order_id, boolean flag) {
        if(flag){
            ((HomeActivity)mContext).showProgressDialog();
        }

        RequestParams params = new RequestParams();
        params.put("id", order_id);
        params.put("user_id", PreferenceUtils.getUserId(mContext));
        params.put("date", Constants.dateModel.date);
        params.put("time", Constants.dateModel.time);

        params.put("service_name", Constants.serviceModel.name);
        params.put("service_price", Constants.serviceModel.price);
        params.put("service_timein", Constants.serviceModel.time_in);
        params.put("payment", mPayment);

        WebClient.post(Constants.BASE_URL_ORDER + "update_corporate_order", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        ((HomeActivity)mContext).showProgressDialog();
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

                                String results = response.getString("result");
                                if(results.equals("200")){

                                    PreferenceUtils.setOrderId(mContext, order_id);

                                    ((HomeActivity)mContext).updateFragment(HomeActivity.ORDER_CONFIRM, "");


                                }

                                if(response.getString("result").equals("400")){
                                    Toast.makeText(mContext, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    };
                    public void onFinish() {
                        ((HomeActivity)mContext).hideProgressDialog();
                    }
                    ;
                });
    }


    private void order(boolean flag ) {

        if(flag)
            ((HomeActivity)mContext).showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("transaction_id", "0");
        params.put("id", PreferenceUtils.getOrderId(mContext));

        if(Constants.MODE ==  Constants.PERSONAL){
            params.put("user_id", PreferenceUtils.getUserId(mContext));
        }else if(Constants.MODE == Constants.GUEST){
            params.put("user_id", "");
        }
        params.put("date", Constants.dateModel.date);
        params.put("time", Constants.dateModel.time);

        params.put("service_name", Constants.serviceModel.name);
        params.put("service_price", Constants.serviceModel.price);
        params.put("service_timein", Constants.serviceModel.time_in);
        params.put("distance", Constants.addressModel.distance);
        params.put("weight", String.valueOf(Constants.getTotalWeight()));
        params.put("quote_type", "0");
        params.put("payment", mPayment);

        params.put("device_id", DeviceUtil.getDeviceId(mContext));
        params.put("device_type", DeviceUtil.getDeviceName());
        // order address
        JSONArray addressArray = new JSONArray();
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("s_address", Constants.addressModel.sourceAddress);
        jsonMap.put("s_area", Constants.addressModel.sourceArea);
        jsonMap.put("s_city", Constants.addressModel.sourceCity);
        jsonMap.put("s_state", Constants.addressModel.sourceState);
        jsonMap.put("s_pincode", Constants.addressModel.sourcePinCode);
        jsonMap.put("s_phone", Constants.addressModel.sourcePhonoe + ":" + Constants.addressModel.senderName);
        jsonMap.put("s_landmark", Constants.addressModel.sourceLandMark);
        jsonMap.put("s_instruction", Constants.addressModel.sourceInstruction);
        jsonMap.put("s_lat", String.valueOf(Constants.addressModel.sourceLat));
        jsonMap.put("s_lng", String.valueOf(Constants.addressModel.sourceLng));

        jsonMap.put("d_address", Constants.addressModel.desAddress);
        jsonMap.put("d_area", Constants.addressModel.desArea);
        jsonMap.put("d_city", Constants.addressModel.desCity);
        jsonMap.put("d_state", Constants.addressModel.desState);
        jsonMap.put("d_pincode", Constants.addressModel.desPinCode);
        jsonMap.put("d_landmark", Constants.addressModel.desLandMark);
        jsonMap.put("d_instruction", Constants.addressModel.desInstruction);
        jsonMap.put("d_lat", String.valueOf(Constants.addressModel.desLat));
        jsonMap.put("d_lng", String.valueOf(Constants.addressModel.desLng));
        jsonMap.put("d_phone", String.valueOf(Constants.addressModel.desPhone));
        jsonMap.put("d_name", String.valueOf(Constants.addressModel.desName));
        if(Constants.MODE == Constants.GUEST){
            jsonMap.put("duration", Constants.guestEmail);
        }else{
            jsonMap.put("duration", Constants.addressModel.duration);
        }
        jsonMap.put("distance", Constants.addressModel.distance);
        String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        jsonMap.put("device_id", android_id);
        JSONObject addressJson = new JSONObject(jsonMap);
        addressArray.put(addressJson);
        params.put("orderaddress", addressArray.toString());
        JSONArray productsArrays = new JSONArray();
        if(Constants.ORDER_TYPE == Constants.CAMERA_OPTION){

            for(int k = 0 ; k < Constants.cameraOrderModel.itemModels.size(); k++){
                File file =  new File(Constants.cameraOrderModel.itemModels.get(k).image);
                try {
                    params.put("file" + String.valueOf(k), file);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }

            for(int i = 0; i < Constants.cameraOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("image", Constants.cameraOrderModel.itemModels.get(i).image);
                cameraMap.put("quantity", Constants.cameraOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.cameraOrderModel.itemModels.get(i).weight);
                cameraMap.put("weight_value", String.valueOf(Constants.cameraOrderModel.itemModels.get(i).weight_value));
                cameraMap.put("product_type", String.valueOf(Constants.CAMERA_OPTION));
                cameraMap.put("package", Constants.cameraOrderModel.itemModels.get(i).mPackage);
                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);

            }
        }else if(Constants.ORDER_TYPE == Constants.ITEM_OPTION){
            for(int i = 0; i < Constants.itemOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("title", Constants.itemOrderModel.itemModels.get(i).title);
                cameraMap.put("dimension", Constants.getDimetion(i));
                cameraMap.put("quantity", Constants.itemOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.itemOrderModel.itemModels.get(i).weight);
                cameraMap.put("product_type", String.valueOf(Constants.ITEM_OPTION));
                cameraMap.put("package", Constants.itemOrderModel.itemModels.get(i).mPackage);

                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);
            }

        }else if(Constants.ORDER_TYPE == Constants.PACKAGE_OPTION){
            for(int i = 0; i < Constants.packageOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("title", Constants.packageOrderModel.itemModels.get(i).title);
                cameraMap.put("quantity", Constants.packageOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.packageOrderModel.itemModels.get(i).weight);
                cameraMap.put("product_type", String.valueOf(Constants.PACKAGE_OPTION));
                cameraMap.put("package", Constants.packageOrderModel.itemModels.get(i).mPackage);
                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);

            }
        }
        params.put("products", productsArrays.toString());
        WebClient.post(Constants.BASE_URL + "order", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {

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
                        if(throwable.getMessage().equals("Read timed out")){
                            order(flag);
                        }
                    }

                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {
                        try {

                            if (response != null) {
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(mContext, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }else if(response.getString("result").equals("300")){
                                    Toast.makeText(mContext, "Already Exist", Toast.LENGTH_SHORT).show();
                                }else{
                                    String order_id = response.getString("order_id");
                                    String track_id = response.getString("track_id");

                                    PreferenceUtils.setOrderId(mContext, order_id);
                                    PreferenceUtils.setTrackId(mContext, track_id);

//                                    Intent intent  = new Intent(mContext, OrderConfirmNewActivity.class);
//                                    startActivity(intent);

                                    ((HomeActivity)mContext).updateFragment(HomeActivity.ORDER_CONFIRM, "");

                                }
                            }
                            ((HomeActivity)mContext).hideProgressDialog();

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            ((HomeActivity)mContext).hideProgressDialog();
                        }
                    };
                    public void onFinish() {

                    }
                    ;
                });
    }


    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();
        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("Status");
        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle("Online Payment");
        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        double amount = 0;
        try {
            String amountt = String.format("%.2f", Float.valueOf(Constants.serviceModel.price));
            amount = Double.valueOf(amountt); // Double.valueOf(Constants.serviceModel.price); // Double.parseDouble(amount_et.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String txnId = System.currentTimeMillis() + "";
        String phone = Constants.addressModel.sourcePhonoe;
        if(Constants.MODE != Constants.GUEST){
            phone = PreferenceUtils.getPhone(mContext);
        }
        String email = Constants.guestEmail; // mobile_til.getEditText().getText().toString().trim();
        if(Constants.MODE == Constants.PERSONAL){
            email = PreferenceUtils.getEmail(mContext);
        }else if(Constants.MODE == Constants.CORPERATION){
            email = PreferenceUtils.getCorEmail(mContext);
        }else {
            email =  Constants.guestEmail;
        }

        String productName = mAppPreference.getProductInfo();
        String firstName = PreferenceUtils.getNickname(mContext);// mAppPreference.getFirstName();
        if(Constants.MODE == Constants.GUEST){
            firstName = "Guest User";
        }

        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        AppEnvironment appEnvironment = ((MyApplication) getActivity().getApplication()).getAppEnvironment();
        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(firstName)
                .setUdf2(firstName)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(appEnvironment.merchant_Key())
                .setMerchantId(appEnvironment.merchant_ID());

        try {
            mPaymentParams = builder.build();
            /*
             * Hash should always be generated from your server side.
             * */
            //generateHashFromServer(mPaymentParams);

            /*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);
            if (AppPreference.selectedTheme != -1) {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,getActivity(), AppPreference.selectedTheme,mAppPreference.isOverrideResultScreen());
            } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,getActivity(), R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
            }

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(mContext, e.getMessage() + " : " + phone, Toast.LENGTH_LONG).show();
            btnPlaceOrder.setEnabled(true);
        }
    }

    public String getPaymentType(){
        return mPayment;
    }



    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        AppEnvironment appEnvironment = ((MyApplication) getActivity().getApplication()).getAppEnvironment();
        stringBuilder.append(appEnvironment.salt());

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }


    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }




}
