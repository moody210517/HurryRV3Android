package com.hurry.custom.controller;

import android.content.Context;

import com.hurry.custom.common.db.PreferenceUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class WebClient {
//
//	public WebClient(){
//		HttpParams httpParameters = new BasicHttpParams();
//		int timeoutConnection = 10000;
//		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//		int timeoutSocket = 10000;
//		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//		client = new MyHttpClient(httpParameters, null);
//	}

	private static MyAsyncHttpClient client = new MyAsyncHttpClient();
	


	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler, Context c) {

		params.put("X-API-KEY", PreferenceUtils.getToken(c));
		client.setConnectTimeout(10000);
		client.cancelAllRequests(true);
		client.get(url, params, responseHandler);
	}


	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(100000);
		client.cancelAllRequests(true);
		client.get(url, params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(50000);
		client.setConnectTimeout(50000);
		client.cancelAllRequests(true);
		client.post(url, params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler, Context c) {
		params.put("X-API-KEY", PreferenceUtils.getToken(c));
		client.cancelAllRequests(true);
		client.post(url, params, responseHandler);
	}

}
