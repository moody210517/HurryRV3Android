package com.hurry.custom.controller;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    public final static int POST_BODY = 3;
    public final static int PUT_BODY = 4;
    public final static int DELETE = 5;
    public final static int GET_SSL = 6;
    public final static int POST_NOJSON = 7;
    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */

    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String url, int method,
                                  List<NameValuePair> params) {

        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST_BODY) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                   // httpPost.setEntity(new UrlEncodedFormEntity(params));
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    JSONObject holder = getJsonObjectFromMap(params);
                    StringEntity se = new StringEntity(holder.toString());
                    httpPost.setEntity(se);
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");
                }
                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("Accept", "application/json");
                httpGet.setHeader("Content-type", "application/json");
                httpResponse = httpClient.execute(httpGet);

            }else if(method == POST){

                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    if (params != null) {
                        String paramString = URLEncodedUtils
                                .format(params, "utf-8");
                        url += "?" + paramString;
                    }
                    httpPost = new HttpPost(url);
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                }
                httpResponse = httpClient.execute(httpPost);

            }else if( method == PUT_BODY){
                HttpPut httpPost = new HttpPut(url);
                // adding post params
                if (params != null) {
                    // httpPost.setEntity(new UrlEncodedFormEntity(params));
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

                    JSONObject holder = getJsonObjectFromMap(params);
                    StringEntity se = new StringEntity(holder.toString());
                    httpPost.setEntity(se);
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");
                }
                httpResponse = httpClient.execute(httpPost);
            }else if(method == DELETE){
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpDelete httpGet = new HttpDelete(url);
                httpResponse = httpClient.execute(httpGet);

            }else if(method == GET_SSL){
                OkHttpClient client = new OkHttpClient();

                if (params != null) {
                    if (params != null) {
                        String paramString = URLEncodedUtils
                                .format(params, "utf-8");
                        url += "?" + paramString;
                    }
                }

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response res = client.newCall(request).execute();
                return res.body().string();
            }else if(method == POST_NOJSON){

                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    if (params != null) {
                        String paramString = URLEncodedUtils
                                .format(params, "utf-8");
                        url += "?" + paramString;
                    }
                    httpPost = new HttpPost(url);
                    //httpPost.setHeader("Accept", "application/json");
                    //httpPost.setHeader("Content-type", "application/json");
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                }
                httpResponse = httpClient.execute(httpPost);

            }

            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;

    }

    String run(OkHttpClient client, String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response res = client.newCall(request).execute();
        return res.body().toString();


    }



    private static JSONObject getJsonObjectFromMap(List<NameValuePair> params) throws JSONException {

        //all the passed parameters from the post request
        //iterator used to loop through all the parameters
        //passed in the post request
        Iterator iter = params.iterator();

        //Stores JSON
        JSONObject holder = new JSONObject();

        //using the earlier example your first entry would get email
        //and the inner while would get the value which would be 'foo@bar.com'
        //{ fan: { email : 'foo@bar.com' } }

        //While there is another entry
        while (iter.hasNext())
        {
            //gets an entry in the params
            BasicNameValuePair pairs = (BasicNameValuePair) iter.next();
            //creates a key for Map
            String key = (String)pairs.getName();
            //Create a new map
            String m = (String)pairs.getValue();
            //object for storing Json


            //gets the value
//			Iterator iter2 = m.entrySet().iterator();
//			while (iter2.hasNext())
//			{
//				Map.Entry pairs2 = (Map.Entry)iter2.next();
//				data.put((String)pairs2.getKey(), (String)pairs2.getValue());
//			}

            //puts email and 'foo@bar.com'  together in map
            if(key.equals("active")){
                holder.put(key, true);
            }else{
                holder.put(key, m);
            }

            if(key.equals("openDate")){
                JSONArray jsonArray = new JSONArray();
                String[] res = m.split(":");
                for(int k = 0 ; k < res.length ; k++){
                    jsonArray.put(getWeekOfDay(res[k]));
                }
                holder.put(key, jsonArray);
            }

            if(key.equals("data")){
                JSONArray jsonArray = new JSONArray();
                String[] res = m.split(":");
                for(int k = 0 ; k < res.length ; k++){
                    jsonArray.put(res[k]);
                }
                holder.put(key, jsonArray);
            }
        }
        return holder;
    }

    public static   String getWeekOfDay (String week){
        if(week.equals("Monday")){
            return "Mon";
        }
        if(week.equals("Tuesday")){
            return "Tue";
        }
        if(week.equals("Wednesday")){
            return "Wed";
        }
        if(week.equals("Thursday")){
            return "Thu";
        }
        if(week.equals("Friday")){
            return "Fri";

        }
        if(week.equals("Saturday")){
            return "Sat";
        }
        if(week.equals("Sunday")){
            return "Sun";
        }
        return "";
    }

}