
package com.hurry.custom.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.model.OrderHisModel;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.custom.IconTextTabLayout;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderHisContainerFragment extends BaseFragment {

    @BindView(R.id.tabs) IconTextTabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;
    Context mContext;
    View view;
    FragmentManager fragmentManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = (View) inflater.inflate(
                R.layout.fragment_order_his_container, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        position = 1;
        fragmentManager = getChildFragmentManager();
        if(Constants.orderHisModels.size() == 0){
            orderHistory(0);
        }else{
            setUpPersonalOrderHis( 0);
        }
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        mContext = getActivity();

    }
    @Override
    public void onResume(){
        super.onResume();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        try{
            ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction().remove(orderHistoryFragment1).commit();
            ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction().remove(orderHistoryFragment2).commit();
            ((HomeActivity)mContext).getSupportFragmentManager().beginTransaction().remove(orderHistoryFragment3).commit();
        }catch (Exception e){};


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
                                    setUpPersonalOrderHis(index);
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

                                setUpPersonalOrderHis( index);


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

    OrderHistoryFragment orderHistoryFragment1;
    OrderHistoryFragment orderHistoryFragment2;
    OrderHistoryFragment orderHistoryFragment3;
    public void setUpPersonalOrderHis(int index) {


        viewPager.removeAllViews();
        tabLayout.setupWithViewPager(viewPager);

        Adapter adapter = new Adapter(fragmentManager);

        orderHistoryFragment1 = new OrderHistoryFragment("1");
        orderHistoryFragment2 = new OrderHistoryFragment("2");
        orderHistoryFragment3 = new OrderHistoryFragment("3");


        adapter.addFragment(orderHistoryFragment1, "First");
        adapter.addFragment(orderHistoryFragment2, "Second");
        adapter.addFragment(orderHistoryFragment3, "Third");

        orderHistoryFragment1.setType("1");
        orderHistoryFragment2.setType("2");
        orderHistoryFragment3.setType("3");

        viewPager.setAdapter(adapter);
        tabLayout.setTabsFromPagerAdapter(adapter);
        //tabLayout.setTabsFromPagerAdapter(adapter);
        //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        if( Constants.page_type.equals("confirm")){
            viewPager.setCurrentItem(1);
        }else{
            viewPager.setCurrentItem(0);
        }

        Constants.page_type = "history";
    }

    private void repop(ViewPager viewPager, int index) {

        viewPager.removeAllViews();
        tabLayout.setupWithViewPager(viewPager);

        Adapter adapter = new Adapter(((HomeActivity)mContext).getSupportFragmentManager());

        OrderHistoryFragment orderHistoryFragment1 = new OrderHistoryFragment("1");
        OrderHistoryFragment orderHistoryFragment2 = new OrderHistoryFragment("2"); //OrderHistoryFragmentSecond
        OrderHistoryFragment orderHistoryFragment3 = new OrderHistoryFragment("3");

        adapter.addFragment(orderHistoryFragment1, "First");
        adapter.addFragment(orderHistoryFragment2, "Second");
        adapter.addFragment(orderHistoryFragment3, "Third");

        viewPager.setAdapter(adapter);

        tabLayout.setTabsFromPagerAdapter(adapter);
        //tabLayout.setTabsFromPagerAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setCurrentItem(0);

    }


    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragments.get(position);
        }

//        public int getItemPosition(Object item) {
//            BaseFragment fragment = (BaseFragment)item;
//
//            int position = fragment.position;
//
//            if (position >= 0) {
//                return position;
//            } else {
//                return POSITION_NONE;
//            }
//        }


        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}