package com.hurry.custom.view.activity.login;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.button.MaterialButton;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.common.utils.JsonHelper;
import com.hurry.custom.common.utils.ValidationHelper;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.CorporateModel;
import com.hurry.custom.view.BaseActivity;
import com.hurry.custom.view.activity.MainActivity;
import com.hurry.custom.view.activity.HomeActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 3/18/2017.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener , GoogleApiClient.OnConnectionFailedListener{


    @BindView(R.id.edt_username) EditText edtUserName;
    EditText edtPassword;
    TextView txtForgotUserName;
    TextView txtForgotPassword;
    @BindView(R.id.txt_user_hint) TextView txtUserHint;
    TextView txtPasswordHint;

    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.btn_register) Button btnRegister;
    @BindView(R.id.btn_facebook) MaterialButton btnFacebook;
    @BindView(R.id.btn_google) MaterialButton btnGoogle;
    @BindView(R.id.underline_username) View underlineUsername;
    @BindView(R.id.underline_password) View underlinePassword;
    @BindView(R.id.img_username_close) ImageView imgUserNameClose;
    @BindView(R.id.img_password_close) ImageView imgPasswordClose;


    String mEmail;
    String mPassword;


    private static final String[] INITIAL_PERMS={
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
    };
    private static final int INITIAL_REQUEST=1337;

    // google
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    // facebook
    private CallbackManager callbackManager;
    AccessToken mAccessToken;
    LoginManager loginManager;
    Collection<String> permissions = Arrays.asList("public_profile", "user_friends", "email");//"public_profile", "user_friends", "email", "user_likes"
    String id, name, first_name, last_name, email, image;


    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        printKeyHash(this);
        FacebookSdk.sdkInitialize(LoginActivity.this);
        FacebookSdk.setIsDebugEnabled(true);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Constants.page_type = "signin";

        if(PreferenceUtils.getLogin(LoginActivity.this)){
            mEmail = PreferenceUtils.getEmail(this);
            mPassword = PreferenceUtils.getPassword(this);
            loadBasicData();
        }

        initView();
        initActions();
        DeviceUtil.hideSoftKeyboard(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
            }
        }

        // google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // facebook login
        callbackManager = CallbackManager.Factory.create();
        loginManager    =   LoginManager.getInstance();
        loginManager.registerCallback(callbackManager ,mCallBack);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INITIAL_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LoginActivity.this, "Permission allowed", Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(LoginActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }

    //0r44WuejGyABUHDh5+fvRhOeGSQ=
    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();
            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            Log.e("Package Name=", context.getApplicationContext().getPackageName());
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return key;
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            mAccessToken = loginResult.getAccessToken();
            final Profile profile = Profile.getCurrentProfile();
            setProfile();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {
        }
    };

    private void setProfile() {
        if(this.mAccessToken==null){
            return;
        }
        //mProfile = profile;
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, link, picture , first_name,last_name,email,gender,birthday");
        GraphRequest request = GraphRequest.newMeRequest(
                this.mAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                        } else {

                            id = me.optString("id");
                            name = me.optString("name");
                            first_name = me.optString("first_name");
                            last_name = me.optString("last_name");
                            email = me.optString("email");
                            try {
                                image = me.getJSONObject("picture").getJSONObject("data").optString("url");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                Profile.fetchProfileForCurrentAccessToken();
                            } catch (NullPointerException e) {
                            }

                            mEmail = email;
                            mPassword = "hurryr12";
                            login("facebook");



                        }
                    }
                });
        request.setParameters(parameters);
        request.executeAsync();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    String provider , uid;
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            PreferenceUtils.setLoginUser(LoginActivity.this, acct.getId(), acct.getId(), acct.getDisplayName());
            //PreferenceUtils.setUserId(LoginActivity.this, acct.getId());
            PreferenceUtils.setEmail(LoginActivity.this, acct.getEmail());
            String image = "";
            if(acct.getPhotoUrl() != null){
                PreferenceUtils.setImage(LoginActivity.this, acct.getPhotoUrl().toString());
                image = acct.getPhotoUrl().toString();
            }

            mEmail= acct.getEmail();
            provider = "google_oauth2";
            uid = acct.getId();
            first_name = acct.getDisplayName();
            last_name = acct.getGivenName();
            email = mEmail;
            String photo = acct.getPhotoUrl() == null ? "" : acct.getPhotoUrl().toString();
            //registerNow( acct.getDisplayName(),  acct.getEmail(),  acct.getDisplayName(), photo);

            login("google");

        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(LoginActivity.this, "Google Login Failed",Toast.LENGTH_SHORT).show();
        }
    }

    private  void initView(){


        edtPassword = (EditText)findViewById(R.id.edt_password);

        if(DeviceUtil.isTablet(this)){
            edtUserName.setHintTextColor(getResources().getColor(R.color.gray));
            edtPassword.setHintTextColor(getResources().getColor(R.color.gray));
        }


        btnLogin.setOnClickListener(this);
        txtForgotUserName = (TextView)findViewById(R.id.txt_forgot_username);
        txtForgotPassword = (TextView)findViewById(R.id.txt_forgot_password);
        txtForgotPassword.setOnClickListener(this);
        txtForgotUserName.setOnClickListener(this);

        btnFacebook.setOnClickListener(this);
        btnFacebook.setIconResource(R.mipmap.ic_facebook_36);
        btnGoogle.setOnClickListener(this);
        btnGoogle.setIconResource(R.mipmap.ic_google_36);

        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtUserName.getText().toString().length() > 0){
                    imgUserNameClose.setVisibility(View.VISIBLE);
                }else{
                    imgUserNameClose.setVisibility(View.GONE);
                }
            }
        });


        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtPassword.getText().toString().length() > 0){
                    imgPasswordClose.setVisibility(View.VISIBLE);
                    txtPasswordHint.setVisibility(View.GONE);
                }else{
                    imgPasswordClose.setVisibility(View.GONE);

                }
            }
        });

        if(Constants.MODE == Constants.PERSONAL){
            if(!PreferenceUtils.getEmail(this).isEmpty()){
                edtUserName.setText(PreferenceUtils.getEmail(this));
            }
            if(!PreferenceUtils.getPassword(this).isEmpty()){
                edtPassword.setText(PreferenceUtils.getPassword(this));
            }
        }else{
            if(!PreferenceUtils.getCorEmail(this).isEmpty()){
                edtUserName.setText(PreferenceUtils.getCorEmail(this));
            }
            if(!PreferenceUtils.getCorPassword(this).isEmpty()){
                edtPassword.setText(PreferenceUtils.getCorPassword(this));
            }
        }

        if(DeviceUtil.isTablet(this)){
            edtUserName.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtPassword.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
        }

        imgUserNameClose.setOnClickListener(this);
        imgPasswordClose.setOnClickListener(this);
    }


    private void initActions(){
        txtPasswordHint = (TextView)findViewById(R.id.txt_password_hint);
        btnRegister.setOnClickListener(this);
        edtUserName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtUserHint.setVisibility(View.GONE);
                imgUserNameClose.setVisibility(View.VISIBLE);

                txtPasswordHint.setVisibility(View.VISIBLE);
                imgPasswordClose.setVisibility(View.GONE);
                return false;
            }
        });

        edtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtUserHint.setVisibility(View.VISIBLE);
                imgUserNameClose.setVisibility(View.GONE);

                txtPasswordHint.setVisibility(View.GONE);
                imgPasswordClose.setVisibility(View.VISIBLE);

                return false;
            }
        });
    }


    private void goToSignUp(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivityForResult(intent, MainActivity.SIGN_UP_ACTIVITY);
    }
    private boolean checkValidate(){
        mEmail = edtUserName.getText().toString();
        mPassword = edtPassword.getText().toString();
        underlineUsername.setBackgroundColor(getResources().getColor(R.color.hint_color));
        underlinePassword.setBackgroundColor(getResources().getColor(R.color.hint_color));
        if(mEmail.isEmpty()){
            edtUserName.requestFocus();
            underlineUsername.setBackgroundColor(getResources().getColor(R.color.red));
            return false;
        }
        if(mPassword.isEmpty()){
            edtPassword.requestFocus();
            underlinePassword.setBackgroundColor(getResources().getColor(R.color.red));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                if(!checkValidate()){
                    Toast.makeText(LoginActivity.this, getString(R.string.input_all), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ValidationHelper.isValidEmailDetails(mEmail)){
                    loadBasicData();
                }else{
                    edtUserName.requestFocus();
                    underlinePassword.setBackgroundColor(getResources().getColor(R.color.red));
                    Toast.makeText(LoginActivity.this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txt_forgot_password:

                showForgot("phone");
                break;
            case R.id.txt_forgot_username:

                showForgot("username");
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_register:
                goToSignUp();
                break;
            case R.id.btn_facebook:
                loginManager.logInWithReadPermissions(this,permissions);
                break;

            case R.id.btn_google:
                googleSignIn();
                break;
            case R.id.img_username_close:
                edtUserName.setText("");
                break;
            case R.id.img_password_close:
                edtPassword.setText("");
                break;
        }
    }


    private void loadBasicData() {
        RequestParams params = new RequestParams();
        //params.put("email", mEmail);
        WebClient.post(Constants.BASIC_DATA_URL + "get_basic", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        showProgressDialog();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        // Log.d("Error String", responseString);
                        hideProgressDialog();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        // Log.d("Error", errorResponse.toString());
                        hideProgressDialog();
                    }

                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {
                        if (response != null) {
                            try{
                                if(response.getString("result").equals("200")){
                                    JsonHelper.parseBasicData(response);
                                    login("normal");
                                }
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    hideProgressDialog();
                                }

                            }catch (Exception e){

                            }
                        }
                    };

                    public void onFinish() {
                        //hideProgressDialog();
                    };

                });
    }


    private void login(String type) {
        RequestParams params = new RequestParams();
        params.put("device_token", PreferenceUtils.getDeviceToken(this));
        params.put("email", mEmail);
        try {
            params.put("password", DeviceUtil.encrypt(mPassword));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("user_type", String.valueOf(Constants.MODE));
        WebClient.post(Constants.BASE_URL + "login", params,
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

                            if (response != null) {
                                try{
                                    if(response.getString("result").equals("400")){

                                        if(type.equals("facebook")){
                                            Intent intent = new Intent(LoginActivity.this, PhoneNumberActivity.class);
                                            intent.putExtra("type", "facebook");
                                            intent.putExtra("first", first_name);
                                            intent.putExtra("last", last_name);
                                            intent.putExtra("email", email);
                                            startActivity(intent);
                                        }else if(type.equals("google")){
                                            Intent intent = new Intent(LoginActivity.this, PhoneNumberActivity.class);
                                            intent.putExtra("type", "facebook");
                                            intent.putExtra("first", first_name);
                                            intent.putExtra("last", last_name);
                                            intent.putExtra("email", email);
                                            startActivity(intent);
                                        }else{
                                            Toast.makeText(LoginActivity.this, "We don’t recognise the User Name and Password. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                }catch (Exception e){


                                    JsonHelper.parseSinUpProcess(LoginActivity.this, response);
                                    if(Constants.MODE == Constants.PERSONAL){
                                        PreferenceUtils.setPassword(LoginActivity.this , mPassword);
                                    }else if(Constants.MODE == Constants.CORPERATION){
                                        PreferenceUtils.setCorPassword(LoginActivity.this , mPassword);
                                    }
                                    Constants.corporateModel = null;
                                    Constants.corporateModel = new CorporateModel();
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("result","res");
                                    setResult(Activity.RESULT_OK,returnIntent);
                                    finish();

                                    Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                                    startActivity(intent);


                                }
                            }
                    };

                    public void onFinish() {
                        hideProgressDialog();
                    }
                    ;
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    LinearLayout linQuestion;
    EditText editText1;
    EditText edtQuestion;
    String mName, mAnswer;
    DialogPlus dialogPlus;
    private void showForgot(String type){
        TextView txtPageTitle;
        TextView txtTitle1;
        TextView txtTitle2;

        Button btnSubmit;
        TextView txtUserHint,txtPasswordHint;
        CountryCodePicker countryCodePicker;
        final String[] cpp = {""};
        ImageView imgUser;
        String mType = type;

        View view = LayoutInflater.from(this).inflate(R.layout.activity_forgot, null);

        dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(view))
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .setGravity(Gravity.TOP)
                .create();

        dialogPlus.show();

        imgUser = (ImageView)view.findViewById(R.id.img_user);
        countryCodePicker = (CountryCodePicker)view.findViewById(R.id.ccp);
        countryCodePicker.setClickable(true);
        countryCodePicker.setVisibility(View.GONE);


        txtPageTitle = (TextView)view.findViewById(R.id.txt_page_title);
        txtTitle1   = (TextView)view.findViewById(R.id.txt_title1);
        txtTitle2  = (TextView)view.findViewById(R.id.txt_title2);

        editText1 = (EditText)view.findViewById(R.id.edt_username);
        edtQuestion = (EditText)view.findViewById(R.id.edt_question);
        linQuestion = (LinearLayout)view.findViewById(R.id.lin_question);


        txtUserHint = (TextView)view.findViewById(R.id.txt_user_hint);
        txtPasswordHint = (TextView)view.findViewById(R.id.txt_password_hint);

        editText1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtUserHint.setVisibility(View.GONE);
                if(edtQuestion.getText().toString().isEmpty()){
                    txtPasswordHint.setVisibility(View.VISIBLE);
                }else{
                    //txtPasswordHint.setVisibility(View.GONE);
                    txtPasswordHint.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });

        edtQuestion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(editText1.getText().toString().isEmpty()){
                    txtUserHint.setVisibility(View.VISIBLE);
                }else{
                    txtUserHint.setVisibility(View.VISIBLE);
                    //txtUserHint.setVisibility(View.GONE);
                }
                txtPasswordHint.setVisibility(View.GONE);
                return false;
            }
        });

        cpp[0] = "+91";
        prefix = "+91";
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                cpp[0] = "+" +  selectedCountry.getPhoneCode();
                prefix = cpp[0];
            }
        });

        if(mType.equals("username")){
            // for got user name
            //txtTitle.setText(getResources().getString(R.string.forgot_username));
            txtPageTitle.setText(getResources().getString(R.string.retrieve_username));
            txtTitle1.setText(getResources().getString(R.string.recognize_phone));
            txtTitle2.setText(getResources().getString(R.string.food));
            txtTitle1.setVisibility(View.GONE);
            editText1.setHint(getResources().getString(R.string.recognize_phone_hint));
            //editText1.setText(getResources().getString(R.string.phone_prefix));
            countryCodePicker.setVisibility(View.VISIBLE);
            countryCodePicker.setClickable(true);
            countryCodePicker.setFocusable(true);

            imgUser.setVisibility(View.GONE);
            txtUserHint.setVisibility(View.GONE);

            InputFilter filter = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    for (int i = start; i < end; ++i)
                    {
                        if (!Pattern.compile("[1234567890]*").matcher(String.valueOf(source.charAt(i))).matches())
                        {
                            return "";
                        }
                    }

                    return null;
                }
            };
            editText1.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(10)});

            //editText1.setHintTextColor(getResources().getColor(R.color.gray));
            edtQuestion.setHint(getResources().getString(R.string.food_hint));
            editText1.setInputType(InputType.TYPE_CLASS_NUMBER );
            int pos = editText1.getText().length();
            editText1.setSelection(pos);

        }else if(mType.equals("phone")){
            //txtTitle.setText(getResources().getString(R.string.forgot_password));
            /// forgot password
            txtPageTitle.setText(getResources().getString(R.string.reset_password));
            txtTitle1.setText(getResources().getString(R.string.recognize_email));
            txtTitle2.setText(getResources().getString(R.string.food));
            txtTitle1.setVisibility(View.GONE);
            editText1.setHint(getResources().getString(R.string.recognize_email_hint));
            //editText1.setHintTextColor(getResources().getColor(R.color.gray));
            edtQuestion.setHint(getResources().getString(R.string.food_hint));

            editText1.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == 66 || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        edtQuestion.requestFocus();
                        return true;
                    }
                    return onKeyDown(keyCode, event);
                }
            });
        }

        txtTitle2.setVisibility(View.GONE);
        edtQuestion.setVisibility(View.GONE);
        linQuestion.setVisibility(View.GONE);

        if(Constants.MODE == Constants.PERSONAL){
            txtTitle2.setText(PreferenceUtils.getCustomerQuestion(this));
            edtQuestion.setHint(PreferenceUtils.getCustomerQuestion(this));
        }else{
            txtTitle2.setText(PreferenceUtils.getCorporateQuestion(this));
            edtQuestion.setHint(PreferenceUtils.getCorporateQuestion(this));
        }

        btnSubmit = (Button)view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
        btnSubmit.setText("Continue");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linQuestion.getVisibility() == View.VISIBLE){
                    if(checkInput()){
                        if(mType.equals("username")){
                            forgotUserName();
                        }else{
                            forgotPassword(mType);
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.input_all), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mName = editText1.getText().toString();
                    if(mName.isEmpty()){
                        Toast.makeText(LoginActivity.this, "Please Input All Info",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(mType.equals("username")){
                        getQuestionFromPhoneCustomer(cpp[0] + mName, txtTitle2, btnSubmit);
                    }else{
                        getQuestionFromEmailCustomer(mName, txtTitle2, btnSubmit);
                    }
                }
            }
        });


        edtQuestion.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == 66 || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return onKeyDown(keyCode, event);
            }
        });

    }

    private  boolean checkInput(){
        mName = editText1.getText().toString();
        mAnswer = edtQuestion.getText().toString();
        if(mName.isEmpty() || mAnswer.isEmpty()){
            return false;
        }
        return true;
    }

    String prefix = "";
    private void forgotUserName() {
        RequestParams params = new RequestParams();
        params.put("phone", prefix + mName);
        params.put("answer", mAnswer);
        WebClient.post(Constants.FORGOT + "forgotUserNameCustomer", params,
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

                        if (response != null) {
                            try{
                                String results = response.getString("result");
                                if(results.equals("200")){
                                    JSONObject object = response.getJSONObject("data");
                                    String email = object.getString("email");
                                    showConfirmDialog("phone");

                                }else{
                                    //txtTitle1.setVisibility(View.VISIBLE);
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.record_error), Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){Toast.makeText(LoginActivity.this, getResources().getString(R.string.record_error), Toast.LENGTH_SHORT).show();};
                        }
                    };
                    public void onFinish() {

                        hideProgressDialog();
                    }
                    ;
                });
    }



    private void forgotPassword(String mType) {
        RequestParams params = new RequestParams();
        params.put("email", mName);
        params.put("answer", mAnswer);
        WebClient.post(Constants.FORGOT + "forgotPasswordCustomer", params,
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
                        if (response != null) {

                            try{
                                String results = response.getString("result");
                                if(results.equals("200")){
                                    JSONObject object = response.getJSONObject("data");
                                    String password = object.getString("password");
                                    showConfirmDialog(mType);
                                }else if(results.equals("500")){
                                    //txtTitle1.setVisibility(View.VISIBLE);
                                    Toast.makeText(LoginActivity.this, "Email sent error , clicksend issue", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.record_error), Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.record_error), Toast.LENGTH_SHORT).show();
                            };
                        }
                    };
                    public void onFinish() {

                        hideProgressDialog();
                    }
                    ;
                });
    }
    private void showConfirmDialog(String mType){
        String title = "", content = "";
        if(mType.equals("username")){
            title = getResources().getString(R.string.recovery_email);
            content = getResources().getString(R.string.forgot_password_alert);
        }else if(mType.equals("phone")){
            title = getResources().getString(R.string.text_message);
            content = getResources().getString(R.string.alert_forgot_username);
        }
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DeviceUtil.hideSoftKeyboard(LoginActivity.this);
                        dialogPlus.dismiss();
                    }}).show();
    }

    private void getQuestionFromEmailCustomer(String email, TextView txtTitle2, Button btnSubmit) {

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("type",Constants.MODE);

        String url = "";
        url = Constants.FORGOT + "getQuestionFromEmailCustomer";
        WebClient.post(url, params,
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

                        if (response != null) {
                            try{
                                String results = response.getString("result");
                                if(results.equals("200")){
                                    JSONObject object = response.getJSONObject("data");
                                    String question = object.getString("question");
                                    txtTitle2.setVisibility(View.GONE);
                                    edtQuestion.setVisibility(View.VISIBLE);
                                    linQuestion.setVisibility(View.VISIBLE);
                                    txtTitle2.setText(question);
                                    edtQuestion.setHint(question);
                                    btnSubmit.setText("Submit");
                                }else{
                                    //.setVisibility(View.VISIBLE);
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.notfound_email), Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){Toast.makeText(LoginActivity.this, getResources().getString(R.string.notfound_email), Toast.LENGTH_SHORT).show();};
                        }
                    };
                    public void onFinish() {

                        hideProgressDialog();
                    }
                    ;
                });
    }


    private void getQuestionFromPhoneCustomer(String phone, TextView txtTitle2, Button btnSubmit) {
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("type",Constants.MODE);
        String url = "";
        url = Constants.FORGOT + "getQuestionFromPhoneCustomer";
        WebClient.post(url , params,
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

                        if (response != null) {
                            try{
                                String results = response.getString("result");
                                if(results.equals("200")){

                                    JSONObject object = response.getJSONObject("data");
                                    String question = object.getString("question");
                                    txtTitle2.setVisibility(View.GONE);
                                    edtQuestion.setVisibility(View.VISIBLE);
                                    linQuestion.setVisibility(View.VISIBLE);
                                    txtTitle2.setText(question);
                                    edtQuestion.setHint(question);
                                    btnSubmit.setText("Submit");

                                }else{
                                    //txtTitle1.setVisibility(View.VISIBLE);
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.notfound_phone) , Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){Toast.makeText(LoginActivity.this,  getResources().getString(R.string.notfound_phone) , Toast.LENGTH_SHORT).show();};
                        }
                    };
                    public void onFinish() {
                        hideProgressDialog();
                    }
                    ;
                });
    }


}
