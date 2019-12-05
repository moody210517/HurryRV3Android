package com.hurry.custom.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.view.BaseBackActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderConfirmNewActivity extends BaseBackActivity implements View.OnClickListener{


    @BindView(R.id.txt_order_id) TextView txtOrderId;
    @BindView(R.id.txt_track_id) TextView txtTrackId;
    @BindView(R.id.btn_cancel) MaterialButton btnCancel;
    @BindView(R.id.btn_reschedule) MaterialButton btnReschedule;
    @BindView(R.id.btn_call) MaterialButton btnCall;
    @BindView(R.id.toolbar) Toolbar toolbar;


    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_order_confirm_new);
        ButterKnife.bind(this);
        initView();
        initBackButton(toolbar, getString(R.string.order_confirmed));

    }

    private void initView(){

        btnCall.setOnClickListener(this);
        btnReschedule.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        btnCall.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_call_24px, 0, 0);
        btnReschedule.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_schedule_white_48dp, 0, 0);
        btnCancel.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_cancel_white_48dp, 0, 0);

        txtOrderId.setText(getString(R.string.order_id) +  " " + PreferenceUtils.getOrderId(this));
        txtTrackId.setText(getString(R.string.track_id)  + " " +PreferenceUtils.getTrackId(this));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("page", "cancel");
                startActivity(intent);
                finish();
                break;
            case R.id.btn_reschedule:
                Intent intent1 = new Intent(this, HomeActivity.class);
                intent1.putExtra("page", "reschedule");
                startActivity(intent1);
                finish();
                break;
            case R.id.btn_call:
                Constants.makePhoneCall(this, PreferenceUtils.getConfPhone(this));
                break;
        }
    }
}
