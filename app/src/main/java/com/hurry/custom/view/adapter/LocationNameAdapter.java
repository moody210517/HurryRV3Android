package com.hurry.custom.view.adapter;


import android.content.Context;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by ravi on 16/11/17.
 */

public class LocationNameAdapter extends RecyclerView.Adapter<LocationNameAdapter.MyViewHolder>
        implements Filterable {


    private Context context;
    private List<PlaceAutocomplete> contactList;
    private List<PlaceAutocomplete> contactListFiltered = new ArrayList<>();
    private ContactsAdapterListener listener;

    private PlacesClient mGoogleApiClient;
    private String pageType = "";
    private LatLngBounds mBounds = new LatLngBounds(new LatLng(38.46572222050097, -107.75668023304138),new LatLng(39.913037779499035, -105.88929176695862));

    RectangularBounds bounds = RectangularBounds.newInstance(
            new LatLng(-33.880490, 151.184363),
            new LatLng(-33.858754, 151.229596));
    double leftLat = 0, leftLng = 0, rightLat = 0, rightLng = 0;


    private ArrayList<PlaceAutocomplete> mResultList;

    List<Integer> filterTypes = new ArrayList<Integer>();


    private RecyclerView recyclerView;

    public void setGoogleApiClient(PlacesClient googleApiClient) {
        if (googleApiClient == null ) {
            mGoogleApiClient = null;
        } else {
            mGoogleApiClient = googleApiClient;
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtLocation, txtCity;


        public MyViewHolder(View view) {
            super(view);
            txtLocation = view.findViewById(R.id.txt_location);
            txtCity = view.findViewById(R.id.txt_city);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public LocationNameAdapter(Context context, ArrayList<PlaceAutocomplete> contactList, ContactsAdapterListener listener, RecyclerView recyclerView, PlacesClient placesClient, String pageType) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
        this.recyclerView = recyclerView;
        this.mGoogleApiClient = placesClient;
        this.pageType = pageType;

        try{
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int k = 0; k < Constants.cityBounds.size(); k++) {
                builder.include(Constants.cityBounds.get(k));

                if(k == 0){
                    leftLat = Constants.cityBounds.get(k).latitude;
                    rightLat = Constants.cityBounds.get(k).latitude;

                    leftLng = Constants.cityBounds.get(k).longitude;
                    rightLng = Constants.cityBounds.get(k).longitude;
                }

                if( leftLat > Constants.cityBounds.get(k).latitude ){
                    leftLat = Constants.cityBounds.get(k).latitude;
                }

                if( leftLng  > Constants.cityBounds.get(k).longitude ){
                    leftLng = Constants.cityBounds.get(k).longitude;
                }

                if( rightLat < Constants.cityBounds.get(k).latitude ){
                    rightLat = Constants.cityBounds.get(k).latitude;
                }

                if( rightLng  < Constants.cityBounds.get(k).longitude ){
                    rightLng = Constants.cityBounds.get(k).longitude;
                }

            }

            mBounds = builder.build();
        }catch (Exception e){};

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_place, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try{

            final PlaceAutocomplete contact = contactListFiltered.get(position);
            holder.txtLocation.setText(contact.description);
            holder.txtCity.setText(contact.country);
        }catch (Exception e){};

    }

    @Override
    public int getItemCount() {
//        return contactListFiltered.size();
        if(mResultList != null){
            return mResultList.size();
        }
        return 0;
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                try{
                    if (constraint != null ) {
                        // Query the autocomplete API for the entered constraint

                        mResultList = getPredictions(constraint);
                        if (mResultList != null) {
                            // Results
                            results.values = mResultList;
                            results.count = mResultList.size();
                        }
                    }
                }catch (Exception e){};

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try{
                        contactListFiltered = (ArrayList<PlaceAutocomplete>) filterResults.values;
                        recyclerView.getRecycledViewPool().clear();
                        notifyDataSetChanged();

                }catch (Exception e){};
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(PlaceAutocomplete contact);
    }
    ArrayList<PlaceAutocomplete> tmp = new ArrayList<>();
    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {
        if (mGoogleApiClient != null) {
            Log.i(TAG, "Executing autocomplete query for: " + constraint);

            ArrayList<PlaceAutocomplete> placeAutocompletes = new ArrayList<>();
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

            RectangularBounds bounds = RectangularBounds.newInstance(
                    new LatLng(leftLat, leftLng),
                    new LatLng(rightLat, rightLng));

            FindAutocompletePredictionsRequest request;
            if(Constants.DELIVERY_STATUS == Constants.SAME_CITY || Constants.DELIVERY_STATUS == Constants.OUT_STATION || pageType.equals("sender") ){
                request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias(bounds)
                        //.setLocationRestriction(bounds)
                        .setCountry("in")
                        //.setTypeFilter(TypeFilter.REGIONS)
                        .setSessionToken(token)
                        .setQuery(constraint.toString())
                        .build();
            }else{
                request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias(bounds)
                        //.setLocationRestriction(bounds)
                        .setCountry("us")
                        .setCountry("gb")
                        .setCountry("gb")
                        .setCountry("au")
                        .setCountry("ca")
                        .setCountry("us|country:au|country:ca|country:gb")
                        //.setTypeFilter(TypeFilter.REGIONS)
                        .setSessionToken(token)
                        .setQuery(constraint.toString())
                        .build();
            }

            mGoogleApiClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {

                    Log.i(TAG, prediction.getPlaceId());
                    Log.i(TAG, prediction.getPrimaryText(null).toString());
                    PlaceAutocomplete placeAutocomplete = new PlaceAutocomplete(prediction.getPlaceId(),
                            prediction.getFullText(null).toString(), prediction.getSecondaryText(null).toString());
                    placeAutocompletes.add(placeAutocomplete);

                }

                Log.e(TAG, "done ");
                //getFilter();

            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            });

            try {
                TimeUnit.MILLISECONDS.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(placeAutocompletes.isEmpty()){
                //return  tmp;
            }else{
                for (int i = 0; i < tmp.size(); i++)
                    tmp.remove(tmp.get(i));
                for(int k = 0; k < placeAutocompletes.size() ; k++){
                    tmp.add(placeAutocompletes.get(k));
                }
                return placeAutocompletes;
            }
        }

        return null;
    }

    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;
        public CharSequence country;

        PlaceAutocomplete(CharSequence placeId, CharSequence description, CharSequence country) {
            this.placeId = placeId;
            this.description = description;
            this.country = country;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }

}