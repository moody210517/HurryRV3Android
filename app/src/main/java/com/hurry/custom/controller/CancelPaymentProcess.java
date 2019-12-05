package com.hurry.custom.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.hurry.custom.view.adapter.OrderHistoryAdapter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * Created by Administrator on 7/21/2016.
 */
 public class CancelPaymentProcess extends AsyncTask<Void, Void, Void> {
    Context  mContext;
    String result;
    String paymentId, refundAmount, orderId;
    OrderHistoryAdapter orderHistoryAdapter;
    public CancelPaymentProcess(OrderHistoryAdapter orderHistoryAdapter, Context con, String paymentId, String refundAmount, String orderId)
    {
        super();
        this.mContext = con;
        this.paymentId = paymentId;
        this.refundAmount = refundAmount;
        this.orderHistoryAdapter = orderHistoryAdapter;
        this.orderId = orderId;
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

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
        RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"employer_id\"\r\n\r\n0\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
        Request request = new Request.Builder()
                .url("https://www.payumoney.com/treasury/merchant/refundPayment?merchantKey=iBzb2IZp&paymentId=" + paymentId+ "&refundAmount=" + refundAmount)
                .post(body)
                .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                .addHeader("Content-Type", "application/json")
                .addHeader("authorization", "/ZanKOwsXGLTryavOYQlL8zKZw3JAnukwokz4H0o07o=")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "e801c16e-984e-4c6a-8230-ba05e7cfd99c")
                .build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void res) {
//        if (null != sweetAlertDialog && sweetAlertDialog.isShowing()) {
//            sweetAlertDialog.dismiss();
//        }
         if (result != null) {
             try {
                 JSONObject obj = new JSONObject(String.valueOf(result));
                 if(obj.getString("status").equals("0")){
                     orderHistoryAdapter.cancelOrder(orderId, 1);
                 }else {
                     Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                 }
             } catch (JSONException e) {
                 e.printStackTrace();
             }
        }
    }
}
