package com.hurry.custom.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hurry.custom.R;
import com.hurry.custom.view.activity.HomeActivity;


public class CommonDialog {


    public static  void showChooseAddress(final Context context, final String type){

        final Dialog dialog = new Dialog(context);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_address);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btnMyAddress = (Button)dialog.findViewById(R.id.btn_my_address);
        Button btnNewAddress = (Button)dialog.findViewById(R.id.btn_new_address);

        btnMyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Constants.orderHisModels.size() == 0){
                    Toast.makeText(context, "No saved addresses available", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(type.equals("source")){

                    if(context instanceof HomeActivity){
                        ((HomeActivity)context).goToProfileSource();
                    }
                }else{
                    if(context instanceof  HomeActivity){
                        ((HomeActivity)context).goToProfileDestination();
                    }
                }
                dialog.dismiss();
            }
        });

        if(Constants.orderHisModels.size() == 0){
            //btnMyAddress.setEnabled(false);
            btnMyAddress.setBackgroundResource(R.drawable.btn_dark_circle);
        }

        btnNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type.equals("source")){
                    if(context instanceof  HomeActivity){
                        ((HomeActivity)context).goToMapSource();
                    }
                }else{
                    if(context instanceof  HomeActivity){
                        ((HomeActivity)context).goToMapDestination();
                    }
                }
                dialog.dismiss();

            }
        });

        final ImageView imgClose = (ImageView)dialog.findViewById(R.id.img_close);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
