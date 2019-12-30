package com.hurry.custom.controller;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.hurry.custom.R;
import com.hurry.custom.view.activity.AddressDetailsNewActivity;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.map.MyAutoCompleteActivity;
import com.hurry.custom.view.activity.map.TouchMapActivity;
import com.hurry.custom.view.fragment.AddressDetailsNewFragment;

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
 public class GetLocationFromLatLng extends AsyncTask<Void, Void, Void> {

    public static String GOOGLE_MAP_URL ="https://maps.googleapis.com/maps/api/geocode/json";
    Context  mContext;
    String result;
    LatLng latLng;

    public GetLocationFromLatLng(Context con, LatLng latLng)
    {
        super();
        this.mContext = con;
        this.latLng = latLng;
        AddressDetailsNewFragment.isCalling = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... args) {
        //place_id
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("latlng", String.valueOf(latLng.latitude) +  "," + String.valueOf(latLng.longitude)  ));
        params.add(new BasicNameValuePair("sensor", "true"));
        params.add(new BasicNameValuePair("language", "US"));
        params.add(new BasicNameValuePair("key", mContext.getResources().getString(R.string.gecode_api_key2)));
        //params.add(new BasicNameValuePair("language", String.valueOf(latLng.longitude)));
        ServiceHandler sh = new ServiceHandler();
        if (isCancelled())
            return null;
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


                            String location = object.getString("formatted_address");
                            String area = "";
                            if(street.isEmpty()){
                                street = establishment;
                            }
                            if(street.isEmpty()){
                                street = premise;
                            }

                            if(area1.isEmpty() && area2.isEmpty()){
                                area = city;
                            }else{
                                area = area1 + " " + area2;
                            }

//                            String[] addresses = address.split(",");

//                            if(mContext instanceof PickupLocationActivity){
//                                ((PickupLocationActivity)mContext).updateLocation(location, street ,area , city , state1 + " " + state2, postCode);
//                            }
                            if(mContext instanceof MyAutoCompleteActivity){
                                ((MyAutoCompleteActivity)mContext).updateLocation(location, street ,area , city , state1 + " " + state2, postCode);
                            }
                            if(mContext instanceof TouchMapActivity){
                                ((TouchMapActivity)mContext).updateLocation(location, street ,area , city , state1 + " " + state2, postCode, latLng);
                            }

                            if(mContext instanceof HomeActivity){
                                ((HomeActivity)mContext).updateLocation(location, street ,area , city , state1 + " " + state2, postCode, latLng);
                            }

                            if(mContext instanceof AddressDetailsNewActivity){
                                ((AddressDetailsNewActivity)mContext).updateLocation(location, street ,area , city , state1 + " " + state2, postCode, latLng);
                            }

                            TouchMapActivity.isCalling = false;
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
