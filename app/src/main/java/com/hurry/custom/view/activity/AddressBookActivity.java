package com.hurry.custom.view.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.AddressHisModel;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.model.OrderHisModel;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.view.BaseActivity;
import com.hurry.custom.view.BaseBackActivity;
import com.hurry.custom.view.adapter.AddressAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 3/18/2017.
 */

public class AddressBookActivity extends BaseBackActivity implements View.OnClickListener{

    ArrayList<AddressHisModel> allHis = new ArrayList<>();
    RecyclerView recyclerView;
    AddressHisModel addressHisModel;
    AutoCompleteTextView mAutocompleteTextView;
    AddressAdapter contactsAdapter;

    @Override
    public void onCreate(Bundle bundle){

        super.onCreate(bundle);
        setContentView(R.layout.fragment_profile);
        addressHisModel = new AddressHisModel();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initBackButton(toolbar, getString(R.string.my_address_book));

        initView();
        getOrderHistory();

        hidekeyboard();


    }

    private void hidekeyboard(){

        InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE );
        View f = getCurrentFocus();
        if( null != f && null != f.getWindowToken() && EditText.class.isAssignableFrom( f.getClass() ) )
            imm.hideSoftInputFromWindow( f.getWindowToken(), 0 );
        else
            getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
    }

    private void initView(){

        mAutocompleteTextView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        mAutocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                contactsAdapter.getFilter().filter(mAutocompleteTextView.getText().toString());
            }
        });
        findViewById(R.id.btn_ok).setOnClickListener(this);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                hidekeyboard();
                finish();
                break;
            case R.id.btn_ok:
                hidekeyboard();
                clickDone();
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = getIntent();
                finish();
                return true;
            case R.id.action_settings:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  getMenuInflater().inflate(R.menu.sample_actions, menu);
        //  return true;
        getMenuInflater().inflate(R.menu.done_menu, menu);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View v = findViewById(R.id.action_settings);
                if (v != null) {
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickDone();
                        }
                    });
                }
            }
        });
        return true;
    }

    public void setValue(AddressHisModel model, boolean flag){
        if(flag){
            findViewById(R.id.btn_ok).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.btn_ok).setVisibility(View.GONE);
        }
        addressHisModel = model;
    }

    public void clickDone(){
        if(addressHisModel != null){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("location", addressHisModel.address);
            returnIntent.putExtra("lat", String.valueOf(addressHisModel.lat));
            returnIntent.putExtra("lng", String.valueOf(addressHisModel.lng));
            returnIntent.putExtra("pinCode", addressHisModel.pincode);
            returnIntent.putExtra("state", addressHisModel.state);
            returnIntent.putExtra("city", addressHisModel.city);
            returnIntent.putExtra("area", addressHisModel.area);
            returnIntent.putExtra("address", addressHisModel.address);
            returnIntent.putExtra("type", getValue("type"));
            returnIntent.putExtra("landmark", addressHisModel.landmark);
            returnIntent.putExtra("name", addressHisModel.name);
            returnIntent.putExtra("instruction", addressHisModel.instruction);

            returnIntent.putExtra("phone", addressHisModel.phone.contains("+") ? addressHisModel.phone.replace("+","").trim().substring(2) : addressHisModel.phone.trim().substring(2));

            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }


    private void setupRecyclerView() {

        initData();

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        contactsAdapter = new AddressAdapter(AddressBookActivity.this,
                allHis, recyclerView);
        recyclerView.setAdapter(contactsAdapter);

    }




    private void initData(){

        allHis.clear();
        for(int k = 0 ; k <  Constants.orderHisModels.size(); k++){
            OrderHisModel model = Constants.orderHisModels.get(k);
            if(!containsSource(model)){

                AddressHisModel addressHisModel = new AddressHisModel();
                addressHisModel.address = model.addressModel.sourceAddress;
                addressHisModel.area = model.addressModel.sourceArea;
                addressHisModel.city = model.addressModel.sourceCity;
                addressHisModel.landmark = model.addressModel.sourceLandMark;
                addressHisModel.pincode = model.addressModel.sourcePinCode;
                addressHisModel.phone = model.addressModel.sourcePhonoe;
                addressHisModel.lat = model.addressModel.sourceLat;
                addressHisModel.lng = model.addressModel.sourceLng;
                addressHisModel.state = model.addressModel.sourceState;
                addressHisModel.name = model.addressModel.senderName;
                addressHisModel.instruction = model.addressModel.sourceInstruction;

                allHis.add(addressHisModel);
            }
            if(!containsDes(model)){

                AddressHisModel addressHisModel = new AddressHisModel();

                addressHisModel.address = model.addressModel.desAddress;
                addressHisModel.area = model.addressModel.desArea;
                addressHisModel.city = model.addressModel.desCity;
                addressHisModel.landmark = model.addressModel.desLandMark;
                addressHisModel.pincode = model.addressModel.desPinCode;
                addressHisModel.phone = model.addressModel.desPhone;
                addressHisModel.lat = model.addressModel.desLat;
                addressHisModel.lng = model.addressModel.desLng;
                addressHisModel.state = model.addressModel.desState;
                addressHisModel.name = model.addressModel.desName;
                addressHisModel.instruction = model.addressModel.desInstruction;
                allHis.add(addressHisModel);
            }
        }
    }


    private boolean containsSource(OrderHisModel model){
        for(int k = 0; k < allHis.size(); k++){


            if( allHis.get(k).address.trim().equals(model.addressModel.sourceAddress.trim())  && allHis.get(k).address.trim().length() == model.addressModel.sourceAddress.trim().length()){

                Log.d(allHis.get(k).address.trim(),model.addressModel.sourceAddress.trim()) ;
                //(allHis.get(k).lat == model.addressModel.sourceLat  && allHis.get(k).lng == model.addressModel.sourceLng )  ||
                return true;
            }
        }
        if( Constants.addressModel.sourceLat == model.addressModel.sourceLat  && Constants.addressModel.sourceLng  == model.addressModel.sourceLng ){
            return true;
        }
        if( Constants.addressModel.desLat == model.addressModel.sourceLat  && Constants.addressModel.desLng == model.addressModel.sourceLng ){
            return true;
        }
        return false;
    }



    private boolean containsDes(OrderHisModel model){
        for(int k = 0; k < allHis.size(); k++){

            if(  allHis.get(k).address.trim().equals(model.addressModel.desAddress.trim())  && allHis.get(k).address.trim().length() ==  model.addressModel.desAddress.trim().length()){
                Log.d(allHis.get(k).address.trim(),model.addressModel.desAddress.trim()) ;
                //(allHis.get(k).lat == model.addressModel.desLat  && allHis.get(k).lng == model.addressModel.desLng)  ||
                return true;
            }
        }
        
        if( Constants.addressModel.sourceLat == model.addressModel.desLat  && Constants.addressModel.sourceLng  == model.addressModel.desLng ){
            return true;
        }
        if( Constants.addressModel.desLat == model.addressModel.desLat  && Constants.addressModel.desLng == model.addressModel.desLng ){
            return true;
        }

        return false;

    }



    private void getOrderHistory() {

        RequestParams params = new RequestParams();
        params.put("user_id", PreferenceUtils.getUserId(AddressBookActivity.this));

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



                                setupRecyclerView();

                            }else if(response.getString("result").equals("400")){
                                Toast.makeText(AddressBookActivity.this , getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
