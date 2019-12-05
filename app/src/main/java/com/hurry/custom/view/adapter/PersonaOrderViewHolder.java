package com.hurry.custom.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.utils.ImageLoaderHelper;
import com.hurry.custom.model.AddressModel;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.model.QuoteModel;
import com.hurry.custom.model.ServiceModel;

import java.util.ArrayList;

/**
 * Created by Administrator on 9/16/2017.
 */


public  class PersonaOrderViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    TextView txtQuote;
    LinearLayout linItems;

    CheckBox checkBox;

    TextView txtOrder;
    TextView txtTracking;
    TextView txtPayment;

    TextView edtSender;
    TextView edtReceiver;

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


    LinearLayout linItem;
    LinearLayout linColumn;

    TextView txtDate;
    TextView txtService;

    ImageView imgExpand;
    Button btnTrack;
    LinearLayout linExpand;

    Context mContext;

    public PersonaOrderViewHolder(View view, Context mContext) {
        super(view);
        mView = view;
        this.mContext = mContext;

        txtQuote = (TextView)view.findViewById(R.id.txt_quote);
        linItems = (LinearLayout)view.findViewById(R.id.lin_items);
        checkBox = (CheckBox)view.findViewById(R.id.checkbox);


        txtOrder = (TextView)view.findViewById(R.id.txt_order_number);
        txtTracking = (TextView)view.findViewById(R.id.txt_tracking);
        txtPayment = (TextView)view.findViewById(R.id.txt_payment_method);

        linItem = (LinearLayout)view.findViewById(R.id.lin_item);
        linColumn = (LinearLayout)view.findViewById(R.id.lin_column);

        edtSender = (TextView)view.findViewById(R.id.edt_sender);
        edtReceiver = (TextView)view.findViewById(R.id.edt_des_name);
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

        txtDate = (TextView)view.findViewById(R.id.txt_date);
        txtService = (TextView)view.findViewById(R.id.txt_service);

        imgExpand = (ImageView)view.findViewById(R.id.img_expand);
        btnTrack = (Button)view.findViewById(R.id.btn_track);
        linExpand = (LinearLayout)view.findViewById(R.id.lin_expand);

    }

    public void showServiceLevel(ArrayList<QuoteModel> mValues, int position){
        if(mValues.get(position).serviceModel != null){
            ServiceModel serviceModel = mValues.get(position).serviceModel;
            txtService.setText("Service Level: " + serviceModel.name + "," + Constants.getPrice(mContext, serviceModel.price) + "," + serviceModel.time_in);
        }
    }



    public void showAddressDetais(ArrayList<QuoteModel> mValues, int  position){
        if(mValues.get(position).addressModel!= null){
            AddressModel addressModel = mValues.get(position).addressModel;
            edtSender.setText(addressModel.senderName);
            edtReceiver.setText(addressModel.desName);
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
            if(addressModel.desInstruction == null || addressModel.desInstruction.isEmpty()){
                edtDesInstruction.setVisibility(View.GONE);
            }else{
                edtDesInstruction.setText(addressModel.desInstruction);
            }
            edtDesPhone.setText(addressModel.desPhone);
        }
    }




    public  void showItemLists(ArrayList<QuoteModel> mValues, int position, final Context context, final View myView){

        int  option = mValues.get(position).orderModel.product_type;
        final OrderModel orderModel = mValues.get(position).orderModel;

        if(option == Constants.CAMERA_OPTION){

            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewCol = inflater.inflate(R.layout.column_camera, null);
            viewCol.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linColumn.addView(viewCol);
            linItem.removeAllViews();
            if(orderModel != null){
                for(int k = 0;  k < orderModel.itemModels.size(); k++){

                    View view = inflater.inflate(R.layout.row_review_camera, null);
                    ImageView img = (ImageView) view.findViewById(R.id.image);
                    TextView txtQuantity = (TextView)view.findViewById(R.id.txt_quantity);
                    TextView txtWeight = (TextView)view.findViewById(R.id.txt_weight);
                    ImageLoaderHelper.showImage(context, orderModel.itemModels.get(k).image, img);


                    final int finalK = k;
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Constants.showImage(context, myView, orderModel.itemModels.get(finalK).image, "url");
                        }
                    });

                    txtQuantity.setText(orderModel.itemModels.get(k).quantity);
                    txtWeight.setText(orderModel.itemModels.get(k).weight);
                    linItem.addView(view);
                    View line = inflater.inflate(R.layout.divider, null);
                    if(k < orderModel.itemModels.size() -1){
                        linItem.addView(line);
                    }
                }
            }


        }else if(option== Constants.ITEM_OPTION){

            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewCol = inflater.inflate(R.layout.column_item, null);
            viewCol.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linColumn.addView(viewCol);
            linItem.removeAllViews();
            if(orderModel != null){
                for(int k = 0;  k < orderModel.itemModels.size(); k++){

                    View view = inflater.inflate(R.layout.row_review_item, null);
                    TextView txtItem = (TextView)view.findViewById(R.id.txt_item);
                    TextView txtDimension = (TextView)view.findViewById(R.id.txt_item);
                    TextView txtQuantity = (TextView)view.findViewById(R.id.txt_quantity);
                    TextView txtWeight = (TextView)view.findViewById(R.id.txt_weight);
                    txtItem.setText(orderModel.itemModels.get(k).title);
                    txtDimension.setText(Constants.getDimentionString(orderModel.itemModels.get(k).dimension1,orderModel.itemModels.get(k).dimension2 ,orderModel.itemModels.get(k).dimension3));
                    txtQuantity.setText(orderModel.itemModels.get(k).quantity);
                    txtWeight.setText(orderModel.itemModels.get(k).weight);
                    linItem.addView(view);
                    View line = inflater.inflate(R.layout.divider, null);
                    if(k < orderModel.itemModels.size() -1){
                        linItem.addView(line);
                    }

                }
            }


        }else if(option== Constants.PACKAGE_OPTION){

            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewCol = inflater.inflate(R.layout.row_review_package, null);
            viewCol.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linColumn.addView(viewCol);
            linItem.removeAllViews();
            if(orderModel != null){
                for(int k = 0;  k < orderModel.itemModels.size(); k++){

                    View view = inflater.inflate(R.layout.row_review_package, null);
                    TextView txtItem = (TextView)view.findViewById(R.id.txt_item);
                    TextView txtQuantity = (TextView)view.findViewById(R.id.txt_quantity);
                    TextView txtWeight = (TextView)view.findViewById(R.id.txt_weight);
                    txtItem.setText(orderModel.itemModels.get(k).title);
                    txtQuantity.setText(orderModel.itemModels.get(k).quantity);
                    txtWeight.setText(orderModel.itemModels.get(k).weight);
                    linItem.addView(view);
                    View line = inflater.inflate(R.layout.divider, null);
                    if(k < orderModel.itemModels.size() -1){
                        linItem.addView(line);
                    }

                }
            }
        }

    }

}



