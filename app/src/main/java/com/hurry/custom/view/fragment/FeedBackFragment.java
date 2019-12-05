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

package com.hurry.custom.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.common.utils.ValidationHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.view.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FeedBackFragment extends Fragment implements View.OnClickListener{
    Context mContext;


    EditText edtName;
    EditText edtEmail;
    EditText edtFeedback;

    Button btnSubmit;

    @BindView(R.id.txt_first_hint) TextView txtFirstHint;
    @BindView(R.id.txt_email_hint) TextView txtEmailHint;
    @BindView(R.id.txt_feedback_hint) TextView txtFeedbackHint;

    String mName;
    String  mEmail;
    String mFeedback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(
                R.layout.fragment_feedback, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        initView(view);
        PreferenceUtils.setFeedBackId(mContext, "0");
        return view;
    }

    private void initView(View view){
        edtName = (EditText)view.findViewById(R.id.edt_name);
        edtEmail = (EditText)view.findViewById(R.id.edt_email);
        edtFeedback = (EditText)view.findViewById(R.id.edt_feedback);
        btnSubmit = (Button)view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        if(DeviceUtil.isTablet(mContext)){
            edtName.setHintTextColor(getResources().getColor(R.color.gray));
            edtEmail.setHintTextColor(getResources().getColor(R.color.gray));
            edtFeedback.setHintTextColor(getResources().getColor(R.color.gray));
        }

        edtName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtFirstHint.setVisibility(View.GONE);
                txtEmailHint.setVisibility(View.VISIBLE);
                txtFeedbackHint.setVisibility(View.VISIBLE);
                return false;
            }
        });

        edtEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtFirstHint.setVisibility(View.VISIBLE);
                txtEmailHint.setVisibility(View.GONE);
                txtFeedbackHint.setVisibility(View.VISIBLE);
                return false;
            }
        });

        edtFeedback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtFirstHint.setVisibility(View.VISIBLE);
                txtEmailHint.setVisibility(View.VISIBLE);
                txtFeedbackHint.setVisibility(View.GONE);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                submit();
                break;
        }
    }



    private void submit(){

        mName = edtName.getText().toString();
        mEmail = edtEmail.getText().toString();
        mFeedback  = edtFeedback.getText().toString();

        if(mName.isEmpty() || mEmail.isEmpty() || mFeedback.isEmpty()){
            Toast.makeText(mContext, mContext.getResources().getString(R.string.input_all) ,Toast.LENGTH_SHORT).show();
            return;
        }

        if(!ValidationHelper.isValidEmailDetails(mEmail)){
            Toast.makeText(mContext, "Invalid Email" ,Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(mContext)
                //.setTitle("")
                .setMessage("Thank you for the Feedback")
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Toast.makeText(MainActivity.this, "Yes", Toast.LENGTH_SHORT).show();
                        edtName.setText("");
                        edtEmail.setText("");
                        edtFeedback.setText("");
                        feedback();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }



    private void feedback() {
        RequestParams params = new RequestParams();
        params.put("user_mode", "0");
        params.put("name", mName);
        params.put("email", mEmail);
        params.put("feedback", mFeedback);
        params.put("user_id", PreferenceUtils.getUserId(mContext));
        params.put("id", PreferenceUtils.getFeedBackId(mContext));

        WebClient.post(Constants.BASE_URL + "feedback", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        ((MainActivity)mContext).showProgressDialog();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(mContext, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        // Log.d("Error String", responseString);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        // Log.d("Error", errorResponse.toString());

                        Toast.makeText(mContext, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }
                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {
                        try {
                            if (response != null) {
                                //{"id":33,"first_name":"a","last_name":"a","email":"a","phone":"a","question":"What is your favorite Sports team?","answer":"a","term":"0","policy":"0"}

                                if(response.getString("result").equals("400")){
                                    Toast.makeText(mContext, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }else {

                                    String id = response.getString("result");
                                    PreferenceUtils.setFeedBackId(mContext, id);
                                    Toast.makeText(mContext, getResources().getString(R.string.success),Toast.LENGTH_SHORT).show();

                                }
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    };
                    public void onFinish() {
                        ((MainActivity)mContext).hideProgressDialog();
                    }
                    ;
                });
    }

}
