package com.hurry.custom.controller;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.JsonHelper;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.MainActivity;
import com.hurry.custom.view.activity.login.LocationActivity;
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

        if(mContext instanceof LocationActivity){
            ((LocationActivity)mContext).hideProgressDialog();
        }

         if (result != null) {
             try {
                 JSONObject response = new JSONObject(result);
                 String result = response.getString("result");
                 if(result.equals("200")){

                     JSONArray jsonArray = response.getJSONArray("cities");
                     JsonHelper.parseCities(jsonArray);

                     if(PreferenceUtils.getCityId(mContext) != -1){
                         Constants.cityBounds =Constants.getGeofences(Constants.cityModels.get(PreferenceUtils.getCityId(mContext)).geofence);
                         Constants.cityName = Constants.cityModels.get(PreferenceUtils.getCityId(mContext)).name;
                         PreferenceUtils.setCityName(mContext, Constants.cityName);
                     }

                     if(mContext instanceof  MainActivity){
                         ((MainActivity)mContext).hideProgressDialog();
                     }

                     if(type.equals("source")){
                         if(mContext instanceof HomeActivity){
                             ((HomeActivity)mContext).goToMapSource();
                         }

                     }else if(type.equals("destination")){
                         if(mContext instanceof HomeActivity){
                             ((HomeActivity)mContext).goToMapDestination();
                         }
                     }

                     if(type.equals("show")){
                         if(mContext instanceof HomeActivity){
                             ((HomeActivity)mContext).showConfirmDialog();
                         }
                     }

                     if(type.isEmpty()){
                         if(mContext instanceof HomeActivity){
                             ((HomeActivity)mContext).initNavigation();
                         }
                     }

                     if(type.isEmpty()){
                         if(mContext instanceof LocationActivity){
                             ((LocationActivity)mContext).setupRecyclerView();
                         }
                     }

                     if(mContext instanceof SplashActivity){

                         if(PreferenceUtils.getFirstStart(mContext)){

                         }else{
                             PreferenceUtils.setFirstStart(mContext, false);
                             Intent intent = new Intent(mContext, HomeActivity.class);
                             ((SplashActivity)mContext).startActivity(intent);
                             ((SplashActivity)mContext).finish();
                         }

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
