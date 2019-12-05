package com.hurry.custom.view.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.model.QuoteModel;
import com.hurry.custom.model.ServiceModel;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */


public class QuoteAdapter
        extends RecyclerView.Adapter<PersonaOrderViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private ArrayList<QuoteModel> mValues;
    private Context context;
    View myView;

    ArrayList<PersonaOrderViewHolder> viewHolders;

    public QuoteAdapter(Context context, ArrayList<QuoteModel> items,  View view) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mValues = items;
        this.context = context;
        viewHolders = new ArrayList<>();
        this.myView = view;
    }

    @Override
    public PersonaOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_quote, parent, false);
        view.setBackgroundResource(mBackground);
        return new PersonaOrderViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final PersonaOrderViewHolder holder, final int position) {
        //holder.mBoundString = mValues.get(position);

        holder.linItems.removeAllViews();
        TextView textView = new TextView(context);
        textView.setText("Order Id: " + mValues.get(position).orderId);
        textView.setPadding(5, 5, 5, 5);

        TextView textView2 = new TextView(context);
        textView2.setText("Reserved for " + PreferenceUtils.getNickname(context));
        textView2.setPadding(5, 5, 5, 5);
        holder.linItems.addView(textView);
        holder.linItems.addView(textView2);

        if(mValues.get(position).selection == true){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }
        
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for( int k = 0; k < viewHolders.size(); k ++){
                    viewHolders.get(k).checkBox.setChecked(false);
                }
                int temp = -1;
                for(int  k = 0 ; k < mValues.size() ; k++){
                    if(mValues.get(k).selection == true){
                        temp = k;
                    }
                    mValues.get(k).selection = false;
                    mValues.get(k).service_choose = false;
                }
                if(position == temp){
                    mValues.get(position).selection = false;
                }else{
                    mValues.get(position).selection = true;
                }

                if(!Constants.quote_order_id.equals(mValues.get(position).orderId)){
                    Constants.serviceModel = null;
                    Constants.serviceModel = new ServiceModel();
                }

                Constants.quote_order_id = mValues.get(position).orderId;
                Constants.trackId = mValues.get(position).trackId;
                Constants.addressModel = mValues.get(position).addressModel;

                Constants.quote_id = mValues.get(position).quote_id;
                // insert items info
                if(mValues.get(position).orderModel.product_type == Constants.CAMERA_OPTION){
                    Constants.cameraOrderModel.product_type = Constants.CAMERA_OPTION;
                    Constants.ORDER_TYPE = Constants.CAMERA_OPTION;
                    Constants.cameraOrderModel.itemModels.clear();
                    for(int k = 0; k < mValues.get(position).orderModel.itemModels.size(); k++){

                        ItemModel itemModel = new ItemModel();
                        itemModel.image = mValues.get(position).orderModel.itemModels.get(k).image;
                        itemModel.weight = mValues.get(position).orderModel.itemModels.get(k).weight;
                        itemModel.quantity = mValues.get(position).orderModel.itemModels.get(k).quantity;
                        Constants.cameraOrderModel.itemModels.add(itemModel);
                    }

                }else if(mValues.get(position).orderModel.product_type == Constants.ITEM_OPTION){

                    Constants.itemOrderModel.product_type = Constants.ITEM_OPTION;
                    Constants.ORDER_TYPE = Constants.ITEM_OPTION;
                    Constants.itemOrderModel.itemModels.clear();

                    for(int k = 0; k < mValues.get(position).orderModel.itemModels.size(); k++){

                        ItemModel itemModel = new ItemModel();
                        itemModel.title = mValues.get(position).orderModel.itemModels.get(k).title;
                        itemModel.dimension1 = mValues.get(position).orderModel.itemModels.get(k).dimension1;
                        itemModel.dimension2 = mValues.get(position).orderModel.itemModels.get(k).dimension2;
                        itemModel.dimension3 = mValues.get(position).orderModel.itemModels.get(k).dimension3;
                        itemModel.weight = mValues.get(position).orderModel.itemModels.get(k).weight;
                        itemModel.quantity = mValues.get(position).orderModel.itemModels.get(k).quantity;
                        Constants.itemOrderModel.itemModels.add(itemModel);

                    }
                }else if(mValues.get(position).orderModel.product_type == Constants.PACKAGE_OPTION){
                    Constants.packageOrderModel.product_type = Constants.PACKAGE_OPTION;
                    Constants.ORDER_TYPE = Constants.PACKAGE_OPTION;
                    Constants.packageOrderModel.itemModels.clear();
                    for(int k = 0; k < mValues.get(position).orderModel.itemModels.size(); k++){

                        ItemModel itemModel = new ItemModel();
                        itemModel.title = mValues.get(position).orderModel.itemModels.get(k).title;
                        itemModel.weight = mValues.get(position).orderModel.itemModels.get(k).weight;
                        itemModel.quantity = mValues.get(position).orderModel.itemModels.get(k).quantity;
                        Constants.packageOrderModel.itemModels.add(itemModel);
                    }
                }
                notifyDataSetChanged();
            }
        });



        holder.txtOrder.setText(mValues.get(position).orderId);
        holder.txtTracking.setText(mValues.get(position).trackId);
        holder.txtDate.setText(mValues.get(position).dateModel.date + " " +mValues.get(position).dateModel.time);
        holder.showAddressDetais(mValues, position);
        holder.showItemLists(mValues, position, context, myView);
        holder.showServiceLevel(mValues, position);

        holder.txtPayment.setText(mValues.get(position).payment);

        holder.linExpand.setVisibility(View.GONE);
        holder.imgExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.linExpand.getVisibility() == View.VISIBLE){

                    //DeviceUtil.collapse(holder.linExpand);
                    holder.linExpand.setVisibility(View.GONE);
                    holder.imgExpand.setImageDrawable(context.getResources().getDrawable(R.mipmap.down));
                }else{
                    holder.linExpand.setVisibility(View.VISIBLE);
                    //DeviceUtil.expand(holder.linExpand);
                    holder.imgExpand.setImageDrawable(context.getResources().getDrawable(R.mipmap.up));
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.linExpand.getVisibility() == View.VISIBLE){

                    //DeviceUtil.collapse(holder.linExpand);
                    holder.linExpand.setVisibility(View.GONE);
                    holder.imgExpand.setImageDrawable(context.getResources().getDrawable(R.mipmap.down));
                }else{
                    holder.linExpand.setVisibility(View.VISIBLE);
                    //DeviceUtil.expand(holder.linExpand);
                    holder.imgExpand.setImageDrawable(context.getResources().getDrawable(R.mipmap.up));
                }
            }
        });



        viewHolders.add(holder);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }



}