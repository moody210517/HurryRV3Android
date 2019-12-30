package com.hurry.custom.view.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.IPositiveButtonDialogListener;
import com.hurry.custom.R;
import com.hurry.custom.view.activity.login.PhoneNumberActivity;
import com.hurry.custom.view.activity.login.SignUpActivity;

/**
 * Sample implementation of custom dialog by extending {@link SimpleDialogFragment}.
 *
 * @author David Vávra (david@inmite.eu)
 */
public class OTPDialog extends SimpleDialogFragment {

    public static String TAG = "jayne";

    EditText edtCode1, edtCode2, edtCode3, edtCode4, edtCode5, edtCode6;
    String mCode1, mCode2, mCode3, mCode4, mCode5, mCode6;
    String nextString = "";
    Button btnNext;


    public static String phone;
    public String code = "";
    public static Activity signUpActivity;


    public void show(AppCompatActivity activity, String phone) {

        new OTPDialog().show(activity.getSupportFragmentManager(), TAG);

        OTPDialog.phone = phone;
        OTPDialog.signUpActivity = activity;
    }

    @Override
    public int getTheme() {

        return R.style.JayneHatDialogTheme;
    }



    @Override
    public Builder build(Builder builder) {
        //builder.setTitle("Choose Service");
        //builder.setMessage("A man walks down the street in that hat, people know he's not afraid of anything.");
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_otp, null);
        builder.setView(view);
        getDialog().setCanceledOnTouchOutside(false);
        builder.setPositiveButton("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (IPositiveButtonDialogListener listener : getPositiveButtonDialogListeners()) {
                    listener.onPositiveButtonClicked(mRequestCode);
                }
                dismiss();
            }
        });

        initView(view);

        TextView txtPhoneNumber = (TextView)view.findViewById(R.id.txt_phone_number);
        txtPhoneNumber.setText(getString(R.string.otp_has_sent) +  " "  + phone);

        btnNext = (Button)view.findViewById(R.id.btn_next);
        btnNext.setTextColor(getResources().getColor(R.color.red_pop));
        btnNext.setEnabled(false);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputCheck()){
                    code = mCode1 + mCode2 + mCode3 + mCode4 + mCode5 + mCode6;
                    if(!code.isEmpty() && code.length() == 6){
                        if(signUpActivity instanceof  SignUpActivity){
                            ((SignUpActivity)signUpActivity).registerNow(code);
                        }else if(signUpActivity instanceof PhoneNumberActivity){
                            ((PhoneNumberActivity)signUpActivity).registerNow(code);
                        }

                    }else{
                        Toast.makeText(signUpActivity, getResources().getString(R.string.enter_otp), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(signUpActivity, getResources().getString(R.string.enter_otp), Toast.LENGTH_SHORT).show();
                }
                //dismiss();
            }
        });

        TextView txtResend= (TextView)view.findViewById(R.id.txt_resend);
        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((SignUpActivity)getActivity()).sendPhone(phone, "OTP has been resent", true);

            }
        });



        return builder;
    }


    private boolean inputCheck(){

        mCode1 = edtCode1.getText().toString();
        mCode2 = edtCode2.getText().toString();
        mCode3 = edtCode3.getText().toString();
        mCode4 = edtCode4.getText().toString();
        mCode5 = edtCode5.getText().toString();
        mCode6 = edtCode6.getText().toString();

        if(mCode1.isEmpty() || mCode2.isEmpty() || mCode3.isEmpty() || mCode4.isEmpty() || mCode5.isEmpty() || mCode6.isEmpty()){
            return false;
        }
        return true;
    }

    private void initView(View view){

        edtCode1 = (EditText)view.findViewById(R.id.edt_code1);
        edtCode2 = (EditText)view.findViewById(R.id.edt_code2);
        edtCode3 = (EditText)view.findViewById(R.id.edt_code3);
        edtCode4 = (EditText)view.findViewById(R.id.edt_code4);
        edtCode5 = (EditText)view.findViewById(R.id.edt_code5);
        edtCode6 = (EditText)view.findViewById(R.id.edt_code6);

        //txtSendAgain.setText(txtSendAgain.getText().toString() + " " + getPhone());
        edtCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtCode1.length() == 2){
                    nextString = edtCode1.getText().toString().substring(1);
                    edtCode1.setText(edtCode1.getText().toString().substring(0,1));
                }else if(edtCode1.length() > 0){
                    if(nextString.isEmpty()){
                        edtCode2.requestFocus();
                    }else{
                        String temp = nextString;
                        nextString = "";
                        edtCode2.setText(temp);
                    }
                }
                updateButton(btnNext);
            }
        });
        edtCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String test = "ss";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String test = "ss";
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(edtCode2.length() == 2){
                    nextString = edtCode2.getText().toString().substring(1);
                    edtCode2.setText(edtCode2.getText().toString().substring(0,1));
                }else if(edtCode2.length() > 0){
                    if(nextString.isEmpty()){
                        edtCode3.requestFocus();
                    }else{
                        String temp = nextString;
                        nextString = "";
                        edtCode3.setText(temp);

                    }
                }else if(edtCode2.length() == 0){
                    edtCode1.setSelection(edtCode1.getText().toString().length());
                    edtCode1.requestFocus();
                }

                updateButton(btnNext);
            }
        });
        edtCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String test = "ss";
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String test = "ss";
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(edtCode3.length() == 2){
                    nextString = edtCode3.getText().toString().substring(1);
                    edtCode3.setText(edtCode3.getText().toString().substring(0,1));

                }else if(edtCode3.length() > 0){
                    if(nextString.isEmpty()){
                        edtCode4.requestFocus();
                    }else{
                        String temp = nextString;
                        nextString = "";
                        edtCode4.setText(temp);
                        edtCode4.setSelection(edtCode4.getText().toString().length());
                        edtCode4.requestFocus();
                    }

                }else if(edtCode3.length() == 0){
                    edtCode2.setSelection(edtCode2.getText().toString().length());
                    edtCode2.requestFocus();
                }

                updateButton(btnNext);
            }
        });

        edtCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String test = "ss";
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String test = "ss";
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(edtCode4.length() == 2){
                    nextString = edtCode4.getText().toString().substring(1);
                    edtCode4.setText(edtCode4.getText().toString().substring(0,1));

                }else if(edtCode4.length() > 0){
                    if(nextString.isEmpty()){
                        edtCode5.requestFocus();
                    }else{
                        String temp = nextString;
                        nextString = "";
                        edtCode5.setText(temp);
                        edtCode5.setSelection(edtCode5.getText().toString().length());
                        edtCode5.requestFocus();
                    }

                }else if(edtCode4.length() == 0){
                    edtCode3.setSelection(edtCode3.getText().toString().length());
                    edtCode3.requestFocus();
                }

                updateButton(btnNext);
            }
        });

        edtCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String test = "ss";
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String test = "ss";
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(edtCode5.length() == 2){
                    nextString = edtCode5.getText().toString().substring(1);
                    edtCode5.setText(edtCode5.getText().toString().substring(0,1));

                }else if(edtCode5.length() > 0){
                    if(nextString.isEmpty()){
                        edtCode6.requestFocus();
                    }else{
                        String temp = nextString;
                        nextString = "";
                        edtCode6.setText(temp);
                        edtCode6.setSelection(edtCode6.getText().toString().length());
                        edtCode6.requestFocus();
                    }

                }else if(edtCode5.length() == 0){
                    edtCode4.setSelection(edtCode3.getText().toString().length());
                    edtCode4.requestFocus();
                }

                updateButton(btnNext);
            }
        });


        edtCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String test = "ss";
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String test = "ss";
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtCode6.length() == 0){
                    edtCode5.setSelection(edtCode5.getText().length());
                    edtCode5.requestFocus();
                }
                if(edtCode6.length() == 2){
                    nextString = edtCode6.getText().toString().substring(1);
                    edtCode6.setText(edtCode6.getText().toString().substring(0,1));
                }
                updateButton(btnNext);
            }
        });



        edtCode2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == 67){
                    if(edtCode2.getText().toString().isEmpty()){
                        edtCode1.setText("");
                        edtCode1.requestFocus();
                        return true;
                    }else{
                        return false;
                    }

                }
                return false;
            }
        });
        edtCode3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == 67){
                    if(edtCode3.getText().toString().isEmpty()){
                        edtCode2.setText("");
                        edtCode2.requestFocus();
                        return true;
                    }else{
                        return false;
                    }

                }
                return false;
            }
        });

        edtCode4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == 67){
                    if(edtCode4.getText().toString().isEmpty()){
                        edtCode3.setText("");
                        edtCode3.requestFocus();
                        return true;
                    }else{
                        return false;
                    }

                }
                return false;
            }
        });

        edtCode5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == 67){
                    if(edtCode5.getText().toString().isEmpty()){
                        edtCode4.setText("");
                        edtCode4.requestFocus();
                        return true;
                    }else{
                        return false;
                    }

                }
                return false;
            }
        });


        edtCode6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == 67){
                    if(edtCode6.getText().toString().isEmpty()){
                        edtCode5.setText("");
                        edtCode5.requestFocus();
                        return true;
                    }else{
                        return false;
                    }
                }
                return false;
            }
        });

    }


    private void updateButton(Button btnNext){
        if(edtCode1.length() == 1 && edtCode2.length() == 1 && edtCode3.length() == 1 && edtCode4.length() == 1 && edtCode5.length() == 1 && edtCode6.length() == 1){
            //btnNext.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_login));
            btnNext.setTextColor(getResources().getColor(R.color.white));
            btnNext.setEnabled(true);
        }else{
            //btnNext.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_cancel));
            btnNext.setTextColor(getResources().getColor(R.color.red_pop));
            btnNext.setEnabled(false);
        }
    }

}