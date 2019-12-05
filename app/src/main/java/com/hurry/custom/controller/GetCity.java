package com.hurry.custom.controller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.JsonHelper;
import com.hurry.custom.view.activity.AddressDetailsActivity;
import com.hurry.custom.view.activity.AddressDetailsNewActivity;
import com.hurry.custom.view.activity.MainActivity;
import com.hurry.custom.view.activity.login.SplashActivity;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 7/21/2016.
 */
 public class GetCity extends AsyncTask<Void, Void, Void> {
    Context  mContext;
    String result, type;

    public GetCity(Context con, String type)
    {
        super();
        this.mContext = con;
        this.type = type;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... args) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        ServiceHandler sh = new ServiceHandler();
        //params.add(new BasicNameValuePair("employer_id", PreferenceUtils.getUserId(mContext)));
        result = sh.makeServiceCall( Constants.BASIC_DATA_URL + "get_cities", ServiceHandler.POST, params); //
        return null;
    }


    @Override
    protected void onPostExecute(Void res) {

         if (result != null) {
             try {
                 JSONObject response = new JSONObject(result);
                 String result = response.getString("result");
                 if(result.equals("200")){

                     JSONArray jsonArray = response.getJSONArray("cities");
                     JsonHelper.parseCities(jsonArray);

                     Constants.cityBounds =Constants.getGeofences(Constants.cityModels.get(PreferenceUtils.getCityId(mContext)).geofence);
                     Constants.cityName = Constants.cityModels.get(PreferenceUtils.getCityId(mContext)).name;

                     if(mContext instanceof  MainActivity){
                         ((MainActivity)mContext).hideProgressDialog();
                     }


                     if(type.equals("source")){
                         if(mContext instanceof  AddressDetailsActivity){
                             ((AddressDetailsActivity)mContext).goToMapSource();
                         }else if(mContext instanceof AddressDetailsNewActivity){
                             ((AddressDetailsNewActivity)mContext).goToMapSource();
                         }

                     }else if(type.equals("destination")){
                         if(mContext instanceof  AddressDetailsActivity){
                             ((AddressDetailsActivity)mContext).goToMapDestination();
                         }else if(mContext instanceof AddressDetailsNewActivity){
                             ((AddressDetailsNewActivity)mContext).goToMapDestination();
                         }

                     }


                     if(mContext instanceof SplashActivity){

                     }

                 }else{
                     Toast.makeText(mContext, mContext.getString(R.string.failed),Toast.LENGTH_SHORT).show();

                 }
             } catch (JSONException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
        }
    }

}
