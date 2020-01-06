package com.hurry.custom.controller;

import android.content.Context;
import android.os.AsyncTask;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.view.activity.HomeActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 7/21/2016.
 */
 public class GetPhone extends AsyncTask<Void, Void, Void> {
    Context  mContext;
    String result;
    public GetPhone(Context con)
    {
        super();
        this.mContext = con;
    }

    @Override
    protected void onPreExecute() {
//        ((Activity)mContext).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
//                sweetAlertDialog.setTitleText("Connecting to server");
//                sweetAlertDialog.setContentText("Logging in, please wait...");
//                sweetAlertDialog.show();
////            }
//        });
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... args) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        ServiceHandler sh = new ServiceHandler();
        //String currentTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        if(Constants.MODE == Constants.PERSONAL){
            params.add(new BasicNameValuePair("employer_id", "0")); //PreferenceUtils.getUserId(mContext)
        }else{
            params.add(new BasicNameValuePair("employer_id", "0")); //PreferenceUtils.getCorporateUserId(mContext)
        }

        //params.add(new BasicNameValuePair("apps",  mPackage));
        result = sh.makeServiceCall( Constants.URL + "/basic/get_Contact_Details", ServiceHandler.POST_NOJSON, params); //
        return null;
    }


    @Override
    protected void onPostExecute(Void res) {
//        if (null != sweetAlertDialog && sweetAlertDialog.isShowing()) {
//            sweetAlertDialog.dismiss();
//        }
         if (result != null) {
             try{
                 JSONArray jsonArray = new JSONArray(result);
                 JSONObject object = jsonArray.getJSONObject(0);
                 String phone  = object.getString("PhoneNumber");
                 PreferenceUtils.setConfPhone(mContext, phone);

                 if(mContext instanceof HomeActivity){
                     ((HomeActivity)mContext).callPhone();
                 }else{
                     new GetCity(mContext, "").execute();
                 }


             }catch (Exception e){};
        }
    }

}
