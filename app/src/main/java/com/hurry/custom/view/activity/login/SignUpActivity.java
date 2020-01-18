package com.hurry.custom.view.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
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
import com.hurry.custom.view.activity.setting.TermActivity;
import com.hurry.custom.view.dialog.OTPDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.angmarch.views.NiceSpinner;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 3/18/2017.
 */

public class SignUpActivity extends BaseBackActivity implements View.OnClickListener {

    TextView txtContact;
    EditText edtFirst;
    EditText edtLast;
    EditText edtPhone;
    TextView txtResend;

    TextView txtAddress;
    EditText edtAddress1;
    AutoCompleteTextView edtAddress2;
    AutoCompleteTextView edtCity;
    EditText edtState;
    AutoCompleteTextView edtPinCode;
    EditText edtLandMark;

    TextView txtEmail;
    EditText edtEmail;
    EditText edtPassword;
    EditText edtConfirmPassword;
    EditText edtSecurity;
    EditText edtAnswer;
    NiceSpinner spSecurity;

    CheckBox chkTerm;
    CheckBox chkPrivacy;
    CheckBox chkPreferred;

    LinearLayout linTerm;
    LinearLayout linPrivacy;

    TextView txtTerm;
    Button btnCreate;


    @BindView(R.id.txt_first_hint) TextView txtFirstHint;
    @BindView(R.id.txt_last_hint) TextView txtLastHint;
    @BindView(R.id.txt_address1_hint) TextView txtAddress1Hint;
    @BindView(R.id.txt_address2_hint) TextView txtAddress2Hint;
    @BindView(R.id.txt_landmark_hint) TextView txtLandHint;
    @BindView(R.id.txt_city_hint) TextView txtCityHint;
    @BindView(R.id.txt_state_hint) TextView txtStateHint;
    @BindView(R.id.txt_pincode_hint) TextView txtPinCodeHint;
    @BindView(R.id.txt_email_hint) TextView txtEmailHint;
    @BindView(R.id.txt_password_hint) TextView txtPasswordHint;
    @BindView(R.id.txt_confirmpassword_hint) TextView txtConfirmPasswordHint;
    @BindView(R.id.txt_phone_hint) TextView txtPhonePrefix;

    @BindView(R.id.rd_individual) Switch rdIndividual;
    @BindView(R.id.rd_business) Switch rdBusiness;

    @BindView(R.id.ccp) CountryCodePicker countryCodePicker;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.lin_create) LinearLayout linCreate;
    @BindView(R.id.btn_business) Button btnBusiness;
    @BindView(R.id.btn_individual) Button btnIndividual;
    @BindView(R.id.txt_chk_term) TextView txtChkTerm;
    @BindView(R.id.txt_chk_privacy) TextView txtChkPrivacy;

    String mFirst , mLast , mPhone , mBusinessType = "0";
    String mAddress1 , mArea , mCity , mState , mPinCode , mLandMark;
    String mEmail , mPassword , mConfirmPassword , mSecurity , mAnswer;

    public String[]  areaLists;
    public String[]  cityLists;
    public String[]  pincodeLists;

    String ccp;
    boolean otpSent = false;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        Constants.page_type = "signup";

        initBackButton(toolbar, getString(R.string.create_profile));

        initFirstVies();
        initView();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_chk_term:
                if(chkTerm.isChecked()){
                    chkTerm.setChecked(false);
                }else{
                    chkTerm.setChecked(true);
                }

                break;
            case R.id.txt_chk_privacy:
                if(chkPrivacy.isChecked()){
                    chkPrivacy.setChecked(false);
                }else{
                    chkPrivacy.setChecked(true);
                }

                break;

            case R.id.btn_individual:
                rdIndividual.setChecked(true);
                rdBusiness.setChecked(false);
                mBusinessType = "0";
                break;
            case R.id.btn_business:
                rdBusiness.setChecked(true);
                rdIndividual.setChecked(false);
                mBusinessType = "1";
                break;
            case R.id.rd_individual:
                rdIndividual.setChecked(true);
                rdBusiness.setChecked(false);
                mBusinessType = "0";
                break;
            case R .id.rd_business:
                rdBusiness.setChecked(true);
                rdIndividual.setChecked(false);
                mBusinessType = "1";
                break;

            case R.id.btn_create:
                if(otpSent){
                    if(checkInput()){

                    }else{
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.input_all), Toast.LENGTH_SHORT).show();
                    }
                }else{

                    if(checkInput()){
                        if(!mPassword.matches(".*\\d.*") || mPassword.length() < 8){
                            // contains a number
                            Toast.makeText(SignUpActivity.this, "Password must be minimum 8 characters with a combo of alphanumeric characters", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(!mPassword.equals(mConfirmPassword)){
                            Toast.makeText(SignUpActivity.this, "Password dose not match", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!ValidationHelper.isPostCode(mPinCode)){
                            Toast.makeText(SignUpActivity.this, "Invalid Post Code", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(!chkTerm.isChecked()){
                            if(android.os.Build.VERSION.SDK_INT >= 21){
                                linTerm.setBackgroundDrawable(getResources().getDrawable(R.drawable.checkbox_border, getTheme()));
                            }else{
                                linTerm.setBackgroundDrawable(getResources().getDrawable(R.drawable.checkbox_border));
                            }

                            Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(!chkPrivacy.isChecked()){
                            if(android.os.Build.VERSION.SDK_INT >= 21){
                                linPrivacy.setBackgroundDrawable(getResources().getDrawable(R.drawable.checkbox_border, getTheme()));
                            }else{
                                linPrivacy.setBackgroundDrawable(getResources().getDrawable(R.drawable.checkbox_border));
                            }
                            Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(!ValidationHelper.isValidPhoneNumber(mPhone)){
                            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.enter_phone), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(ValidationHelper.isValidEmailDetails(mEmail)){
//                            mFirst = edtFirst.getText().toString();
//                            String otp = edtOtp.getText().toString();
//                            if(!otp.isEmpty() && otp.length() == 6){
//                                registerNow();
//                            }else{
//                                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.enter_otp), Toast.LENGTH_SHORT).show();
//                            }
                            String phone = ccp + edtPhone.getText().toString();
                            if(ValidationHelper.isValidPhoneNumber(phone)){
                                findViewById(R.id.lin_phone).setBackgroundColor(getResources().getColor(R.color.hint_color));
                                sendPhone(phone, "", false);
                            }else{
                                findViewById(R.id.lin_phone).setBackgroundColor(getResources().getColor(R.color.red));
                                Toast.makeText(SignUpActivity.this, getString(R.string.enter_valid_phone) , Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(SignUpActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                        }



                    }

                }
                break;

            case R.id.txt_term:
                Intent intent = new Intent(SignUpActivity.this, TermActivity.class);
                intent.putExtra("type", "term");
                startActivity(intent);
                break;
            case R.id.txt_privacy:
                Intent privacy = new Intent(SignUpActivity.this, TermActivity.class);
                privacy.putExtra("type", "privacy");
                startActivity(privacy);
                break;
        }
    }

    private void showHints(TextView txtView){

        txtFirstHint.setVisibility(View.VISIBLE);
        txtLastHint.setVisibility(View.VISIBLE);
        txtAddress1Hint.setVisibility(View.VISIBLE);
        txtAddress2Hint.setVisibility(View.VISIBLE);
        txtLandHint.setVisibility(View.VISIBLE);
        txtCityHint.setVisibility(View.VISIBLE);
        txtStateHint.setVisibility(View.VISIBLE);
        txtPinCodeHint.setVisibility(View.VISIBLE);
        txtEmailHint.setVisibility(View.VISIBLE);
        txtPasswordHint.setVisibility(View.VISIBLE);

        txtView.setVisibility(View.GONE);

    }


    boolean flag = true;
    private void initFirstVies(){

        txtContact = (TextView)findViewById(R.id.txt_contact);
        edtFirst = (EditText)findViewById(R.id.edt_first);
        edtLast = (EditText)findViewById(R.id.edt_last);
        edtPhone = (EditText)findViewById(R.id.edt_phone);
        txtResend = (TextView)findViewById(R.id.txt_resend);
        txtResend.setOnClickListener(this);

        txtAddress = (TextView)findViewById(R.id.txt_address);
        edtAddress1 = (EditText)findViewById(R.id.edt_address1);
        edtAddress2 = (AutoCompleteTextView) findViewById(R.id.edt_address2);

        edtCity= (AutoCompleteTextView)findViewById(R.id.edt_city);
        edtState = (EditText)findViewById(R.id.edt_state);

        edtPinCode  = (AutoCompleteTextView)findViewById(R.id.edt_pincode);
        edtLandMark = (EditText)findViewById(R.id.edt_landmark);

        txtEmail = (TextView)findViewById(R.id.txt_email);
        edtEmail= (EditText)findViewById(R.id.edt_email);
        edtPassword= (EditText)findViewById(R.id.edt_password);
        edtConfirmPassword = (EditText)findViewById(R.id.edt_confirmpassword);
        edtSecurity = (EditText)findViewById(R.id.edt_security);
        edtAnswer = (EditText)findViewById(R.id.edt_answer);


        edtFirst.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHints(txtFirstHint);
                return false;
            }
        });
        edtLast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHints(txtLastHint);
                return false;
            }
        });
        edtAddress1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHints(txtAddress1Hint);
                return false;
            }
        });
        edtAddress2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHints(txtAddress2Hint);
                return false;
            }
        });
        edtCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHints(txtCityHint);
                return false;
            }
        });
        edtState.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHints(txtStateHint);
                return false;
            }
        });
        edtPinCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHints(txtPinCodeHint);
                return false;
            }
        });
        edtLandMark.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHints(txtLandHint);
                return false;
            }
        });
        edtEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHints(txtEmailHint);
                return false;
            }
        });
        edtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHints(txtPasswordHint);
                return false;
            }
        });

        edtConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showHints(txtConfirmPasswordHint);
                return false;
            }
        });



        if(DeviceUtil.isTablet(this)){
            edtFirst.setHintTextColor(getResources().getColor(R.color.gray));
            edtLast.setHintTextColor(getResources().getColor(R.color.gray));
            edtPhone.setHintTextColor(getResources().getColor(R.color.gray));

            edtCity.setHintTextColor(getResources().getColor(R.color.gray));
            edtState.setHintTextColor(getResources().getColor(R.color.gray));
            edtPinCode.setHintTextColor(getResources().getColor(R.color.gray));
            edtLandMark.setHintTextColor(getResources().getColor(R.color.gray));

            edtEmail.setHintTextColor(getResources().getColor(R.color.gray));
            edtPassword.setHintTextColor(getResources().getColor(R.color.gray));
            edtConfirmPassword.setHintTextColor(getResources().getColor(R.color.gray));
            edtAnswer.setHintTextColor(getResources().getColor(R.color.gray));
            edtAddress1.setHintTextColor(getResources().getColor(R.color.gray));
            edtAddress2.setHintTextColor(getResources().getColor(R.color.gray));

            if(android.os.Build.VERSION.SDK_INT >= 21){
                edtLast.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtFirst.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtPhone.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtCity.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtState.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtPinCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtLandMark.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtEmail.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtPassword.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtConfirmPassword.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtAnswer.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtAddress1.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));
                edtAddress2.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe, getTheme()));

            } else {
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
            }




            findViewById(R.id.line_last).setVisibility(View.VISIBLE);
            findViewById(R.id.line_phone).setVisibility(View.VISIBLE);

            findViewById(R.id.line_city).setVisibility(View.VISIBLE);
            findViewById(R.id.line_state).setVisibility(View.VISIBLE);
            findViewById(R.id.line_pincode).setVisibility(View.VISIBLE);
            findViewById(R.id.line_landmark).setVisibility(View.VISIBLE);

            findViewById(R.id.line_email).setVisibility(View.VISIBLE);
            findViewById(R.id.line_password).setVisibility(View.VISIBLE);
            findViewById(R.id.line_confirmpassword).setVisibility(View.VISIBLE);
            findViewById(R.id.line_answer).setVisibility(View.VISIBLE);
            findViewById(R.id.line_address).setVisibility(View.VISIBLE);
            findViewById(R.id.line_address2).setVisibility(View.VISIBLE);
        }


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
                    txtPhonePrefix.setVisibility(View.GONE);
                }else{
                    txtPhonePrefix.setVisibility(View.VISIBLE);
                }
            }
        });


    }


    private void initView(){

        rdIndividual.setOnClickListener(this);
        rdBusiness.setOnClickListener(this);
        btnBusiness.setOnClickListener(this);
        btnIndividual.setOnClickListener(this);
        txtChkTerm.setOnClickListener(this);
        txtChkPrivacy.setOnClickListener(this);

        initAreaLists();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, R.layout.custom_auto, areaLists);
        //edtAddress2.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        //edtAddress2.setThreshold(1);//will start working from first character

        edtCity= (AutoCompleteTextView)findViewById(R.id.edt_city);
        edtState = (EditText)findViewById(R.id.edt_state);

        edtPinCode  = (AutoCompleteTextView)findViewById(R.id.edt_pincode);
        edtLandMark = (EditText)findViewById(R.id.edt_landmark);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>
                (this, R.layout.custom_auto, cityLists);
        //edtCity.setAdapter(cityAdapter);
        //edtCity.setThreshold(1);//will start working from first character

        ArrayAdapter<String> pincodeAdapter = new ArrayAdapter<String>
                (this, R.layout.custom_auto, pincodeLists);

        //edtPinCode.setAdapter(pincodeAdapter);
        //edtPinCode.setThreshold(1);//will start working from first character

        txtEmail = (TextView)findViewById(R.id.txt_email);
        edtEmail= (EditText)findViewById(R.id.edt_email);
        edtPassword= (EditText)findViewById(R.id.edt_password);
        edtConfirmPassword = (EditText)findViewById(R.id.edt_confirmpassword);
        edtSecurity = (EditText)findViewById(R.id.edt_security);
        edtAnswer = (EditText)findViewById(R.id.edt_answer);

        if(DeviceUtil.isTablet(this)){
            edtFirst.setHintTextColor(getResources().getColor(R.color.gray));
            edtLast.setHintTextColor(getResources().getColor(R.color.gray));
            edtPhone.setHintTextColor(getResources().getColor(R.color.gray));

            edtCity.setHintTextColor(getResources().getColor(R.color.gray));
            edtState.setHintTextColor(getResources().getColor(R.color.gray));
            edtPinCode.setHintTextColor(getResources().getColor(R.color.gray));
            edtLandMark.setHintTextColor(getResources().getColor(R.color.gray));

            edtEmail.setHintTextColor(getResources().getColor(R.color.gray));
            edtPassword.setHintTextColor(getResources().getColor(R.color.gray));
            edtConfirmPassword.setHintTextColor(getResources().getColor(R.color.gray));
            edtAnswer.setHintTextColor(getResources().getColor(R.color.gray));
            edtAddress1.setHintTextColor(getResources().getColor(R.color.gray));
            edtAddress2.setHintTextColor(getResources().getColor(R.color.gray));


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


            findViewById(R.id.line_last).setVisibility(View.VISIBLE);
            findViewById(R.id.line_phone).setVisibility(View.VISIBLE);

            findViewById(R.id.line_city).setVisibility(View.VISIBLE);
            findViewById(R.id.line_state).setVisibility(View.VISIBLE);
            findViewById(R.id.line_pincode).setVisibility(View.VISIBLE);
            findViewById(R.id.line_landmark).setVisibility(View.VISIBLE);

            findViewById(R.id.line_email).setVisibility(View.VISIBLE);
            findViewById(R.id.line_password).setVisibility(View.VISIBLE);
            findViewById(R.id.line_confirmpassword).setVisibility(View.VISIBLE);
            findViewById(R.id.line_answer).setVisibility(View.VISIBLE);
            findViewById(R.id.line_address).setVisibility(View.VISIBLE);
            findViewById(R.id.line_address2).setVisibility(View.VISIBLE);
        }


        chkPreferred = (CheckBox)findViewById(R.id.chk_address);
        chkTerm = (CheckBox)findViewById(R.id.chk_term);
        chkPrivacy = (CheckBox)findViewById(R.id.chk_privacy);

        btnCreate = (Button)findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(this);
        txtTerm = (TextView)findViewById(R.id.txt_term);
        txtTerm.setOnClickListener(this);

        TextView privacy = (TextView)findViewById(R.id.txt_privacy);
        privacy.setOnClickListener(this);


        spSecurity = (NiceSpinner)findViewById(R.id.sp_security);
        spSecurity.setPadding(20, 10,10,10);
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

        linTerm = (LinearLayout)findViewById(R.id.lin_term);
        linPrivacy = (LinearLayout)findViewById(R.id.lin_privacy);

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
                if(checkInput()){
                    linCreate.setVisibility(View.VISIBLE);
                }
                edtFirst.requestFocus();
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
                if(checkInput()){
                    linCreate.setVisibility(View.VISIBLE);
                }
                edtLast.requestFocus();
            }
        });

        ccp = "+91";
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                ccp = "+" +  selectedCountry.getPhoneCode();
            }
        });


        edtAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!edtAnswer.getText().toString().isEmpty()){
                    if(checkInput()){
                        linCreate.setVisibility(View.VISIBLE);
                    }
                    edtAnswer.requestFocus();
                }
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
        mPhone = ccp + edtPhone.getText().toString();
        mAddress1 = edtAddress1.getText().toString();
        mArea = edtAddress2.getText().toString();
        mCity = edtCity.getText().toString();
        mState = edtState.getText().toString();
        mPinCode  = edtPinCode.getText().toString();
        mLandMark = edtLandMark.getText().toString();

        mEmail= edtEmail.getText().toString();
        mPassword = edtPassword.getText().toString();
        mConfirmPassword = edtConfirmPassword.getText().toString();
        mAnswer= edtAnswer.getText().toString();


        if(mFirst.isEmpty()){

            edtFirst.requestFocus();
            return false;
        }else{

        }

        if(mLast.isEmpty()){
            findViewById(R.id.line_last).setBackgroundColor(getResources().getColor(R.color.red));
            edtLast.requestFocus();
            return false;
        }else{
            findViewById(R.id.line_last).setBackgroundColor(getResources().getColor(R.color.hint_color));
        }

        if(mAddress1.isEmpty()){
            findViewById(R.id.line_address).setBackgroundColor(getResources().getColor(R.color.red));
            edtAddress1.requestFocus();
            return false;
        }else{
            findViewById(R.id.line_address).setBackgroundColor(getResources().getColor(R.color.hint_color));
        }
        if(mLandMark.isEmpty()){
            findViewById(R.id.line_landmark).setBackgroundColor(getResources().getColor(R.color.red));
            edtLandMark.requestFocus();
            return false;
        }else{
            findViewById(R.id.line_landmark).setBackgroundColor(getResources().getColor(R.color.hint_color));
        }

        if(mArea.isEmpty()){
            findViewById(R.id.line_address2).setBackgroundColor(getResources().getColor(R.color.red));
            edtAddress2.requestFocus();
            return false;
        }else{
            findViewById(R.id.line_address2).setBackgroundColor(getResources().getColor(R.color.hint_color));
        }


        if(mCity.isEmpty()){
            findViewById(R.id.line_city).setBackgroundColor(getResources().getColor(R.color.red));
            edtCity.requestFocus();
            return false;
        }else{
            findViewById(R.id.line_city).setBackgroundColor(getResources().getColor(R.color.hint_color));
        }
        if(mState.isEmpty()){
            findViewById(R.id.line_state).setBackgroundColor(getResources().getColor(R.color.red));
            edtState.requestFocus();
            return false;
        }else{
            findViewById(R.id.line_state).setBackgroundColor(getResources().getColor(R.color.hint_color));
        }
        if(mPinCode.isEmpty()){
            findViewById(R.id.line_pincode).setBackgroundColor(getResources().getColor(R.color.red));
            edtPinCode.requestFocus();
            return false;
        }else{
            findViewById(R.id.line_pincode).setBackgroundColor(getResources().getColor(R.color.hint_color));
        }

        if(mEmail.isEmpty()){
            findViewById(R.id.line_email).setBackgroundColor(getResources().getColor(R.color.red));
            edtEmail.requestFocus();
            return false;
        }else{
            findViewById(R.id.line_email).setBackgroundColor(getResources().getColor(R.color.hint_color));
        }

        if(mPassword.isEmpty()){
            findViewById(R.id.line_password).setBackgroundColor(getResources().getColor(R.color.red));
            edtPassword.requestFocus();
            return false;
        }else{
            findViewById(R.id.line_password).setBackgroundColor(getResources().getColor(R.color.hint_color));
        }
        if(mConfirmPassword.isEmpty()){
            findViewById(R.id.line_confirmpassword).setBackgroundColor(getResources().getColor(R.color.red));
            edtConfirmPassword.requestFocus();
            return false;
        }else{
            findViewById(R.id.line_confirmpassword).setBackgroundColor(getResources().getColor(R.color.hint_color));
        }

        if(mAnswer.isEmpty()){
            findViewById(R.id.line_answer).setBackgroundColor(getResources().getColor(R.color.red));
            edtAnswer.requestFocus();
            return false;
        }else{
            findViewById(R.id.line_answer).setBackgroundColor(getResources().getColor(R.color.hint_color));
        }

        if(mFirst.isEmpty() || mLandMark.isEmpty() || mPhone.isEmpty() ||
                mAddress1.isEmpty() || mCity.isEmpty() || mState.isEmpty() ||
                mPinCode.isEmpty() || mLandMark.isEmpty() || mEmail.isEmpty() || mPassword.isEmpty() ||
                mConfirmPassword.isEmpty() || mSecurity.isEmpty() || mAnswer.isEmpty() || mArea.isEmpty()){
            return false;
        }
        return true;
    }



    public void registerNow(String code){

        RequestParams params = new RequestParams();
        params.put("device_token", PreferenceUtils.getDeviceToken(this));
        params.put("firstName", mFirst);
        params.put("lastName", mLast);
        params.put("email", mEmail);
        params.put("business_type", mBusinessType);

        try {
            params.put("password", DeviceUtil.encrypt(mPassword));
        } catch (Exception e) {
            e.printStackTrace();
        }

        params.put("phone", mPhone);
        params.put("question",mSecurity);
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
            jsonMap.put("address1", mAddress1);
            jsonMap.put("address2", mArea);
            jsonMap.put("city", mCity);
            jsonMap.put("state", mState);
            jsonMap.put("pincode", mPinCode);
            jsonMap.put("landmark", mLandMark);
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
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                       // Log.d("Error String", responseString);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        // Log.d("Error", errorResponse.toString());
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }
                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {
                        try {
                            if (response != null) {
                                //{"id":33,"first_name":"a","last_name":"a","email":"a","phone":"a","question":"What is your favorite Sports team?","answer":"a","term":"0","policy":"0"}
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(SignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                }else if(response.getString("result").equals("600")){
                                    Toast.makeText(SignUpActivity.this, "Invalid otp code", Toast.LENGTH_SHORT).show();
                                }else{
                                    JsonHelper.parseSinUpProcess(SignUpActivity.this, response);
                                    if(Constants.MODE == Constants.PERSONAL){
                                        PreferenceUtils.setPassword(SignUpActivity.this , mPassword);
                                    }else if(Constants.MODE == Constants.CORPERATION){
                                        PreferenceUtils.setCorPassword(SignUpActivity.this , mPassword);
                                    }

                                    PreferenceUtils.setLogIn(SignUpActivity.this);
                                    PreferenceUtils.setNickname(SignUpActivity.this,mFirst);

                                    Toast.makeText(SignUpActivity.this, "Your Profile is created successfully",Toast.LENGTH_SHORT).show();
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("result","res");
                                    setResult(Activity.RESULT_OK,returnIntent);
                                    finish();

                                    Intent intent = new Intent(SignUpActivity.this, LocationActivity.class);
                                    startActivity(intent);
//                                    Intent main = new Intent(SignUpActivity.this, HomeActivity.class);
//                                    startActivity(main);
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


    public void sendPhone(String phone, String message, boolean isShowingDialog) {

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
                        Toast.makeText(SignUpActivity.this, "Check Network Connection", Toast.LENGTH_SHORT).show();
                        //((MainActivity)mContext).finish();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        // Log.d("Error", errorResponse.toString());
                        hideProgressDialog();
                        Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        //((MainActivity)mContext).finish();
                    }
                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {
                        if (response != null) {
                            try{
                                if(response.getString("result").equals("200")){
                                    if(isShowingDialog){

                                    }else{
                                        OTPDialog otpDialog = new OTPDialog();
                                        otpDialog.show(SignUpActivity.this, ccp +  edtPhone.getText().toString());
                                        otpDialog.setCancelable(false);
                                        otpDialog.getDialog().setCanceledOnTouchOutside(false);
                                    }
                                    if(message.isEmpty()){
                                        Toast.makeText(SignUpActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }

                                }else if(response.getString("result").equals("400")){
                                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.already_register), Toast.LENGTH_SHORT).show();
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
}
