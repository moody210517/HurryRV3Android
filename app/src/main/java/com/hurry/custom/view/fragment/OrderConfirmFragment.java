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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.view.activity.HomeActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderConfirmFragment extends Fragment implements View.OnClickListener{
    Context mContext;
    @BindView(R.id.txt_order_id) TextView txtOrderId;
    @BindView(R.id.txt_track_id) TextView txtTrackId;
    @BindView(R.id.btn_cancel)
    MaterialButton btnCancel;
    @BindView(R.id.btn_reschedule) MaterialButton btnReschedule;
    @BindView(R.id.btn_call) MaterialButton btnCall;


    String payment, transactionId;

    public OrderConfirmFragment(String payment, String transactionId){
        this.payment = payment;
        this.transactionId = transactionId;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(
                R.layout.activity_order_confirm_new, container, false);
        mContext = getActivity();
        Constants.page_type = "confirm";
        ButterKnife.bind(this, view);
        initView();
        return view;
    }


    private void initView(){

        btnCall.setOnClickListener(this);
        btnReschedule.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        btnCall.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_call_24px, 0, 0);
        btnReschedule.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_schedule_white_48dp, 0, 0);
        btnCancel.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_cancel_white_48dp, 0, 0);

        txtOrderId.setText(getString(R.string.order_id) +  " " + PreferenceUtils.getOrderId(mContext));
        txtTrackId.setText(getString(R.string.track_id)  + " " +PreferenceUtils.getTrackId(mContext));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                Constants.orderHisModels.clear();
                Constants.page_type = "confirm";
                //((HomeActivity)mContext).showViewPager(1);
                Constants.clearData();
                ((HomeActivity)mContext).updateOrderHis(1);

                break;
            case R.id.btn_reschedule:
                Constants.orderHisModels.clear();
                Constants.clearData();
                //((HomeActivity)mContext).showViewPager(1);
                Constants.page_type = "confirm";
                ((HomeActivity)mContext).updateOrderHis(1);
                break;
            case R.id.btn_call:
                Constants.makePhoneCall(mContext, PreferenceUtils.getConfPhone(mContext));
                break;
        }
    }




}
