package com.hurry.custom.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.common.utils.ImageLoaderHelper;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.view.BaseActivity;
import com.hurry.custom.view.BaseBackActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 3/18/2017.
 */

public class OrderConfirmActivity extends BaseBackActivity implements View.OnClickListener{

    LinearLayout linContainer;

    ImageView imgHome;

    TextView txtOrder;
    TextView txtTracking;
    TextView txtPayment;

    TextView txtSenderName;
    TextView edtPickAddress;
    TextView edtPickCity;
    TextView edtPickState;
    TextView edtPickPinCode;
    TextView edtPickPhone;
    TextView edtPickLandMark;
    TextView edtPickInstrunction;

    TextView edtDesAddress;
    TextView edtDesCityp;
    TextView edtDesState;
    TextView edtDesPinCode;
    TextView edtDesLandmark;
    TextView edtDesInstruction;
    TextView edtDesPhone;
    TextView edtDesName;

    LinearLayout linItem;
    LinearLayout linColumn, linOrderDetails;
    LinearLayout linBottom;

    OrderModel orderModel ;

    TextView txtDate;
    TextView txtService;

    Button btnReschedule;
    Button btnCancel;
    Button btnCall;
    LinearLayout linDetails;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_order_confirm);
        ButterKnife.bind(this);
        initBackButton(toolbar, getString(R.string.order_confirmed));
        initView();

        txtDate.setText("Pick up on: " + Constants.dateModel.date  + "  " + Constants.dateModel.time);
        txtService.setText(Constants.serviceModel.name + ", "+ Constants.getPrice(OrderConfirmActivity.this, Constants.serviceModel.price) + ", " + Constants.serviceModel.time_in); ///"Service Level: " +

        if(getValue("payment") == null || getValue("payment").isEmpty()){
            if(Constants.MODE != Constants.CORPERATION){
                done_order();
            }
        }else{
            showOrdreConfirm(true);
        }
    }

    private  void initView(){
        linContainer = (LinearLayout)findViewById(R.id.lin_container);
        txtOrder = (TextView)findViewById(R.id.txt_order_number);
        txtTracking = (TextView)findViewById(R.id.txt_tracking);
        txtPayment = (TextView)findViewById(R.id.txt_payment_method);

        txtPayment.setText(Constants.paymentType); //"Payment Method: " + "Cash on Pick Up"

        linItem = (LinearLayout)findViewById(R.id.lin_item);
        linColumn = (LinearLayout)findViewById(R.id.lin_column);
        linOrderDetails = (LinearLayout)findViewById(R.id.lin_order_detail);

        txtSenderName = (TextView)findViewById(R.id.edt_sender);
        edtPickAddress = (TextView)findViewById(R.id.edt_address);
        edtPickCity = (TextView)findViewById(R.id.edt_city);
        edtPickState = (TextView)findViewById(R.id.edt_state);
        edtPickPinCode = (TextView)findViewById(R.id.edt_pincode);
        edtPickPhone = (TextView)findViewById(R.id.edt_phone);
        edtPickLandMark = (TextView)findViewById(R.id.edt_landmark);
        edtPickInstrunction = (TextView)findViewById(R.id.edt_instruction);
        edtDesAddress = (TextView)findViewById(R.id.edt_address_des);
        edtDesCityp = (TextView)findViewById(R.id.edt_city_des);
        edtDesState = (TextView)findViewById(R.id.edt_state_des);
        edtDesPinCode = (TextView)findViewById(R.id.edt_pincode_des);
        edtDesLandmark = (TextView)findViewById(R.id.edt_landmark_des);
        edtDesInstruction = (TextView)findViewById(R.id.edt_instruction_des);
        edtDesPhone = (TextView)findViewById(R.id.edt_phone_des);
        edtDesName = (TextView)findViewById(R.id.edt_des_name);

        txtDate = (TextView)findViewById(R.id.txt_date);
        txtService = (TextView)findViewById(R.id.txt_service);

        imgHome = (ImageView)findViewById(R.id.img_home);

        imgHome.setOnClickListener(this);
        imgHome.setVisibility(View.VISIBLE);

        btnReschedule = (Button)findViewById(R.id.btn_reschedule);
        btnReschedule.setOnClickListener(this);
        btnCancel = (Button)findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnCall = (Button)findViewById(R.id.btn_call);
        btnCall.setOnClickListener(this);

        linBottom = (LinearLayout)findViewById(R.id.lin_bottom);
        LinearLayout linReschedule = (LinearLayout)findViewById(R.id.lin_reschedule);
        LinearLayout linCancel = (LinearLayout)findViewById(R.id.lin_cancel);
        LinearLayout linCall = (LinearLayout)findViewById(R.id.lin_call);

        if(Constants.MODE == Constants.GUEST || Constants.MODE == Constants.CORPERATION){
            linReschedule.setVisibility(View.GONE);
            linCancel.setVisibility(View.GONE);
        }else{
            linBottom.setVisibility(View.VISIBLE);
        }
        linDetails = (LinearLayout)findViewById(R.id.lin_details);
    }


    private  void showItemLists(){

        if(Constants.ORDER_TYPE == Constants.CAMERA_OPTION){
            orderModel = Constants.cameraOrderModel;
            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewCol = inflater.inflate(R.layout.column_camera, null);
            viewCol.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linColumn.addView(viewCol);
            if(orderModel != null){
                for(int k = 0;  k < orderModel.itemModels.size(); k++){

                    View view = inflater.inflate(R.layout.row_review_camera, null);
                    ImageView img = (ImageView) view.findViewById(R.id.image);
                    TextView txtQuantity = (TextView)view.findViewById(R.id.txt_quantity);
                    TextView txtWeight = (TextView)view.findViewById(R.id.txt_weight);
                    if(PreferenceUtils.getQuote(OrderConfirmActivity.this)){
                        ImageLoaderHelper.showImage(this, orderModel.itemModels.get(k).image, img);
                    }else{
                        ImageLoaderHelper.showImageFromLocal(this, orderModel.itemModels.get(k).image, img);
                    }

                    final int finalK = k;
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(PreferenceUtils.getQuote(OrderConfirmActivity.this)){
                                Constants.showImage(OrderConfirmActivity.this, linContainer, orderModel.itemModels.get(finalK).image, "url");
                            }else{
                                Constants.showImage(OrderConfirmActivity.this, linContainer, orderModel.itemModels.get(finalK).image, "local");
                                //Constants.showImageFromLocal(OrderConfirmActivity.this, orderModel.itemModels.get(finalK).image);
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
                }
            }


        }else if(Constants.ORDER_TYPE == Constants.ITEM_OPTION){
            orderModel = Constants.itemOrderModel;

            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewCol = inflater.inflate(R.layout.column_item, null);
            viewCol.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linColumn.addView(viewCol);

            if(orderModel != null){
                for(int k = 0;  k < orderModel.itemModels.size(); k++){

                    View view = inflater.inflate(R.layout.row_review_item, null);
                    TextView txtItem = (TextView)view.findViewById(R.id.txt_item);
                    TextView txtDimension = (TextView)view.findViewById(R.id.txt_dimension);
                    TextView txtQuantity = (TextView)view.findViewById(R.id.txt_quantity);
                    TextView txtWeight = (TextView)view.findViewById(R.id.txt_weight);

                    txtItem.setText(orderModel.itemModels.get(k).title);
                    txtDimension.setText(orderModel.itemModels.get(k).dimension1 + "X" + orderModel.itemModels.get(k).dimension2 + "X" + orderModel.itemModels.get(k).dimension3);
                    txtQuantity.setText(orderModel.itemModels.get(k).quantity);
                    txtWeight.setText(orderModel.itemModels.get(k).weight);
                    linItem.addView(view);
                    View line = inflater.inflate(R.layout.divider, null);
                    if(k < orderModel.itemModels.size() -1){
                        linItem.addView(line);
                    }

                }
            }
        }else if(Constants.ORDER_TYPE == Constants.PACKAGE_OPTION){
            orderModel = Constants.packageOrderModel;

            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                }
            }
        }
    }

    private  void showAddressDetails(){

        txtOrder.setText(PreferenceUtils.getOrderId(this));
        txtTracking.setText(PreferenceUtils.getTrackId(this));

        if(Constants.addressModel.desAddress != null){

            txtSenderName.setText(Constants.addressModel.senderName);
            edtPickAddress.setText(Constants.addressModel.sourceAddress);
            edtPickCity.setText(Constants.addressModel.sourceCity);
            edtPickState.setText(Constants.addressModel.sourceState);
            edtPickPinCode.setText(Constants.addressModel.sourcePinCode);
            edtPickPhone.setText(Constants.addressModel.sourcePhonoe);

            if(Constants.addressModel.sourceLandMark == null || Constants.addressModel.sourceLandMark.isEmpty()){
                edtPickLandMark.setVisibility(View.GONE);
            }else{
                edtPickLandMark.setText(Constants.addressModel.sourceLandMark); //"Land Mark:\n" + //house no
            }
            if(Constants.addressModel.sourceInstruction == null || Constants.addressModel.sourceInstruction.isEmpty()){
                edtPickInstrunction.setVisibility(View.GONE);
            }else{
                edtPickInstrunction.setText( Constants.addressModel.sourceInstruction); //"Instructions:\n" +
            }


            edtDesAddress.setText(Constants.addressModel.desAddress);
            edtDesCityp.setText(Constants.addressModel.desCity);
            edtDesState.setText(Constants.addressModel.desState);
            edtDesPinCode.setText(Constants.addressModel.desPinCode);

            if(Constants.addressModel.desLandMark == null || Constants.addressModel.desLandMark.isEmpty()){
                edtDesLandmark.setVisibility(View.GONE);
            }else{
                edtDesLandmark.setText(Constants.addressModel.desLandMark); //"Land Mark:\n" +
            }

            if(Constants.addressModel.desInstruction == null || Constants.addressModel.desInstruction.isEmpty()){
                edtDesInstruction.setVisibility(View.GONE);
            }else{
                edtDesInstruction.setText(Constants.addressModel.desInstruction); //"Instructions:\n" +
            }

            edtDesPhone.setText(Constants.addressModel.desPhone);
            edtDesName.setText(Constants.addressModel.desName);
        }
    }
    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                Constants.page_type = "home";
                finish();

                break;
            case R.id.btn_reschedule:
                Constants.page_type ="reschedule";
                finish();
                if(CameraOrderActivity.cameraOrderActivity != null){
                    CameraOrderActivity.cameraOrderActivity.finish();
                    CameraOrderActivity.cameraOrderActivity  = null;
                }
                if(ItemOrderActivity.itemOrderActivity != null){
                    ItemOrderActivity.itemOrderActivity.finish();
                    ItemOrderActivity.itemOrderActivity  =  null;
                }
                if(PackageOrderActivity.packageOrderActivity != null){
                    PackageOrderActivity.packageOrderActivity.finish();
                    PackageOrderActivity.packageOrderActivity  = null;
                }
                Constants.clearData();
                break;
            case R.id.btn_cancel:
                Constants.page_type = "cancel";
                finish();
                if(CameraOrderActivity.cameraOrderActivity != null){
                    CameraOrderActivity.cameraOrderActivity.finish();
                    CameraOrderActivity.cameraOrderActivity  = null;
                }
                if(ItemOrderActivity.itemOrderActivity != null){
                    ItemOrderActivity.itemOrderActivity.finish();
                    ItemOrderActivity.itemOrderActivity  =  null;
                }
                if(PackageOrderActivity.packageOrderActivity != null){
                    PackageOrderActivity.packageOrderActivity.finish();
                    PackageOrderActivity.packageOrderActivity  = null;
                }


                Constants.clearData();
                break;
            case R.id.btn_call:
                makePhoneCall(PreferenceUtils.getConfPhone(this));
                break;
            case R.id.img_home:
                if(Constants.MODE == Constants.CORPERATION){
                    Constants.page_type = "";
                }else if(Constants.MODE == Constants.GUEST){
                    Constants.page_type = "home";
                }else{
                    Constants.page_type = "home";
                }
                if(CameraOrderActivity.cameraOrderActivity != null){
                    CameraOrderActivity.cameraOrderActivity.finish();
                    CameraOrderActivity.cameraOrderActivity  = null;
                }
                if(ItemOrderActivity.itemOrderActivity != null){
                    ItemOrderActivity.itemOrderActivity.finish();
                    ItemOrderActivity.itemOrderActivity  =  null;
                }
                if(PackageOrderActivity.packageOrderActivity != null){
                    PackageOrderActivity.packageOrderActivity.finish();
                    PackageOrderActivity.packageOrderActivity  = null;
                }
                Constants.clearData();
                finish();
                break;
        }
    }

    private void makePhoneCall(String finalPhonenumber){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + finalPhonenumber));
        startActivity(callIntent);
    }


    private void done_order() {

        showItemLists();
        showAddressDetails();

        RequestParams params = new RequestParams();
        params.put("order_id", PreferenceUtils.getOrderId(this));
        WebClient.post(Constants.BASE_URL_ORDER + "done_order", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                     //   showProgressDialog();
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

                        if (response != null) {
                            try{
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(OrderConfirmActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){

                            }
                        }
                    };
                    public void onFinish() {

                        //hideProgressDialog();
                    }
                    ;
                });
    }




    private  void showOrdreConfirm(boolean flag){
        if(PreferenceUtils.getQuote(this)){
            if(Constants.MODE == Constants.PERSONAL){
                PreferenceUtils.setOrderId(this, Constants.quote_order_id);
                update_order(Constants.quote_order_id, flag);
            }else{
                PreferenceUtils.setOrderId(this, Constants.quote_order_id);
                update_corporate_order(Constants.quote_order_id, flag);
            }
        }else{
            order(flag);
        }
    }

    private void update_order(final String order_id, boolean flag) {
        if(flag){
            showProgressDialog();
        }

        RequestParams params = new RequestParams();
        params.put("transaction_id", getValue("transaction_id"));
        params.put("id", order_id);
        params.put("user_id", PreferenceUtils.getUserId(this));
        params.put("date", Constants.dateModel.date);
        params.put("time", Constants.dateModel.time);

        params.put("service_name", Constants.serviceModel.name);
        params.put("service_price", Constants.serviceModel.price);
        params.put("service_timein", Constants.serviceModel.time_in);

        params.put("weight", String.valueOf(Constants.getTotalWeight()));
        params.put("payment", getValue("payment"));

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
                                    PreferenceUtils.setOrderId(OrderConfirmActivity.this, order_id);

                                    done_order();

                                    Toast.makeText(OrderConfirmActivity.this, "quote success", Toast.LENGTH_SHORT).show();
                                }
                                if(response.getString("result").equals("400")){
                                    finish();
                                    Toast.makeText(OrderConfirmActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(OrderConfirmActivity.this, "quote success??? faield ", Toast.LENGTH_SHORT).show();
                        }
                    };
                    public void onFinish() {
                        hideProgressDialog();
                    }
                    ;
                });
    }


    private void update_corporate_order(final String order_id, boolean flag) {
        if(flag){
            showProgressDialog();
        }

        RequestParams params = new RequestParams();
        params.put("id", order_id);
        params.put("user_id", PreferenceUtils.getUserId(this));
        params.put("date", Constants.dateModel.date);
        params.put("time", Constants.dateModel.time);

        params.put("service_name", Constants.serviceModel.name);
        params.put("service_price", Constants.serviceModel.price);
        params.put("service_timein", Constants.serviceModel.time_in);
        params.put("payment", getValue("payment"));

        WebClient.post(Constants.BASE_URL_ORDER + "update_corporate_order", params,
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

                                String results = response.getString("result");
                                if(results.equals("200")){


                                    PreferenceUtils.setOrderId(OrderConfirmActivity.this, order_id);
                                    txtOrder.setText(PreferenceUtils.getOrderId(OrderConfirmActivity.this));
                                    txtTracking.setText(PreferenceUtils.getTrackId(OrderConfirmActivity.this));
                                    if(Constants.MODE != Constants.CORPERATION){
                                        done_order();
                                    }
                                }

                                if(response.getString("result").equals("400")){
                                    finish();
                                    Toast.makeText(OrderConfirmActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }

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


    private void order(boolean flag ) {

        if(flag)
            showProgressDialog();
        RequestParams params = new RequestParams();

        params.put("transaction_id", getValue("transaction_id"));
        params.put("id", PreferenceUtils.getOrderId(this));

        if(Constants.MODE ==  Constants.PERSONAL){
            params.put("user_id", PreferenceUtils.getUserId(this));
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
        params.put("payment", getValue("payment"));

        params.put("device_id", DeviceUtil.getDeviceId(this));
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
        String android_id = Settings.Secure.getString(getContentResolver(),
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
                    }

                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {
                        try {

                            if (response != null) {
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(OrderConfirmActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }else if(response.getString("result").equals("300")){
                                    Toast.makeText(OrderConfirmActivity.this, "Already Exist", Toast.LENGTH_SHORT).show();
                                }else{
                                    String order_id = response.getString("order_id");
                                    String track_id = response.getString("track_id");

                                    PreferenceUtils.setOrderId(OrderConfirmActivity.this, order_id);
                                    PreferenceUtils.setTrackId(OrderConfirmActivity.this, track_id);
                                    txtOrder.setText(PreferenceUtils.getOrderId(OrderConfirmActivity.this));
                                    txtTracking.setText(PreferenceUtils.getTrackId(OrderConfirmActivity.this));
                                    if(Constants.MODE != Constants.CORPERATION){
                                        done_order();
                                    }
                                }
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

}
