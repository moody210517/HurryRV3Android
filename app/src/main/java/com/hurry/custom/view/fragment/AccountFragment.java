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

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.common.utils.JsonHelper;
import com.hurry.custom.common.utils.ValidationHelper;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.setting.TermActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.angmarch.views.NiceSpinner;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends BaseFragment implements  View.OnClickListener, TextWatcher{



    TextView txtContact;
    EditText edtFirst;
    EditText edtLast;
    EditText edtPhone;

    TextView txtAddress;
    EditText edtAddress1;
    AutoCompleteTextView edtAddress2;
    AutoCompleteTextView  edtCity;
    EditText edtState;
    AutoCompleteTextView  edtPinCode;
    EditText edtLandMark;

    TextView txtEmail;
    EditText edtEmail;
    EditText edtPassword;
    EditText edtConfirmPassword;
    EditText edtSecurity;
    EditText edtAnswer;

    CheckBox chkPreferred;
    Button btnCreate;
    NiceSpinner spSecurity;
    TextView txtTerm;

    String mFirst , mLast , mPhone;
    String mAddress1 , mAddress2, mCity , mState , mPinCode, mLandMark;
    String mEmail , mPassword , mConfirmPassword;

    String mSecurity;
    String mAnswer;
    boolean isChanged = false;

    public String[]  areaLists;
    public String[]  cityLists;
    public String[]  pincodeLists;

    Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = (View) inflater.inflate(
                R.layout.activity_update_profile, container, false);
        mContext = getActivity();

        initView(view);
        return view;
    }

    
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_create:
                if(checkInput()){
                    if(isChanged == true){if(!mPassword.matches(".*\\d.*") || mPassword.length() < 8){
                        // contains a number
                        Toast.makeText(mContext, "Password must be minimum 8 characters with a combo of alphanumeric characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                        if(!mPassword.equals(mConfirmPassword)){
                            Toast.makeText(mContext, "Password doesn’t match", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(!ValidationHelper.isValidEmailDetails(mEmail)){
                            Toast.makeText(mContext, "Invalid Email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(ValidationHelper.isPostCode(mPinCode)){
                            update();
                        }else{
                            Toast.makeText(mContext, "Invalid Post Code", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(mContext, "No Changes", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext, "Check Input", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_term:
                Intent intent = new Intent(mContext, TermActivity.class);
                intent.putExtra("type", "term");
                startActivity(intent);
                break;
            case R.id.txt_privacy:
                Intent privacy = new Intent(mContext, TermActivity.class);
                privacy.putExtra("type", "privacy");
                startActivity(privacy);
                break;
        }
    }

    private void initView(View view){

        TextView txtBusinessType = (TextView)view.findViewById(R.id.txt_business_type);
        txtBusinessType.setText(PreferenceUtils.getBusinessType(mContext).equals("0")? getString(R.string.individual_user): getString(R.string.business_user) );

        txtContact = (TextView)view.findViewById(R.id.txt_contact);
        edtFirst = (EditText)view.findViewById(R.id.edt_first);
        edtLast = (EditText)view.findViewById(R.id.edt_last);
        edtPhone = (EditText)view.findViewById(R.id.edt_phone);
        edtPhone.setEnabled(false);

        txtAddress = (TextView)view.findViewById(R.id.txt_address);
        edtAddress1 = (EditText)view.findViewById(R.id.edt_address1);
        edtAddress2 = (AutoCompleteTextView)view.findViewById(R.id.edt_address2);

        edtCity= (AutoCompleteTextView)view.findViewById(R.id.edt_city);
        edtState = (EditText)view.findViewById(R.id.edt_state);
        edtPinCode  = (AutoCompleteTextView)view.findViewById(R.id.edt_pincode);
        edtLandMark = (EditText)view.findViewById(R.id.edt_landmark);

        txtEmail = (TextView)view.findViewById(R.id.txt_email);
        edtEmail= (EditText)view.findViewById(R.id.edt_email);
        edtPassword= (EditText)view.findViewById(R.id.edt_password);
        edtConfirmPassword = (EditText)view.findViewById(R.id.edt_confirmpassword);
        edtSecurity = (EditText)view.findViewById(R.id.edt_security);
        edtAnswer = (EditText)view.findViewById(R.id.edt_answer);

        chkPreferred = (CheckBox)view.findViewById(R.id.chk_address);
        btnCreate = (Button)view.findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(this);

        spSecurity = (NiceSpinner)view.findViewById(R.id.sp_security);
        //spSecurity.type = 1;
        spSecurity.attachDataSource(Constants.securityLists);
        mSecurity = Constants.securityLists.get(0);
        spSecurity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSecurity = Constants.securityLists.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // init auto complete text part
        initAreaLists();
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>
                (mContext, R.layout.custom_auto, areaLists);

        edtAddress2.setAdapter(areaAdapter);
        edtAddress2.setThreshold(1);//will start working from first character

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>
                (mContext, R.layout.custom_auto, cityLists);

        edtCity.setAdapter(cityAdapter);
        edtCity.setThreshold(1);//will start working from first character

        ArrayAdapter<String> pincodeAdapter = new ArrayAdapter<String>
                (mContext, R.layout.custom_auto, pincodeLists);

        edtPinCode.setAdapter(pincodeAdapter);
        edtPinCode.setThreshold(1);//will start working from first character

        // set data part
        edtFirst.setText(PreferenceUtils.getNickname(mContext));
        edtLast.setText(PreferenceUtils.getLastName(mContext));
        edtPhone.setText(PreferenceUtils.getPhone(mContext));
        edtAddress1.setText(PreferenceUtils.getAddress1(mContext));
        edtAddress2.setText(PreferenceUtils.getAddress2(mContext));
        edtCity.setText(PreferenceUtils.getCity(mContext));
        edtState.setText(PreferenceUtils.getState(mContext));
        edtPinCode.setText(PreferenceUtils.getPincode(mContext));
        edtLandMark.setText(PreferenceUtils.getLandMark(mContext));

        if(Constants.MODE == Constants.CORPERATION){
            edtEmail.setText(PreferenceUtils.getCorEmail(mContext));
            edtPassword.setText(PreferenceUtils.getCorPassword(mContext));
            edtConfirmPassword.setText(PreferenceUtils.getCorPassword(mContext));
            spSecurity.setText(PreferenceUtils.getCorporateQuestion(mContext));
        }else{
            edtEmail.setText(PreferenceUtils.getEmail(mContext));
            edtPassword.setText(PreferenceUtils.getPassword(mContext));
            edtConfirmPassword.setText(PreferenceUtils.getPassword(mContext));
            spSecurity.setText(PreferenceUtils.getCustomerQuestion(mContext));
        }


        edtAnswer.setText(PreferenceUtils.getAnswer(mContext));

        edtFirst.addTextChangedListener(this);
        edtLast.addTextChangedListener(this);
        edtPhone.addTextChangedListener(this);
        edtAddress1.addTextChangedListener(this);
        edtAddress2.addTextChangedListener(this);
        edtCity.addTextChangedListener(this);
        edtState.addTextChangedListener(this);
        edtPinCode.addTextChangedListener(this);
        edtLandMark.addTextChangedListener(this);
        edtEmail.addTextChangedListener(this);
        edtPassword.addTextChangedListener(this);
        edtConfirmPassword.addTextChangedListener(this);
        edtAnswer.addTextChangedListener(this);

        txtTerm = (TextView)view.findViewById(R.id.txt_term);
        txtTerm.setOnClickListener(this);
        TextView privacy = (TextView)view.findViewById(R.id.txt_privacy);
        privacy.setOnClickListener(this);

        if(DeviceUtil.isTablet(mContext)){

            edtFirst.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtLast.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtPhone.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));

            edtCity.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtState.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtPinCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtLandMark.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));

            edtEmail.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtPassword.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtConfirmPassword.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtAnswer.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtAddress1.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtAddress2.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));


            view.findViewById(R.id.line_first).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_last).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_phone).setVisibility(View.VISIBLE);

            view.findViewById(R.id.line_city).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_state).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_pincode).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_landmark).setVisibility(View.VISIBLE);

            view.findViewById(R.id.line_email).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_password).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_confirmpassword).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_answer).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_address).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_address2).setVisibility(View.VISIBLE);
        }

        edtFirst.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(ValidationHelper.isSpecialCharacter(edtFirst.getText().toString())){
                    edtFirst.setText(edtFirst.getText().toString().subSequence(0,s.length() -1 ));
                    edtFirst.setSelection(edtFirst.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtLast.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(ValidationHelper.isSpecialCharacter(edtLast.getText().toString())){
                    edtLast.setText(edtLast.getText().toString().subSequence(0,s.length() -1 ));
                    edtLast.setSelection(edtLast.length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private  void initAreaLists(){
        areaLists = new String[Constants.areaLists.size()];
        for(int k = 0; k < Constants.areaLists.size(); k++){
            areaLists[k] = Constants.areaLists.get(k).title;
        }

        cityLists = new String[Constants.cityLists.size()];
        for(int k = 0; k < Constants.cityLists.size() ; k++){
            cityLists[k] = Constants.cityLists.get(k).title;
        }

        pincodeLists = new String[Constants.pincodeLists.size()];
        for(int k = 0 ; k < Constants.pincodeLists.size(); k++){
            pincodeLists[k] = Constants.pincodeLists.get(k).title;
        }
    }

    private  boolean checkInput(){

        mFirst = edtFirst.getText().toString();
        mLast = edtLast.getText().toString();
        mPhone = edtPhone.getText().toString();

        mAddress1 = edtAddress1.getText().toString();
        mAddress2 = edtAddress2.getText().toString();
        mCity = edtCity.getText().toString();
        mState = edtState.getText().toString();
        mPinCode  = edtPinCode.getText().toString();
        mLandMark = edtLandMark.getText().toString();

        mEmail= edtEmail.getText().toString();
        mPassword = edtPassword.getText().toString();
        mConfirmPassword = edtConfirmPassword.getText().toString();

        mAnswer= edtAnswer.getText().toString();

        if(mFirst.isEmpty() || mLandMark.isEmpty() || mPhone.isEmpty() ||
                mAddress1.isEmpty() || mCity.isEmpty() || mState.isEmpty() ||
                mPinCode.isEmpty() || mLandMark.isEmpty() || mEmail.isEmpty() || mPassword.isEmpty() ||
                mConfirmPassword.isEmpty() || mSecurity.isEmpty() || mAnswer.isEmpty() || mAddress2.isEmpty()){
            return false;
        }
        return true;
    }

    private void update() {
        RequestParams params = new RequestParams();

        if(Constants.MODE == Constants.PERSONAL){
            params.put("id", PreferenceUtils.getUserId(mContext));
        }else{
            params.put("id", PreferenceUtils.getCorporateUserId(mContext));
        }
        params.put("firstName", mFirst);
        params.put("lastName", mLast);
        params.put("email", mEmail);
        try {
            params.put("password", DeviceUtil.encrypt(mPassword));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("phone", mPhone);
        params.put("question",mSecurity);
        params.put("answer", mAnswer);
        JSONArray addressArray = new JSONArray();

        Map<String, String> jsonMap = new HashMap<String, String>();

        jsonMap.put("id", PreferenceUtils.getAddressId(mContext));
        jsonMap.put("address1", mAddress1);
        jsonMap.put("address2", mAddress2);
        jsonMap.put("city", mCity);
        jsonMap.put("state", mState);
        jsonMap.put("pincode", mPinCode);
        jsonMap.put("landmark", mLandMark);
        JSONObject json = new JSONObject(jsonMap);
        addressArray.put(json);
//        }
        params.put("address", addressArray.toString());

        WebClient.post(Constants.BASE_URL + "update", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        ((HomeActivity)mContext).showProgressDialog();
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
                        try {
                            if (response != null) {

                                JsonHelper.parseSinUpProcess(mContext, response);
                                if(Constants.MODE == Constants.PERSONAL){
                                    PreferenceUtils.setPassword(mContext , mPassword);
                                }else if(Constants.MODE == Constants.CORPERATION){
                                    PreferenceUtils.setCorPassword(mContext , mPassword);
                                }
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("result","res");
                                //setResult(Activity.RESULT_OK,returnIntent);
                                Toast.makeText(mContext, "Changes updated successfully", Toast.LENGTH_SHORT).show();

                            }else if(response.getString("result").equals("400")){
                                Toast.makeText(mContext, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    };
                    public void onFinish() {
                        ((HomeActivity)mContext).hideProgressDialog();

                    }
                    ;
                });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        isChanged = true;
    }

}
