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
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;
import com.hurry.custom.R;
import com.hurry.custom.common.CommonDialog;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.common.utils.GpsUtil;
import com.hurry.custom.controller.GetLocationFromLatLng;
import com.hurry.custom.controller.GetLocationFromPlaceId;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.AddressHisModel;
import com.hurry.custom.model.AddressModel;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.model.OrderHisModel;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.adapter.AddressAdapter;
import com.hurry.custom.view.adapter.LocationNameAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.ViewHolder;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static com.hurry.custom.common.Constants.addressModel;
import static com.hurry.custom.controller.MyAsyncHttpClient.LOG_TAG;

public class AddressDetailsNewFragment extends Fragment implements View.OnClickListener , TextWatcher , LocationNameAdapter.ContactsAdapterListener {
    Context mContext;

    SupportMapFragment mapView;
    GoogleMap map;
    Marker marker;
    private PlacesClient mGoogleApiClient;

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
    @BindView(R.id.txt_back) TextView txtBack;
    @BindView(R.id.txt_next) TextView txtNext;
    @BindView(R.id.lin_container) LinearLayout linContainer;
    @BindView(R.id.img_marker) ImageView markerImage;
    @BindView(R.id.rl_map_container) RelativeLayout rlMapContainer;

    String prefix = "+91";
    String pageType = "sender";
    int CAMERA_UPDATE = 16;
    boolean first_load = true;
    boolean pickup_auto_complete = false;
    LatLng center;
    public static boolean isCalling = false;

    String page = "";
    private AsyncTask<Void, Void, Void> Mytask;

    public AddressDetailsNewFragment(String page){
        this.page = page;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = (View) inflater.inflate(
                R.layout.activity_address_details_new, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        DeviceUtil.hideSoftKeyboard(getActivity());

        Places.initialize(mContext.getApplicationContext(), getString(R.string.gecode_api_key2));
        mGoogleApiClient = Places.createClient(mContext);
        PreferenceUtils.setOrderId(mContext, "0");

        if(page.equals("datetime")){
            pageType = "receiver";
        }

        initView();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void initView(){

        hideReceiver();
        btnSearchAddress.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        txtNext.setOnClickListener(this);
        txtNext.setVisibility(View.GONE);
        txtBack.setOnClickListener(this);

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

        FragmentManager fmanager = getChildFragmentManager(); //((HomeActivity)mContext).
        Fragment fragment = fmanager.findFragmentById(R.id.mapFragment);
        mapView = (SupportMapFragment) fragment;

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpMapView(googleMap);

            }
        });


    }
    public  void showAddressDetails(String currentAddress){
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
            if(addressModel.sourcePhonoe != null  && addressModel.sourcePhonoe.length() >= 10 ){
                edtPhone.setText(addressModel.sourcePhonoe.substring(addressModel.sourcePhonoe.length() - 10));
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

            if(addressModel.sourceLat != 0){
                pickupLocation = new LatLng(addressModel.sourceLat, addressModel.sourceLng);
                pickup_auto_complete = true;
                isCorrectLocation = true;
                updateMarker(pickupLocation);

                if(addressModel.sourceAddress != null && !addressModel.sourceAddress.isEmpty()){
                    if( Constants.DELIVERY_STATUS == Constants.SAME_CITY && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                            || Constants.DELIVERY_STATUS == Constants.OUT_STATION && pageType.equals("sender") && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                            || Constants.DELIVERY_STATUS == Constants.OUT_STATION && pageType.equals("receiver") && !GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds) && addressModel.sourceAddress.contains("India")
                            || Constants.DELIVERY_STATUS == Constants.INTERNATIONAL && pageType.equals("sender") && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                            || Constants.DELIVERY_STATUS == Constants.INTERNATIONAL && pageType.equals("receiver") && !addressModel.sourceAddress.contains("India")){

                    }else{
                        isCorrectLocation = false;
                        showToast();
                    }
                }
            }

            if(addressModel.senderName != null  && !addressModel.senderName.isEmpty()){
                edtName.setText(addressModel.senderName);
            }else{
                edtName.setHint(getString(R.string.sender_name));
            }

        }else{
            txtHeader.setText(getString(R.string.receiver_details));
            if(addressModel.desAddress != null && !addressModel.desAddress.isEmpty()){
                txtAddress.setText(addressModel.desAddress);
            }else{
                txtAddress.setText(currentAddress);
            }
            if(addressModel.desPhone != null &&addressModel.desPhone.length() >= 10 ){
                edtPhone.setText(addressModel.desPhone.substring(addressModel.desPhone.length() - 10));
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


            if(addressModel.desLat != 0){
                pickupLocation = new LatLng(addressModel.desLat, addressModel.desLng);
                if(addressModel.desAddress != null && !addressModel.desAddress.isEmpty()){
                    pickup_auto_complete = true;
                }else{
                    pickup_auto_complete = false;
                }
                isCorrectLocation = true;
                updateMarker(pickupLocation);

                if(addressModel.desAddress != null && !addressModel.desAddress.isEmpty()){
                    if( Constants.DELIVERY_STATUS == Constants.SAME_CITY && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                            || Constants.DELIVERY_STATUS == Constants.OUT_STATION && pageType.equals("sender") && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                            || Constants.DELIVERY_STATUS == Constants.OUT_STATION && pageType.equals("receiver") && !GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds) && addressModel.desAddress.contains("India")
                            || Constants.DELIVERY_STATUS == Constants.INTERNATIONAL && pageType.equals("sender") && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                            || Constants.DELIVERY_STATUS == Constants.INTERNATIONAL && pageType.equals("receiver") && !addressModel.desAddress.contains("India")){
                    }else{
                        isCorrectLocation = false;
                        showToast();
                    }
                }

            }

            if(addressModel.desName != null  && !addressModel.desName.isEmpty()){
                edtName.setText(addressModel.desName);
            }else{
                edtName.setHint(getString(R.string.receiver_name));
            }

        }

    }



    int Type = 0;

    private void setUpMapView(GoogleMap mmap) {
        this.map = mmap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);


        Location lastKnownLocation = getLastKnownLocation();
        double latt = 35.89093;
        double lng = -106.326907;
        if (lastKnownLocation != null) {
            lng = lastKnownLocation.getLongitude();
            latt = lastKnownLocation.getLatitude();
        }

        LatLng userPosition = new LatLng(latt, lng);
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (mGoogleApiClient != null) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, CAMERA_UPDATE));
                }
                return false;
            }
        });


        if(mapView!=null){
            mapView.onResume();
        }
        txtAddress.setText("Locating...");
        if(!pageType.equals("receiver")){

            if(addressModel.sourceLat != 0 && addressModel.sourceLng != 0 && addressModel.sourceAddress != null) {
                pickup_auto_complete = true;
                pickupLocation = new LatLng(addressModel.sourceLat, addressModel.sourceLng);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(pickupLocation, CAMERA_UPDATE);
                map.moveCamera(update);
                MapsInitializer.initialize(mContext);

            }else if(isPointInPolygon(userPosition, Constants.cityBounds) && addressModel.sourceLat == 0){
                Type = 2;
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userPosition, CAMERA_UPDATE);
                map.moveCamera(update);
                MapsInitializer.initialize(mContext);

            }else{

                if(Constants.cityBounds.size() > 0 && addressModel.sourceLat == 0){
                    Type = 3;
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (int k = 0 ; k < Constants.cityBounds.size(); k++) {
                        builder.include(Constants.cityBounds.get(k));
                    }
                    LatLngBounds bounds = builder.build();
                    first_load = true;
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(
                            bounds, 0));
                }else{
                    LatLng latLng = new LatLng(addressModel.sourceLat, addressModel.sourceLng);
                    updateMarker(latLng);
                }
            }
        }else{
            pickupLocation = new LatLng(addressModel.desLat, addressModel.desLng);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(pickupLocation, CAMERA_UPDATE);
            map.moveCamera(update);
            MapsInitializer.initialize(mContext);
        }

        setUpMap(userPosition);
        showAddressDetails("");
    }

    private void setUpMap(LatLng userPosition) {
        try {
            marker = map.addMarker(new MarkerOptions().position(userPosition) //position
                    .title("")
                    .snippet("1")
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(mContext, R.drawable.ic_my_location_24px)))
            );

            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                    if(first_load){
                        if(Type == 2){
                            if(addressModel.desLat == 0 && addressModel.desLng == 0){
                                addressModel.desLat = userPosition.latitude;
                                addressModel.desLng = userPosition.longitude;
                            }
                        }else if(Type == 3){
                            if(addressModel.desLat == 0 && addressModel.desLng == 0){
                                center = map.getCameraPosition().target;
                                addressModel.desLat = center.latitude;
                                addressModel.desLng = center.longitude;
                            }
                        }
                    }

                }
            });


            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition arg0) {
                    // TODO Auto-generated method stub
                    center = map.getCameraPosition().target;
                    map.clear();
                    markerImage.setVisibility(View.VISIBLE);

                    double lan = center.latitude;
                    double lng = center.longitude;
                    LatLng latLng = new LatLng(Double.valueOf(lan), Double.valueOf(lng));
                    pickupLocation = latLng;
                    if(first_load){
                        pickupLocation = latLng;
                        first_load = false;
//                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_UPDATE);
//                        map.moveCamera(update);
                        if(Type == 2){
                            if(addressModel.desLat == 0 && addressModel.desLng == 0){
                                addressModel.desLat = userPosition.latitude;
                                addressModel.desLng = userPosition.longitude;
                            }
                        }else if(Type == 3){
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_UPDATE);
                            map.moveCamera(update);
                            if(addressModel.desLat == 0 && addressModel.desLng == 0){
                                center = map.getCameraPosition().target;
                                addressModel.desLat = center.latitude;
                                addressModel.desLng = center.longitude;
                            }
                        }
                    }


                    if(!pickup_auto_complete && pickupLocation != null ){
                        try {
                            txtAddress.setText("Locating ...");
                            if(Mytask != null){
                                Mytask.cancel(true);
                            }
                            Mytask = new GetLocationFromLatLng(mContext, pickupLocation).execute();

                        } catch (Exception e) {
                        }
                    }else{
                        pickup_auto_complete = false;
                    }
                }
            });

            markerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPointInPolygon(LatLng tap, ArrayList<LatLng> vertices) {
        int intersectCount = 0;
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(tap, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }
        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }
    private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }
        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    private void updateMarker(LatLng userPosition){
        if( map != null){
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userPosition, CAMERA_UPDATE);
            map.moveCamera(update);
            MapsInitializer.initialize(mContext);
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

    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) mContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
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

    public void hideNotification(){
        if(snackbar != null && snackbar.isShown())
            snackbar.dismiss();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search_address:
                if(pageType.equals("sender")){
                    if( Constants.orderHisModels.size() == 0){
                        getOrderHistory("source");
                    }else{
                        CommonDialog.showChooseAddress(mContext, "source");
                    }
                }else{
                    if( Constants.orderHisModels.size() == 0){
                        getOrderHistory("destination");
                    }else{
                        CommonDialog.showChooseAddress(mContext, "destination");
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
                    initInput();
                    pageType = "sender";
                    showAddressDetails("");
                }else{
                    if(Constants.ORDER_TYPE == Constants.CAMERA_OPTION){
                        ((HomeActivity)mContext).updateFragment(HomeActivity.CAMERA_ORDER, "");
                    }else{
                        ((HomeActivity)mContext).updateFragment(HomeActivity.ITEM_ORDER, "");
                    }
                }
                hideNotification();

                break;

            case R.id.txt_next:
            case R.id.img_receiver:
                if(enable){
                    if(pageType.equals("sender")){
                        addressModel.senderName = edtName.getText().toString();
                        addressModel.sourcePhonoe = edtPhone.getText().toString();
                        addressModel.sourceLandMark = edtLandMark.getText().toString();
                        addressModel.sourceInstruction = edtInstruction.getText().toString();
                        initInput();
                        pageType = "receiver";
                        hideReceiver();
                        showAddressDetails("");

                    }else{
                        if(checkInput()){
                            if(Constants.DELIVERY_STATUS == Constants.SAME_CITY){
                                getDistanceWebService();
                            }else if(Constants.DELIVERY_STATUS == Constants.OUT_STATION){
                                int price1 = 0; int price2 = 0; int price3 = 0;
                                price1 = Constants.getRate(Constants.getTotalWeight(), 1);
                                price2 = Constants.getRate(Constants.getTotalWeight(), 2);
                                price3 = Constants.getRate(Constants.getTotalWeight(), 3);

                                if(Constants.getTotalWeight() <= 10){
                                    price1 += 299;
                                    price2 += 299;
                                    price3 += 299;
                                }else if(Constants.getTotalWeight() <= 20){
                                    price1 += 399;
                                    price2 += 299;
                                    price3 += 299;
                                }else if(Constants.getTotalWeight() <= 30){
                                    price1 += 499;
                                    price2 += 299;
                                    price3 += 299;
                                }else if(Constants.getTotalWeight() <= 50){
                                    price1 += 799;
                                    price2 += 299;
                                    price3 += 299;
                                }else if(Constants.getTotalWeight() <= 70){
                                    price1 += 999;
                                    price2 += 299;
                                    price3 += 299;
                                }

                                Constants.priceType.expeditied_price = String.valueOf(price1);
                                Constants.priceType.express_price = String.valueOf(price2);
                                Constants.priceType.economy_price = String.valueOf(price3);
                                //Constants.priceType.distance = "45345";
                                Constants.MAP_HEIGHT = rlMapContainer.getHeight();
                                ((HomeActivity)mContext).updateFragment(HomeActivity.DATE_TIME, "");

                            }else if(Constants.DELIVERY_STATUS == Constants.INTERNATIONAL){

                                boolean isFood = true;
                                if(Constants.ORDER_TYPE == Constants.ITEM_OPTION){
                                    if(Constants.itemOrderModel.itemModels.get(0).title.contains("Food")){
                                        isFood = true;
                                    }
                                }else{
                                    isFood = false;
                                }

                                int price = 0;
                                if(addressModel.desAddress.contains("USA")){
                                    if(Constants.getTotalWeight() <= 30){
                                        price  = Constants.getInternational(Constants.getTotalWeight(), 1, isFood);
                                    }else{
                                        price = Constants.getTotalWeight() * 490;
                                    }

                                }else if(addressModel.desAddress.contains("Canada")){
                                    if(Constants.getTotalWeight() <= 30 ){
                                        price = Constants.getInternational(Constants.getTotalWeight(), 2, isFood);
                                    }else{
                                        price = Constants.getTotalWeight() * 560;
                                    }

                                }else if(addressModel.desAddress.contains("Australia")){
                                    if(Constants.getTotalWeight() <= 30 ){
                                        price = Constants.getInternational(Constants.getTotalWeight(), 3, isFood);
                                    }else{
                                        price = Constants.getTotalWeight() * 500;
                                    }
                                }else if(addressModel.desAddress.contains("UK")){
                                    if(Constants.getTotalWeight() <= 30 ){
                                        price = Constants.getInternational(Constants.getTotalWeight(), 4, isFood);
                                    }else{
                                        price = Constants.getTotalWeight() * 490;
                                    }
                                }else{
                                    return;
                                }

                                if(Constants.getTotalWeight() <= 10){
                                    price += 299;
                                }else if(Constants.getTotalWeight() <= 20){
                                    price += 699;
                                }else if(Constants.getTotalWeight() <= 30){
                                    price += 999;
                                }else if(Constants.getTotalWeight() <= 50){
                                    price += 1499;
                                }else if(Constants.getTotalWeight() <= 70){
                                    price += 1999;
                                }

                                Constants.priceType.expeditied_price = String.valueOf(price);
                                Constants.priceType.express_price = "";
                                Constants.priceType.economy_price = "";
                                Constants.priceType.distance = 0;
                                Constants.MAP_HEIGHT = rlMapContainer.getHeight();
                                ((HomeActivity)mContext).updateFragment(HomeActivity.DATE_TIME, "");

                            }

                        }
                    }
                }

                break;
            case R.id.btn_continue:

                if(checkInput()){
                    getDistanceWebService();
                }
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
        params.put("user_id", PreferenceUtils.getUserId(mContext));

        WebClient.post(Constants.BASE_URL_ORDER + "get_orders_his", params,
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

                                String result = response.getString("result");
                                if(result.equals("400")){
                                    CommonDialog.showChooseAddress(mContext, type);
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

                                CommonDialog.showChooseAddress(mContext, type);

                            }else if(response.getString("result").equals("400")){
                                Toast.makeText(mContext , getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
        checkValidate();
    }

    private void checkValidate(){
        String sourceLandMark = "", sourceInstruction = "", senderName = "", sourcePhonoe = "";
        String desLandMark  = "", desInstruction  = "", desName  = "", desPhone  = "";
        if(pageType.equals("sender")){
            sourceLandMark = edtLandMark.getText().toString();
            sourceInstruction = edtInstruction.getText().toString();
            senderName = edtName.getText().toString();
            sourcePhonoe = edtPhone.getText().toString();

            if(!sourceLandMark.isEmpty() && !senderName.isEmpty() && !sourcePhonoe.isEmpty()){
                if(checkInput()){
                    if(sourcePhonoe.length() == 10){
                        showNext();
                    }else{
                        hideReceiver();
                    }
                 }
            }else{
                hideReceiver();
            }

        }else{
            desLandMark = edtLandMark.getText().toString();
            desInstruction = edtInstruction.getText().toString();
            desName = edtName.getText().toString();
            desPhone = edtPhone.getText().toString();

            if(!desLandMark.isEmpty()  && !desName.isEmpty() && !desPhone.isEmpty()){
                if(checkInput()){
                    //linContinue.setVisibility(View.VISIBLE);
                    if(desPhone.length() == 10 && !txtAddress.getText().toString().isEmpty()){
                        showNext();
                    }else{
                        hideReceiver();
                    }
                }
            }else{
                hideReceiver();
            }
        }
    }



    public void hideReceiver(){
        imgReceiver.setVisibility(View.VISIBLE);
        imgReceiver.setImageResource(R.mipmap.ic_right_arrow_disabled);
        txtNext.setVisibility(View.GONE);
        enable = false;
        imgReceiver.clearAnimation();
    }

    boolean enable = false;
    boolean isCorrectLocation = false;

    public void showNext(){
        if(isCorrectLocation){
            imgReceiver.setVisibility(View.VISIBLE);
            imgReceiver.setImageResource(R.mipmap.ic_right_arrow);
            txtNext.setVisibility(View.GONE);
            enable = true;

            Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
            animation.setDuration(1000); //1 second duration for each animation cycle
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
            animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
            imgReceiver.startAnimation(animation); //to start animation
        }else{
            hideReceiver();

        }


    }

    public boolean checkInput(){

        if(pageType.equals("sender")){
            addressModel.sourceLandMark = edtLandMark.getText().toString();
            addressModel.sourceInstruction = edtInstruction.getText().toString();
            addressModel.senderName = edtName.getText().toString();
            addressModel.sourcePhonoe = countryCodePicker.getSelectedCountryCodeWithPlus() +   edtPhone.getText().toString();
            if(!addressModel.sourceLandMark.isEmpty() &&  !addressModel.senderName.isEmpty() && !addressModel.sourcePhonoe.isEmpty()){
                return true;
            }

        }else if(pageType.equals("receiver")){
            addressModel.desLandMark = edtLandMark.getText().toString();
            addressModel.desInstruction = edtInstruction.getText().toString();
            addressModel.desName = edtName.getText().toString();
            addressModel.desPhone =  countryCodePicker.getSelectedCountryCodeWithPlus() +  edtPhone.getText().toString();
            if(!addressModel.desLandMark.isEmpty() &&  !addressModel.desName.isEmpty() && !addressModel.desPhone.isEmpty()){
                return true;
            }
        }

        return false;
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }


    private void getDistanceWebService() {
        Constants.MAP_HEIGHT = rlMapContainer.getHeight();
        RequestParams params = new RequestParams();
        params.put("org", String.valueOf(addressModel.sourceLat) + ","  +String.valueOf(addressModel.sourceLng));
        params.put("des", String.valueOf(addressModel.desLat) + ","  +String.valueOf(addressModel.desLng));
        params.put("weight", String.valueOf(Constants.getTotalWeight()));
        params.put("user_id", PreferenceUtils.getUserId(mContext));

        WebClient.get(Constants.BASE_URL + "getDistance" , params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        ((HomeActivity)mContext).showProgressDialog();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);

                        Constants.priceType.expeditied_price = "354";
                        Constants.priceType.express_price = "567";
                        Constants.priceType.economy_price = "780";
                        Constants.priceType.distance = 0;

                        Constants.MAP_HEIGHT = rlMapContainer.getHeight();
                        ((HomeActivity)mContext).updateFragment(HomeActivity.DATE_TIME, "");

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        getDistanceWebService();
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
                                    }catch (Exception e){

                                    };

                                    if(Constants.priceType.distance  == 0){
                                        Constants.priceType.distance = distance(addressModel.sourceLat, addressModel.desLat, addressModel.sourceLng, addressModel.desLng, 0,0) / 1000;
                                    }

                                }

                                if(Constants.priceType.expeditied_price != null && !Constants.priceType.expeditied_price.isEmpty()){

                                    Constants.MAP_HEIGHT = rlMapContainer.getHeight();
                                    ((HomeActivity)mContext).updateFragment(HomeActivity.DATE_TIME, "");
                                }
                                ((HomeActivity)mContext).hideProgressDialog();
                            }
                        }catch (Exception e){};
                    };
                    public void onFinish() {
                        ((HomeActivity)mContext).hideProgressDialog();
                    }
                    ;
                });
    }

    ArrayList<AddressHisModel> allHis = new ArrayList<>();
    DialogPlus addressDialog;
    LatLng pickupLocation;

    public void dismissAddressDialog(AddressHisModel model){

        pickupLocation = new LatLng(model.lat, model.lng);

        if( Constants.DELIVERY_STATUS == Constants.SAME_CITY && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                || Constants.DELIVERY_STATUS == Constants.OUT_STATION && pageType.equals("sender") && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                || Constants.DELIVERY_STATUS == Constants.OUT_STATION && pageType.equals("receiver") && !GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds) && model.address.contains("India")
                || Constants.DELIVERY_STATUS == Constants.INTERNATIONAL && pageType.equals("sender") && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                || Constants.DELIVERY_STATUS == Constants.INTERNATIONAL && pageType.equals("receiver") && !model.address.contains("India")){

            isCorrectLocation = true;
            hideNotification();


            edtPhone.setText(model.phone.substring(model.phone.length() - 10));
            if(model.phone.replace(edtPhone.getText().toString(), "").trim().isEmpty()){
                countryCodePicker.setCountryForPhoneCode(91);
            }else{
                countryCodePicker.setCountryForPhoneCode(Integer.valueOf(model.phone.replace(edtPhone.getText().toString(), "").trim()));
            }

            txtAddress.setText(model.address);
            edtLandMark.setText(model.landmark);
            edtInstruction.setText(model.instruction);
            edtName.setText(model.name);

            if(pageType.equals("sender")){
                addressModel.sourceLat = model.lat;
                addressModel.sourceLng = model.lng;
                addressModel.sourceAddress = model.address;
            }else if(pageType.equals("receiver")){
                addressModel.desLat = model.lat;
                addressModel.desLng = model.lng;
                addressModel.desAddress = model.address;
            }
            updateMarker(pickupLocation);
            addressDialog.dismiss();

        }else{
            isCorrectLocation = false;
            showToast();
            addressDialog.dismiss();
        }

        checkValidate();

    }


    public void showAutoCompleteAddress(String type){

        ImageView imgClose;
        AutoCompleteTextView mAutocompleteTextView;
        LocationNameAdapter contactsAdapter;
        ArrayList<LocationNameAdapter.PlaceAutocomplete> contactList;

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_pickup, null);
        DialogPlusBuilder builder = DialogPlus.newDialog(mContext);
        addressDialog = builder.setContentHolder(new ViewHolder(view))
                .setExpanded(true, linContainer.getHeight() + (int)DeviceUtil.dipToPixels(mContext, 53))  // This will enable the expand feature, (similar to android L share dialog)
                .setGravity(Gravity.BOTTOM)
                .create();
        addressDialog.show();

        imgClose = (ImageView)view.findViewById(R.id.img_close);
        mAutocompleteTextView = (AutoCompleteTextView) view.findViewById(R.id
                .autoCompleteTextView);
        contactList = new ArrayList<LocationNameAdapter.PlaceAutocomplete>();
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.street_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        contactsAdapter = new LocationNameAdapter(mContext,
                contactList, this, recyclerView, mGoogleApiClient, pageType);
        recyclerView.setAdapter(contactsAdapter);

        mAutocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                contactsAdapter.getFilter().filter(mAutocompleteTextView.getText().toString());
                imgClose.setVisibility(View.VISIBLE);
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteTextView.setText("");
                imgClose.setVisibility(View.GONE);
            }
        });



    }


    public void showAddressBook(String type){
        hideNotification();

        AutoCompleteTextView mAutocompleteTextView;
        RecyclerView recyclerView;
        AddressAdapter contactsAdapter;

        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_profile, null);
        DialogPlusBuilder builder = DialogPlus.newDialog(mContext);
        addressDialog = builder.setContentHolder(new ViewHolder(view))
                .setExpanded(true, linContainer.getHeight() + (int)DeviceUtil.dipToPixels(mContext, 53))  // This will enable the expand feature, (similar to android L share dialog)
                .setGravity(Gravity.BOTTOM)
                .create();
        addressDialog.show();

        mAutocompleteTextView = (AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        allHis.clear();
        for(int k = 0 ; k <  Constants.orderHisModels.size(); k++){
            OrderHisModel model = Constants.orderHisModels.get(k);
            if(!containsSource(model) ){

                if(!pageType.equals("sender") && addressModel.senderName.equals(model.addressModel.senderName)  || !pageType.equals("sender") && addressModel.sourceAddress.equals(model.addressModel.sourceAddress)  ||  !pageType.equals("sender") && addressModel.sourceLat == model.addressModel.sourceLat && addressModel.sourceLng == model.addressModel.sourceLng ){

                }else{
                    AddressHisModel addressHisModel = new AddressHisModel();
                    addressHisModel.address = model.addressModel.sourceAddress;
                    addressHisModel.area = model.addressModel.sourceArea;
                    addressHisModel.city = model.addressModel.sourceCity;
                    addressHisModel.landmark = model.addressModel.sourceLandMark;
                    addressHisModel.pincode = model.addressModel.sourcePinCode;
                    addressHisModel.phone = model.addressModel.sourcePhonoe;
                    addressHisModel.lat = model.addressModel.sourceLat;
                    addressHisModel.lng = model.addressModel.sourceLng;
                    addressHisModel.state = model.addressModel.sourceState;
                    addressHisModel.name = model.addressModel.senderName;
                    addressHisModel.instruction = model.addressModel.sourceInstruction;
                    allHis.add(addressHisModel);
                }
            }

            if(!containsDes(model)){
                if(!pageType.equals("sender") && addressModel.senderName.equals(model.addressModel.desName)  || !pageType.equals("sender") && addressModel.sourceAddress.equals(model.addressModel.desAddress)  ||  !pageType.equals("sender") && addressModel.sourceLat == model.addressModel.desLat && addressModel.sourceLng == model.addressModel.desLng ){

                }else{
                    AddressHisModel addressHisModel = new AddressHisModel();
                    addressHisModel.address = model.addressModel.desAddress;
                    addressHisModel.area = model.addressModel.desArea;
                    addressHisModel.city = model.addressModel.desCity;
                    addressHisModel.landmark = model.addressModel.desLandMark;
                    addressHisModel.pincode = model.addressModel.desPinCode;
                    addressHisModel.phone = model.addressModel.desPhone;
                    addressHisModel.lat = model.addressModel.desLat;
                    addressHisModel.lng = model.addressModel.desLng;
                    addressHisModel.state = model.addressModel.desState;
                    addressHisModel.name = model.addressModel.desName;
                    addressHisModel.instruction = model.addressModel.desInstruction;
                    allHis.add(addressHisModel);
                }

            }
        }
        contactsAdapter = new AddressAdapter(mContext,
                allHis, recyclerView);
        recyclerView.setAdapter(contactsAdapter);
        mAutocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                contactsAdapter.getFilter().filter(mAutocompleteTextView.getText().toString());
            }
        });
        view.findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    private boolean containsSource(OrderHisModel model){
        for(int k = 0; k < allHis.size(); k++){
            if( allHis.get(k).address.trim().equals(model.addressModel.sourceAddress.trim())  && allHis.get(k).address.trim().length() == model.addressModel.sourceAddress.trim().length()){

                Log.d(allHis.get(k).address.trim(),model.addressModel.sourceAddress.trim()) ;
                //(allHis.get(k).lat == model.addressModel.sourceLat  && allHis.get(k).lng == model.addressModel.sourceLng )  ||
                return true;
            }
        }
        if( Constants.addressModel.sourceLat == model.addressModel.sourceLat  && Constants.addressModel.sourceLng  == model.addressModel.sourceLng ){
            return true;
        }
        if( Constants.addressModel.desLat == model.addressModel.sourceLat  && Constants.addressModel.desLng == model.addressModel.sourceLng ){
            return true;
        }
        return false;
    }



    private boolean containsDes(OrderHisModel model){
        for(int k = 0; k < allHis.size(); k++){

            if(  allHis.get(k).address.trim().equals(model.addressModel.desAddress.trim())  && allHis.get(k).address.trim().length() ==  model.addressModel.desAddress.trim().length()){
                Log.d(allHis.get(k).address.trim(),model.addressModel.desAddress.trim()) ;
                //(allHis.get(k).lat == model.addressModel.desLat  && allHis.get(k).lng == model.addressModel.desLng)  ||
                return true;
            }
        }

        if( Constants.addressModel.sourceLat == model.addressModel.desLat  && Constants.addressModel.sourceLng  == model.addressModel.desLng ){
            return true;
        }
        if( Constants.addressModel.desLat == model.addressModel.desLat  && Constants.addressModel.desLng == model.addressModel.desLng ){
            return true;
        }
        return false;
    }


    @Override
    public void onContactSelected(LocationNameAdapter.PlaceAutocomplete contact) {
        final LocationNameAdapter.PlaceAutocomplete item = contact;
        final String placeId = String.valueOf(item.placeId);
        String selectedAddress = String.valueOf(item.description);
        Log.i(LOG_TAG, "Selected: " + item.description);

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS, Place.Field.PLUS_CODE);
        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        mGoogleApiClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            //Log.i(TAG, "Place found: " + place.getName());
            if(pageType.equals("sender")){
                addressModel.sourceLat = place.getLatLng().latitude;
                addressModel.sourceLng = place.getLatLng().longitude;
            }else if(pageType.equals("receiver")){
                addressModel.desLat = place.getLatLng().latitude;
                addressModel.desLng = place.getLatLng().longitude;
            }

            //txtAddress.setText(place.getAddress() + "");
            txtAddress.setText("Locating ...");


            AddressComponents temp = place.getAddressComponents();
            PlusCode temp1 = place.getPlusCode();


            DeviceUtil.hideSoftKeyboard(getActivity());
            pickupLocation = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            pickup_auto_complete = true;
            updateMarker(pickupLocation);
            String address = place.getAddress();

            if(Constants.MODE != Constants.CORPERATION){
                if( Constants.DELIVERY_STATUS == Constants.SAME_CITY && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                        || Constants.DELIVERY_STATUS == Constants.OUT_STATION && pageType.equals("sender") && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                        || Constants.DELIVERY_STATUS == Constants.OUT_STATION && pageType.equals("receiver") && !GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds) && address.contains("India")
                        || Constants.DELIVERY_STATUS == Constants.INTERNATIONAL && pageType.equals("sender") && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                        || Constants.DELIVERY_STATUS == Constants.INTERNATIONAL && pageType.equals("receiver") && !address.contains("India")){

                    new GetLocationFromPlaceId(mContext, place.getId(), selectedAddress, pickupLocation).execute();
                    DeviceUtil.hideSoftKeyboard(getActivity());
                    isCorrectLocation = true;
                    hideNotification();


                }else{
                    hideReceiver();
                    isCorrectLocation = false;
                    showToast();
                }
                checkValidate();
            }else{
                new GetLocationFromLatLng(mContext, place.getLatLng()).execute();
                DeviceUtil.hideSoftKeyboard(getActivity());
            }

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                // Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
        addressDialog.dismiss();
    }

    public void updateLocation(String location, String street,String area, String city, String state,  String pincode, LatLng latLng){
        //this.location = location;
        isCalling = false;
        txtAddress.setText(location);

        if(latLng.latitude != 0){
            if(pageType.equals("sender")){
                addressModel.sourceAddress = location;
                addressModel.sourceLat = latLng.latitude;
                addressModel.sourceLng = latLng.longitude;
            }else if(pageType.equals("receiver")){
                addressModel.desAddress = location;
                addressModel.desLat = latLng.latitude;
                addressModel.desLng = latLng.longitude;
            }
        }

        if( Constants.DELIVERY_STATUS == Constants.SAME_CITY && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                || Constants.DELIVERY_STATUS == Constants.OUT_STATION && pageType.equals("sender") && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                || Constants.DELIVERY_STATUS == Constants.OUT_STATION && pageType.equals("receiver") && !GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds) && location.contains("India")
                || Constants.DELIVERY_STATUS == Constants.INTERNATIONAL && pageType.equals("sender") && GpsUtil.isPointInPolygon(pickupLocation, Constants.cityBounds)
                || Constants.DELIVERY_STATUS == Constants.INTERNATIONAL && pageType.equals("receiver") && !location.contains("India")){

            isCorrectLocation = true;
            hideNotification();
        }else{
            showToast();
            isCorrectLocation = false;
        }

        checkValidate();

        if(location.isEmpty() || area.isEmpty() || city.isEmpty() || state.isEmpty() ){
            //mNameTextView.setText("Choose Correct Location, Can not get location info");
        }else{
            try{
//                this.location = location;
//                this.address = street;
//                this.pinCode = pincode;
//                this.state = state;
//                this.city = city;
//                this.area = area;
            }catch (Exception e){};
        }
    }




    private Toast mToastToShow;
    Snackbar snackbar;
    private boolean isValidAddress = false;
    public void showToast() {
        // Set the toast and duration
        int toastDurationInMilliSeconds = 1000000;
        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 500 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {

                if(Constants.DELIVERY_STATUS == Constants.INTERNATIONAL){
                    mToastToShow = Toast.makeText(mContext, "Please enter a valid international delivery address.",Toast.LENGTH_LONG);
                }else if(Constants.DELIVERY_STATUS == Constants.OUT_STATION){
                    mToastToShow = Toast.makeText(mContext, "Please enter a valid outstation delivery address.",Toast.LENGTH_LONG);
                }else{
                    mToastToShow = Toast.makeText(mContext, "Sorry we do not operate in the location you selected.",Toast.LENGTH_LONG);
                }

                if(!isCorrectLocation){
                    mToastToShow.show();
                }else{
                    mToastToShow.cancel();
                }
            }
            public void onFinish() {
                mToastToShow.cancel();
            }
        };
        // Show the toast and starts the countdown
        //mToastToShow.show();
        //toastCountDown.start();


        String message = "";
        if(pageType.equals("sender")){
            message = "Pickup location should be in the City you have selected.";

        }else{
            if(Constants.DELIVERY_STATUS == Constants.INTERNATIONAL){
                message = "Please enter a valid international delivery address.";
            }else if(Constants.DELIVERY_STATUS == Constants.OUT_STATION){
                message = "Please enter a valid outstation delivery address.";
            }else{
                message = "Sorry we do not operate in the location you selected.";
            }
        }
        snackbar = Snackbar
                .make(linContainer, message , Snackbar.LENGTH_LONG);
        addMargins(snackbar);
        snackbar.setDuration(1999999999);
        snackbar.show();
    }


    private void showToastOneTime(){
        String message = "";
        if(pageType.equals("sender")){
            message = "Pickup location should be in the City you have selected.";

        }else{
            if(Constants.DELIVERY_STATUS == Constants.INTERNATIONAL){
                message = "Please enter a valid international delivery address.";
            }else if(Constants.DELIVERY_STATUS == Constants.OUT_STATION){
                message = "Please enter a valid outstation delivery address.";
            }else{
                message = "Sorry we do not operate in the location you selected.";
            }
        }
        snackbar = Snackbar
                .make(linContainer, message , Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void addMargins(Snackbar snack) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snack.getView().getLayoutParams();
        params.setMargins(0, 0, 0, (int)DeviceUtil.dipToPixels(mContext, 53));
        snack.getView().setLayoutParams(params);
    }
}
