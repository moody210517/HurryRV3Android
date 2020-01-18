package com.hurry.custom.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.model.OrderHisModel;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.adapter.OrderHistoryAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@SuppressLint("ValidFragment")
public class OrderHistoryFragment extends BaseFragment implements View.OnClickListener{

    Context mContext;
    RecyclerView rv;

    LinearLayout linNoOrders;
    public  ArrayList<OrderHisModel> hisModels;

    RadioButton rdComplete;
    RadioButton rdPendding;
    RadioButton rdReturned;

    public OrderHistoryFragment(){
    }

    String type;
    public OrderHistoryFragment(String type){
        this.position = Integer.valueOf(type);
        this.type = type;
    }

    public void setType(String type){
        this.type = type;

    }
    View view;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (View) inflater.inflate(
                R.layout.fragment_order_his, container, false);
        hisModels = new ArrayList<>();

        if (savedInstanceState != null) {
            type = savedInstanceState.getString("type", "1");
        }

        rv = (RecyclerView)view.findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);

        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayout.VERTICAL);
        rv.setLayoutManager(MyLayoutManager);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = MyLayoutManager.getChildCount();
                    totalItemCount = MyLayoutManager.getItemCount();
                    pastVisiblesItems = MyLayoutManager.findFirstVisibleItemPosition();

                }
            }
        });


        linNoOrders = (LinearLayout)view.findViewById(R.id.lin_no_order);

        rdComplete = (RadioButton) view.findViewById(R.id.rd_completed);
        rdPendding = (RadioButton) view.findViewById(R.id.rd_pending);
        rdReturned = (RadioButton) view.findViewById(R.id.rd_returned);

        rdComplete.setVisibility(View.GONE);
        rdPendding.setVisibility(View.GONE);
        rdReturned.setVisibility(View.GONE);

        mContext = getActivity();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putString("type", type);
    }

    @Override
    public void onResume(){
        super.onResume();
        try{
            if(type.equals("1")){
                rdComplete.setChecked(true);
            }else if(type.equals("2")){
                rdPendding.setChecked(true);
            }else{
                rdReturned.setChecked(true);
            }

            setupRecyclerView(rv);
        }catch (Exception e){};

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    OrderHistoryAdapter adapter;
    private void setupRecyclerView(RecyclerView recyclerView) {

//        if(Constants.orderHisModels.size() == 0){
//            orderHistory(0);
//        }else{
//
//        }

        initData();
        if(hisModels!= null && hisModels.size() > 0){
            linNoOrders.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            adapter = new OrderHistoryAdapter(getActivity(),
                    hisModels, recyclerView);
            recyclerView.setAdapter(adapter);
//            recyclerView.setNestedScrollingEnabled(false);
        }else{
            linNoOrders.setVisibility(View.VISIBLE);
        }
    }


    public void updateData(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rd_completed:
                setupRecyclerView(rv);
                break;
            case R.id.rd_pending:
                setupRecyclerView(rv);
                break;
            case R.id.rd_returned:
                setupRecyclerView(rv);
                break;
        }
    }

    public void initData(){
        int type = 0;
        if(rdComplete.isChecked()){
            type = 4;
        }else if(rdPendding.isChecked()){
            type = 2;
        }else if(rdReturned.isChecked()){
            type = 6;
        }


        hisModels.clear();
        for(int k = 0; k < Constants.orderHisModels.size() ; k++){
            String state = Constants.orderHisModels.get(k).state;
            if(type == 2){
                if(state.equals(String.valueOf(type))  || state.equals("3") || state.equals("5") || state.equals("1")){
                    if(!(Constants.orderHisModels.get(k).is_quote_request.equals("1") && state.equals("1"))){
                        OrderHisModel model = Constants.orderHisModels.get(k);
                        hisModels.add(model);
                    }
                }
            }else if(type == 4){
                if(state.equals(String.valueOf(type))  || state.equals("0") ){
                    OrderHisModel model = Constants.orderHisModels.get(k);
                    hisModels.add(model);
                }
            }else{
                if(state.equals(String.valueOf(type)) ){
                    OrderHisModel model = Constants.orderHisModels.get(k);
                    hisModels.add(model);
                }
            }
        }

        // sort by id
        Collections.sort(hisModels, new ContactComparator());
    }


    public class ContactComparator implements Comparator<OrderHisModel> {
        public int compare(OrderHisModel contact1, OrderHisModel contact2) {
            //In the following line you set the criterion,
            //which is the name of Contact in my example scenario

            int first = Integer.valueOf(contact1.orderId);
            int second = Integer.valueOf(contact2.orderId);

            if(first > second){
                return -1;
            }else if(first < second){
                return 1;
            }else{
                return 0;
            }
            //return contact1.orderId.compareTo(contact2.orderId);
        }
    }

    public void orderHistory(final int index) {
        RequestParams params = new RequestParams();
        params.put("user_id", PreferenceUtils.getUserId(mContext));
        WebClient.post(Constants.BASE_URL_ORDER + "get_orders_his", params,
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

                                String result = response.getString("result");
                                if(result.equals("400")){
                                    setupRecyclerView(rv);
                                    return;
                                }

                                JSONArray jsonArray = response.getJSONArray("orders");
                                Constants.orderHisModels.clear();

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

                                setupRecyclerView(rv);


                            }else if(response.getString("result").equals("400")){
                                Toast.makeText(mContext , getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
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

}