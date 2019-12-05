package com.hurry.custom.view.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.model.QuoteCoperationModel;

import java.util.ArrayList;


/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */


public class QuoteCorporateAdapter
        extends RecyclerView.Adapter<CorporateOrderViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private ArrayList<QuoteCoperationModel> mValues;
    private Context context;
    ArrayList<CorporateOrderViewHolder> viewHolders;

    public QuoteCorporateAdapter(Context context, ArrayList<QuoteCoperationModel> items) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mValues = items;
        this.context = context;
        viewHolders = new ArrayList<>();
    }

    @Override
    public CorporateOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_quote_coperation, parent, false);
        view.setBackgroundResource(mBackground);
        return new CorporateOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CorporateOrderViewHolder holder, final int position) {
        //holder.mBoundString = mValues.get(position);

        holder.txtOrder.setText(mValues.get(position).orderId);
        holder.txtTracking.setText(mValues.get(position).trackId);


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
                Constants.quote_order_id = mValues.get(position).orderId;
                Constants.addressModel = mValues.get(position).addressModel;
                Constants.quote_id = mValues.get(position).quote_id;
                Constants.delivery = mValues.get(position).description;
                Constants.loadType = mValues.get(position).loadType;

                // insert items info
                notifyDataSetChanged();
            }
        });




        holder.txtDate.setText(mValues.get(position).dateModel.date + " " +mValues.get(position).dateModel.time);

        holder.showDetails(position);
        holder.showAddressDetais(position);

        holder.linExpand.setVisibility(View.GONE);
        holder.imgExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.linExpand.getVisibility() == View.VISIBLE){
                    //DeviceUtil.collapse(holder.linExpand);
                    holder.linExpand.setVisibility(View.GONE);
                    holder.imgExpand.setImageDrawable(context.getResources().getDrawable(R.mipmap.down));
                }else{
                    //DeviceUtil.expand(holder.linExpand);
                    holder.linExpand.setVisibility(View.VISIBLE);
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
                    //DeviceUtil.expand(holder.linExpand);
                    holder.linExpand.setVisibility(View.VISIBLE);
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