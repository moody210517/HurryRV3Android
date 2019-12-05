package com.hurry.custom.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.fragment.app.FragmentActivity;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.utils.DeviceUtil;

/**
 * Created by Administrator on 3/22/2017.
 */

public class FragmentBaseActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        Constants.initWeight();
        DeviceUtil.setStatusBarColor(this);
    }


    ProgressDialog mProgressDialog;
    public void showProgressDialog() {

        mProgressDialog = null;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);

        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showProgressDialog();
            }
        });

    }

    public void showProgressDialog(final String title) {

        mProgressDialog = null;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(title);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showProgressDialog(title);
            }
        });
        mProgressDialog.setCanceledOnTouchOutside(false);
    }


    @Override
    public void onBackPressed() {
        return;
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void setTitle(String title){
        TextView txtTitle = (TextView)findViewById(R.id.txt_title);
        txtTitle.setText(title);
        ImageView img = (ImageView)findViewById(R.id.img_back);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceUtil.hideSoftKeyboard(FragmentBaseActivity.this);
                finish();
            }
        });
    }


    public String  getValue(String type){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            return bundle.getString( type);
        }
        return "";
    }

    public void initBackButton(Toolbar toolbar, String title){
        toolbar.setTitle(title);

        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
