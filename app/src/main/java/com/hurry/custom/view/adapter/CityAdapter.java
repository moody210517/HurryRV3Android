package com.hurry.custom.view.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.model.CityModel;
import com.hurry.custom.view.activity.MainActivity;
import com.hurry.custom.view.activity.login.LoginActivity;
import java.util.ArrayList;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */

public class CityAdapter
        extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private ArrayList<CityModel> mValues;
    private Context context;
    View myView;


    public CityAdapter(Context context, ArrayList<CityModel> items, View view) {
        mBackground = mTypedValue.resourceId;
        mValues = items;
        this.context = context;
        this.myView = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_city, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtTitle.setText(mValues.get(position).name);
        Glide.with(context)
                .load(Constants.PHOTO_URL + "employer/" +  mValues.get(position).image)
                .dontAnimate()
                .centerCrop()
                .error(com.gun0912.tedpicker.R.drawable.no_image)
                .into(holder.imgCity);
        if(!Constants.cityName.isEmpty() && Constants.cityName.equals(mValues.get(position).name)){
            holder.linMask.setVisibility(View.VISIBLE);
        }else{
            holder.linMask.setVisibility(View.GONE);
        }
        Constants.cityBounds = Constants.getGeofences(mValues.get(position).geofence);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.cityBounds = Constants.getGeofences(mValues.get(position).geofence);
                Constants.cityName = mValues.get(position).name;
                PreferenceUtils.setCityId(context, position);

                if(context instanceof MainActivity)
                    ((MainActivity)context).hideDialog();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public  class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        TextView txtTitle;
        ImageView imgCity;
        LinearLayout linMask;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtTitle = (TextView)view.findViewById(R.id.txt_name);
            imgCity  = (ImageView)view.findViewById(R.id.img_city);
            linMask = (LinearLayout)view.findViewById(R.id.lin_mask);
        }
    }




}