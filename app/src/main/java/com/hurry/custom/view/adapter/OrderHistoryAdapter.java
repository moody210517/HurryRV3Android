package com.hurry.custom.view.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.utils.ImageLoaderHelper;
import com.hurry.custom.common.utils.TimeHelper;
import com.hurry.custom.controller.CancelPaymentProcess;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.AddressModel;
import com.hurry.custom.model.OrderHisModel;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.model.ServiceModel;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.MainActivity;
import com.hurry.custom.view.activity.map.LocationMapActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */

public class OrderHistoryAdapter
        extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private ArrayList<OrderHisModel> mValues;
    private Context context;
    View myView;

    public OrderHistoryAdapter(Context context, ArrayList<OrderHisModel> items, View view) {
        mBackground = mTypedValue.resourceId;
        mValues = items;
        this.context = context;
        this.myView = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_his, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtOrder.setText(mValues.get(position).orderId);
        holder.txtTracking.setText(mValues.get(position).trackId);
        holder.btnCancel.setVisibility(View.GONE);
        holder.btnReschedule.setVisibility(View.GONE);
        final String status = mValues.get(position).state;
        holder.txtStep2.setText(context.getString(R.string.step2));

        if(status.equals("0")){
            holder.txtStatus.setText("Order Cancel");
            holder.imgStep.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.step_cancel1));
            holder.imgDirection.setVisibility(View.GONE);
            holder.btnTrack.setVisibility(View.GONE);

            holder.txtStep1.setText(context.getString(R.string.pickup_cancelled));
            holder.txtStep2.setText("");
            holder.txtStep3.setText("");//context.getString(R.string.order_delivered)

        }else if(status.equals("2")){
            holder.txtStatus.setText("Associate on the way for pickup");
            holder.imgStep.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.step1));
            holder.imgDirection.setVisibility(View.VISIBLE);
            holder.btnTrack.setVisibility(View.VISIBLE);
            holder.txtStep3.setText(context.getString(R.string.order_delivered));
        }else if(status.equals("3")){
            holder.txtStatus.setText("On the way to destination");
            holder.imgStep.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.step2));
            holder.imgDirection.setVisibility(View.VISIBLE);
            holder.btnTrack.setVisibility(View.VISIBLE);
            holder.txtStep3.setText(context.getString(R.string.order_delivered));
        }else if(status.equals("4")){
            holder.txtStatus.setText("Order Delivered");
            holder.imgStep.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.step3));
            holder.imgDirection.setVisibility(View.GONE);
            holder.btnTrack.setVisibility(View.GONE);
            holder.txtStep3.setText(context.getString(R.string.order_delivered));
        }else if(status.equals("5")){
            holder.txtStatus.setText("Order on hold");
            holder.imgStep.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.step_onhold));
            holder.imgDirection.setVisibility(View.VISIBLE);
            holder.btnTrack.setVisibility(View.VISIBLE);
            holder.txtStep3.setText(context.getString(R.string.order_on_hold));
        }else if(status.equals("6")){
            holder.txtStatus.setText("Returned Order");
            holder.imgStep.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.step_return));
            holder.imgDirection.setVisibility(View.GONE);
            holder.btnTrack.setVisibility(View.GONE);
            holder.txtStep3.setText(context.getString(R.string.order_returned));
        }else if(status.equals("1")){
            holder.txtStatus.setText("In Progress");
            holder.imgStep.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.step_inprogress));
            holder.imgDirection.setVisibility(View.GONE);
            holder.btnTrack.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnReschedule.setVisibility(View.VISIBLE);
            holder.txtStep3.setText(context.getString(R.string.order_delivered));
        }

        holder.imgDirection.setVisibility(View.GONE);
        holder.showAddressDetais(position);
        holder.showItemLists(position);
        holder.showServiceLevel(position);

        holder.txtDate.setText(mValues.get(position).dateModel.date + " " +mValues.get(position).dateModel.time); //"Date: " +
        holder.txtPayment.setText(mValues.get(position).payment);

        holder.btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.addressModel = mValues.get(position).addressModel;
                Constants.state = mValues.get(position).state;

                Constants.ORDER_TYPE = mValues.get(position).orderModel.product_type;
                if(Constants.ORDER_TYPE == Constants.CAMERA_OPTION){
                    Constants.cameraOrderModel = mValues.get(position).orderModel;
                }else if(Constants.ORDER_TYPE == Constants.ITEM_OPTION){
                    Constants.itemOrderModel = mValues.get(position).orderModel;
                }else if(Constants.ORDER_TYPE == Constants.PACKAGE_OPTION){
                    Constants.packageOrderModel = mValues.get(position).orderModel;
                }
                // status 2, 3  :  ready for pick up  , pick up to destination
                orderTracking(mValues.get(position).orderId, mValues.get(position).accepted_by, status);
            }
        });

        holder.linExpand.setVisibility(View.GONE);
        holder.imgExpand.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.down));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.linExpand.getVisibility() == View.VISIBLE){
                    //DeviceUtil.collapse(holder.linExpand);
                    holder.linExpand.setVisibility(View.GONE);
                    holder.imgExpand.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.down));
                }else{
                    //DeviceUtil.expand(holder.linExpand);
                    holder.linExpand.setVisibility(View.VISIBLE);
                    holder.imgExpand.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.up));
                    holder.showItemLists(position);
                }
            }
        });

        holder.imgDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                        .setTitle("Are you sure")
                        .setMessage("You want to cancel the pick up?")
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (mValues.get(position).payment.toLowerCase().trim().contains("Cash On Pick up".toLowerCase()) || mValues.get(position).paymentId.equals("0")) {
                                    cancelOrder(mValues.get(position).orderId, 0);
                                } else {
                                    if (mValues.get(position).paymentId != null && !mValues.get(position).paymentId.isEmpty()) {
                                        cancelPayment(mValues.get(position).orderId, mValues.get(position).paymentId, mValues.get(position).price);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                dialog.setCancelable(false);
                dialog.show();
            }
        });


        holder.btnReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrackingDialog(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void showTrackingDialog(final  int position){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_reschedule);
        final EditText edtTrackNum = (EditText)dialog.findViewById(R.id.edt_track_number);
        edtTrackNum.setInputType(InputType.TYPE_NULL);
        edtTrackNum.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                showDateTimeDialog(position, edtTrackNum);
            }
        });

        Button btnOk = (Button)dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = edtTrackNum.getText().toString();
                if(!number.isEmpty()){
                    reschedule(mValues.get(position).orderId, mValues.get(position).new_date, mValues.get(position).new_time);
                }
                dialog.hide();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        TextView txtOrder;
        TextView txtTracking;
        TextView txtStatus;
        TextView txtPayment;

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

        LinearLayout linItem;
        LinearLayout linColumn;

        TextView txtDate;
        TextView txtService;

        ImageView imgExpand;
        Button btnTrack;
        Button btnCancel;
        Button btnReschedule;

        TextView txtStep1, txtStep2, txtStep3;

        LinearLayout linExpand;
        LinearLayout imgStep;
        ImageView imgDirection;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            txtOrder = (TextView)view.findViewById(R.id.txt_order_number);
            txtTracking = (TextView)view.findViewById(R.id.txt_tracking);
            txtStatus = (TextView)view.findViewById(R.id.txt_status);
            txtPayment = (TextView)view.findViewById(R.id.txt_payment_method);

            linItem = (LinearLayout)view.findViewById(R.id.lin_item);
            linColumn = (LinearLayout)view.findViewById(R.id.lin_column);

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
            edtDesPhone  = (TextView)view.findViewById(R.id.edt_phone_des);
            edtDesName = (TextView)view.findViewById(R.id.edt_des_name);

            txtDate = (TextView)view.findViewById(R.id.txt_date);
            txtService = (TextView)view.findViewById(R.id.txt_service);

            imgExpand = (ImageView)view.findViewById(R.id.img_expand);
            btnTrack = (Button)view.findViewById(R.id.btn_track);
            btnCancel = (Button)view.findViewById(R.id.btn_cancel);
            btnReschedule = (Button)view.findViewById(R.id.btn_reschedule);

            txtStep1 = (TextView)view.findViewById(R.id.txt_step1);
            txtStep2 = (TextView)view.findViewById(R.id.txt_step2);
            txtStep3 = (TextView)view.findViewById(R.id.txt_step3);

            linExpand = (LinearLayout)view.findViewById(R.id.lin_expand);

            imgStep  = (LinearLayout) view.findViewById(R.id.img_step);
            imgDirection  = (ImageView)view.findViewById(R.id.img_direction);

        }


        public void showServiceLevel(int position){
            if(mValues.get(position).serviceModel != null){
                ServiceModel serviceModel = mValues.get(position).serviceModel;
                txtService.setText(serviceModel.name + ", " + context.getResources().getString(R.string.rupee) + String.format("%.2f", Float.valueOf(serviceModel.price)) + ", " + serviceModel.time_in); //"Service Level: " +
            }
        }

        public void showAddressDetais(int  position){
            if(mValues.get(position).addressModel!= null){
                AddressModel addressModel = mValues.get(position).addressModel;

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
                edtDesPhone.setText(addressModel.desPhone);
                edtDesName.setText(addressModel.desName);
                if(addressModel.desInstruction == null || addressModel.desInstruction.isEmpty()){
                    edtDesInstruction.setVisibility(View.GONE);
                }else{
                    edtDesInstruction.setText(addressModel.desInstruction);
                }
            }
        }


        private  void showItemLists(int position){
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
                        View line = inflater.inflate(R.layout.divider_hint, null);
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
                        TextView txtDimension = (TextView)view.findViewById(R.id.txt_dimension);
                        TextView txtQuantity = (TextView)view.findViewById(R.id.txt_quantity);
                        TextView txtWeight = (TextView)view.findViewById(R.id.txt_weight);
                        txtItem.setText(orderModel.itemModels.get(k).title);
                        txtDimension.setText(Constants.getDimentionString(orderModel.itemModels.get(k).dimension1,orderModel.itemModels.get(k).dimension2 ,orderModel.itemModels.get(k).dimension3));
                        txtQuantity.setText(orderModel.itemModels.get(k).quantity);
                        txtWeight.setText(orderModel.itemModels.get(k).weight);
                        linItem.addView(view);
                        View line = inflater.inflate(R.layout.divider_hint, null);
                        if(k < orderModel.itemModels.size() -1){
                            linItem.addView(line);
                        }

                    }
                }

            }else if(option== Constants.PACKAGE_OPTION){


                linColumn.removeAllViews();
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewCol = inflater.inflate(R.layout.row_review_package_title, null);
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
                        View line = inflater.inflate(R.layout.divider_hint, null);
                        if(k < orderModel.itemModels.size() -1){
                            linItem.addView(line);
                        }
                    }
                }
            }
        }
    }


    public void orderTracking(final String orderId, String employer_id, final String status) {
        RequestParams params = new RequestParams();
        params.put( "order_id", orderId );
        params.put( "employer_id", employer_id );
        params.put("type", Constants.MODE);
        WebClient.post(Constants.URL  + "/Track/order_track", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        if(context instanceof  MainActivity)
                            ((MainActivity)context).showProgressDialog();
                        if(context instanceof HomeActivity)
                            ((HomeActivity)context).showProgressDialog();
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
                                String results = response.getString("result");
                                if(results.equals("200")){
                                    JSONObject orderObj = response.getJSONObject("orders");
                                    Constants.orderTrackModel.id = orderObj.getString("id");
                                    Constants.orderTrackModel.order_id = orderObj.getString("order_id");
                                    Constants.orderTrackModel.pickup = orderObj.getString("pickup");
                                    Constants.orderTrackModel.location = orderObj.getString("location");
                                    Constants.orderTrackModel.employer_id = orderObj.getString("employer_id");
                                    Constants.orderTrackModel.dt = orderObj.getString("dt");
                                    Constants.orderTrackModel.first_name = orderObj.getString("first_name");
                                    Constants.orderTrackModel.last_name = orderObj.getString("last_name");
                                    Constants.orderTrackModel.email = orderObj.getString("email");
                                    Constants.orderTrackModel.phone = orderObj.getString("phone");
                                    Constants.orderTrackModel.picture = orderObj.getString("picture");
                                    Constants.orderTrackModel.type = results;

                                    Intent intent = new Intent(context, LocationMapActivity.class);
                                    intent.putExtra("status", status);
                                    context.startActivity(intent);

                                }else if(results.equals("300")){
                                    return;
                                }
                            }else if(response.getString("result").equals("400")){
                                Toast.makeText(context , context.getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    };
                    public void onFinish() {

                        if(context instanceof  MainActivity)
                            ((MainActivity)context).hideProgressDialog();
                        if(context instanceof  HomeActivity)
                            ((HomeActivity)context).hideProgressDialog();
                    }
                    ;
                });
    }

    private void cancelPayment(final String orderId, String paymentId, String refundAmount){

       // paymentId = "234295121";
        RequestParams params = new RequestParams();
        params.put("merchantKey", "iBzb2IZp");
        params.put("paymentId", paymentId);
        params.put("merchantKey", refundAmount);

        new CancelPaymentProcess(this, context, paymentId, refundAmount, orderId).execute();
    }

    public void cancelOrder(String order_id, final int type) {
        RequestParams params = new RequestParams();
        params.put("id", order_id);

        WebClient.post(Constants.BASE_URL_ORDER + "cancel_order", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        ((MainActivity)context).showProgressDialog();
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

                        if (response != null) {

                            try{

                                if(response.getString("result").equals("400")){
                                    Toast.makeText(context, context.getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }else if(response.getString("result").equals("200")){
                                    if(type == 0){

                                        ((MainActivity)context).updateFragment(MainActivity.ORDER_HIS);
                                        Handler mHandler = new Handler();
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Constants.showConfirmDialog(context, "", context.getString(R.string.your_order_cancel_cash));
                                            }
                                        },500);
                                    }else{

                                        ((MainActivity)context).updateFragment(MainActivity.ORDER_HIS);
                                        Handler mHandler = new Handler();
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Constants.showConfirmDialog(context, "",  context.getString(R.string.your_order_cancel));
                                            }
                                        },500);
                                    }
                                }
                            }catch (Exception e){

                            }
                        }
                    };
                    public void onFinish() {
                        ((MainActivity)context).hideProgressDialog();
                    }
                    ;
                });
    }


    private void reschedule(String order_id, String date, String time) {
        RequestParams params = new RequestParams();
        params.put("id", order_id);
        params.put("date", date);
        params.put("time", time);

        WebClient.post(Constants.BASE_URL_ORDER + "reschedule", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        ((MainActivity)context).showProgressDialog();
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

                        if (response != null) {

                            try{
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(context, context.getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }else if(response.getString("result").equals("200")){
                                    Toast.makeText(context, context.getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();
                                    ((MainActivity)context).updateFragment(MainActivity.ORDER_HIS);

                                }
                            }catch (Exception e){

                            }
                        }
                    };

                    public void onFinish() {
                        ((MainActivity)context).hideProgressDialog();
                    }
                    ;
                });
    }




    private void showDateTimeDialog(final int position, final EditText editText){

        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());

        final int year , month, day, hour, minute;
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_date_time);

        final String current = new StringBuilder().append(day)
                .append("-").append(month + 1).append("-").append(year)
                .append(" ").toString();

        mValues.get(position).new_date = new StringBuilder().append(day)
                .append("-").append(month + 1).append("-").append(year)
                .append(" ").toString();

        final DatePicker datePicker = (DatePicker)dialog.findViewById(R.id.datepicker);

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        });


        final TimePicker timePicker =(TimePicker)dialog.findViewById(R.id.timepicker);

        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        
        mValues.get(position).new_time = TimeHelper.getTime();

        timePicker.setIs24HourView(false);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minuteOfHour) {
            }
        });

        Button btnDone = (Button)dialog.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check date
                int monthOfYear = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();

                if(monthOfYear > month || (monthOfYear == month && dayOfMonth >= day)){
                    mValues.get(position).new_date = new StringBuilder().append(dayOfMonth)
                            .append("-").append(monthOfYear+ 1 ).append("-").append(year)
                            .append(" ").toString();
                }else{
                    Toast.makeText(context, context.getString(R.string.pickup_time), Toast.LENGTH_SHORT).show();
                    return;
                }

                // check time
                int hourOfDay = timePicker.getCurrentHour();
                int minuteOfHour = timePicker.getCurrentMinute();
                if(mValues.get(position).new_date.equals(current)) {
                    if ( hour > hourOfDay || (hour == hourOfDay && minute > minuteOfHour)) {
                        Toast.makeText(context, context.getString(R.string.pickup_time), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // change am/pm
                String status = "AM";
                if(hourOfDay > 11)
                {
                    status = "PM";
                }
                int hour_of_12_hour_format;
                if(hourOfDay > 11){
                    hour_of_12_hour_format = hourOfDay - 12;
                }
                else {
                    hour_of_12_hour_format = hourOfDay;
                }

                mValues.get(position).new_time = TimeHelper.getTwoDigital(hour_of_12_hour_format, true) + " : " + TimeHelper.getTwoDigital(minuteOfHour, false) + " " + status;
                notifyDataSetChanged();
                editText.setText(mValues.get(position).new_date + "  " + mValues.get(position).new_time);
                dialog.hide();
            }
        });
        dialog.show();
    }

}