package com.hurry.custom.view.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hurry.custom.R;
import com.hurry.custom.model.AddressHisModel;
import com.hurry.custom.view.activity.AddressBookActivity;
import com.hurry.custom.view.activity.HomeActivity;

import java.util.ArrayList;

/**
 * Simple example of ListAdapter for using with Folding Cell
 * Adapter holds indexes of unfolded elements for correct work with default reusable views behavior
 */

public class AddressAdapter
        extends RecyclerView.Adapter<AddressAdapter.ViewHolder> implements Filterable {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private ArrayList<AddressHisModel> mValues;
    private ArrayList<AddressHisModel> contactListFiltered = new ArrayList<>();
    private Context context;
    View myView;

    public static ArrayList<ViewHolder> viewHolders = new ArrayList<>();

    public AddressAdapter(Context context, ArrayList<AddressHisModel> items, View view) {

        mBackground = mTypedValue.resourceId;
        mValues = items;
        contactListFiltered = items;
        this.context = context;
        this.myView = view;
        viewHolders.clear();


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_address, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.txtSource.setText(contactListFiltered.get(position).address);
        holder.txtDestination.setText(contactListFiltered.get(position).city);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int k = 0; k < viewHolders.size(); k++){
                    viewHolders.get(k).checkBox.setChecked(false);
                }
                holder.checkBox.setChecked(true);
                if(context instanceof  AddressBookActivity)
                    ((AddressBookActivity)context).setValue(contactListFiltered.get(position), true);

                if(context instanceof HomeActivity){
                    ((HomeActivity)context).dismissAddressDialog(contactListFiltered.get(position));
                }

            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int k = 0; k < viewHolders.size(); k++){
                    viewHolders.get(k).checkBox.setChecked(false);
                }
                holder.checkBox.setChecked(true);
                if(context instanceof AddressBookActivity)
                    ((AddressBookActivity)context).setValue(contactListFiltered.get(position), true);

                if(context instanceof HomeActivity){
                    ((HomeActivity)context).dismissAddressDialog(contactListFiltered.get(position));
                }
            }
        });

        viewHolders.add(holder);

    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = mValues;
                } else {
                    ArrayList<AddressHisModel> filteredList = new ArrayList<>();
                    for (AddressHisModel row : mValues) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.address.toLowerCase().contains(charString.toLowerCase()) || row.address.contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }
                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contactListFiltered = (ArrayList<AddressHisModel>) results.values;
                viewHolders.clear();
                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
        return filter;
    }


    public  class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        TextView txtSource;
        TextView txtDestination;
        CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtSource = (TextView)view.findViewById(R.id.txt_pick_address);
            txtDestination = (TextView)view.findViewById(R.id.txt_des_address);
            checkBox = (CheckBox)view.findViewById(R.id.checkbox);
        }
    }

}