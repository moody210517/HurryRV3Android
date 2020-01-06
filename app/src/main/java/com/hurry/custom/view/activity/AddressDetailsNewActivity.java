package com.hurry.custom.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hurry.custom.R;
import com.hurry.custom.common.CommonDialog;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.controller.GetCity;
import com.hurry.custom.controller.GetLocationFromLatLng;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.AddressModel;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.model.OrderHisModel;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.view.BaseBackActivity;
import com.hurry.custom.view.activity.map.TouchMapActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.hurry.custom.common.Constants.addressModel;


public class AddressDetailsNewActivity extends BaseBackActivity implements View.OnClickListener , TextWatcher{

    SupportMapFragment mapView;
    GoogleMap map;
    Marker marker;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.txt_address) TextView txtAddress;
    @BindView(R.id.txt_header) TextView txtHeader;
    @BindView(R.id.btn_search_address) Button btnSearchAddress;
    @BindView(R.id.edt_landmark) EditText edtLandMark;
    @BindView(R.id.edt_instruction) EditText edtInstruction;
    @BindView(R.id.edt_name) EditText edtName;
    @BindView(R.id.edt_phone) EditText edtPhone;
    @BindView(R.id.ccp) CountryCodePicker countryCodePicker;
    @BindView(R.id.img_sender) ImageView imgSender;
    @BindView(R.id.img_receiver) ImageView imgReceiver;
    @BindView(R.id.lin_continue) LinearLayout linContinue;
    @BindView(R.id.btn_continue) Button btnContinue;


    String prefix = "+91";
    String pageType = "sender";

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_address_details_new);
        ButterKnife.bind(this);
        initBackButton(toolbar, getString(R.string.address_detail));
        PreferenceUtils.setOrderId(this, "0");
        initView();
        showAddressDetails("");
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == 100 ) {

            }else if(requestCode == 200){

            }else if(requestCode == 300){
                if(data.getStringExtra("address") != null){

                    addressModel.sourceLat = Double.valueOf(data.getExtras().getString("lat"));
                    addressModel.sourceLng = Double.valueOf(data.getExtras().getString("lng"));
                    addressModel.sourcePinCode = data.getStringExtra("pinCode");
                    addressModel.sourceState = data.getStringExtra("state");
                    addressModel.sourceArea = data.getStringExtra("area");
                    addressModel.sourceCity = data.getStringExtra("city");
                    Constants.addressModel.sourceAddress = data.getStringExtra("location");// + "," + data.getStringExtra("area") + "," + data.getStringExtra("city");



                    try{
                        if(data.getStringExtra("phone") != null && !data.getStringExtra("phone").isEmpty()){

                            addressModel.sourcePhonoe = data.getStringExtra("phone");
                            addressModel.sourceLandMark = data.getStringExtra("landmark");
                            addressModel.senderName = data.getStringExtra("name");
                            addressModel.sourceInstruction = data.getStringExtra("instruction");
                        }
                    }catch (Exception e){};
                    showAddressDetails("");
                    if(checkInput()){
                        //linContinue.setVisibility(View.VISIBLE);
                        imgReceiver.setVisibility(View.VISIBLE);

                    }
                }

            }else if(requestCode == 400){

                if( data.getStringExtra("address")  != null){

                    addressModel.desLat = Double.valueOf(data.getExtras().getString("lat"));
                    addressModel.desLng = Double.valueOf(data.getExtras().getString("lng"));

                    addressModel.desPinCode = data.getStringExtra("pinCode");
                    addressModel.desState = data.getStringExtra("state");
                    addressModel.desArea = data.getStringExtra("area");
                    addressModel.desCity = data.getStringExtra("city");

                    Constants.addressModel.desAddress = data.getStringExtra("location");// + "," + data.getStringExtra("area") + "," + data.getStringExtra("city");
                    try{
                        if(data.getStringExtra("phone") != null && !data.getStringExtra("phone").isEmpty()){
                            addressModel.desPhone = data.getStringExtra("phone");
                            addressModel.desLandMark = data.getStringExtra("landmark");
                            addressModel.desName = data.getStringExtra("name");
                            addressModel.desInstruction = data.getStringExtra("instruction");
                        }

                    }catch (Exception e){};
                    showAddressDetails("");
                    if(checkInput()){
                        //linContinue.setVisibility(View.VISIBLE);
                        imgReceiver.setVisibility(View.VISIBLE);

                    }
                }
            }
        }
    }


    private void initView(){
        imgSender.setVisibility(View.GONE);
        imgReceiver.setVisibility(View.GONE);
        btnSearchAddress.setOnClickListener(this);
        btnContinue.setOnClickListener(this);


        edtName.addTextChangedListener(this);
        edtInstruction.addTextChangedListener(this);
        edtPhone.addTextChangedListener(this);
        edtLandMark.addTextChangedListener(this);

        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                prefix = "+" +  selectedCountry.getPhoneCode();
            }
        });
        imgSender.setOnClickListener(this);
        imgReceiver.setOnClickListener(this);

        FragmentManager fmanager = getSupportFragmentManager();
        Fragment fragment = fmanager.findFragmentById(R.id.mapFragment);
        mapView = (SupportMapFragment) fragment;
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpMapView(googleMap);
            }
        });
    }
    private  void showAddressDetails(String currentAddress){
        AddressModel addressModel = Constants.addressModel;
        try {
            addressModel = Constants.addressModel.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if(pageType.equals("sender")){
            txtHeader.setText(getString(R.string.sender_details));
            if(addressModel.sourceAddress != null && !addressModel.sourceAddress.isEmpty()){
                txtAddress.setText(addressModel.sourceAddress);
            }else{
                txtAddress.setText(currentAddress);
            }
            if(addressModel.sourcePhonoe != null ){
                edtPhone.setText(addressModel.sourcePhonoe);
            }else{
                edtPhone.setHint(getString(R.string.sender_phone));
            }
            if(addressModel.sourceLandMark != null && !addressModel.sourceLandMark.isEmpty()){
                edtLandMark.setText(addressModel.sourceLandMark);
            }else{
                edtLandMark.setHint(getString(R.string.landmark));
            }

            if(addressModel.sourceInstruction != null && !addressModel.sourceInstruction.isEmpty()){
                edtInstruction.setText(addressModel.sourceInstruction);
            }else{
                edtInstruction.setHint(getString(R.string.instruction));
            }

            if(addressModel.senderName != null  && !addressModel.senderName.isEmpty()){
                edtName.setText(addressModel.senderName);
            }else{
                edtName.setHint(getString(R.string.sender_name));
            }

            if(addressModel.sourceLat != 0){
                LatLng userPosition = new LatLng(addressModel.sourceLat, addressModel.sourceLng);
                updateMarker(userPosition);
            }

        }else{
            txtHeader.setText(getString(R.string.receiver_details));
            if(addressModel.desAddress != null && !addressModel.desAddress.isEmpty()){
                txtAddress.setText(addressModel.desAddress);
            }else{
                txtAddress.setText(currentAddress);
            }
            if(addressModel.desPhone != null ){
                edtPhone.setText(addressModel.desPhone);
            }else{
                edtPhone.setHint(getString(R.string.receiver_phone));
            }

            if(addressModel.desLandMark != null && !addressModel.desLandMark.isEmpty()){
                edtLandMark.setText(addressModel.desLandMark);
            }else{
                edtLandMark.setHint(getString(R.string.landmark));
            }

            if(addressModel.desInstruction != null && !addressModel.desInstruction.isEmpty()){
                edtInstruction.setText(addressModel.desInstruction);
            }else{
                edtInstruction.setHint(getString(R.string.instruction));
            }

            if(addressModel.desName != null  && !addressModel.desName.isEmpty()){
                edtName.setText(addressModel.desName);
            }else{
                edtName.setHint(getString(R.string.receiver_name));
            }
            if(addressModel.desLat != 0){
                LatLng userPosition = new LatLng(addressModel.desLat, addressModel.desLng);
                updateMarker(userPosition);
            }


        }

    }

    private void setUpMapView(GoogleMap mmap) {
        this.map = mmap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        //map.setMyLocationEnabled(true);
        //Map Settings
        //  map.getUiSettings().setAllGesturesEnabled(true);
        //  map.getUiSettings().setMyLocationButtonEnabled(true);
        // map.getUiSettings().setCompassEnabled(true);
        //map.setInfoWindowAdapter(new CustomInfoWindowAdapter(1));
//        boolean success = map.setMapStyle(
//                MapStyleOptions.loadRawResourceStyle(
//                        this, R.raw.style_json));
//        if (!success) {
//            Log.e("TAG", "Style parsing failed.");
//        }

        Location lastKnownLocation = getLastKnownLocation();
        double latt = 35.89093;
        double lng = -106.326907;
        if (lastKnownLocation != null) {
            lng = lastKnownLocation.getLongitude();
            latt = lastKnownLocation.getLatitude();
        }


        LatLng userPosition = new LatLng(latt, lng);
        updateMarker(userPosition);
        if(mapView!=null){
            mapView.onResume();
        }

        txtAddress.setText("Waiting...");
        new GetLocationFromLatLng(this, userPosition).execute();

    }

    private void updateMarker(LatLng userPosition){

        if( map != null){
            marker = map.addMarker(new MarkerOptions().position(userPosition) //position
                    .title("")
                    .snippet("1")
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(this, R.drawable.ic_my_location_24px)))
            );

            MapsInitializer.initialize(this);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userPosition, 16);
            map.moveCamera(update);
            MapsInitializer.initialize(this);
        }

    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public void updateLocation(String location, String street,String area, String city, String state,  String pincode, LatLng ltlng){

        if(location.isEmpty() || street.isEmpty() || area.isEmpty() || city.isEmpty() || state.isEmpty() ){
            //mNameTextView.setText("Choose Correct Location, Can not get location info");
            //Toast.makeText(this, "Choose Correct Location, Can not get location info",Toast.LENGTH_SHORT).show();
        }else{

            txtAddress.setText(location);
            addressModel.sourceAddress = location;
            addressModel.sourceLat = ltlng.latitude;
            addressModel.sourceLng = ltlng.longitude;

            addressModel.desAddress = location;
            addressModel.desLat = ltlng.latitude;
            addressModel.desLng = ltlng.longitude;

        }
    }

    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search_address:
                if(pageType.equals("sender")){
                    if( Constants.orderHisModels.size() == 0){
                        getOrderHistory("source");
                    }else{
                        CommonDialog.showChooseAddress(AddressDetailsNewActivity.this, "source");
                    }
                }else{
                    if( Constants.orderHisModels.size() == 0){
                        getOrderHistory("destination");
                    }else{
                        CommonDialog.showChooseAddress(AddressDetailsNewActivity.this, "destination");
                    }
                }
                break;

            case R.id.txt_back:
            case R.id.img_sender:
                if(pageType.equals("receiver")){
                    addressModel.desName = edtName.getText().toString();
                    addressModel.desPhone= edtPhone.getText().toString();
                    addressModel.desLandMark = edtLandMark.getText().toString();
                    addressModel.desInstruction = edtInstruction.getText().toString();
                }
                initInput();
                pageType = "sender";
                showAddressDetails("");
                break;
            case R.id.txt_next:
            case R.id.img_receiver:

                if(pageType.equals("sender")){

                    addressModel.senderName = edtName.getText().toString();
                    addressModel.sourcePhonoe = edtPhone.getText().toString();
                    addressModel.sourceLandMark = edtLandMark.getText().toString();
                    addressModel.sourceInstruction = edtInstruction.getText().toString();

                    initInput();
                    pageType = "receiver";
                    imgSender.setVisibility(View.VISIBLE);
                    imgReceiver.setVisibility(View.GONE);

                    showAddressDetails("");

                }else{
                    if(checkInput()){
                        getDistanceWebService();
                    }
                }

                break;
            case R.id.btn_continue:
                //if(ValidationHelper.isPostCode(mPickPinCode) && ValidationHelper.isPostCode(mDesPinCode)){//
                if(checkInput()){
                    getDistanceWebService();
                }

//                }else{
//                    Toast.makeText(AddressDetailsActivity.this,"Invalid Post Code", Toast.LENGTH_SHORT).show();
//                }
                break;
        }
    }

    private void initInput(){

        edtLandMark.setText("");
        edtInstruction.setText("");
        edtName.setText("");
        edtPhone.setText("");

        edtLandMark.setHint(getString(R.string.landmark));
        edtInstruction.setHint(getString(R.string.instruction));
        if(pageType.equals("sender")){
            edtName.setHint(getString(R.string.sender_name));
            edtPhone.setHint(getString(R.string.sender_phone));
        }else{
            edtName.setHint(getString(R.string.receiver_name));
            edtPhone.setHint(getString(R.string.receiver_phone));
        }
    }



    private void getOrderHistory(final String type) {
        RequestParams params = new RequestParams();
        params.put("user_id", PreferenceUtils.getUserId(AddressDetailsNewActivity.this));

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
                                    CommonDialog.showChooseAddress(AddressDetailsNewActivity.this, type);
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

                                CommonDialog.showChooseAddress(AddressDetailsNewActivity.this, type);

                            }else if(response.getString("result").equals("400")){
                                Toast.makeText(AddressDetailsNewActivity.this , getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String sourceLandMark = "", sourceInstruction = "", senderName = "", sourcePhonoe = "";
        String desLandMark  = "", desInstruction  = "", desName  = "", desPhone  = "";
        if(pageType.equals("sender")){
            sourceLandMark = edtLandMark.getText().toString();
            sourceInstruction = edtInstruction.getText().toString();
            senderName = edtName.getText().toString();
            sourcePhonoe = edtPhone.getText().toString();
        }else{
            desLandMark = edtLandMark.getText().toString();
            desInstruction = edtInstruction.getText().toString();
            desName = edtName.getText().toString();
            desPhone = edtPhone.getText().toString();
        }

        if(!sourceLandMark.isEmpty() && !senderName.isEmpty() && !sourcePhonoe.isEmpty()){
            imgReceiver.setVisibility(View.VISIBLE);
            imgSender.setVisibility(View.GONE);
            if(checkInput()){
                //linContinue.setVisibility(View.VISIBLE);
            }
        }

        if(!desLandMark.isEmpty()  && !desName.isEmpty() && !desPhone.isEmpty()){
            if(checkInput()){
                //linContinue.setVisibility(View.VISIBLE);
                imgReceiver.setVisibility(View.VISIBLE);
            }
        }
    }


    private boolean checkInput(){
        if(pageType.equals("sender")){
            addressModel.sourceLandMark = edtLandMark.getText().toString();
            addressModel.sourceInstruction = edtInstruction.getText().toString();
            addressModel.senderName = edtName.getText().toString();
            addressModel.sourcePhonoe = edtPhone.getText().toString();
        }else{
            addressModel.desLandMark = edtLandMark.getText().toString();
            addressModel.desInstruction = edtInstruction.getText().toString();
            addressModel.desName = edtName.getText().toString();
            addressModel.desPhone = edtPhone.getText().toString();
        }

        try{
            if(!addressModel.sourceLandMark.isEmpty() &&  !addressModel.senderName.isEmpty() && !addressModel.sourcePhonoe.isEmpty()){
                if(!addressModel.desLandMark.isEmpty() &&  !addressModel.desName.isEmpty() && !addressModel.desPhone.isEmpty()){
                    return true;
                }
            }
        }catch (Exception e){
            return false;
        }
        return false;
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
                        Toast.makeText(AddressDetailsNewActivity.this, "Invalid Pin Code ", Toast.LENGTH_SHORT).show();
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
                                        Constants.priceType.distance = Double.valueOf(response.getString("distance")) / 1000;
                                    }catch (Exception e){};
                                }

                                if(Constants.priceType.expeditied_price != null && !Constants.priceType.expeditied_price.isEmpty()){
                                    Intent dateIntent = new Intent(AddressDetailsNewActivity.this, DateTimeActivity.class);
                                    startActivity(dateIntent);
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



}
