package com.hurry.custom.controller;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.hurry.custom.view.activity.map.MyAutoCompleteActivity;
import com.hurry.custom.view.activity.map.TouchMapActivity;

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
 public class GetLocationNameFromLatLng extends AsyncTask<Void, Void, Void> {

    public static String GOOGLE_MAP_URL ="https://maps.googleapis.com/maps/api/geocode/json";
    //SweetAlertDialog sweetAlertDialog;
    Context  mContext;
    //HttpResponse result;
    String result;
    LatLng latLng;


    public GetLocationNameFromLatLng(Context con, LatLng latLng)
    {
        super();
        this.mContext = con;
        this.latLng = latLng;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... args) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("latlng", String.valueOf(latLng.latitude) +  "," + String.valueOf(latLng.longitude)  ));
        params.add(new BasicNameValuePair("sensor", "false"));
        //params.add(new BasicNameValuePair("language", String.valueOf(latLng.longitude)));
        ServiceHandler sh = new ServiceHandler();
        result = sh.makeServiceCall(GOOGLE_MAP_URL  , ServiceHandler.GET, params);
        return null;
    }
    String res  = null;

    @Override
    protected void onPostExecute(Void res) {

        if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("status").toLowerCase().equals("ok")) {

                        JSONArray results = jsonObject.getJSONArray("results");
                        if(results.length() > 0){
                            JSONObject object = results.getJSONObject(0);

                            JSONArray componetArray = object.getJSONArray("address_components");

                            String postCode = "";
                            String state  = "";
                            String city = "";
                            String area = "";
                            String address = "";
                            for(int k = 0 ; k < componetArray.length() ; k++){
                                JSONObject obj = componetArray.getJSONObject(k);
                                String type = obj.getString("types");
                                if(type.contains("postal_code")){
                                    postCode = obj.getString("long_name");
                                }

                                if(type.contains("administrative_area_level_2")){
                                    state =  obj.getString("long_name");
                                }

                                if(type.contains("administrative_area_level_1")){
                                    state = obj.getString("long_name") + "  " + state;
                                }

                                if(type.contains("locality")){
                                    city = obj.getString("long_name");
                                }

                                if(type.contains("sublocality_level_2")){
                                    area = obj.getString("long_name");
                                }
                                if(type.contains("sublocality_level_1")){
                                    area = obj.getString("long_name") + " " + area;
                                }
                                if(type.contains("route")){
                                    address = obj.getString("long_name");
                                }

                                if(type.contains("establishment")){
                                    address = address + obj.getString("long_name") ;
                                }
                            }

                            String location = object.getString("formatted_address");
//
//                            String[] addresses = address.split(",");

                            if(mContext instanceof MyAutoCompleteActivity){
                                ((MyAutoCompleteActivity)mContext).updateLocation(location, address ,area, city , state, postCode);
                            }
                            if(mContext instanceof TouchMapActivity){
                                ((TouchMapActivity)mContext).setLocation(location);
                            }
                        }
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
