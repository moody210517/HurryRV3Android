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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.model.OrderHisModel;
import com.hurry.custom.view.adapter.OrderHistoryAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@SuppressLint("ValidFragment")
public class OrderHistoryFragmentSecond extends BaseFragment implements View.OnClickListener{

    Context mContext;
    RecyclerView rv;

    LinearLayout linNoOrders;
    public  ArrayList<OrderHisModel> hisModels;

    RadioButton rdComplete;
    RadioButton rdPendding;
    RadioButton rdReturned;

    public OrderHistoryFragmentSecond(){
    }

    String type;
    public OrderHistoryFragmentSecond(String type){
        this.position = Integer.valueOf(type);
        this.type = type;
    }

    public void setType(String type){
        this.type = type;
        rdComplete = (RadioButton) view.findViewById(R.id.rd_completed);
        rdPendding = (RadioButton) view.findViewById(R.id.rd_pending);
        rdReturned = (RadioButton) view.findViewById(R.id.rd_returned);
        if(type.equals("1")){
            rdComplete.setChecked(true);
        }else if(type.equals("2")){
            rdPendding.setChecked(true);
        }else{
            rdReturned.setChecked(true);
        }
        setupRecyclerView(rv);
    }
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = (View) inflater.inflate(
                R.layout.fragment_order_his, container, false);
        hisModels = new ArrayList<>();

        rv = (RecyclerView)view.findViewById(R.id.recyclerview);
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

    private void setupRecyclerView(RecyclerView recyclerView) {

        initData();
        if(hisModels!= null && hisModels.size() > 0){
            linNoOrders.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            recyclerView.setAdapter(new OrderHistoryAdapter(getActivity(),
                    hisModels, recyclerView));
//            recyclerView.setNestedScrollingEnabled(false);
        }else{
            linNoOrders.setVisibility(View.VISIBLE);
        }
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


        hisModels.clear();
        for(int k = 0; k < Constants.orderHisModels.size() ; k++){
            String state = Constants.orderHisModels.get(k).state;
            if(state.equals(String.valueOf(type))  || state.equals("3") || state.equals("5") || state.equals("1")){
                if(!(Constants.orderHisModels.get(k).is_quote_request.equals("1") && state.equals("1"))){
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
}
