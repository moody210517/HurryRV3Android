/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hurry.custom.view.activity.setting;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.view.BaseActivity;
import com.hurry.custom.view.BaseBackActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermActivity extends BaseBackActivity implements View.OnClickListener{



    TextView txtPrivacy;
    WebView news_desc;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_term);
        ButterKnife.bind(this);
        Constants.page_type = getValue("type");

        initView();

        privacy();
    }


    private void initView(){


        if(getValue("type").equals("term")){
            initBackButton(toolbar, getResources().getString(R.string.term_and_condition));
        }else if(getValue("type").equals("privacy")){
            initBackButton(toolbar, getResources().getString(R.string.policy_and_term));
        }

        txtPrivacy = (TextView)findViewById(R.id.txt_privacy);
        news_desc = (WebView)findViewById(R.id.desc);
    }

    private void privacy() {
        RequestParams params = new RequestParams();
        String url = "";
        if(getValue("type").equals("term")){
            url = Constants.BASE_URL + "term";
        }else if(getValue("type").equals("privacy")){
            url = Constants.BASE_URL + "privacy";
        }

        if(!url.isEmpty() && url.contains("http")){

            WebClient.post(url, params,
                    new JsonHttpResponseHandler() {
                        public void onStart() {
                            //((MainActivity)mContext).showProgressDialog();

                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            // Log.d("Error String", responseString);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            // Log.d("Error", errorResponse.toString());
                        }
                        public void onSuccess(int statusCode,
                                              Header[] headers,
                                              JSONObject response) {
                            if (response != null) {
                                try{
                                    String text = response.getString("result");
                                    txtPrivacy.setText(text);
                                    showWebView(text);
                                }catch (Exception e){

                                }
                            }
                        };
                        public void onFinish()
                        {
                            //((MainActivity)mContext).hideProgressDialog();
                        }
                        ;
                    });

        }
    }



    public void showWebView(String title) {
        news_desc.setBackgroundColor(Color.parseColor("#ffffff"));
        news_desc.setFocusableInTouchMode(false);
        news_desc.setFocusable(false);
        news_desc.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = news_desc.getSettings();
        Resources res = getResources();
        int fontSize = 14;
        webSettings.setDefaultFontSize(fontSize);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = title;


        String text = "<html><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        news_desc.loadData(htmlText, mimeType, encoding);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
        }
    }
}
