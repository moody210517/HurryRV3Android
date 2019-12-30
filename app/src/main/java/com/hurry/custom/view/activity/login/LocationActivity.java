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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.hurry.custom.controller.GetCity;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.CorporateModel;
import com.hurry.custom.view.BaseActivity;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.MainActivity;
import com.hurry.custom.view.adapter.CityAdapter;
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

public class LocationActivity extends BaseActivity {


    @BindView(R.id.recyclerview) RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_city);
        ButterKnife.bind(this);

        showProgressDialog();
        new GetCity(this,"").execute();
    }


    public void setupRecyclerView() {
        if(Constants.cityModels!= null && Constants.cityModels.size() > 0){
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            recyclerView.setAdapter(new CityAdapter(this,
                    Constants.cityModels, recyclerView));
        }else{
            recyclerView.setAdapter(null);
        }
    }

    public void goToMain(){
        PreferenceUtils.setFirstStart(LocationActivity.this, false);
        Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
