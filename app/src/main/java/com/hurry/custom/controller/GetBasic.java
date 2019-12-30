package com.hurry.custom.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.utils.JsonHelper;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.MainActivity;
import com.hurry.custom.view.activity.login.SplashActivity;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 7/21/2016.
 */
 public class GetBasic extends AsyncTask<Void, Void, Void> {
    Context  mContext;
    String result;
    String type = "";
    public GetBasic(Context con, String  type)
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
        //String currentTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        //params.add(new BasicNameValuePair("apps",  mPackage));
        result = sh.makeServiceCall( Constants.BASIC_DATA_URL + "get_basic", ServiceHandler.POST, params); //
        return null;
    }


    @Override
    protected void onPostExecute(Void res) {
         if (result != null) {
             try{
                 JSONObject response = new JSONObject(result);
                 if(response.getString("result").equals("200")){
                     JsonHelper.parseBasicData(response);

                     if(type.equals("auto")){
                         if(mContext instanceof SplashActivity){
                             ((SplashActivity)mContext).getCities();
                         }
                     }

                     if(type.isEmpty() && mContext instanceof  HomeActivity){
                         ((HomeActivity)mContext).getCities();
                     }


                     if(mContext instanceof SplashActivity){
                         ((SplashActivity)mContext).callNext();
                     }
                 }else if(response.getString("result").equals("400")){
                     Toast.makeText(mContext, mContext.getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                 }
             }catch (Exception e){};

         }
    }

}
