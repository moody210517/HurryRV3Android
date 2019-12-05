package com.hurry.custom.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.view.BaseActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.angmarch.views.NiceSpinner;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.hurry.custom.common.Constants.addressModel;

/**
 * Created by Administrator on 2/12/2018.
 */

public class PackageOrderActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout linItem;
    TextView txtMore;
    Button btnContinue;
    public static PackageOrderActivity packageOrderActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_package_order);
        packageOrderActivity = this;
        setTitle(getString(R.string.i_want_to_order));
        Constants.page_type = "package";
        if(Constants.packageOrderModel.itemModels.size() == 0){
            Constants.packageOrderModel.itemModels.clear();
            ItemModel itemModel = new ItemModel();
            itemModel.title = getData();
            itemModel.dimension1 = "";
            itemModel.dimension2 = "";
            itemModel.dimension3 = "";
            itemModel.weight = Constants.weight.get(0);
            itemModel.weight_value = Constants.weight_value.get(0);
            itemModel.quantity = Constants.quantity.get(0);
            Constants.packageOrderModel.itemModels.add(itemModel);
        }else{
            Constants.packageOrderModel.itemModels.get(0).title = getData();
        }
        initView();
        updateView();
    }

    private String getData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            return bundle.getString("data");
        }
        return "";
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    private void initView(){
        linItem = (LinearLayout)findViewById(R.id.lin_item);
        txtMore = (TextView)findViewById(R.id.txt_add_more);
        txtMore.setOnClickListener(this);
        btnContinue = (Button)findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(this);
    }

    private  void updateView(){

        if(Constants.packageOrderModel != null && Constants.packageOrderModel.itemModels.size() == 10){
            txtMore.setVisibility(View.GONE);
        }else{
            txtMore.setVisibility(View.VISIBLE);
        }

        linItem.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int k = 0; k < Constants.packageOrderModel.itemModels.size() ; k++){

            ItemModel itemModel = Constants.packageOrderModel.itemModels.get(k);
            View v = layoutInflater.inflate(R.layout.row_package_item, null);
            TextView txtRemove = (TextView)v.findViewById(R.id.txt_remove);
            final int finalK1 = k;
            txtRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Constants.packageOrderModel.itemModels.size() > finalK1 && Constants.packageOrderModel.itemModels.size() > 1 ){
                        ItemModel model = Constants.packageOrderModel.itemModels.get(finalK1);
                        Constants.packageOrderModel.itemModels.remove(model);
                        updateView();
                    }
                }
            });

            final CheckBox chkPackage = (CheckBox)v.findViewById(R.id.chk_package);
            chkPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chkPackage.isChecked()){
                        Constants.packageOrderModel.itemModels.get(finalK1).mPackage = "1";
                    }else{
                        Constants.packageOrderModel.itemModels.get(finalK1).mPackage = "0";
                    }
                }
            });

            final NiceSpinner spQuantity = (NiceSpinner)v.findViewById(R.id.sp_quantity);
            //spQuantity.type = 1;
            spQuantity.setPadding(0, 10,0,10);
            final NiceSpinner spWeight = (NiceSpinner)v.findViewById(R.id.sp_weight);
            //spWeight.type = 2;
            spWeight.setPadding(0, 10,0,10);


            final NiceSpinner spPackage = (NiceSpinner)v.findViewById(R.id.sp_package);
            //spPackage.type = 2;
            spPackage.setPadding(0, 10,0,10);

            spQuantity.attachDataSource(Constants.quantity);
            spWeight.attachDataSource(Constants.weight);
            spPackage.attachDataSource(Constants.packageLists);


            if(!itemModel.quantity.isEmpty()){
                spQuantity.setText(itemModel.quantity);
            }else{
                spQuantity.setSelectedIndex(0);
            }
            if(!itemModel.weight.isEmpty()){
                spWeight.setText(itemModel.weight);
            }else{
                spWeight.setSelectedIndex(0);
            }

            if(!itemModel.title.isEmpty()){
                spPackage.setText(itemModel.title);
            }else{
                spPackage.setSelectedIndex(0);
            }
            if(itemModel.mPackage.equals("1")){
                chkPackage.setChecked(true);
            }

            final int finalK = k;
            spQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Constants.packageOrderModel.itemModels.get(finalK).quantity = Constants.quantity.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            spWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try{
                        Constants.packageOrderModel.itemModels.get(finalK).weight = Constants.weight.get(position);
                        Constants.packageOrderModel.itemModels.get(finalK).weight_value = Constants.weight_value.get(position);
                    }catch (Exception e){};

                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            spPackage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try{
                        Constants.packageOrderModel.itemModels.get(finalK).title = Constants.packageLists.get(position);
                    }catch (Exception e){};

                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            final EditText edtPackage = (EditText)v.findViewById(R.id.edt_package);
            edtPackage.setText(Constants.packageOrderModel.itemModels.get(k).title);
            edtPackage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Constants.packageOrderModel.itemModels.get(finalK).title = edtPackage.getText().toString().trim();
                }
            });
            linItem.addView(v);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_add_more:
                if(Constants.packageOrderModel.itemModels.size() < 10){
                    ItemModel itemModel = new ItemModel();
                    itemModel.title = "";
                    itemModel.dimension1 = "";
                    itemModel.dimension2 = "";
                    itemModel.dimension3 = "";
                    itemModel.weight = Constants.weight.get(0);
                    itemModel.weight_value = Constants.weight_value.get(0);
                    itemModel.quantity = Constants.quantity.get(0);
                    Constants.packageOrderModel.itemModels.add(itemModel);
                    updateView();
                }else{
                    Toast.makeText(this, "Can not upload more", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_continue:

                if(checkInput()){
                    Intent intent = new Intent(this, AddressDetailsNewActivity.class);
                    intent.putExtra("type", "exceed");
                    startActivity(intent);
                }
                break;
        }
    }

    private boolean checkInput(){

        if(Constants.packageOrderModel.itemModels.size() == 0){
            return false;
        }

        for(int k = 0; k < Constants.packageOrderModel.itemModels.size(); k++){
            if(Constants.packageOrderModel.itemModels.get(k).title.isEmpty() || Constants.packageOrderModel.itemModels.get(k).title.trim().isEmpty()){
                Toast.makeText(PackageOrderActivity.this, getString(R.string.input_all), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }



    private void order_quote() {
        RequestParams params = new RequestParams();
        params.put("user_id", PreferenceUtils.getUserId(this));
        params.put("weight", String.valueOf(Constants.getTotalWeight()));
        params.put("distance", "");
        params.put("quote_type", "1"); // is_quote_request
        params.put("device_id", DeviceUtil.getDeviceId(this));
        params.put("device_type", DeviceUtil.getDeviceName());
        // order address
        JSONArray addressArray = new JSONArray();
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("s_address", addressModel.sourceAddress);
        jsonMap.put("s_area", addressModel.sourceArea);
        jsonMap.put("s_city", addressModel.sourceCity);
        jsonMap.put("s_state", addressModel.sourceState);
        jsonMap.put("s_pincode", addressModel.sourcePinCode);
        jsonMap.put("s_phone", addressModel.sourcePhonoe + ":" + addressModel.senderName);
        jsonMap.put("s_landmark", addressModel.sourceLandMark);
        jsonMap.put("s_instruction", addressModel.sourceInstruction);
        jsonMap.put("s_lat", String.valueOf(addressModel.sourceLat));
        jsonMap.put("s_lng", String.valueOf(addressModel.sourceLng));

        jsonMap.put("d_address", addressModel.desAddress);
        jsonMap.put("d_area", addressModel.desArea);
        jsonMap.put("d_city", addressModel.desCity);
        jsonMap.put("d_state", addressModel.desState);
        jsonMap.put("d_pincode", addressModel.desPinCode);
        jsonMap.put("d_landmark", addressModel.desLandMark);
        jsonMap.put("d_instruction", addressModel.desInstruction);
        jsonMap.put("d_lat", String.valueOf(addressModel.desLat));
        jsonMap.put("d_lng", String.valueOf(addressModel.desLng));
        jsonMap.put("d_phone", String.valueOf(addressModel.desPhone));
        jsonMap.put("d_name", String.valueOf(addressModel.desName));

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
                cameraMap.put("dimension", Constants.itemOrderModel.itemModels.get(i).dimension1 + "X" + Constants.itemOrderModel.itemModels.get(i).dimension2 + "X"  +Constants.itemOrderModel.itemModels.get(i).dimension3);
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
        WebClient.post(Constants.BASE_URL + "order_quote", params,
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
                                Constants.clearData();
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(PackageOrderActivity.this, getResources().getString(R.string.quote_message), Toast.LENGTH_LONG).show();
                                    //Toast.makeText(AddressDetailsActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }else if(response.getString("result").equals("200")){
                                    String order_id = response.getString("order_id");
                                    showConfirmDialog();
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


    private  void showConfirmDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        //builder1.setMessage("Write your message here.");
        builder1.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View)inflater.inflate(com.gun0912.tedpicker.R.layout.dialog_forgot, null);
        builder1.setView(view);
        final AlertDialog alert11 = builder1.create();

        TextView txtTitle = (TextView)view.findViewById(com.gun0912.tedpicker.R.id.txt_title);
        TextView txtContent  = (TextView)view.findViewById(com.gun0912.tedpicker.R.id.txt_content);
        txtTitle.setText("");
        txtContent.setText(getResources().getString(R.string.quote_message));

        Button btnOk = (Button)view.findViewById(com.gun0912.tedpicker.R.id.btn_ok);
        btnOk.setText("Ok");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert11.hide();
                Constants.page_type = "";
                PackageOrderActivity.this.finish();

            }
        });
        alert11.show();
    }
}
