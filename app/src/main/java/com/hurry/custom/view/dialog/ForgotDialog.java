package com.hurry.custom.view.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.IPositiveButtonDialogListener;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.view.activity.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * Sample implementation of custom dialog by extending {@link SimpleDialogFragment}.
 *
 * @author David Vávra (david@inmite.eu)
 */
public class ForgotDialog extends SimpleDialogFragment implements View.OnClickListener {

    public static String TAG = "jayne";

    TextView txtTitle;
    ImageView imgBack;
    TextView txtPageTitle;
    TextView txtTitle1;
    TextView txtTitle2;
    EditText editText1;
    EditText edtQuestion;
    Button btnSubmit;
    LinearLayout linQuestion;
    TextView txtUserHint,txtPasswordHint;


    CountryCodePicker countryCodePicker;
    public String cpp = "";
    ImageView imgUser;
    
    String mName, mAnswer;
    boolean flag = true;
    public static String mType;


    public static String type;
    public static Activity signUpActivity;


    public static void show(AppCompatActivity activity, String type) {
        new ForgotDialog().show(activity.getSupportFragmentManager(), TAG);
        ForgotDialog.mType = type;
        ForgotDialog.signUpActivity = activity;
    }

    @Override
    public int getTheme() {
        return R.style.JayneHatDialogTheme;
    }

    @Override
    public Builder build(Builder builder) {
        //builder.setTitle("Choose Service");
        //builder.setMessage("A man walks down the street in that hat, people know he's not afraid of anything.");
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_forgot, null);
        builder.setView(view);
        initView(view);

        builder.setPositiveButton("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (IPositiveButtonDialogListener listener : getPositiveButtonDialogListeners()) {
                    listener.onPositiveButtonClicked(mRequestCode);
                }
                dismiss();
            }
        });


        return builder;
    }



    private  void initView(View view){
        imgUser = (ImageView)view.findViewById(R.id.img_user);
        countryCodePicker = (CountryCodePicker)view.findViewById(R.id.ccp);
        txtTitle = (TextView)view.findViewById(R.id.txt_title);
        imgBack = (ImageView)view.findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);

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

        this.cpp = "+91";
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                cpp = "+" +  selectedCountry.getPhoneCode();
            }
        });

        if(mType.equals("username")){
            // for got user name
            txtTitle.setText(getResources().getString(R.string.forgot_username));
            txtPageTitle.setText(getResources().getString(R.string.retrieve_username));
            txtTitle1.setText(getResources().getString(R.string.recognize_phone));
            txtTitle2.setText(getResources().getString(R.string.food));
            txtTitle1.setVisibility(View.GONE);
            editText1.setHint(getResources().getString(R.string.recognize_phone_hint));
            //editText1.setText(getResources().getString(R.string.phone_prefix));
            countryCodePicker.setVisibility(View.VISIBLE);
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
            editText1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
//                    if(editText1.getText().toString().length() < 3 && flag){
//                        flag = false;
//                        editText1.setText(getResources().getString(R.string.phone_prefix));
//                        int pos = editText1.getText().length();
//                        editText1.setSelection(pos);
//                        Handler handle = new Handler();
//                        handle.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                flag = true;
//                            }
//                        }, 100);
//                        cpp.setVisibility(View.VISIBLE);
//                    }else{
//                        if(editText1.getText().length() > 3){
//                            cpp.setVisibility(View.GONE);
//                        }
//                    }
                }
            });

        }else if(mType.equals("phone")){
            txtTitle.setText(getResources().getString(R.string.forgot_password));
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
                    return getActivity().onKeyDown(keyCode, event);
                }
            });
        }

        txtTitle2.setVisibility(View.GONE);
        edtQuestion.setVisibility(View.GONE);
        linQuestion.setVisibility(View.GONE);

        if(Constants.MODE == Constants.PERSONAL){
            txtTitle2.setText(PreferenceUtils.getCustomerQuestion(getActivity()));
            edtQuestion.setHint(PreferenceUtils.getCustomerQuestion(getActivity()));
        }else{
            txtTitle2.setText(PreferenceUtils.getCorporateQuestion(getActivity()));
            edtQuestion.setHint(PreferenceUtils.getCorporateQuestion(getActivity()));
        }

        btnSubmit = (Button)view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
        btnSubmit.setText("Continue");


        edtQuestion.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == 66 || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return getActivity().onKeyDown(keyCode, event);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:

                if(linQuestion.getVisibility() == View.VISIBLE){
                    if(checkInput()){
                        if(mType.equals("username")){
                            forgotUserName();
                        }else{
                            forgotPassword();
                        }
                    }else{
                        Toast.makeText(getActivity(), getResources().getString(R.string.input_all), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mName = editText1.getText().toString();
                    if(mName.isEmpty()){
                        Toast.makeText(getActivity(), "Please Input All Info",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(mType.equals("username")){
                        getQuestionFromPhoneCustomer(cpp + mName);
                    }else{
                        getQuestionFromEmailCustomer(mName);
                    }
                }
                break;

        }
    }

    private  boolean checkInput(){
        mName = editText1.getText().toString();
        mAnswer = edtQuestion.getText().toString();
        if(mName.isEmpty() || mAnswer.isEmpty()){
            return false;
        }
        return true;
    }





    private void getQuestionFromEmailCustomer(String email) {

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("type",Constants.MODE);

        String url = "";
        url = Constants.FORGOT + "getQuestionFromEmailCustomer";
        WebClient.post(url, params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        
                        ((LoginActivity)getActivity()).showProgressDialog();
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
                                    Toast.makeText(getActivity(), getResources().getString(R.string.notfound_email), Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){Toast.makeText(getActivity(), getResources().getString(R.string.notfound_email), Toast.LENGTH_SHORT).show();};
                        }
                    };
                    public void onFinish() {
                        
                        ((LoginActivity)getActivity()).hideProgressDialog();
                    }
                    ;
                });
    }


    private void getQuestionFromPhoneCustomer(String phone) {
        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("type",Constants.MODE);
        String url = "";
        url = Constants.FORGOT + "getQuestionFromPhoneCustomer";
        WebClient.post(url , params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        ((LoginActivity)getActivity()).showProgressDialog();
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
                                    Toast.makeText(getActivity(), getResources().getString(R.string.notfound_phone) , Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){Toast.makeText(getActivity(),  getResources().getString(R.string.notfound_phone) , Toast.LENGTH_SHORT).show();};
                        }
                    };
                    public void onFinish() {
                        ((LoginActivity)getActivity()).hideProgressDialog();
                    }
                    ;
                });
    }




    private void forgotUserName() {
        RequestParams params = new RequestParams();
        params.put("phone", mName);
        params.put("answer", mAnswer);
        WebClient.post(Constants.FORGOT + "forgotUserNameCustomer", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        ((LoginActivity)getActivity()).showProgressDialog();
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
                                    ForgotDialog.this.dismiss();
                                    showConfirmDialog(email);

                                }else{
                                    //txtTitle1.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity(), getResources().getString(R.string.record_error), Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){Toast.makeText(getActivity(), getResources().getString(R.string.record_error), Toast.LENGTH_SHORT).show();};
                        }
                    };
                    public void onFinish() {
                        ((LoginActivity)getActivity()).hideProgressDialog();
                    }
                    ;
                });
    }



    private void forgotPassword() {
        RequestParams params = new RequestParams();
        params.put("email", mName);
        params.put("answer", mAnswer);
        WebClient.post(Constants.FORGOT + "forgotPasswordCustomer", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {

                        ((LoginActivity)getActivity()).showProgressDialog();
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
                                    showConfirmDialog(password);
                                }else if(results.equals("500")){
                                    //txtTitle1.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity(), "Email sent error , clicksend issue", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getActivity(), getResources().getString(R.string.record_error), Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                Toast.makeText(getActivity(), getResources().getString(R.string.record_error), Toast.LENGTH_SHORT).show();
                            };
                        }
                    };
                    public void onFinish() {
                        ((LoginActivity)getActivity()).hideProgressDialog();
                    }
                    ;
                });
    }
    private void showConfirmDialog(String results){

        String title = "", content = "";
        if(mType.equals("username")){
            title = getResources().getString(R.string.recovery_email);
            content = getResources().getString(R.string.alert_forgot_username);
        }else if(mType.equals("phone")){
            title = getResources().getString(R.string.text_message);
            content = getResources().getString(R.string.forgot_password_alert);

        }

        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(content)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }}).show();
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).show();


//        final Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_forgot);
//        // dialog.setTitle("Title...");
//        TextView txtTitle = (TextView)dialog.findViewById(R.id.txt_title);
//        TextView txtContent  = (TextView)dialog.findViewById(R.id.txt_content);
//
//        if(mType.equals("username")){
//            txtTitle.setText(getResources().getString(R.string.recovery_email));
//            txtContent.setText(getResources().getString(R.string.forgot_username_alert));
//            //txtContent.setText(getResources().getString(R.string.recovery_email_content));
//        }else if(mType.equals("phone")){
//            txtTitle.setText(getResources().getString(R.string.text_message));
//            try {
//                txtContent.setText(getResources().getString(R.string.forgot_password_alert));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            //txtContent.setText(getResources().getString(R.string.text_message_content));
//        }
//        txtTitle.setVisibility(View.GONE);
//        Button btnOk = (Button)dialog.findViewById(R.id.btn_ok);
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.hide();
//            }
//        });
//        dialog.show();
    }


}