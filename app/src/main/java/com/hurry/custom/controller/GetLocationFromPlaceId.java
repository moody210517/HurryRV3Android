package com.hurry.custom.controller;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.hurry.custom.R;
import com.hurry.custom.view.activity.HomeActivity;
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
 public class GetLocationFromPlaceId extends AsyncTask<Void, Void, Void> {

    public static String GOOGLE_MAP_URL ="https://maps.googleapis.com/maps/api/geocode/json";
    Context  mContext;
    String result;
    String place_id;
    String location;
    LatLng latLng;

    public GetLocationFromPlaceId(Context con, String place_id, String location, LatLng latLng)
    {
        super();
        this.mContext = con;
        this.place_id = place_id;
        this.location = location;
        this.latLng = latLng;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... args) {
        //
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("place_id", place_id));
        params.add(new BasicNameValuePair("sensor", "true"));
        params.add(new BasicNameValuePair("language", "US"));
        params.add(new BasicNameValuePair("key", mContext.getResources().getString(R.string.gecode_api_key2)));
        //params.add(new BasicNameValuePair("language", String.valueOf(latLng.longitude)));
        ServiceHandler sh = new ServiceHandler();
        result = sh.makeServiceCall(GOOGLE_MAP_URL  , ServiceHandler.GET, params);
        return null;
    }

    String res  = null;
    @Override
    protected void onPostExecute(Void res) {

        MyAutoCompleteActivity.isCalling = false;
        if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.getString("status").toLowerCase().equals("ok")) {

                        JSONArray results = jsonObject.getJSONArray("results");
                        if(results.length() > 0){
                            JSONObject object = results.getJSONObject(0);

                            JSONArray componetArray = object.getJSONArray("address_components");

                            String postCode = "";
                            String state1  = "";
                            String state2 = "";
                            String city = "";
                            String area1 = "";
                            String area2 = "";
                            String street = "";
                            String establishment = "";
                            String premise = "";

                            for(int k = 0 ; k < componetArray.length() ; k++){

                                JSONObject obj = componetArray.getJSONObject(k);

                                String type = obj.getString("types");

                                if(type.contains("postal_code")){
                                    postCode = obj.getString("long_name");
                                }

                                if(type.contains("administrative_area_level_2")){
                                    state2 =  obj.getString("long_name");
                                }

                                if(type.contains("administrative_area_level_1")){
                                    state1 = obj.getString("long_name") ;
                                }

                                if(type.contains("locality")){
                                    city = obj.getString("long_name");
                                }

                                if(type.contains("sublocality_level_2")){
                                    area2 = obj.getString("long_name");
                                }
                                if(type.contains("sublocality_level_1")){
                                    area1 = obj.getString("long_name");
                                }

                                if(type.contains("route")){
                                    street = obj.getString("long_name");
                                }

                                if(type.contains("establishment")){
                                    establishment = obj.getString("long_name") ;
                                }
                                if(type.contains("premise")){
                                    premise = obj.getString("long_name") ;
                                }
                            }

                            //String location = object.getString("formatted_address");

                            String area = "";
//                            if(street.isEmpty()){
//                                street = establishment;
//                            }
//
//                            if(street.isEmpty()){
//                                street = premise;
//                            }

                            if(!establishment.isEmpty()){
                                street = establishment + " " + street;
                            }

                            if(!premise.isEmpty()){
                                street = premise + " " + street;
                            }

                            if(area1.isEmpty() && area2.isEmpty()){
                                area = city;
                            }else{
                                area = area1 + " " + area2;
                            }

//                            if(mContext instanceof PickupLocationActivity){
//                                ((PickupLocationActivity)mContext).updateLocation(location, street ,area , city , state1 + " " + state2, postCode);
//                            }

                            if(mContext instanceof HomeActivity){
                                ((HomeActivity)mContext).updateLocation(location, street ,area , city , state1 + " " + state2, postCode, latLng);
                            }
                            if(mContext instanceof TouchMapActivity){
                                ((TouchMapActivity)mContext).updateLocation2(location, street ,area , city , state1 + " " + state2, postCode);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }else{

        }

    }


}
