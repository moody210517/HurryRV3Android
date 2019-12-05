package com.hurry.custom.common.utils;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.model.BasicModel;
import com.hurry.custom.model.CityModel;
import com.hurry.custom.model.TruckModel;

/**
 * Created by Administrator on 6/2/2016.
 */
public class JsonHelper {


    public static void parseSinUpProcess(Context context, JSONObject response){

        try{

            String userId = response.getString("id");
            String first_name = response.getString("first_name");
            String last_name = response.getString("last_name");

            String email = response.getString("email");
            String phone = response.getString("phone");
            String question = response.getString("question");
            String answer = response.getString("answer");
            String mBusinessType = "0";


            String term = "";
            try {
                term   = response.getString("term");
            }catch (Exception e){
            }

            String policy = "";
            try{
                policy     = response.getString("policy");
                mBusinessType = response.getBoolean("business_type") == true? "1" :"0";

            }catch (Exception e){
            }


            JSONArray addressArray = response.getJSONArray("address");
            for( int k = 0; k < addressArray.length(); k++){

                JSONObject object = addressArray.getJSONObject(k);
                PreferenceUtils.setAddress1(context, object.getString("address"));
                PreferenceUtils.setAddress2(context, object.getString("address2"));
                PreferenceUtils.setCity(context, object.getString("city"));
                PreferenceUtils.setState(context, object.getString("state"));
                PreferenceUtils.setPincode(context, object.getString("pincode"));
                PreferenceUtils.setLandMark(context, object.getString("landmark"));
                PreferenceUtils.setAddressId(context, object.getString("id"));
            }

            if(Constants.MODE == Constants.PERSONAL){
                PreferenceUtils.setUserId(context, userId);
                PreferenceUtils.setEmail(context, email);
                PreferenceUtils.setCustomerQuestion(context, question);
            }else if(Constants.MODE == Constants.CORPERATION){
                PreferenceUtils.setCorporateUserId(context, userId);
                PreferenceUtils.setCorEmail(context, email);
                PreferenceUtils.setCorporateQuestion(context, question);
            }

            PreferenceUtils.setNickname(context, first_name);
            PreferenceUtils.setLast(context, last_name);
            PreferenceUtils.setPhone(context, phone);

            PreferenceUtils.setAnswer(context, answer);
            PreferenceUtils.setPolicy(context, policy);
            PreferenceUtils.setTerm(context, term);
            PreferenceUtils.setLogIn(context);

            PreferenceUtils.setBusinessType(context, mBusinessType);

        }catch (Exception e){

        }
    }




    public static void parseSourceLocation(JSONObject resultsObject){

        try{

            //JSONObject resultsObject = new JSONObject(results);
            String status = resultsObject.getString("status");
            if(status.equals("OK")){
                JSONArray resultArray = resultsObject.getJSONArray("results");
                for(int k = 0; k < resultArray.length() ;k++){

                    JSONObject object = resultArray.getJSONObject(k);
                    String types = object.getJSONArray("types").toString();

                    //if(types.equals("postal_code")){
                        JSONObject geometry = object.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");

                        Constants.addressModel.sourceLat = location.getDouble("lat");
                        Constants.addressModel.sourceLng = location.getDouble("lng");

                   // }

                }

            }
        }catch (Exception e){

        }
    }


    public static void parseDesLocatoin(JSONObject resultsObject){
        try{
            //JSONObject resultsObject = new JSONObject(results);
            String status = resultsObject.getString("status");
            if(status.equals("OK")){
                JSONArray resultArray = resultsObject.getJSONArray("results");
                for(int k = 0; k < resultArray.length() ;k++){
                    JSONObject object = resultArray.getJSONObject(k);
                    String types = object.getJSONArray("types").toString();
                    //if(types.equals("postal_code")){
                        JSONObject geometry = object.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        Constants.addressModel.desLat = location.getDouble("lat");
                        Constants.addressModel.desLng = location.getDouble("lng");
                   // }
                }
            }
        }catch (Exception e){

        }
    }



    public static String parseNearestDriverDurationString(JSONObject jsonObject) {

        try{
            String status = jsonObject.getString("status");
            if(status.equals("OK")){
                JSONArray rows  = jsonObject.getJSONArray("rows");
                JSONObject rowObj = rows.getJSONObject(0);
                JSONArray elementsArray = rowObj.getJSONArray("elements");
                JSONObject elementObj = elementsArray.getJSONObject(0);
                String element_status= elementObj.getString("status");

                if(element_status.equals("OK")){
                    JSONObject duration = elementObj.getJSONObject("duration");
                    String text = duration.getString("text");
                    String value = duration.getString("value");
                    Constants.addressModel.duration = text;

                    JSONObject distance = elementObj.getJSONObject("distance");
                    String dis_value = distance.getString("value");
                    String dis_text = distance.getString("text");
                    Constants.addressModel.distance = dis_value;
                    Constants.addressModel.distance_text = dis_text;
                    return text;
                }
            }
        }catch (Exception e){

        }
        return "";
    }


    public static void parseBasicData(JSONObject object){

        try{

            JSONArray itemsArray  =  object.getJSONArray("items");
            Constants.itemLists.clear();

            for(int j = 0 ; j < itemsArray.length();  j++){
                JSONObject obj = itemsArray.getJSONObject(j);
                BasicModel model = new BasicModel();
                model.id = obj.getString("id");
                model.title = obj.getString("title");
                Constants.itemLists.add(model);
            }

			
            Collections.sort(Constants.areaLists, new Comparator<BasicModel>() {
                public int compare(BasicModel v1, BasicModel v2) {
                    return v1.title.compareTo(v2.title);
                }
            });

            Collections.sort(Constants.cityLists, new Comparator<BasicModel>() {
                public int compare(BasicModel v1, BasicModel v2) {
                    return v1.title.compareTo(v2.title);
                }
            });

            Collections.sort(Constants.pincodeLists, new Comparator<BasicModel>() {
                public int compare(BasicModel v1, BasicModel v2) {
                    return v1.title.compareTo(v2.title);
                }
            });

		 Collections.sort(Constants.itemLists, new Comparator<BasicModel>() {
                public int compare(BasicModel v1, BasicModel v2) {
                    return v1.title.compareTo(v2.title);
                }
            });

        }catch (Exception e){

        }

    }


    public static void parseTruck(JSONObject object){

        try{
            JSONArray jsonArray = object.getJSONArray("truck");
            Constants.truckModels.clear();
            for(int k = 0; k < jsonArray.length(); k++){

                JSONObject obj  = jsonArray.getJSONObject(k);
                TruckModel model = new TruckModel();
                model.id = obj.getString("id");
                model.name =obj.getString("name");
                model.description= obj.getString("description");
                model.code = obj.getString("code");

                Constants.truckModels.add(model);
            }
        }catch (Exception e){

        }
    }



    public static void parseCities(JSONArray jsonArray ){
        try{
            Constants.cityModels.clear();
            for(int k = 0; k < jsonArray.length(); k++){
                JSONObject obj  = jsonArray.getJSONObject(k);
                CityModel model = new CityModel();
                model.id = obj.getString("id");
                model.name =obj.getString("name");
                model.lat= obj.getString("lat");
                model.lng = obj.getString("lng");
                model.geofence = obj.getString("geofence");
                model.image = obj.getString("image");
                Constants.cityModels.add(model);
            }
        }catch (Exception e){

        }
    }


}
