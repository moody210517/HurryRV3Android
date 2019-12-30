package com.hurry.custom.controller;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.hurry.custom.R;
import com.hurry.custom.view.activity.AddressDetailsNewActivity;
import com.hurry.custom.view.activity.DateTimeActivity;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.map.MyAutoCompleteActivity;
import com.hurry.custom.view.activity.map.TouchMapActivity;
import com.hurry.custom.view.fragment.DateTimeFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 7/21/2016.
 */
 public class GetRoute extends AsyncTask<Void, Void, Void> {

    public static String GOOGLE_MAP_URL ="https://maps.googleapis.com/maps/api/geocode/json";
    Context  mContext;
    String result;
    LatLng source, destination;
    DateTimeFragment dateTimeFragment;


    public GetRoute(Context con, DateTimeFragment dateTimeFragment,  LatLng source, LatLng destination)
    {
        super();
        this.mContext = con;
        this.source = source;
        this.destination = destination;
        this.dateTimeFragment = dateTimeFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... args) {
        //place_id
        List<NameValuePair> params = new ArrayList<NameValuePair>();


        params.add(new BasicNameValuePair("origin", source.latitude + "," + source.longitude  ));
        params.add(new BasicNameValuePair("destination", destination.latitude + "," + destination.longitude  ));
        params.add(new BasicNameValuePair("mode", "driving"));
        params.add(new BasicNameValuePair("sensor", "true"));
        params.add(new BasicNameValuePair("language", "US"));
        params.add(new BasicNameValuePair("key", mContext.getResources().getString(R.string.gecode_api_key2)));
        //params.add(new BasicNameValuePair("language", String.valueOf(latLng.longitude)));
        ServiceHandler sh = new ServiceHandler();
        result = sh.makeServiceCall("https://maps.googleapis.com/maps/api/directions/json"  , ServiceHandler.GET_SSL, params);
        return null;
    }

    String res  = null;
    @Override
    protected void onPostExecute(Void res) {

        if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if(mContext instanceof HomeActivity && dateTimeFragment instanceof  DateTimeFragment){
                        dateTimeFragment.drawRoute(jsonObject);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }else{
            //Toast.makeText(mContext, mContext.getResources().getString(R.string.),Toast.LENGTH_SHORT).show();
//            if (null != sweetAlertDialog && sweetAlertDialog.isShowing()) {
//                sweetAlertDialog.dismiss();
//            }
        }
        //((Activity)mContext).finish();
    }


}
