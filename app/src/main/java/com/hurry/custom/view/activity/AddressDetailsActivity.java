package com.hurry.custom.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.hurry.custom.R;
import com.hurry.custom.common.CommonDialog;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.common.utils.ValidationHelper;
import com.hurry.custom.controller.GetCity;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.AddressModel;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.model.OrderHisModel;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.view.BaseActivity;
import com.hurry.custom.view.BaseBackActivity;
import com.hurry.custom.view.activity.map.TouchMapActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hurry.custom.common.Constants.addressModel;

/**
 * Created by Administrator on 3/18/2017.
 */

public class AddressDetailsActivity extends BaseBackActivity implements View.OnClickListener  ,TextWatcher{


    TextView txtPhoneHint;
    TextView txtPhoneDesHint;
    EditText edtSenderName;
    EditText edtPickAddress;
    AutoCompleteTextView edtPickCity;
    AutoCompleteTextView edtPickupArea;
    EditText edtPickState;
    AutoCompleteTextView edtPickPinCode;
    EditText edtPickPhone;
    EditText edtPickLandMark;
    EditText edtPickInstrunction;
    EditText edtDesAddress;
    AutoCompleteTextView edtDesCity;
    AutoCompleteTextView edtDesArea;
    EditText edtDesState;
    AutoCompleteTextView edtDesPinCode;
    EditText edtDesLandmark;
    EditText edtDesInstruction;
    EditText edtDesPhone;
    EditText edtDesName;
    TextView txtSourceProfile;
    TextView txtDesProfile;
    CheckBox chkGetGuote;
    LinearLayout linQuote;
    Button btnContinue;
    TextView txtChooseSource;
    TextView txtChooseDes;

    LinearLayout linPickup, linDropoff, linPickupTitle, linDropoffTitle, linChoosePickup, linChooseDropoff;
    String mSenderName = "", mPickAddress = "", mPickArea = "", mPickCity = "", mPickState = "", mPickPinCode = "" , mPickPhone = "", mPickLandmark = "", mPickInstruction = "";
    String mDesAddress = "" , mDesArea = "" , mDesCity = "" , mDesState = "" , mDesPinCode = "" , mDesLandmark = "" , mDesInstruction = "", mDesPhone ="", mDesName = "";

    public String[]  areaLists;
    public String[]  cityLists;
    public String[]  pincodeLists;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btn_choose_source) MaterialButton btnChooseSource;
    @BindView(R.id.btn_choose_des) MaterialButton btnChooseDes;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_address_details);
        ButterKnife.bind(this);
        initBackButton(toolbar, getString(R.string.address_detail));

        PreferenceUtils.setOrderId(this, "0");
        initView();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!getValue("type").isEmpty() && getValue("type").equals("edit")){
            showAddressDetails();
        }else{
            if(results == false){
                showAddressDetails();
            }
        }
    }


    private  void initView(){

        btnChooseDes.setOnClickListener(this);
        btnChooseSource.setOnClickListener(this);
        btnChooseDes.setIconResource(R.mipmap.dropoff_location);
        btnChooseSource.setIconResource(R.mipmap.dropoff_location);

        txtPhoneHint  = (TextView)findViewById(R.id.txt_phone_hint);
        txtPhoneDesHint = (TextView)findViewById(R.id.txt_phone_des_hint);

        edtSenderName = (EditText)findViewById(R.id.edt_sender);
        edtPickAddress = (EditText)findViewById(R.id.edt_address);
        edtPickupArea = (AutoCompleteTextView)findViewById(R.id.edt_area);
        edtPickCity = (AutoCompleteTextView)findViewById(R.id.edt_city);
        edtPickState = (EditText)findViewById(R.id.edt_state);
        edtPickPinCode = (AutoCompleteTextView)findViewById(R.id.edt_pincode);
        edtPickPhone = (EditText)findViewById(R.id.edt_phone);

        edtPickLandMark = (EditText)findViewById(R.id.edt_landmark);
        edtPickInstrunction = (EditText)findViewById(R.id.edt_instruction);

        edtDesAddress = (EditText)findViewById(R.id.edt_address_des);
        edtDesArea = (AutoCompleteTextView)findViewById(R.id.edt_area_des);
        edtDesCity = (AutoCompleteTextView)findViewById(R.id.edt_city_des);
        edtDesState = (EditText)findViewById(R.id.edt_state_des);
        edtDesPinCode = (AutoCompleteTextView)findViewById(R.id.edt_pincode_des);
        edtDesLandmark = (EditText)findViewById(R.id.edt_landmark_des);
        edtDesInstruction = (EditText)findViewById(R.id.edt_instruction_des);
        edtDesPhone = (EditText)findViewById(R.id.edt_phone_des);
        edtDesName = (EditText)findViewById(R.id.edt_des_name);
        // new ...hide
        edtPickupArea.setVisibility(View.GONE);
        edtPickCity.setVisibility(View.GONE);
        edtPickState.setVisibility(View.GONE);
        edtPickPinCode.setVisibility(View.GONE);

        edtDesArea.setVisibility(View.GONE);
        edtDesCity.setVisibility(View.GONE);
        edtDesState.setVisibility(View.GONE);
        edtDesPinCode.setVisibility(View.GONE);

        edtSenderName.addTextChangedListener(this);
        edtPickAddress.addTextChangedListener(this);
        edtDesArea.addTextChangedListener(this);
        edtPickCity.addTextChangedListener(this);
        edtPickState.addTextChangedListener(this);
        edtPickPinCode.addTextChangedListener(this);
        edtPickPhone.addTextChangedListener(this);
        edtPickLandMark.addTextChangedListener(this);
        edtPickInstrunction.addTextChangedListener(this);

        edtDesAddress.addTextChangedListener(this);
        edtDesArea.addTextChangedListener(this);
        edtDesCity.addTextChangedListener(this);
        edtDesState.addTextChangedListener(this);
        edtDesPinCode.addTextChangedListener(this);
        edtDesLandmark.addTextChangedListener(this);
        edtDesInstruction.addTextChangedListener(this);
        edtDesPhone.addTextChangedListener(this);

        chkGetGuote = (CheckBox)findViewById(R.id.chk_get_quote);
        chkGetGuote.setOnClickListener(this);
        chkGetGuote.setVisibility(View.GONE);

        linQuote = (LinearLayout)findViewById(R.id.lin_quote);

        btnContinue = (Button)findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(this);


        txtSourceProfile = (TextView)findViewById(R.id.txt_source_profile);
        txtDesProfile = (TextView)findViewById(R.id.txt_des_profile);
        txtSourceProfile.setOnClickListener(this);
        txtDesProfile.setOnClickListener(this);

        if(Constants.MODE == Constants.GUEST){
            txtSourceProfile.setVisibility(View.GONE);
            txtDesProfile.setVisibility(View.GONE);
        }
        initAreaLists();
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>
                (this, R.layout.custom_auto, areaLists);
        edtPickupArea.setThreshold(1);//will start working from first character
        edtPickupArea.setAdapter(areaAdapter);

        edtDesArea.setThreshold(1);
        edtDesArea.setAdapter(areaAdapter);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>
                (this, R.layout.custom_auto, cityLists);
        edtPickCity.setThreshold(1);
        edtPickCity.setAdapter(cityAdapter);

        edtDesCity.setThreshold(1);
        edtDesCity.setAdapter(cityAdapter);

        ArrayAdapter<String> pincodeAdapter = new ArrayAdapter<String>
                (this, R.layout.custom_auto, pincodeLists);
        edtPickPinCode.setThreshold(1);
        edtPickPinCode.setAdapter(pincodeAdapter);

        edtDesPinCode.setThreshold(1);
        edtDesPinCode.setAdapter(pincodeAdapter);

        edtPickAddress.addTextChangedListener(this);
        edtDesArea.addTextChangedListener(this);
        edtPickCity.addTextChangedListener(this);
        edtPickState.addTextChangedListener(this);
        edtPickPinCode.addTextChangedListener(this);
        edtPickPhone.addTextChangedListener(this);
        edtPickLandMark.addTextChangedListener(this);
        edtPickInstrunction.addTextChangedListener(this);

        edtDesAddress.addTextChangedListener(this);
        edtDesArea.addTextChangedListener(this);
        edtDesCity.addTextChangedListener(this);
        edtDesState.addTextChangedListener(this);
        edtDesPinCode.addTextChangedListener(this);
        edtDesLandmark.addTextChangedListener(this);
        edtDesInstruction.addTextChangedListener(this);

        if(DeviceUtil.isTablet(this)){

            edtPickAddress.setHintTextColor(getResources().getColor(R.color.gray));
            edtPickupArea.setHintTextColor(getResources().getColor(R.color.gray));
            edtPickCity.setHintTextColor(getResources().getColor(R.color.gray));
            edtPickState.setHintTextColor(getResources().getColor(R.color.gray));
            edtPickPinCode.setHintTextColor(getResources().getColor(R.color.gray));
            edtPickPhone.setHintTextColor(getResources().getColor(R.color.gray));
            edtPickLandMark.setHintTextColor(getResources().getColor(R.color.gray));
            edtPickInstrunction.setHintTextColor(getResources().getColor(R.color.gray));

            edtDesAddress.setHintTextColor(getResources().getColor(R.color.gray));
            edtDesArea.setHintTextColor(getResources().getColor(R.color.gray));
            edtDesCity.setHintTextColor(getResources().getColor(R.color.gray));
            edtDesState.setHintTextColor(getResources().getColor(R.color.gray));
            edtDesPinCode.setHintTextColor(getResources().getColor(R.color.gray));
            edtDesLandmark.setHintTextColor(getResources().getColor(R.color.gray));
            edtDesInstruction.setHintTextColor(getResources().getColor(R.color.gray));

            edtPickAddress.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtPickupArea.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtPickCity.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtPickState.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtPickPinCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtPickPhone.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtPickLandMark.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtPickInstrunction.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));

            edtDesAddress.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtDesArea.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtDesCity.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtDesState.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtDesPinCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtDesLandmark.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtDesInstruction.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));


            findViewById(R.id.line_address).setVisibility(View.VISIBLE);

            //findViewById(R.id.line_area).setVisibility(View.VISIBLE);
            //findViewById(R.id.line_city).setVisibility(View.VISIBLE);
            //findViewById(R.id.line_state).setVisibility(View.VISIBLE);
            //findViewById(R.id.line_pincode).setVisibility(View.VISIBLE);
            findViewById(R.id.line_phone).setVisibility(View.VISIBLE);
            findViewById(R.id.line_landmark).setVisibility(View.VISIBLE);
            findViewById(R.id.line_instruction).setVisibility(View.VISIBLE);
            findViewById(R.id.line_address_des).setVisibility(View.VISIBLE);
            //findViewById(R.id.line_area_des).setVisibility(View.VISIBLE);
            //findViewById(R.id.line_city_des).setVisibility(View.VISIBLE);
            //findViewById(R.id.line_state_des).setVisibility(View.VISIBLE);
            //findViewById(R.id.line_pincode_des).setVisibility(View.VISIBLE);
            findViewById(R.id.line_phone).setVisibility(View.VISIBLE);
            findViewById(R.id.lin_landmark_des).setVisibility(View.VISIBLE);
            findViewById(R.id.line_instruction_des).setVisibility(View.VISIBLE);
            //  findViewById(R.id.line_phone_des).setVisibility(View.VISIBLE);
        }

        txtChooseSource = (TextView)findViewById(R.id.txt_choose_source);
        txtChooseDes = (TextView)findViewById(R.id.txt_choose_des);
        if(getValue("type").equals("exceed")){
            txtChooseSource.setText(getString(R.string.enter_source));
            chkGetGuote.setVisibility(View.VISIBLE);
        }else{
            txtChooseSource.setText(getString(R.string.choose_source));
        }
        ImageView imgChooseSource = (ImageView)findViewById(R.id.img_choose_source);
        ImageView imgChooseDes = (ImageView)findViewById(R.id.img_choose_des);
        txtChooseSource.setOnClickListener(this);
        txtChooseDes.setOnClickListener(this);
        imgChooseSource.setOnClickListener(this);
        imgChooseDes.setOnClickListener(this);

        linPickup = (LinearLayout)findViewById(R.id.lin_pickup);
        linDropoff = (LinearLayout)findViewById(R.id.lin_dropoff);
        linPickupTitle = (LinearLayout)findViewById(R.id.lin_pickup_title);
        linDropoffTitle = (LinearLayout)findViewById(R.id.lin_dropoff_title);
        linChoosePickup = (LinearLayout)findViewById(R.id.lin_choose_pickup);
        linChooseDropoff =  (LinearLayout)findViewById(R.id.lin_choose_dropoff);

        edtSenderName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(ValidationHelper.isSpecialCharacter(edtSenderName.getText().toString())){
                    edtSenderName.setText(edtSenderName.getText().toString().subSequence(0,s.length() -1 ));
                    edtSenderName.setSelection(edtSenderName.length());
                }

                Constants.addressModel.senderName = edtSenderName.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtDesName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(ValidationHelper.isSpecialCharacter(edtDesName.getText().toString())){
                    edtDesName.setText(edtDesName.getText().toString().subSequence(0,s.length() -1 ));
                    edtDesName.setSelection(edtDesName.length());
                }
                Constants.addressModel.desName = edtDesName.getText().toString();
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


    private  void showAddressDetails(){
        AddressModel addressModel = Constants.addressModel;
        try {
            addressModel = Constants.addressModel.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        edtSenderName.setEnabled(false);
        edtDesName.setEnabled(false);
        if(addressModel.sourceAddress != null && !addressModel.sourceAddress.isEmpty()){
            txtChooseSource.setText(addressModel.sourceAddress);
            edtPickAddress.setText(addressModel.sourceAddress);
            edtPickAddress.setEnabled(false);
            edtDesAddress.setEnabled(false);
            txtSourceProfile.setVisibility(View.GONE);
            txtDesProfile.setVisibility(View.GONE);
        }else{
            disableSourceView(false, false);
            disableDestinatiionView(false, false);
        }

        if(addressModel.sourceAddress != null && !addressModel.sourceAddress.isEmpty()){
            disableSourceView(true, true);
        }else{
            disableSourceView(false, false);
        }

        if(addressModel.desAddress != null && !addressModel.desAddress.isEmpty()){
            disableDestinatiionView(true, true);
        }else{
            disableDestinatiionView(false, false);
        }

        if(addressModel.sourceArea != null && !addressModel.sourceArea.isEmpty()){
            edtPickupArea.setText(addressModel.sourceArea);
        }
        if(addressModel.sourceCity != null && !addressModel.sourceCity.isEmpty()){
            edtPickCity.setText(addressModel.sourceCity);
        }
        if(addressModel.sourceState != null && !addressModel.sourceState.isEmpty()){
            edtPickState.setText(addressModel.sourceState);
        }
        if(addressModel.sourcePinCode != null && !addressModel.sourcePinCode.isEmpty()){
            edtPickPinCode.setText(addressModel.sourcePinCode);
        }

        if(addressModel.sourcePhonoe != null && !addressModel.sourcePhonoe.equals(getResources().getString(R.string.phone_prefix)) && addressModel.sourcePhonoe.length() == 13){
            edtPickPhone.setText(addressModel.sourcePhonoe.substring(3,addressModel.sourcePhonoe.length()));
            edtPickPhone.setEnabled(true);
        }

        if(addressModel.sourceLandMark != null && !addressModel.sourceLandMark.isEmpty()){
            edtPickLandMark.setText(addressModel.sourceLandMark);
            edtPickLandMark.setEnabled(true);
        }

        if(addressModel.sourceInstruction != null && !addressModel.sourceInstruction.isEmpty()){
            edtPickInstrunction.setText(addressModel.sourceInstruction);
            edtPickInstrunction.setEnabled(true);
        }

        if(addressModel.desAddress != null && !addressModel.desAddress.isEmpty()){

            txtChooseDes.setText(addressModel.desAddress);
            edtDesAddress.setText(addressModel.desAddress);
            edtDesAddress.setEnabled(false);
            txtSourceProfile.setVisibility(View.GONE);
            txtDesProfile.setVisibility(View.GONE);
        }

        if(addressModel.desArea != null && !addressModel.desArea.isEmpty()){
            edtDesArea.setText(addressModel.desArea);
        }

        if(addressModel.desCity != null && !addressModel.desCity.isEmpty()){
            edtDesCity.setText(addressModel.desCity);
        }

        if(addressModel.desState != null && !addressModel.desState.isEmpty()){
            edtDesState.setText(addressModel.desState);
        }

        if(addressModel.desPinCode != null  && !addressModel.desPinCode.isEmpty()){
            edtDesPinCode.setText(addressModel.desPinCode);
            edtDesPinCode.setEnabled(true);
        }

        if(addressModel.desLandMark != null  && !addressModel.desLandMark.isEmpty()){
            edtDesLandmark.setText(addressModel.desLandMark);
            edtDesLandmark.setEnabled(true);
        }

        if(addressModel.desInstruction != null && !addressModel.desInstruction.isEmpty()){
            edtDesInstruction.setText(addressModel.desInstruction);
            edtDesInstruction.setEnabled(true);
        }

        if(addressModel.desPhone != null  && !addressModel.desPhone.equals(getResources().getString(R.string.phone_prefix)) && addressModel.desPhone.length() == 13){
            edtDesPhone.setText(addressModel.desPhone.substring(3,addressModel.desPhone.length()));
            edtDesPhone.setEnabled(true);
        }

        if(addressModel.desName != null  && !addressModel.desName.isEmpty()){
            edtDesName.setText(addressModel.desName);
            edtDesName.setEnabled(true);
            // show bottom button
            showBottomButton();
        }

        if(addressModel.senderName != null  && !addressModel.senderName.isEmpty()){
            edtSenderName.setText(addressModel.senderName);
            edtSenderName.setEnabled(true);
        }
        //addService();
    }

    private void showBottomButton(){
        findViewById(R.id.lin_continue).setVisibility(View.VISIBLE);
        LinearLayout layout = (LinearLayout)findViewById(R.id.lin_address_container);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layout.getLayoutParams();
        params.setMargins(0, 0, 0, 100);
        layout.setLayoutParams(params);
    }

    private void disableSourceView(boolean flag, boolean show){

        edtPickAddress.setEnabled(false);
        edtPickupArea.setEnabled(false);
        edtPickCity.setEnabled(false);
        edtPickState.setEnabled(false);
        edtPickPinCode.setEnabled(false);
        edtPickPhone.setEnabled(false);
        edtPickLandMark.setEnabled(false);
        edtPickInstrunction.setEnabled(false);
        edtSenderName.setEnabled(false);

        if(show){
            edtPickAddress.setEnabled(false);
            edtPickPhone.setEnabled(true);
            edtPickLandMark.setEnabled(true);
            edtPickInstrunction.setEnabled(true);
            edtSenderName.setEnabled(true);
        }
        if(flag){
            linPickup.setBackgroundColor(Color.TRANSPARENT);
            linPickupTitle.setBackgroundColor(Color.TRANSPARENT);
            linChoosePickup.setBackgroundColor(Color.TRANSPARENT);
        }else{

            linPickup.setBackgroundColor(getResources().getColor(R.color.trans_black));
            linPickupTitle.setBackgroundColor(getResources().getColor(R.color.trans_black));
            linChoosePickup.setBackgroundColor(getResources().getColor(R.color.trans_black));
        }
    }

    public void disableDestinatiionView(boolean flag, boolean show){

        edtDesAddress.setEnabled(false);
        edtDesArea.setEnabled(false);
        edtDesCity.setEnabled(false);
        edtDesState.setEnabled(false);
        edtDesPinCode.setEnabled(false);
        edtDesLandmark.setEnabled(false);
        edtDesInstruction.setEnabled(false);
        edtDesPhone .setEnabled(false);
        edtDesName.setEnabled(false);

        if(show){
            edtDesAddress.setEnabled(false);
            edtDesPhone.setEnabled(true);
            edtDesInstruction.setEnabled(true);
            edtDesLandmark.setEnabled(true);
            edtDesName.setEnabled(true);
        }

        if(flag){
            linDropoff.setBackgroundColor(Color.TRANSPARENT);
            linDropoffTitle.setBackgroundColor(Color.TRANSPARENT);
            linChooseDropoff.setBackgroundColor(Color.TRANSPARENT);
        }else{
            linDropoff.setBackgroundColor(getResources().getColor(R.color.trans_black));
            linDropoffTitle.setBackgroundColor(getResources().getColor(R.color.trans_black));
            linChooseDropoff.setBackgroundColor(getResources().getColor(R.color.trans_black));
        }
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        checkInput();
        DeviceUtil.hideSoftKeyboard(this);
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_continue:
                //checkInput();
                if(checkInput()){
                    if(Constants.MODE == Constants.CORPERATION){
                        Intent intent = new Intent(AddressDetailsActivity.this, DateTimeActivity.class);
                        startActivity(intent);
                    }else if(getValue("type").equals("exceed") && Constants.MODE == Constants.PERSONAL){
                        if(ValidationHelper.isPostCode(mPickPinCode) && ValidationHelper.isPostCode(mDesPinCode)){
                            if(chkGetGuote.isChecked()){
                                order_quote();
                            }else{
                                chkGetGuote.setVisibility(View.VISIBLE);
                                final int sdk = android.os.Build.VERSION.SDK_INT;
                                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    linQuote.setBackgroundDrawable( getResources().getDrawable(R.drawable.checkbox_border) );
                                } else {
                                    linQuote.setBackground( getResources().getDrawable(R.drawable.checkbox_border));
                                }
                                Toast.makeText(AddressDetailsActivity.this, "Enable checkbox", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(AddressDetailsActivity.this,"Invalid Post Code", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if(ValidationHelper.isPostCode(mPickPinCode) && ValidationHelper.isPostCode(mDesPinCode)){
                            getDistanceWebService();
                        }else{
                            Toast.makeText(AddressDetailsActivity.this,"Invalid Post Code", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(AddressDetailsActivity.this,"Please enter all information", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.chk_get_quote:

                break;
            case R.id.img_back:
                checkInput();
                DeviceUtil.hideSoftKeyboard(this);
                finish();
                break;
            case R.id.txt_source_profile:

                break;
            case  R.id.txt_des_profile:

                break;
            case R.id.btn_choose_source:
            case R.id.img_choose_source:
            case R.id.txt_choose_source:
                if( Constants.orderHisModels.size() == 0){
                    getOrderHistory("source");
                }else{
                    CommonDialog.showChooseAddress(AddressDetailsActivity.this, "source");
                }
                // goToMapSource();
                break;
            case R.id.btn_choose_des:
            case R.id.img_choose_des:
            case R.id.txt_choose_des:
                if( Constants.orderHisModels.size() == 0){
                    getOrderHistory("destination");
                }else{
                    CommonDialog.showChooseAddress(AddressDetailsActivity.this, "destination");
                }
                //goToMapDestination();
                break;
        }
    }


    public void goToMapSource(){
        if(Constants.cityBounds.size() > 0){
            Intent source = new Intent(AddressDetailsActivity.this, TouchMapActivity.class);
            source.putExtra("type", "source");
            startActivityForResult(source, 300);
        }else{
            new GetCity(this, "source").execute();
        }
    }
    public void goToMapDestination(){
        if(Constants.cityBounds.size() > 0){
            Intent destination = new Intent(AddressDetailsActivity.this, TouchMapActivity.class);
            destination.putExtra("type", "destination");
            startActivityForResult(destination, 400);
        }else{
            new GetCity(this, "destination").execute();
        }
    }

    public void goToProfileSource(){
        Intent intent = new Intent(this, AddressBookActivity.class);
        intent.putExtra("type", "source");
        startActivityForResult(intent, 300);
    }
    public void goToProfileDestination(){
        Intent intent2 = new Intent(this, AddressBookActivity.class);
        intent2.putExtra("type", "des");
        startActivityForResult(intent2, 400);
    }

    private  void showConfirmDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        //builder1.setMessage("Write your message here.");
        builder1.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View)inflater.inflate(com.gun0912.tedpicker.R.layout.dialog_forgot, null);
        builder1.setView(view);
        final AlertDialog alert11 = builder1.create();

        TextView txtTitle = (TextView)view.findViewById(com.gun0912.tedpicker.R.id.txt_title);
        TextView txtContent  = (TextView)view.findViewById(com.gun0912.tedpicker.R.id.txt_content);
        txtTitle.setText("");
        txtContent.setText(getResources().getString(R.string.quote_message));

        Button btnOk = (Button)view.findViewById(com.gun0912.tedpicker.R.id.btn_ok);
        btnOk.setText("Ok");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert11.hide();
                Constants.page_type = "";
                AddressDetailsActivity.this.finish();
                if(CameraOrderActivity.cameraOrderActivity != null){
                    CameraOrderActivity.cameraOrderActivity.finish();
                }
                if(ItemOrderActivity.itemOrderActivity != null){
                    ItemOrderActivity.itemOrderActivity.finish();
                }
                if(PackageOrderActivity.packageOrderActivity != null){
                    PackageOrderActivity.packageOrderActivity.finish();
                }
            }
        });
        alert11.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(getValue("type").equals("exceed")){
            if(mPickAddress.isEmpty() || mPickCity.isEmpty() || mPickState.isEmpty() || mPickPinCode.isEmpty() || mPickPhone.isEmpty() || mPickLandmark.isEmpty() ){
                return ;
            }
            if(mDesAddress.isEmpty() || mDesCity.isEmpty() || mDesState.isEmpty() || mDesPinCode.isEmpty() || mDesLandmark.isEmpty()|| mDesPhone.isEmpty() ){
                return ;
            }
            chkGetGuote.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(edtPickPhone.getText().toString().length() > 0) {
            if (edtPickPhone.getText().toString().contains(".")) {
                edtPickPhone.setText(edtPickPhone.getText().toString().replace(".", ""));
                edtPickPhone.setSelection(edtPickPhone.getText().toString().length());
                return;
            }
        }

        if(edtDesPhone.getText().toString().length() > 0){
            if(edtDesPhone.getText().toString().contains(".")){
                try {
                    edtDesPhone.setText(edtDesPhone.getText().toString().replace(".", ""));
                    edtDesPhone.setSelection(edtPickPhone.getText().toString().length());
                }catch (Exception e){
                }
                return;
            }
        }
        if(edtPickPhone.getText().toString().length() > 0 ){
            txtPhoneHint.setVisibility(View.GONE);
            Constants.addressModel.sourcePhonoe = edtPickPhone.getText().toString();
        }else{
            txtPhoneHint.setVisibility(View.VISIBLE);
        }
        if(edtDesPhone.getText().toString().length() > 0){
            txtPhoneDesHint.setVisibility(View.GONE);
            Constants.addressModel.desPhone = edtPickPhone.getText().toString();
        }else{
            txtPhoneDesHint.setVisibility(View.VISIBLE);
        }

        if(edtPickLandMark.getText().toString().length() > 0){
            Constants.addressModel.sourceLandMark = edtPickLandMark.getText().toString();
        }
        if(edtDesLandmark.getText().toString().length() > 0){
            Constants.addressModel.desLandMark = edtDesLandmark.getText().toString();
        }

    }



    private  boolean checkInput(){

        mSenderName = edtSenderName.getText().toString();
        mPickAddress = edtPickAddress.getText().toString();
        mPickArea = edtPickupArea.getText().toString();
        mPickCity = edtPickCity.getText().toString();
        mPickState = edtPickState.getText().toString();
        mPickPinCode = edtPickPinCode.getText().toString();
        mPickPhone = getString(R.string.phone_prefix) +  edtPickPhone.getText().toString();
        mPickLandmark = edtPickLandMark.getText().toString();
        mPickInstruction = edtPickInstrunction.getText().toString();

        mDesAddress = edtDesAddress.getText().toString();
        mDesArea = edtDesArea.getText().toString();
        mDesCity = edtDesCity.getText().toString();
        mDesState = edtDesState.getText().toString();
        mDesPinCode = edtDesPinCode.getText().toString();
        mDesLandmark = edtDesLandmark.getText().toString();
        mDesInstruction  = edtDesInstruction.getText().toString();
        mDesPhone = getString(R.string.phone_prefix) + edtDesPhone.getText().toString();
        mDesName = edtDesName.getText().toString();


        addressModel.senderName = mSenderName;
        addressModel.sourceAddress  = mPickAddress;
        addressModel.sourceArea = mPickArea;
        addressModel.sourceCity  = mPickCity;
        addressModel.sourceState  = mPickState;
        addressModel.sourcePinCode  = mPickPinCode;
        addressModel.sourcePhonoe  = mPickPhone;
        addressModel.sourceLandMark  = mPickLandmark;
        addressModel.sourceInstruction  = mPickInstruction;

        addressModel.desAddress = mDesAddress;
        addressModel.desArea = mDesArea;
        addressModel.desCity = mDesCity;
        addressModel.desState = mDesState;
        addressModel.desPinCode = mDesPinCode;
        addressModel.desLandMark = mDesLandmark;
        addressModel.desInstruction  = mDesInstruction;
        addressModel.desPhone = mDesPhone;
        addressModel.desName = mDesName;

        if(mSenderName.isEmpty()){
            edtSenderName.setBackgroundResource(R.drawable.text_box_underline_selector);
            edtSenderName.requestFocus();
            return false;
        }else{
            edtSenderName.setBackgroundResource(R.drawable.text_box_underline_selector_tint);
        }

        if(mPickAddress.isEmpty()){
            edtPickAddress.requestFocus();
            return false;
        }else{
            edtPickAddress.setBackgroundResource(R.drawable.text_box_underline_selector_tint);
        }

        if( mPickLandmark.isEmpty() ){
            edtPickLandMark.setBackgroundResource(R.drawable.text_box_underline_selector);
            edtPickLandMark.requestFocus();
            return false;
        }else{
            edtPickLandMark.setBackgroundResource(R.drawable.text_box_underline_selector_tint);
        }

        if(mPickPhone.isEmpty() || mPickPhone.length() == 3 || !ValidationHelper.isValidPhoneNumber(mPickPhone)){
            edtPickPhone.setBackgroundResource(R.drawable.text_box_underline_selector);
            edtPickPhone.requestFocus();
            return false;
        }else{
            edtPickPhone.setBackgroundResource(R.drawable.text_box_underline_selector_tint);
        }

        if(mPickAddress.isEmpty() || mPickPhone.isEmpty() || mPickLandmark.isEmpty() ){ //mPickCity.isEmpty() || mPickState.isEmpty() || mPickPinCode.isEmpty() ||
            return false;
        }



        if(mDesName.isEmpty()){
            edtDesName.setBackgroundResource(R.drawable.text_box_underline_selector);
            edtDesName.requestFocus();
            return false;
        }else{
            edtDesPhone.setBackgroundResource(R.drawable.text_box_underline_selector_tint);
        }

        if(mDesLandmark.isEmpty()){
            edtDesLandmark.setBackgroundResource(R.drawable.text_box_underline_selector);
            edtDesLandmark.requestFocus();
            return false;
        }else{
            edtDesLandmark.setBackgroundResource(R.drawable.text_box_underline_selector_tint);
        }


        if(mDesPhone.isEmpty() || mPickPhone.length() == 3 || !ValidationHelper.isValidPhoneNumber(mDesPhone)){
            edtDesPhone.setBackgroundResource(R.drawable.text_box_underline_selector);
            edtDesPhone.requestFocus();
            return false;
        }else{
            edtDesPhone.setBackgroundResource(R.drawable.text_box_underline_selector_tint);
        }


        if(mDesAddress.isEmpty()){
            edtDesAddress.setBackgroundResource(R.drawable.text_box_underline_selector);
            edtDesAddress.requestFocus();
            return false;
        }else{
            edtDesAddress.setBackgroundResource(R.drawable.text_box_underline_selector_tint);
        }


        if(mDesAddress.isEmpty() || mDesLandmark.isEmpty()|| mDesPhone.isEmpty() ){ // mDesCity.isEmpty() || mDesState.isEmpty() || mDesPinCode.isEmpty() ||
            return false;
        }

        if(addressModel.sourceLat == 0 || addressModel.sourceLng == 0){
            return false;
        }

        if(!ValidationHelper.isValidPhoneNumber(addressModel.sourcePhonoe)){
            Toast.makeText(this, "Invalid Phone Number",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!ValidationHelper.isValidPhoneNumber(addressModel.desPhone)){
            Toast.makeText(this, "Invalid Phone Number",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void order_quote() {
        RequestParams params = new RequestParams();
        params.put("user_id", PreferenceUtils.getUserId(this));
        params.put("weight", String.valueOf(Constants.getTotalWeight()));
        params.put("distance", "");
        params.put("quote_type", "1"); // is_quote_request
        params.put("device_id", DeviceUtil.getDeviceId(this));
        params.put("device_type", DeviceUtil.getDeviceName());
        // order address
        JSONArray addressArray = new JSONArray();
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("s_address", addressModel.sourceAddress);
        jsonMap.put("s_area", addressModel.sourceArea);
        jsonMap.put("s_city", addressModel.sourceCity);
        jsonMap.put("s_state", addressModel.sourceState);
        jsonMap.put("s_pincode", addressModel.sourcePinCode);
        jsonMap.put("s_phone", addressModel.sourcePhonoe + ":" + addressModel.senderName);
        jsonMap.put("s_landmark", addressModel.sourceLandMark);
        jsonMap.put("s_instruction", addressModel.sourceInstruction);
        jsonMap.put("s_lat", String.valueOf(addressModel.sourceLat));
        jsonMap.put("s_lng", String.valueOf(addressModel.sourceLng));

        jsonMap.put("d_address", addressModel.desAddress);
        jsonMap.put("d_area", addressModel.desArea);
        jsonMap.put("d_city", addressModel.desCity);
        jsonMap.put("d_state", addressModel.desState);
        jsonMap.put("d_pincode", addressModel.desPinCode);
        jsonMap.put("d_landmark", addressModel.desLandMark);
        jsonMap.put("d_instruction", addressModel.desInstruction);
        jsonMap.put("d_lat", String.valueOf(addressModel.desLat));
        jsonMap.put("d_lng", String.valueOf(addressModel.desLng));
        jsonMap.put("d_phone", String.valueOf(addressModel.desPhone));
        jsonMap.put("d_name", String.valueOf(addressModel.desName));

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
                cameraMap.put("dimension", Constants.itemOrderModel.itemModels.get(i).dimension1 + "X" + Constants.itemOrderModel.itemModels.get(i).dimension2 + "X"  +Constants.itemOrderModel.itemModels.get(i).dimension3);
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
        WebClient.post(Constants.BASE_URL + "order_quote", params,
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

                                Constants.clearData();
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(AddressDetailsActivity.this, getResources().getString(R.string.quote_message), Toast.LENGTH_LONG).show();
                                    //Toast.makeText(AddressDetailsActivity.this, getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }else if(response.getString("result").equals("200")){
                                    String order_id = response.getString("order_id");
                                    showConfirmDialog();

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




    private void getDistanceWebService() {

        RequestParams params = new RequestParams();
        params.put("org", String.valueOf(addressModel.sourceLat) + ","  +String.valueOf(addressModel.sourceLng));
        params.put("des", String.valueOf(addressModel.desLat) + ","  +String.valueOf(addressModel.desLng));
        params.put("weight", String.valueOf(Constants.getTotalWeight()));
        params.put("user_id", PreferenceUtils.getUserId(this));

        WebClient.get(Constants.BASE_URL + "getDistance" , params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        showProgressDialog();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(AddressDetailsActivity.this, "Invalid Pin Code ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {
                        try{
                            if (response != null) {
                                if(response.getString("result").equals("200")){


                                    Constants.priceType.expeditied_price = response.getString("expedited");
                                    Constants.priceType.express_price = response.getString("express");
                                    Constants.priceType.economy_price = response.getString("economy");
                                    try{
                                        Constants.priceType.distance = String.valueOf(Double.valueOf(response.getString("distance")) / 1000);
                                    }catch (Exception e){};
                                }

                                if(Constants.priceType.expeditied_price != null && !Constants.priceType.expeditied_price.isEmpty()){
                                    Intent dateIntent = new Intent(AddressDetailsActivity.this, DateTimeActivity.class);
                                    startActivity(dateIntent);
                                    finish();
                                }
                                hideProgressDialog();
                            }
                        }catch (Exception e){};
                    };
                    public void onFinish() {
                        hideProgressDialog();
                    }
                    ;
                });
    }


    boolean results = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == 100 ) {
                txtDesProfile.setVisibility(View.GONE);
            }else if(requestCode == 200){
                txtSourceProfile.setVisibility(View.GONE);
            }else if(requestCode == 300){
                if(data.getStringExtra("address") != null){
                    disableSourceView(true, true);

                    addressModel.sourceLat = Double.valueOf(data.getExtras().getString("lat"));
                    addressModel.sourceLng = Double.valueOf(data.getExtras().getString("lng"));
                    txtChooseSource.setText(data.getStringExtra("location"));
                    edtPickAddress.setText(data.getStringExtra("location")) ; //address data.getStringExtra("address") + "," + data.getStringExtra("area") + "," + data.getStringExtra("city")
                    edtPickPinCode.setText(data.getStringExtra("pinCode"));
                    edtPickState.setText(data.getStringExtra("state"));
                    edtPickupArea.setText(data.getStringExtra("area"));
                    edtPickCity.setText(data.getStringExtra("city"));
                    Constants.addressModel.sourceAddress = data.getStringExtra("location");// + "," + data.getStringExtra("area") + "," + data.getStringExtra("city");

                    try{
                        if(data.getStringExtra("phone") != null && !data.getStringExtra("phone").isEmpty()){
                            edtPickPhone.setText(data.getStringExtra("phone"));
                            edtPickLandMark.setText(data.getStringExtra("landmark"));
                            edtSenderName.setText(data.getStringExtra("name"));
                            edtPickInstrunction.setText(data.getStringExtra("instruction"));
                        }
                    }catch (Exception e){};
                    results = true;
                }

            }else if(requestCode == 400){

                if( data.getStringExtra("address")  != null){

                    disableDestinatiionView(true, true);
                    addressModel.desLat = Double.valueOf(data.getExtras().getString("lat"));
                    addressModel.desLng = Double.valueOf(data.getExtras().getString("lng"));
                    txtChooseDes.setText(data.getStringExtra("location"));
                    edtDesAddress.setText(data.getStringExtra("location")); //address
                    edtDesPinCode.setText(data.getStringExtra("pinCode"));
                    edtDesState.setText(data.getStringExtra("state"));
                    edtDesArea.setText(data.getStringExtra("area"));
                    edtDesCity.setText(data.getStringExtra("city"));
                    Constants.addressModel.desAddress = data.getStringExtra("location");// + "," + data.getStringExtra("area") + "," + data.getStringExtra("city");
                    try{
                        if(data.getStringExtra("phone") != null && !data.getStringExtra("phone").isEmpty()){
                            edtDesPhone.setText(data.getStringExtra("phone"));
                            edtDesLandmark.setText(data.getStringExtra("landmark"));
                            edtDesName.setText(data.getStringExtra("name"));
                            edtDesInstruction.setText(data.getStringExtra("instruction"));
                        }

                    }catch (Exception e){};

                    showBottomButton();

                    results = true;
                }

            }
        }
    }


    private void getOrderHistory(final String type) {
        RequestParams params = new RequestParams();
        params.put("user_id", PreferenceUtils.getUserId(AddressDetailsActivity.this));

        WebClient.post(Constants.BASE_URL_ORDER + "get_orders_his", params,
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

                                String result = response.getString("result");
                                if(result.equals("400")){
                                    CommonDialog.showChooseAddress(AddressDetailsActivity.this, type);
                                    return;
                                }

                                JSONArray jsonArray = response.getJSONArray("orders");
                                Constants.orderHisModels = new ArrayList<OrderHisModel>();

                                for (int k= 0; k < jsonArray.length() ; k++){

                                    JSONObject object = jsonArray.getJSONObject(k);
                                    OrderHisModel orderHisModel = new OrderHisModel();
                                    orderHisModel.orderId = object.getString("id");
                                    orderHisModel.trackId = object.getString("track");
                                    orderHisModel.payment = object.getString("payment");
                                    orderHisModel.accepted_by = object.getString("accepted_by");

                                    orderHisModel.dateModel.date = object.getString("date");
                                    orderHisModel.dateModel.time = object.getString("time");

                                    orderHisModel.serviceModel.name = object.getString("service_name");
                                    orderHisModel.serviceModel.price = object.getString("service_price");
                                    orderHisModel.serviceModel.time_in = object.getString("service_timein");

                                    orderHisModel.state = object.getString("state");
                                    orderHisModel.is_quote_request = object.getString("is_quote_request");
                                    orderHisModel.price = object.getString("price");
                                    orderHisModel.paymentId = object.getString("transaction_id");

                                    try{
                                        JSONArray addressArray = object.getJSONArray("address");
                                        for(int i = 0; i < addressArray.length(); i++){
                                            JSONObject addressObj = addressArray.getJSONObject(i);
                                            orderHisModel.addressModel.sourceAddress = addressObj.getString("s_address");
                                            orderHisModel.addressModel.sourceArea =  addressObj.getString("s_area");
                                            orderHisModel.addressModel.sourceCity = addressObj.getString("s_city");
                                            orderHisModel.addressModel.sourceState = addressObj.getString("s_state");
                                            orderHisModel.addressModel.sourcePinCode = addressObj.getString("s_pincode");
                                            try{
                                                String res = addressObj.getString("s_phone");
                                                orderHisModel.addressModel.sourcePhonoe = res;
                                                String[] resArray = res.split(":");
                                                if(resArray.length == 2){
                                                    orderHisModel.addressModel.sourcePhonoe = resArray[0];
                                                    orderHisModel.addressModel.senderName = resArray[1];
                                                }
                                            }catch (Exception e){};

                                            //orderHisModel.addressModel.sourcePhonoe = addressObj.getString("s_phone");
                                            orderHisModel.addressModel.sourceLandMark = addressObj.getString("s_landmark");
                                            orderHisModel.addressModel.sourceInstruction = addressObj.getString("s_instruction");
                                            orderHisModel.addressModel.sourceLat = addressObj.getDouble("s_lat");
                                            orderHisModel.addressModel.sourceLng = addressObj.getDouble("s_lng");

                                            orderHisModel.addressModel.desAddress = addressObj.getString("d_address");
                                            orderHisModel.addressModel.desArea = addressObj.getString("d_area");
                                            orderHisModel.addressModel.desCity = addressObj.getString("d_city");
                                            orderHisModel.addressModel.desState = addressObj.getString("d_state");
                                            orderHisModel.addressModel.desPinCode = addressObj.getString("d_pincode");
                                            orderHisModel.addressModel.desLandMark = addressObj.getString("d_landmark");
                                            orderHisModel.addressModel.desInstruction = addressObj.getString("d_instruction");
                                            orderHisModel.addressModel.desLat = addressObj.getDouble("d_lat");
                                            orderHisModel.addressModel.desLng = addressObj.getDouble("d_lng");
                                            orderHisModel.addressModel.desPhone = addressObj.getString("d_phone");
                                            orderHisModel.addressModel.desName = addressObj.getString("d_name");
                                        }

                                    }catch (Exception e){};


                                    try{
                                        JSONArray productArray = object.getJSONArray("products");
                                        orderHisModel.orderModel = new OrderModel();
                                        orderHisModel.orderModel.itemModels = new ArrayList<ItemModel>();

                                        for(int j = 0; j < productArray.length(); j++){
                                            JSONObject productObj = productArray.getJSONObject(j);
                                            int product_type = productObj.getInt("product_type");

                                            if(product_type == Constants.CAMERA_OPTION){
                                                orderHisModel.orderModel.product_type = product_type;

                                                ItemModel model = new ItemModel();
                                                model.image = productObj.getString("image");
                                                model.quantity  = productObj.getString("quantity");
                                                model.weight = productObj.getString("weight");

                                                orderHisModel.orderModel.itemModels.add(model);

                                            }else if(product_type == Constants.ITEM_OPTION){
                                                orderHisModel.orderModel.product_type = product_type;
                                                ItemModel model = new ItemModel();
                                                model.title = productObj.getString("title");

                                                model.quantity  = productObj.getString("quantity");
                                                model.weight = productObj.getString("weight");
                                                try{
                                                    String[] dim = productObj.getString("dimension").split("X");
                                                    model.dimension1 = dim[0];
                                                    model.dimension2 = dim[1];
                                                    model.dimension3 = dim[2];
                                                }catch (Exception e){};
                                                orderHisModel.orderModel.itemModels.add(model);

                                            }else if(product_type == Constants.PACKAGE_OPTION){
                                                orderHisModel.orderModel.product_type = product_type;
                                                ItemModel model = new ItemModel();
                                                model.title = productObj.getString("title");
                                                model.quantity  = productObj.getString("quantity");
                                                model.weight = productObj.getString("weight");
                                                orderHisModel.orderModel.itemModels.add(model);
                                            }
                                        }
                                    }catch (Exception e){
                                        String err  = e.toString();
                                    }
                                    Constants.orderHisModels.add(orderHisModel);
                                }

                                CommonDialog.showChooseAddress(AddressDetailsActivity.this, type);

                            }else if(response.getString("result").equals("400")){
                                Toast.makeText(AddressDetailsActivity.this , getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
