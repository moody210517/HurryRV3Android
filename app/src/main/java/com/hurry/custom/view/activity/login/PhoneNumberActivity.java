package com.hurry.custom.view.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.common.utils.JsonHelper;
import com.hurry.custom.common.utils.ValidationHelper;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.view.BaseBackActivity;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.MainActivity;
import com.hurry.custom.view.dialog.OTPDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhoneNumberActivity extends BaseBackActivity implements View.OnClickListener  {


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rd_individual) RadioButton rdIndividual;
    @BindView(R.id.rd_business) RadioButton rdBusiness;
    @BindView(R.id.btn_individual) Button btnIndividual;
    @BindView(R.id.btn_business) Button btnBusiness;
    @BindView(R.id.edt_phone) EditText edtPhone;
    @BindView(R.id.ccp) CountryCodePicker countryCodePicker;
    @BindView(R.id.txt_phone_hint) TextView txtPhoneHint;
    @BindView(R.id.btn_create) Button btnCreate;
    @BindView(R.id.chk_term) CheckBox chkTerm;
    @BindView(R.id.chk_privacy) CheckBox chkPrivacy;
    @BindView(R.id.lin_term) LinearLayout linTerm;
    @BindView(R.id.lin_privacy) LinearLayout linPrivacy;

    String mBusinessType, mPhone ,ccp;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_phone_number);
        ButterKnife.bind(this);
        initBackButton(toolbar, getString(R.string.create_profile));
        initView();
    }

    private void initView(){
        rdIndividual.setOnClickListener(this);
        rdBusiness.setOnClickListener(this);
        btnIndividual.setOnClickListener(this);
        btnBusiness.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        chkTerm.setOnClickListener(this);
        chkPrivacy.setOnClickListener(this);
        ccp = "+91";
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                ccp = "+" +  selectedCountry.getPhoneCode();
            }
        });
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(edtPhone.getText().toString().length() > 0){
                    txtPhoneHint.setVisibility(View.GONE);
                }else{
                    txtPhoneHint.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rd_individual:
                mBusinessType = "0";
                break;
            case R.id.rd_business:
                mBusinessType = "1";
                break;
            case R.id.btn_individual:
                rdIndividual.setChecked(true);
                mBusinessType = "0";
                break;
            case R.id.btn_business:
                rdBusiness.setChecked(true);
                mBusinessType = "1";
                break;
            case R.id.btn_create:
                mPhone = ccp + edtPhone.getText().toString();

                if(!chkTerm.isChecked()){
                    if(android.os.Build.VERSION.SDK_INT >= 21){
                        linTerm.setBackgroundDrawable(getResources().getDrawable(R.drawable.checkbox_border, getTheme()));
                    }else{
                        linTerm.setBackgroundDrawable(getResources().getDrawable(R.drawable.checkbox_border));
                    }
                    Toast.makeText(PhoneNumberActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!chkPrivacy.isChecked()){
                    if(android.os.Build.VERSION.SDK_INT >= 21){
                        linPrivacy.setBackgroundDrawable(getResources().getDrawable(R.drawable.checkbox_border, getTheme()));
                    }else{
                        linPrivacy.setBackgroundDrawable(getResources().getDrawable(R.drawable.checkbox_border));
                    }
                    Toast.makeText(PhoneNumberActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!ValidationHelper.isValidPhoneNumber(mPhone)){
                    Toast.makeText(PhoneNumberActivity.this, getResources().getString(R.string.enter_phone), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(ValidationHelper.isValidEmailDetails(getValue("email"))){

                    String phone = ccp + edtPhone.getText().toString();
                    if(ValidationHelper.isValidPhoneNumber(phone)){
                        findViewById(R.id.lin_phone).setBackgroundColor(getResources().getColor(R.color.hint_color));
                        sendPhone(phone);
                    }else{
                        findViewById(R.id.lin_phone).setBackgroundColor(getResources().getColor(R.color.red));
                        Toast.makeText(PhoneNumberActivity.this, getString(R.string.enter_valid_phone) , Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(PhoneNumberActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }



                break;
            case R.id.chk_term:

                break;
            case R.id.chk_privacy:

                break;
        }
    }

    public void sendPhone(String phone) {

        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("user_id", PreferenceUtils.getUserId(this));
        WebClient.post(Constants.BASIC_DATA_URL + "getOtpSignup", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        showProgressDialog();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        // Log.d("Error String", responseString);

                        hideProgressDialog();
                        Toast.makeText(PhoneNumberActivity.this, "Check Network Connection", Toast.LENGTH_SHORT).show();
                        //((MainActivity)mContext).finish();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        // Log.d("Error", errorResponse.toString());
                        hideProgressDialog();
                        Toast.makeText(PhoneNumberActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        //((MainActivity)mContext).finish();
                    }
                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {
                        if (response != null) {
                            try{
                                if(response.getString("result").equals("200")){
                                    //edtPhone.setEnabled(false);
                                    OTPDialog otpDialog = new OTPDialog();
                                    otpDialog.show(PhoneNumberActivity.this, ccp +  edtPhone.getText().toString());
                                    otpDialog.getDialog().setCanceledOnTouchOutside(false);

                                    Toast.makeText(PhoneNumberActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                                }else if(response.getString("result").equals("400")){
                                    Toast.makeText(PhoneNumberActivity.this, getResources().getString(R.string.already_register), Toast.LENGTH_SHORT).show();
                                }

                            }catch (Exception e){
                            }
                        }
                    };
                    public void onFinish() {
                        hideProgressDialog();
                    }
                    ;
                });
    }


    public void registerNow(String code){

        RequestParams params = new RequestParams();
        params.put("device_token", PreferenceUtils.getDeviceToken(this));
        params.put("firstName", getValue("first"));
        params.put("lastName", getValue("last"));
        params.put("email", getValue("email"));
        params.put("business_type", mBusinessType);

        try {
            params.put("password", DeviceUtil.encrypt("hurryr12"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        params.put("phone", mPhone);
        String mSecurity = Constants.securityLists.get(0);
        params.put("question",mSecurity);
        String mAnswer = "HurryR";
        params.put("answer", mAnswer);
        params.put("term", chkTerm.isChecked()? "1":"0");
        params.put("policy", chkPrivacy.isChecked()? "1":"0");
        params.put("user_type", String.valueOf(Constants.MODE));
        String otp = code;
        params.put("otp", otp);

        JSONArray addressArray = new JSONArray();

//        for(int i = 0; i < smsList.size(); i++)
//        {
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("address1", "");
        jsonMap.put("address2", "");
        jsonMap.put("city", "");
        jsonMap.put("state", "");
        jsonMap.put("pincode", "");
        jsonMap.put("landmark", "");
        JSONObject json = new JSONObject(jsonMap);
        addressArray.put(json);
//        }

        params.put("address", addressArray.toString());

        WebClient.post(Constants.BASE_URL + "register", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        showProgressDialog();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(PhoneNumberActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        // Log.d("Error String", responseString);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        // Log.d("Error", errorResponse.toString());
                        Toast.makeText(PhoneNumberActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }
                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {
                        try {
                            if (response != null) {
                                //{"id":33,"first_name":"a","last_name":"a","email":"a","phone":"a","question":"What is your favorite Sports team?","answer":"a","term":"0","policy":"0"}
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(PhoneNumberActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                }else if(response.getString("result").equals("600")){
                                    Toast.makeText(PhoneNumberActivity.this, "Invalid otp code", Toast.LENGTH_SHORT).show();
                                }else{
                                    JsonHelper.parseSinUpProcess(PhoneNumberActivity.this, response);
                                    if(Constants.MODE == Constants.PERSONAL){
                                        PreferenceUtils.setPassword(PhoneNumberActivity.this , getValue("hurryr12"));
                                    }else if(Constants.MODE == Constants.CORPERATION){
                                        PreferenceUtils.setCorPassword(PhoneNumberActivity.this , getValue("hurryr12"));
                                    }

                                    PreferenceUtils.setLogIn(PhoneNumberActivity.this);
                                    PreferenceUtils.setNickname(PhoneNumberActivity.this,getValue("first"));

                                    Toast.makeText(PhoneNumberActivity.this, "Your Profile is created successfully",Toast.LENGTH_SHORT).show();
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("result","res");
                                    setResult(Activity.RESULT_OK,returnIntent);
                                    finish();


                                    Intent main = new Intent(PhoneNumberActivity.this, HomeActivity.class);
                                    startActivity(main);

                                }
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    };
                    public void onFinish() {
                        hideProgressDialog();
                    }
                    ;
                });
    }
    

}
