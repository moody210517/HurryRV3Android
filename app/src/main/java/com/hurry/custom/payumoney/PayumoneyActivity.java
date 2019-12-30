package com.hurry.custom.payumoney;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.hurry.custom.MyApplication;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.controller.WebClient;

import com.hurry.custom.view.BaseBackActivity;
import com.hurry.custom.view.activity.ReviewActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.angmarch.views.NiceSpinner;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PayumoneyActivity extends BaseBackActivity implements View.OnClickListener {

    public static final String TAG = "MainActivity : ";

    private String userMobile, userEmail;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private SharedPreferences userDetailsPreference;
    private EditText email_et, mobile_et, amount_et;
    private TextInputLayout email_til, mobile_til;
    private RadioGroup radioGroup_color_theme, radioGroup_select_env;
    private SwitchCompat switch_disable_wallet, switch_disable_netBanks, switch_disable_cards;
    private TextView logoutBtn;
    private AppCompatRadioButton radio_btn_default;
    private AppPreference mAppPreference;
    private AppCompatRadioButton radio_btn_theme_purple, radio_btn_theme_pink, radio_btn_theme_green, radio_btn_theme_grey;

    private Button payNowButton;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;

    String mPayment = "Cash On Pick up";
    int paymentType = 0;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payu);
        mAppPreference = new AppPreference();
        ButterKnife.bind(this);
        initBackButton(toolbar, getString(R.string.payment));

        settings = getSharedPreferences("settings", MODE_PRIVATE);
        logoutBtn = (TextView) findViewById(R.id.logout_button);
        email_et = (EditText) findViewById(R.id.email_et);
        mobile_et = (EditText) findViewById(R.id.mobile_et);
        amount_et = (EditText) findViewById(R.id.amount_et);
        amount_et.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(7, 2)});

        email_til = (TextInputLayout) findViewById(R.id.email_til);
        mobile_til = (TextInputLayout) findViewById(R.id.mobile_til);

        radioGroup_color_theme = (RadioGroup) findViewById(R.id.radio_grp_color_theme);
        radio_btn_default = (AppCompatRadioButton) findViewById(R.id.radio_btn_theme_default);
        radio_btn_theme_pink = (AppCompatRadioButton) findViewById(R.id.radio_btn_theme_pink);
        radio_btn_theme_purple = (AppCompatRadioButton) findViewById(R.id.radio_btn_theme_purple);
        radio_btn_theme_green = (AppCompatRadioButton) findViewById(R.id.radio_btn_theme_green);
        radio_btn_theme_grey = (AppCompatRadioButton) findViewById(R.id.radio_btn_theme_grey);

        if (PayUmoneyFlowManager.isUserLoggedIn(getApplicationContext())) {
            logoutBtn.setVisibility(View.VISIBLE);
        } else {
            logoutBtn.setVisibility(View.GONE);
        }

        logoutBtn.setOnClickListener(this);
        switch_disable_wallet = (SwitchCompat) findViewById(R.id.switch_disable_wallet);
        switch_disable_netBanks = (SwitchCompat) findViewById(R.id.switch_disable_netbanks);
        switch_disable_cards = (SwitchCompat) findViewById(R.id.switch_disable_cards);
        AppCompatRadioButton radio_btn_sandbox = (AppCompatRadioButton) findViewById(R.id.radio_btn_sandbox);
        AppCompatRadioButton radio_btn_production = (AppCompatRadioButton) findViewById(R.id.radio_btn_production);
        radioGroup_select_env = (RadioGroup) findViewById(R.id.radio_grp_env);

        payNowButton = (Button) findViewById(R.id.pay_now_button);
        payNowButton.setOnClickListener(this);
        initListeners();
        //Set Up SharedPref
        setUpUserDetails();
        if (settings.getBoolean("is_prod_env", false)) {
            ((MyApplication) getApplication()).setAppEnvironment(AppEnvironment.PRODUCTION);
            radio_btn_production.setChecked(true);
        } else {
            ((MyApplication) getApplication()).setAppEnvironment(AppEnvironment.SANDBOX);
            radio_btn_sandbox.setChecked(true);
        }
        setupCitrusConfigs();
        initView();
        selectProdEnv();
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        return;
    }

    LinearLayout linPayment;
    NiceSpinner spType;
    public void initView(){

        ImageView imgBack = (ImageView)findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);

        linPayment = (LinearLayout)findViewById(R.id.lin_payment);
        spType = (NiceSpinner)findViewById(R.id.sp_type);

        if(PreferenceUtils.getQuote(this)){
            mPayment = "Pay using Card";
            spType.attachDataSource(Constants.quotePaymentWay);
        }else{
            spType.attachDataSource(Constants.paymentWay);
        }

        spType.setSelectedIndex(0);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(PreferenceUtils.getQuote(PayumoneyActivity.this)){

                    if(position == 0){
                        //linPayment.setVisibility(View.VISIBLE);
                        payNowButton.setText("Pay now");
                        Constants.paymentType = "Pay using Card";
                        mPayment = "Pay using Card";
                    }else if(position == 1 ){
                        //linPayment.setVisibility(View.GONE);
                        payNowButton.setText("Pay now");
                        Constants.paymentType = "Pay using Bank";
                        mPayment ="Pay using Bank";
                    }

                }else{
                    if(position == 0){
                        //linPayment.setVisibility(View.VISIBLE);
                        payNowButton.setText("Pickup my order");
                        Constants.paymentType = "Cash On Pick up";
                        mPayment = "Cash On Pick up";
                    }else if(position == 1 ){
                        //linPayment.setVisibility(View.GONE);
                        payNowButton.setText("Pay now");
                        Constants.paymentType = "Pay using Card";
                        mPayment = "Pay using Card";
                    }else if(position == 2){
                        payNowButton.setText("Pay now");
                        Constants.paymentType = "Pay using Bank";
                        mPayment ="Pay using Bank";
                    }else if(position == 3){
                        payNowButton.setText("Pay now");
                        Constants.paymentType = "COD";
                        mPayment ="COD";
                    }
                }
                paymentType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }


    public static void setErrorInputLayout(EditText editText, String msg, TextInputLayout textInputLayout) {
        textInputLayout.setError(msg);
        editText.requestFocus();
    }

    public static boolean isValidEmail(String strEmail) {
        return strEmail != null && android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches();
    }

    public static boolean isValidPhone(String phone) {
        Pattern pattern = Pattern.compile(AppPreference.PHONE_PATTERN);

        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private void setUpUserDetails() {
        userDetailsPreference = getSharedPreferences(AppPreference.USER_DETAILS, MODE_PRIVATE);
        userEmail = userDetailsPreference.getString(AppPreference.USER_EMAIL, mAppPreference.getDummyEmail());
        userMobile = userDetailsPreference.getString(AppPreference.USER_MOBILE, mAppPreference.getDummyMobile());
        email_et.setText(userEmail);
        mobile_et.setText(userMobile);
        amount_et.setText(mAppPreference.getDummyAmount());
        restoreAppPref();
    }

    private void restoreAppPref() {
        //Set Up Disable Options
        switch_disable_wallet.setChecked(mAppPreference.isDisableWallet());
        switch_disable_cards.setChecked(mAppPreference.isDisableSavedCards());
        switch_disable_netBanks.setChecked(mAppPreference.isDisableNetBanking());

        //Set Up saved theme pref
        switch (AppPreference.selectedTheme) {
            case -1:
                radio_btn_default.setChecked(true);
                break;
            case R.style.AppTheme_pink:
                radio_btn_theme_pink.setChecked(true);
                break;
            case R.style.AppTheme_Grey:
                radio_btn_theme_grey.setChecked(true);
                break;
            case R.style.AppTheme_purple:
                radio_btn_theme_purple.setChecked(true);
                break;
            case R.style.AppTheme_Green:
                radio_btn_theme_green.setChecked(true);
                break;
            default:
                radio_btn_default.setChecked(true);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        payNowButton.setEnabled(true);

        if (PayUmoneyFlowManager.isUserLoggedIn(getApplicationContext())) {
            logoutBtn.setVisibility(View.VISIBLE);
        } else {
            logoutBtn.setVisibility(View.GONE);
        }
    }

    /**
     * This function sets the mode to PRODUCTION in Shared Preference
     */
    private void selectProdEnv() {

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((MyApplication) getApplication()).setAppEnvironment(AppEnvironment.PRODUCTION);
                editor = settings.edit();
                editor.putBoolean("is_prod_env", true);
                editor.apply();

                if (PayUmoneyFlowManager.isUserLoggedIn(getApplicationContext())) {
                    logoutBtn.setVisibility(View.VISIBLE);
                } else {
                    logoutBtn.setVisibility(View.GONE);
                }

                setupCitrusConfigs();
            }
        }, AppPreference.MENU_DELAY);
    }

    /**
     * This function sets the mode to SANDBOX in Shared Preference
     */
    private void selectSandBoxEnv() {
        ((MyApplication) getApplication()).setAppEnvironment(AppEnvironment.SANDBOX);
        editor = settings.edit();
        editor.putBoolean("is_prod_env", false);
        editor.apply();

        if (PayUmoneyFlowManager.isUserLoggedIn(getApplicationContext())) {
            logoutBtn.setVisibility(View.VISIBLE);
        } else {
            logoutBtn.setVisibility(View.GONE);

        }
        setupCitrusConfigs();
    }

    private void setupCitrusConfigs() {
        AppEnvironment appEnvironment = ((MyApplication) getApplication()).getAppEnvironment();
        if (appEnvironment == AppEnvironment.PRODUCTION) {
         //   Toast.makeText(PayumoneyActivity.this, "Environment Set to Production", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(PayumoneyActivity.this, "Environment Set to SandBox", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //name_on_card
        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);
            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    //Toast.makeText(this, "Success",Toast.LENGTH_SHORT).show();
                    //showOrdreConfirm(false);

                    // Response from Payumoney
                    String payuResponse = transactionResponse.getPayuResponse();
                    try {
                        JSONObject jsonObject = new JSONObject(payuResponse);
                        JSONObject  jsonRes = jsonObject.getJSONObject("result");
                        String name_on_car = jsonRes.getString("name_on_card");
                        String paymentId = jsonRes.getString("paymentId");
                        String merchantResponse = transactionResponse.getTransactionDetails();

                        // call order confirm
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Response from SURl and FURL


                } else {
                    //Failure Transaction
                    Toast.makeText(this, "failed",Toast.LENGTH_SHORT).show();
                }


            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
                Toast.makeText(this, "Error response : " + resultModel.getError().getTransactionResponse(),Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Both objects are null!");
               // Toast.makeText(this, "Both objects are null!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        userEmail = email_et.getText().toString().trim();
        userMobile = mobile_et.getText().toString().trim();
        if (v.getId() == R.id.logout_button || validateDetails(userEmail, userMobile)) {
            switch (v.getId()) {
                case R.id.pay_now_button:
                    payNowButton.setEnabled(false);
                    Constants.paymentType = mPayment;

                    if(PreferenceUtils.getQuote(PayumoneyActivity.this)){
                        payNowButton.setEnabled(false);
                        launchPayUMoneyFlow();
                    }else{
                        if(paymentType == 0 || paymentType == 3){
                            showOrdreConfirm(true);
                        }else{
                            payNowButton.setEnabled(false);
                            launchPayUMoneyFlow();
                        }
                    }
                    break;
                case R.id.logout_button:
                    PayUmoneyFlowManager.logoutUser(getApplicationContext());
                    logoutBtn.setVisibility(View.GONE);
                    break;
                case R.id.img_back:
                    Intent intent  = new Intent(this, ReviewActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    }

    private void initListeners() {
        email_et.addTextChangedListener(new EditTextInputWatcher(email_til));
        mobile_et.addTextChangedListener(new EditTextInputWatcher(mobile_til));


        radioGroup_color_theme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                mAppPreference.setOverrideResultScreen(true);

                switch (i) {
                    case R.id.radio_btn_theme_default:
                        AppPreference.selectedTheme = -1;
                        break;
                    case R.id.radio_btn_theme_pink:
                        AppPreference.selectedTheme = R.style.AppTheme_pink;
                        break;
                    case R.id.radio_btn_theme_grey:
                        AppPreference.selectedTheme = R.style.AppTheme_Grey;
                        break;
                    case R.id.radio_btn_theme_purple:
                        AppPreference.selectedTheme = R.style.AppTheme_purple;
                        break;
                    case R.id.radio_btn_theme_green:
                        AppPreference.selectedTheme = R.style.AppTheme_Green;
                        break;
                    default:
                        AppPreference.selectedTheme = -1;
                        break;
                }
            }
        });

        radioGroup_select_env.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.radio_btn_sandbox:
                        selectSandBoxEnv();
                        break;
                    case R.id.radio_btn_production:
                        selectProdEnv();
                        break;
                    default:
                        selectSandBoxEnv();
                        break;
                }
            }
        });

        switch_disable_cards.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //PPConfig.getInstance().disableSavedCards(b);
            }
        });

        switch_disable_netBanks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               // PPConfig.getInstance().disableNetBanking(b);
            }
        });

        switch_disable_wallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               // PPConfig.getInstance().disableWallet(b);
            }
        });

    }

    /**
     * This fucntion checks if email and mobile number are valid or not.
     *
     * @param email  email id entered in edit text
     * @param mobile mobile number entered in edit text
     * @return boolean value
     */
    public boolean validateDetails(String email, String mobile) {
        email = email.trim();
        mobile = mobile.trim();

        if (TextUtils.isEmpty(mobile)) {
            setErrorInputLayout(mobile_et, getString(R.string.err_phone_empty), mobile_til);
            return false;
        } else if (!isValidPhone(mobile)) {
            setErrorInputLayout(mobile_et, getString(R.string.err_phone_not_valid), mobile_til);
            return false;
        } else if (TextUtils.isEmpty(email)) {
            setErrorInputLayout(email_et, getString(R.string.err_email_empty), email_til);
            return false;
        } else if (!isValidEmail(email)) {
            setErrorInputLayout(email_et, getString(R.string.email_not_valid), email_til);
            return false;
        } else {
            return true;
        }
    }

    /**
     * This function prepares the data for payment and launches payumoney plug n play sdk
     */
    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();
        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText(((EditText) findViewById(R.id.status_page_et)).getText().toString());
        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle(((EditText) findViewById(R.id.activity_title_et)).getText().toString());
        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        double amount = 0;
        try {
            String amountt = String.format("%.2f", Float.valueOf(Constants.serviceModel.price));
            amount = Double.valueOf(amountt); // Double.valueOf(Constants.serviceModel.price); // Double.parseDouble(amount_et.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String txnId = System.currentTimeMillis() + "";
        String phone = Constants.addressModel.sourcePhonoe;
        if(Constants.MODE != Constants.GUEST){
            phone = PreferenceUtils.getPhone(this);
        }
        String email = Constants.guestEmail; // mobile_til.getEditText().getText().toString().trim();
        if(Constants.MODE == Constants.PERSONAL){
            email = PreferenceUtils.getEmail(this);
        }else if(Constants.MODE == Constants.CORPERATION){
            email = PreferenceUtils.getCorEmail(this);
        }else {
            email =  Constants.guestEmail;
        }

        String productName = mAppPreference.getProductInfo();
        String firstName = PreferenceUtils.getNickname(this);// mAppPreference.getFirstName();
        if(Constants.MODE == Constants.GUEST){
            firstName = "Guest User";
        }

        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        AppEnvironment appEnvironment = ((MyApplication) getApplication()).getAppEnvironment();
        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(firstName)
                .setUdf2(firstName)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(appEnvironment.merchant_Key())
                .setMerchantId(appEnvironment.merchant_ID());

        try {
            mPaymentParams = builder.build();
            /*
            * Hash should always be generated from your server side.
            * */
            //generateHashFromServer(mPaymentParams);

/*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */
           mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);
           if (AppPreference.selectedTheme != -1) {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,this, AppPreference.selectedTheme,mAppPreference.isOverrideResultScreen());
            } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,this, R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
            }


        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getMessage() + " : " + phone, Toast.LENGTH_LONG).show();
            payNowButton.setEnabled(true);
        }
    }

    /**
     * Thus function calculates the hash for transaction
     *
     * @param paymentParam payment params of transaction
     * @return payment params along with calculated merchant hash
     */
    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        AppEnvironment appEnvironment = ((MyApplication) getApplication()).getAppEnvironment();
        stringBuilder.append(appEnvironment.salt());

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    /**
     * This method generates hash from server.
     *
     * @param paymentParam payments params used for hash generation
     */
    public void generateHashFromServer(PayUmoneySdkInitializer.PaymentParam paymentParam) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        HashMap<String, String> params = paymentParam.getParams();

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params.get(PayUmoneyConstants.KEY)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params.get(PayUmoneyConstants.AMOUNT)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params.get(PayUmoneyConstants.TXNID)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params.get(PayUmoneyConstants.EMAIL)));
        postParamsBuffer.append(concatParams("productinfo", params.get(PayUmoneyConstants.PRODUCT_INFO)));
        postParamsBuffer.append(concatParams("firstname", params.get(PayUmoneyConstants.FIRSTNAME)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params.get(PayUmoneyConstants.UDF1)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params.get(PayUmoneyConstants.UDF2)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params.get(PayUmoneyConstants.UDF3)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params.get(PayUmoneyConstants.UDF4)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params.get(PayUmoneyConstants.UDF5)));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PayumoneyActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        /**
                         * This hash is mandatory and needs to be generated from merchant's server side
                         *
                         */
                        case "payment_hash":
                            merchantHash = response.getString(key);
                            break;
                        default:
                            break;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);

            progressDialog.dismiss();
            payNowButton.setEnabled(true);

            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(PayumoneyActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mPaymentParams.setMerchantHash(merchantHash);

                if (AppPreference.selectedTheme != -1) {
                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, PayumoneyActivity.this, AppPreference.selectedTheme, mAppPreference.isOverrideResultScreen());
                } else {
                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, PayumoneyActivity.this, R.style.AppTheme_default, mAppPreference.isOverrideResultScreen());
                }
            }
        }
    }

    public static class EditTextInputWatcher implements TextWatcher {

        private TextInputLayout textInputLayout;

        EditTextInputWatcher(TextInputLayout textInputLayout) {
            this.textInputLayout = textInputLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
            }
        }
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }


    private  void showOrdreConfirm(boolean flag){
        if(PreferenceUtils.getQuote(this)){
            if(Constants.MODE == Constants.PERSONAL){
                PreferenceUtils.setOrderId(this, Constants.quote_order_id);
                update_order(Constants.quote_order_id, flag);
            }else{
                PreferenceUtils.setOrderId(this, Constants.quote_order_id);
                update_corporate_order(Constants.quote_order_id, flag);
            }
        }else{
            order(flag);
        }
    }

    private void update_order(final String order_id, boolean flag) {
        if(flag){
            showProgressDialog();
        }

        RequestParams params = new RequestParams();
        params.put("id", order_id);
        params.put("user_id", PreferenceUtils.getUserId(this));
        params.put("date", Constants.dateModel.date);
        params.put("time", Constants.dateModel.time);

        params.put("service_name", Constants.serviceModel.name);
        params.put("service_price", Constants.serviceModel.price);
        params.put("service_timein", Constants.serviceModel.time_in);

        params.put("weight", String.valueOf(Constants.getTotalWeight()));
        params.put("payment", mPayment);

        // order address
        JSONArray addressArray = new JSONArray();
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("s_address", Constants.addressModel.sourceAddress);
        jsonMap.put("s_area", Constants.addressModel.sourceArea);
        jsonMap.put("s_city", Constants.addressModel.sourceCity);
        jsonMap.put("s_state", Constants.addressModel.sourceState);
        jsonMap.put("s_pincode", Constants.addressModel.sourcePinCode);
        jsonMap.put("s_phone", Constants.addressModel.sourcePhonoe + ":" + Constants.addressModel.senderName);
        jsonMap.put("s_landmark", Constants.addressModel.sourceLandMark);
        jsonMap.put("s_instruction", Constants.addressModel.sourceInstruction);
        jsonMap.put("s_lat", String.valueOf(Constants.addressModel.sourceLat));
        jsonMap.put("s_lng", String.valueOf(Constants.addressModel.sourceLng));

        jsonMap.put("d_address", Constants.addressModel.desAddress);
        jsonMap.put("d_area", Constants.addressModel.desArea);
        jsonMap.put("d_city", Constants.addressModel.desCity);
        jsonMap.put("d_state", Constants.addressModel.desState);
        jsonMap.put("d_pincode", Constants.addressModel.desPinCode);
        jsonMap.put("d_landmark", Constants.addressModel.desLandMark);
        jsonMap.put("d_instruction", Constants.addressModel.desInstruction);
        jsonMap.put("d_lat", String.valueOf(Constants.addressModel.desLat));
        jsonMap.put("d_lng", String.valueOf(Constants.addressModel.desLng));
        jsonMap.put("d_phone", Constants.addressModel.desPhone);
        jsonMap.put("d_name", Constants.addressModel.desName);

        JSONObject addressJson = new JSONObject(jsonMap);
        addressArray.put(addressJson);
        params.put("orderaddress", addressArray.toString());

        JSONArray productsArrays = new JSONArray();
        if(Constants.ORDER_TYPE == Constants.CAMERA_OPTION){

            try{
                for(int k = 0 ; k < Constants.cameraOrderModel.itemModels.size(); k++){
                    File file =  new File(Constants.cameraOrderModel.itemModels.get(k).image);
                    params.put("file" + String.valueOf(k), file);
                }
            }catch (Exception e){

            }
            for(int i = 0; i < Constants.cameraOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("image", Constants.cameraOrderModel.itemModels.get(i).image);
                cameraMap.put("quantity", Constants.cameraOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.cameraOrderModel.itemModels.get(i).weight);
                cameraMap.put("weight_value", String.valueOf(Constants.cameraOrderModel.itemModels.get(i).weight_value));
                cameraMap.put("product_type", String.valueOf(Constants.CAMERA_OPTION));
                cameraMap.put("package", Constants.cameraOrderModel.itemModels.get(i).mPackage);
                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);
            }

        }else if(Constants.ORDER_TYPE == Constants.ITEM_OPTION){
            for(int i = 0; i < Constants.itemOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("title", Constants.itemOrderModel.itemModels.get(i).title);
                cameraMap.put("dimension", Constants.getDimetion(i));
                cameraMap.put("quantity", Constants.itemOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.itemOrderModel.itemModels.get(i).weight);
                cameraMap.put("product_type", String.valueOf(Constants.ITEM_OPTION));
                cameraMap.put("package", Constants.itemOrderModel.itemModels.get(i).mPackage);
                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);
            }
        }else if(Constants.ORDER_TYPE == Constants.PACKAGE_OPTION){
            for(int i = 0; i < Constants.packageOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("title", Constants.packageOrderModel.itemModels.get(i).title);
                cameraMap.put("quantity", Constants.packageOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.packageOrderModel.itemModels.get(i).weight);
                cameraMap.put("product_type", String.valueOf(Constants.PACKAGE_OPTION));
                cameraMap.put("package", Constants.packageOrderModel.itemModels.get(i).mPackage);
                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);
            }
        }
        params.put("products", productsArrays.toString());

        WebClient.post(Constants.BASE_URL_ORDER + "update_order", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {

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
                                String results = response.getString("result");
                                if(results.equals("200")){
                                    PreferenceUtils.setOrderId(PayumoneyActivity.this, order_id);
                                    // call order confirm
                                    finish();
                                }
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(PayumoneyActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
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


    private void update_corporate_order(final String order_id, boolean flag) {
        if(flag){
            showProgressDialog();
        }

        RequestParams params = new RequestParams();
        params.put("id", order_id);
        params.put("user_id", PreferenceUtils.getUserId(this));
        params.put("date", Constants.dateModel.date);
        params.put("time", Constants.dateModel.time);

        params.put("service_name", Constants.serviceModel.name);
        params.put("service_price", Constants.serviceModel.price);
        params.put("service_timein", Constants.serviceModel.time_in);
        params.put("payment", mPayment);

        WebClient.post(Constants.BASE_URL_ORDER + "update_corporate_order", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        showProgressDialog();
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

                                String results = response.getString("result");
                                if(results.equals("200")){

                                    PreferenceUtils.setOrderId(PayumoneyActivity.this, order_id);
                                    // call order confirm
                                    finish();

                                }

                                if(response.getString("result").equals("400")){
                                    Toast.makeText(PayumoneyActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
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


    private void order(boolean flag ) {

        if(flag)
            showProgressDialog();
        RequestParams params = new RequestParams();
        params.put("transaction_id", "0");
        params.put("id", PreferenceUtils.getOrderId(this));

        if(Constants.MODE ==  Constants.PERSONAL){
            params.put("user_id", PreferenceUtils.getUserId(this));
        }else if(Constants.MODE == Constants.GUEST){
            params.put("user_id", "");
        }
        params.put("date", Constants.dateModel.date);
        params.put("time", Constants.dateModel.time);

        params.put("service_name", Constants.serviceModel.name);
        params.put("service_price", Constants.serviceModel.price);
        params.put("service_timein", Constants.serviceModel.time_in);
        params.put("distance", Constants.addressModel.distance);
        params.put("weight", String.valueOf(Constants.getTotalWeight()));
        params.put("quote_type", "0");
        params.put("payment", mPayment);

        params.put("device_id", DeviceUtil.getDeviceId(this));
        params.put("device_type", DeviceUtil.getDeviceName());
        // order address
        JSONArray addressArray = new JSONArray();
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("s_address", Constants.addressModel.sourceAddress);
        jsonMap.put("s_area", Constants.addressModel.sourceArea);
        jsonMap.put("s_city", Constants.addressModel.sourceCity);
        jsonMap.put("s_state", Constants.addressModel.sourceState);
        jsonMap.put("s_pincode", Constants.addressModel.sourcePinCode);
        jsonMap.put("s_phone", Constants.addressModel.sourcePhonoe + ":" + Constants.addressModel.senderName);
        jsonMap.put("s_landmark", Constants.addressModel.sourceLandMark);
        jsonMap.put("s_instruction", Constants.addressModel.sourceInstruction);
        jsonMap.put("s_lat", String.valueOf(Constants.addressModel.sourceLat));
        jsonMap.put("s_lng", String.valueOf(Constants.addressModel.sourceLng));

        jsonMap.put("d_address", Constants.addressModel.desAddress);
        jsonMap.put("d_area", Constants.addressModel.desArea);
        jsonMap.put("d_city", Constants.addressModel.desCity);
        jsonMap.put("d_state", Constants.addressModel.desState);
        jsonMap.put("d_pincode", Constants.addressModel.desPinCode);
        jsonMap.put("d_landmark", Constants.addressModel.desLandMark);
        jsonMap.put("d_instruction", Constants.addressModel.desInstruction);
        jsonMap.put("d_lat", String.valueOf(Constants.addressModel.desLat));
        jsonMap.put("d_lng", String.valueOf(Constants.addressModel.desLng));
        jsonMap.put("d_phone", String.valueOf(Constants.addressModel.desPhone));
        jsonMap.put("d_name", String.valueOf(Constants.addressModel.desName));
        if(Constants.MODE == Constants.GUEST){
            jsonMap.put("duration", Constants.guestEmail);
        }else{
            jsonMap.put("duration", Constants.addressModel.duration);
        }
        jsonMap.put("distance", Constants.addressModel.distance);
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        jsonMap.put("device_id", android_id);
        JSONObject addressJson = new JSONObject(jsonMap);
        addressArray.put(addressJson);
        params.put("orderaddress", addressArray.toString());
        JSONArray productsArrays = new JSONArray();
        if(Constants.ORDER_TYPE == Constants.CAMERA_OPTION){

            for(int k = 0 ; k < Constants.cameraOrderModel.itemModels.size(); k++){
                File file =  new File(Constants.cameraOrderModel.itemModels.get(k).image);
                try {
                    params.put("file" + String.valueOf(k), file);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }

            for(int i = 0; i < Constants.cameraOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("image", Constants.cameraOrderModel.itemModels.get(i).image);
                cameraMap.put("quantity", Constants.cameraOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.cameraOrderModel.itemModels.get(i).weight);
                cameraMap.put("weight_value", String.valueOf(Constants.cameraOrderModel.itemModels.get(i).weight_value));
                cameraMap.put("product_type", String.valueOf(Constants.CAMERA_OPTION));
                cameraMap.put("package", Constants.cameraOrderModel.itemModels.get(i).mPackage);
                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);

            }
        }else if(Constants.ORDER_TYPE == Constants.ITEM_OPTION){
            for(int i = 0; i < Constants.itemOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("title", Constants.itemOrderModel.itemModels.get(i).title);
                cameraMap.put("dimension", Constants.getDimetion(i));
                cameraMap.put("quantity", Constants.itemOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.itemOrderModel.itemModels.get(i).weight);
                cameraMap.put("product_type", String.valueOf(Constants.ITEM_OPTION));
                cameraMap.put("package", Constants.itemOrderModel.itemModels.get(i).mPackage);

                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);
            }

        }else if(Constants.ORDER_TYPE == Constants.PACKAGE_OPTION){
            for(int i = 0; i < Constants.packageOrderModel.itemModels.size(); i++)
            {
                Map<String, String> cameraMap = new HashMap<String, String>();
                cameraMap.put("title", Constants.packageOrderModel.itemModels.get(i).title);
                cameraMap.put("quantity", Constants.packageOrderModel.itemModels.get(i).quantity);
                cameraMap.put("weight", Constants.packageOrderModel.itemModels.get(i).weight);
                cameraMap.put("product_type", String.valueOf(Constants.PACKAGE_OPTION));
                cameraMap.put("package", Constants.packageOrderModel.itemModels.get(i).mPackage);
                JSONObject json = new JSONObject(cameraMap);
                productsArrays.put(json);

            }
        }
        params.put("products", productsArrays.toString());
        WebClient.post(Constants.BASE_URL + "order", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {

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
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(PayumoneyActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }else if(response.getString("result").equals("300")){
                                    Toast.makeText(PayumoneyActivity.this, "Already Exist", Toast.LENGTH_SHORT).show();
                                }else{
                                    String order_id = response.getString("order_id");
                                    String track_id = response.getString("track_id");

                                    PreferenceUtils.setOrderId(PayumoneyActivity.this, order_id);
                                    PreferenceUtils.setTrackId(PayumoneyActivity.this, track_id);

                                   // call order confirm
                                    finish();
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


    private final String key = AppEnvironment.PRODUCTION.merchant_Key();      //put your merchant key value
    private final String salt = AppEnvironment.PRODUCTION.salt();    //put your merchant salt value

    private final String PAYMENT_HASH = "payment_hash";
    private final String GET_MERCHANT_IBIBO_CODES_HASH = "get_merchant_ibibo_codes_hash";
    private final String VAS_FOR_MOBILE_SDK_HASH = "vas_for_mobile_sdk_hash";
    private final String PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK_HASH = "payment_related_details_for_mobile_sdk_hash";
    private final String DELETE_USER_CARD_HASH = "delete_user_card_hash";
    private final String GET_USER_CARDS_HASH = "get_user_cards_hash";
    private final String EDIT_USER_CARD_HASH = "edit_user_card_hash";
    private final String SAVE_USER_CARD_HASH = "save_user_card_hash";
    private final String CHECK_OFFER_STATUS_HASH = "check_offer_status_hash";
    private final String CHECK_ISDOMESTIC_HASH = "check_isDomestic_hash";
    private final String VERIFY_PAYMENT_HASH = "verify_payment_hash";


    public String getHashes(String txnid, String amount, String productInfo, String firstname, String email,
                            String user_credentials, String udf1, String udf2, String udf3, String udf4, String udf5
    ) {

        try{
            JSONObject response = new JSONObject();

            String ph = checkNull(key) + "|" + checkNull(txnid) + "|" + checkNull(amount) + "|" + checkNull(productInfo)
                    + "|" + checkNull(firstname) + "|" + checkNull(email) + "|" + checkNull(udf1) + "|" + checkNull(udf2)
                    + "|" + checkNull(udf3) + "|" + checkNull(udf4) + "|" + checkNull(udf5) + "||||||" + salt;
            String paymentHash = getSHA(ph);
            System.out.println("Payment Hash "+paymentHash);
            response.put(PAYMENT_HASH, paymentHash);
            response.put(VAS_FOR_MOBILE_SDK_HASH, generateHashString("vas_for_mobile_sdk", "default"));

            //Use var1 as user_credentials if user_credential is not empty
            if (!checkNull(user_credentials).isEmpty()) {

                response.put(PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK_HASH,
                        generateHashString("payment_related_details_for_mobile_sdk", user_credentials));

                response.put(DELETE_USER_CARD_HASH, generateHashString("delete_user_card", user_credentials));
                response.put(GET_USER_CARDS_HASH, generateHashString("get_user_cards", user_credentials));
                response.put(EDIT_USER_CARD_HASH, generateHashString("edit_user_card", user_credentials));
                response.put(SAVE_USER_CARD_HASH, generateHashString("save_user_card", user_credentials));
            }
            else{
                response.put(PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK_HASH,
                        generateHashString("payment_related_details_for_mobile_sdk","default"));

            }
            System.out.println("Vas_for _mobile_sdk  "+generateHashString("vas_for_mobile_sdk", "default"));
            System.out.println("payment_related_details_sdk_hash  "+ generateHashString("payment_related_details_for_mobile_sdk", user_credentials));

            System.out.println("delete_user_card Hash"+generateHashString("delete_user_card", user_credentials));


            return response.toString();

        }catch (Exception e){};

        return  null;

    }
    private String checkNull(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    private String generateHashString(String command, String var1) {
        return getSHA(key + "|" + command + "|" + var1 + "|" + salt);
    }

    private String getSHA(String str) {

        MessageDigest md;
        String out = "";
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(str.getBytes());
            byte[] mb = md.digest();

            for (int i = 0; i < mb.length; i++) {
                byte temp = mb[i];
                String s = Integer.toHexString(new Byte(temp));
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                out += s;
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return out;

    }



}
