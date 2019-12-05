package com.hurry.custom.controller;

import android.content.Context;
import android.os.AsyncTask;

import com.hurry.custom.common.Constants;
import com.hurry.custom.view.activity.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 7/21/2016.
 */
 public class SendPhoneNumber extends AsyncTask<Void, Void, Void> {

    //http://maps.googleapis.com/maps/api/geocode/json?latlng=41.771042,123.443602&sensor=true&language=US
    //https://maps.googleapis.com/maps/api/geocode/json
    //SweetAlertDialog sweetAlertDialog;
    Context  mContext;
    //HttpResponse result;
    String result;
    String phone;


    public SendPhoneNumber(Context con, String phone)
    {
        super();
        this.mContext = con;
        this.phone = phone;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... args) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("phone", phone));
        //params.add(new BasicNameValuePair("language", String.valueOf(latLng.longitude)));
        ServiceHandler sh = new ServiceHandler();
        result = sh.makeServiceCall(Constants.BASIC_DATA_URL + "getOtpGuest" , ServiceHandler.POST, params);
        return null;
    }
    String res  = null;

    @Override
    protected void onPostExecute(Void res) {

        if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String result= jsonObject.getString("result");
                    if(result.equals("200")){
                        if(mContext instanceof MainActivity){
                            
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
