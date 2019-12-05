package com.hurry.custom.view.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.model.AddressModel;
import com.hurry.custom.model.QuoteCoperationModel;

import static com.hurry.custom.common.Constants.quoteCoperationModels;

/**
 * Created by Administrator on 9/16/2017.
 */


public  class CorporateOrderViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    TextView txtOrder;
    TextView txtTracking;
    CheckBox checkBox;

    TextView txtName;
    TextView txtEmail;
    TextView txtPhone;
    TextView txtDeliver;
    TextView txtLoadType;

    TextView edtSender;
    TextView edtPickAddress;
    TextView edtPickCity;
    TextView edtPickState;
    TextView edtPickPinCode;
    TextView edtPickPhone;
    TextView edtPickLandMark;
    TextView edtPickInstrunction;

    TextView edtDesAddress;
    TextView edtDesCityp;
    TextView edtDesState;
    TextView edtDesPinCode;
    TextView edtDesLandmark;
    TextView edtDesInstruction;
    TextView edtDesPhone;
    TextView edtDesName;

    TextView txtDate;

    ImageView imgExpand;
    LinearLayout linExpand;



    public CorporateOrderViewHolder(View view) {
        super(view);
        mView = view;


        txtOrder = (TextView)view.findViewById(R.id.txt_order_number);
        txtTracking = (TextView)view.findViewById(R.id.txt_tracking);

        checkBox = (CheckBox)view.findViewById(R.id.checkbox);

        txtName = (TextView)view.findViewById(R.id.txt_name);
        txtEmail = (TextView)view.findViewById(R.id.txt_email);
        txtPhone= (TextView)view.findViewById(R.id.txt_phone);
        txtDeliver= (TextView)view.findViewById(R.id.txt_deliver);
        txtLoadType = (TextView)view.findViewById(R.id.txt_load_type);

        edtSender = (TextView)view.findViewById(R.id.edt_sender);
        edtPickAddress = (TextView)view.findViewById(R.id.edt_address);
        edtPickCity = (TextView)view.findViewById(R.id.edt_city);
        edtPickState = (TextView)view.findViewById(R.id.edt_state);
        edtPickPinCode = (TextView)view.findViewById(R.id.edt_pincode);
        edtPickPhone = (TextView)view.findViewById(R.id.edt_phone);
        edtPickLandMark = (TextView)view.findViewById(R.id.edt_landmark);
        edtPickInstrunction = (TextView)view.findViewById(R.id.edt_instruction);
        edtDesAddress = (TextView)view.findViewById(R.id.edt_address_des);
        edtDesCityp = (TextView)view.findViewById(R.id.edt_city_des);
        edtDesState = (TextView)view.findViewById(R.id.edt_state_des);
        edtDesPinCode = (TextView)view.findViewById(R.id.edt_pincode_des);
        edtDesLandmark = (TextView)view.findViewById(R.id.edt_landmark_des);
        edtDesInstruction = (TextView)view.findViewById(R.id.edt_instruction_des);
        edtDesPhone = (TextView)view.findViewById(R.id.edt_phone_des);
        edtDesName = (TextView)view.findViewById(R.id.edt_des_name);

        txtDate = (TextView)view.findViewById(R.id.txt_date);

        imgExpand = (ImageView)view.findViewById(R.id.img_expand);
        linExpand = (LinearLayout)view.findViewById(R.id.lin_expand);
    }


    public void showDetails(int position){
        if(quoteCoperationModels.get(position) != null){
            QuoteCoperationModel model = quoteCoperationModels.get(position);
            txtName.setText(model.name);
            txtEmail.setText(model.address);
            txtDeliver.setText(model.description);
            txtPhone.setText(model.phone);
            txtLoadType.setText(Constants.getTruck(model.loadType));
        }
    }
//        public void showServiceLevel(int position){
//            if(Constants.quoteCoperationModels.get(position).serviceModel != null){
//                ServiceModel serviceModel = Constants.quoteCoperationModels.get(position).serviceModel;
//                txtService.setText("Service Level " + serviceModel.name + "," + serviceModel.price + "," + serviceModel.time_in);
//            }
//        }

    public void showAddressDetais(int  position){
        if(quoteCoperationModels.get(position).addressModel!= null){

            AddressModel addressModel = quoteCoperationModels.get(position).addressModel;

            edtSender.setText(addressModel.senderName);
            edtPickAddress.setText(addressModel.sourceAddress);
            edtPickCity.setText(addressModel.sourceCity);
            edtPickState.setText(addressModel.sourceState);
            edtPickPinCode.setText(addressModel.sourcePinCode);
            edtPickPhone.setText(addressModel.sourcePhonoe);
            edtPickLandMark.setText(addressModel.sourceLandMark);
            if(addressModel.sourceInstruction == null || addressModel.sourceInstruction.isEmpty()){
                edtPickInstrunction.setVisibility(View.GONE);
            }else{
                edtPickInstrunction.setText(addressModel.sourceInstruction);
            }


            edtDesAddress.setText(addressModel.desAddress);
            edtDesCityp.setText(addressModel.desCity);
            edtDesState.setText(addressModel.desState);
            edtDesPinCode.setText(addressModel.desPinCode);
            edtDesLandmark.setText(addressModel.desLandMark);
            if(addressModel.desInstruction == null ||  addressModel.desInstruction.isEmpty()){
                edtDesInstruction.setVisibility(View.GONE);
            }else{
                edtDesInstruction.setText(addressModel.desInstruction);
            }

            edtDesPhone.setText(addressModel.desPhone);
            edtDesName.setText(addressModel.desName);
        }
    }

}

