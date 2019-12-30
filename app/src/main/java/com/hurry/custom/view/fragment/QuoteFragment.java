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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hurry.custom.model.ItemModel;
import com.hurry.custom.view.activity.HomeActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.QuoteModel;
import com.hurry.custom.view.activity.DateTimeActivity;
import com.hurry.custom.view.activity.MainActivity;
import com.hurry.custom.view.adapter.QuoteAdapter;

public class QuoteFragment extends Fragment implements View.OnClickListener{


    Context mContext;
    Button btnContinue;
    RecyclerView rv;
    LinearLayout linNoOrders;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = (View) inflater.inflate(
                R.layout.fragment_quote, container, false);
        rv = (RecyclerView)view.findViewById(R.id.recyclerview);
        Constants.page_type = "quote";
        linNoOrders = (LinearLayout)view.findViewById(R.id.lin_no_order);
        mContext = getActivity();

        initView(view);
        if(Constants.quoteModels.size() > 0){
            setupRecyclerView(rv);
        }else{
            getQuote();
        }
        return view;
    }

    private void initView(View view){
        btnContinue = (Button)view.findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(this);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        if(Constants.quoteModels != null && Constants.quoteModels.size() > 0){
            linNoOrders.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            recyclerView.setAdapter(new QuoteAdapter(getActivity(),
                    Constants.quoteModels,recyclerView));
        }else{
            linNoOrders.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_continue:
                if(check()){
                    if( !Constants.quote_id.isEmpty()){
                        getService();
//                        Intent dateIntent = new Intent(mContext, DateTimeActivity.class);
//                        PreferenceUtils.setQuote(mContext, true);
//                        startActivity(dateIntent);
                    }else{
                        Toast.makeText(mContext, "Invalid Quote Id", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(mContext, "Choose a Order", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean check(){
        for(int  k = 0; k < Constants.quoteModels.size();k++){
            if(Constants.quoteModels.get(k).selection == true){ // && Constants.quoteModels.get(k).service_choose == true

                return true;
            }
        }
        return false;
    }

    private void getQuote() {
        clearQuote();
        RequestParams params = new RequestParams();
        params.put("user_id", PreferenceUtils.getUserId(mContext));

        WebClient.post(Constants.BASE_URL + "get_quote", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        if(mContext instanceof MainActivity)
                            ((MainActivity)mContext).showProgressDialog();
                        if(mContext instanceof HomeActivity)
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


                                JSONArray jsonArray = response.getJSONArray("orders");
                                clearQuote();

                                for (int k= 0; k < jsonArray.length() ; k++){

                                    QuoteModel quoteModel = new QuoteModel();
                                    try{
                                        JSONObject object = jsonArray.getJSONObject(k);

                                        quoteModel.orderId = object.getString("id");
                                        quoteModel.trackId = object.getString("track");
                                        quoteModel.dateModel.date = object.getString("date");
                                        quoteModel.dateModel.time = object.getString("time");
                                        quoteModel.serviceModel.name = object.getString("service_name");
                                        quoteModel.serviceModel.price = object.getString("service_price");
                                        quoteModel.serviceModel.time_in = object.getString("service_timein");
                                        quoteModel.quote_id = object.getString("quote_id");

                                        JSONArray addressArray = object.getJSONArray("address");

                                        for(int i = 0; i < addressArray.length(); i++){
                                            JSONObject addressObj = addressArray.getJSONObject(i);
                                            quoteModel.addressModel.sourceAddress = addressObj.getString("s_address");
                                            quoteModel.addressModel.sourceCity = addressObj.getString("s_city");
                                            quoteModel.addressModel.sourceState = addressObj.getString("s_state");
                                            quoteModel.addressModel.sourcePinCode = addressObj.getString("s_pincode");

                                            quoteModel.addressModel.sourceLat = Double.valueOf(addressObj.getString("s_lat"));
                                            quoteModel.addressModel.sourceLng = Double.valueOf(addressObj.getString("s_lng"));
                                            quoteModel.addressModel.desLat = Double.valueOf(addressObj.getString("d_lat"));
                                            quoteModel.addressModel.desLng = Double.valueOf(addressObj.getString("d_lng"));


                                            try{
                                                String res = addressObj.getString("s_phone");
                                                quoteModel.addressModel.sourcePhonoe = res;
                                                String[] resArray = res.split(":");
                                                if(resArray.length == 2){
                                                    quoteModel.addressModel.sourcePhonoe = resArray[0];
                                                    quoteModel.addressModel.senderName = resArray[1];
                                                }
                                            }catch (Exception e){};


                                            //quoteModel.addressModel.sourcePhonoe = addressObj.getString("s_phone");
                                            quoteModel.addressModel.sourceLandMark = addressObj.getString("s_landmark");
                                            quoteModel.addressModel.sourceInstruction = addressObj.getString("s_instruction");

                                            quoteModel.addressModel.desAddress = addressObj.getString("d_address");
                                            quoteModel.addressModel.desCity = addressObj.getString("d_city");
                                            quoteModel.addressModel.desState = addressObj.getString("d_state");

                                            quoteModel.addressModel.desPinCode = addressObj.getString("d_pincode");
                                            quoteModel.addressModel.desLandMark = addressObj.getString("d_landmark");
                                            quoteModel.addressModel.desInstruction = addressObj.getString("d_instruction");
                                            quoteModel.addressModel.desPhone = addressObj.getString("d_phone");
                                            quoteModel.addressModel.desName = addressObj.getString("d_name");
                                        }

                                        JSONArray productArray = object.getJSONArray("products");
                                        quoteModel.orderModel.itemModels.clear();
                                        for(int j = 0; j < productArray.length(); j++){
                                            JSONObject productObj = productArray.getJSONObject(j);
                                            int product_type = productObj.getInt("product_type");
                                            if(product_type == Constants.CAMERA_OPTION){
                                                quoteModel.orderModel.product_type = product_type;
                                                ItemModel model = new ItemModel();
                                                model.image = productObj.getString("image");
                                                model.quantity  = productObj.getString("quantity");
                                                model.weight = productObj.getString("weight");
                                                quoteModel.orderModel.itemModels.add(model);

                                            }else if(product_type == Constants.ITEM_OPTION){
                                                quoteModel.orderModel.product_type = product_type;
                                                ItemModel model = new ItemModel();
                                                model.title = productObj.getString("title");
                                                String[] dim = productObj.getString("dimension").split("X");
                                                model.dimension1 = dim[0];
                                                model.dimension2 = dim[1];
                                                model.dimension3 = dim[2];
                                                model.quantity  = productObj.getString("quantity");
                                                model.weight = productObj.getString("weight");
                                                quoteModel.orderModel.itemModels.add(model);

                                            }else if(product_type == Constants.PACKAGE_OPTION){
                                                quoteModel.orderModel.product_type = product_type;
                                                ItemModel model = new ItemModel();
                                                model.title = productObj.getString("title");
                                                model.quantity  = productObj.getString("quantity");
                                                model.weight = productObj.getString("weight");
                                                quoteModel.orderModel.itemModels.add(model);
                                            }
                                        }
                                        if(quoteModel.dateModel.date.isEmpty() || quoteModel.dateModel.time.isEmpty()){

                                        }

                                    }catch (Exception e){};


                                    Constants.quoteModels.add(quoteModel);
                                }
                                setupRecyclerView(rv);
                            }else if(response.getString("result").equals("400")){
                                //Toast.makeText(mContext , getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                linNoOrders.setVisibility(View.VISIBLE);
                                clearQuote();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            linNoOrders.setVisibility(View.VISIBLE);
                            clearQuote();
                        }
                    };
                    public void onFinish() {

                        if(mContext instanceof MainActivity)
                            ((MainActivity)mContext).hideProgressDialog();
                        if(mContext instanceof HomeActivity)
                            ((HomeActivity)mContext).hideProgressDialog();
                    }
                    ;
                });
    }


    private  void clearQuote(){
        for(int k = 0; k < Constants.quoteModels.size(); k++){
            Constants.quoteModels.get(k).orderModel.itemModels.clear();
        }
        Constants.quoteModels.clear();
    }

    private void getService() {
        RequestParams params = new RequestParams();
        params.put("quote_id", Constants.quote_id);
        WebClient.post(Constants.BASE_URL + "get_service", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {

                        if(mContext instanceof  MainActivity)
                            ((MainActivity)mContext).showProgressDialog();
                        if(mContext instanceof  HomeActivity)
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

                                if(response.getString("result").equals("200")){
                                    JSONArray jsonArray = response.getJSONArray("service");
                                    JSONObject serviceObj = jsonArray.getJSONObject(0);

                                    Constants.priceType.expeditied_price = serviceObj.getString("expedited_price");
                                    Constants.priceType.express_price = serviceObj.getString("express_price");
                                    Constants.priceType.economy_price = serviceObj.getString("economy_price");

//                                    Constants.priceType.expedited_duration = serviceObj.getString("expedited_duration");
//                                    Constants.priceType.express_duration = serviceObj.getString("express_duration");
//                                    Constants.priceType.economy_duraiton = serviceObj.getString("economy_duration");

                                    Intent dateIntent = new Intent(mContext, DateTimeActivity.class);
                                    PreferenceUtils.setQuote(mContext, true);
                                    PreferenceUtils.setTrackId(mContext, Constants.trackId);
                                    startActivity(dateIntent);
                                }

                                if(response.getString("result").equals("400")){
                                    Toast.makeText(mContext , getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    };
                    public void onFinish() {
                        if(mContext instanceof  MainActivity)
                            ((MainActivity)mContext).hideProgressDialog();
                        if(mContext instanceof  HomeActivity)
                            ((HomeActivity)mContext).hideProgressDialog();
                    }
                    ;
                });
    }


}
