

package com.hurry.custom.view.activity.map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.common.utils.GpsUtil;
import com.hurry.custom.controller.GetLocationFromLatLng;
import com.hurry.custom.controller.GetLocationFromPlaceId;
import com.hurry.custom.view.FragmentBaseActivity;
import com.hurry.custom.view.adapter.LocationNameAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 7/20/2017.
 */

public class MyAutoCompleteActivity extends FragmentBaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks , View.OnClickListener,OnMapReadyCallback, LocationNameAdapter.ContactsAdapterListener {

    //https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=bengaluru&inputtype=textquery&key=AIzaSyBRHZ24e9OnPUSM4J2pLfOzVGrXBkFia_g


    private static final String LOG_TAG = "MainActivity";
    private AutoCompleteTextView mAutocompleteTextView;
    private TextView mNameTextView;
    ImageView imgClose;


    private PlacesClient mGoogleApiClient;
    private LocationNameAdapter contactsAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private LocationManager locationManager;
    String mLat, mLng;
    ArrayList<LocationNameAdapter.PlaceAutocomplete> contactList;
    String selectedAddress = "";
    public static boolean isCalling = false;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);
        ButterKnife.bind(this);
        DeviceUtil.setStatusBarColor(this);
        initBackButton(toolbar, "Choose Location");

        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale locale = new Locale("US"); // <---- your target language
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        imgClose = (ImageView)findViewById(R.id.img_close);
        imgClose.setOnClickListener(this);
        ImageView imgBack = (ImageView)findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);

        Places.initialize(getApplicationContext(), getString(R.string.gecode_api_key2));
        mGoogleApiClient = Places.createClient(this);

        initView();
        Button btnDone = (Button)findViewById(R.id.btn_done);
        btnDone.setOnClickListener(this);
        if(getValue("type").equals("source")){
            btnDone.setText(getString(R.string.set_pickup));
        }else{
            btnDone.setText(getString(R.string.set_dropoff));
        }
        initGps();
    }

    public String  getValue(String type){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            return bundle.getString( type);
        }
        return "";
    }


    private void initView(){
        mNameTextView = (TextView) findViewById(R.id.name);
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
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

        contactList = new ArrayList<LocationNameAdapter.PlaceAutocomplete>();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.street_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        contactsAdapter = new LocationNameAdapter(this,
                contactList, this, recyclerView, mGoogleApiClient, "");
        recyclerView.setAdapter(contactsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }


    @Override
    public void onPause() {
        super.onPause();
    }



    @Override
    public void onConnected(Bundle bundle) {
//            mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);

        contactsAdapter.setGoogleApiClient(mGoogleApiClient);

        Log.i(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

//        Toast.makeText(this,
//                "Google Places API connection failed with error code:" +
//                        connectionResult.getErrorCode(),1
//                Toast.LENGTH_LONG).show();
    }
    @Override
    public void onConnectionSuspended(int i) {
        //mPlaceArrayAdapter.setGoogleApiClient(null);
        contactsAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }


    private void initGps() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        for (String provider : locationManager.getProviders(criteria, true)) {
            if (provider.contains("gps")) {
                // locationManager.requestSingleUpdate(provider,this,null);
                return;
            }
        }
        // startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    String location = "";
    String address;
    String state;
    String city;
    String area = "";
    String pinCode;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_done:
                if(location != null && !location.isEmpty()){
                    if(isCalling == false){
                        clickDone(mLat, mLng);
                    }
                }
                break;

            case R.id.img_close:
                mAutocompleteTextView.setText("");
                imgClose.setVisibility(View.GONE);
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    }



    public void clickDone(String lat, String lng){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("location", location);
        returnIntent.putExtra("lat", lat);
        returnIntent.putExtra("lng", lng);
        returnIntent.putExtra("pinCode", pinCode);
        returnIntent.putExtra("state", state);
        returnIntent.putExtra("city", city);
        if(area == null || area.isEmpty()){
            area = city;
        }
        returnIntent.putExtra("area", area);
        returnIntent.putExtra("address", address);
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    public void updateLocation(String location, String street,String area, String city, String state,  String pincode){
        isCalling = false;
        this.location = location;
        if(location.isEmpty() || area.isEmpty() || city.isEmpty() || state.isEmpty() ){
            mNameTextView.setText("Choose Correct Location, Can not get location info");
            Toast.makeText(this, "Choose Correct Location, Can not get location info",Toast.LENGTH_SHORT).show();
        }else{
            try{
                this.location = location;
                this.address = street;
                this.pinCode = pincode;
                this.state = state;
                this.city = city;
                this.area = area;
                mNameTextView.setText("");
                mAutocompleteTextView.setText(location);

            }catch (Exception e){};
        }
    }


    @Override
    public void onContactSelected(LocationNameAdapter.PlaceAutocomplete contact) {

        final LocationNameAdapter.PlaceAutocomplete item = contact;
        final String placeId = String.valueOf(item.placeId);
        selectedAddress = String.valueOf(item.description);
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

            mNameTextView.setText(Html.fromHtml(place.getName().toString() + ""));

            mLat = String.valueOf(place.getLatLng().latitude);
            mLng  = String.valueOf(place.getLatLng().longitude);
            this.location = place.getAddress() + "";
            String[] addresses = location.split(",");
            AddressComponents temp = place.getAddressComponents();
            PlusCode temp1 = place.getPlusCode();
            pinCode = addresses[addresses.length - 1];
            //mNameTextView.setText("Please Waiting....");
            DeviceUtil.hideSoftKeyboard(MyAutoCompleteActivity.this);
            LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            if(Constants.MODE != Constants.CORPERATION){
                if(GpsUtil.isPointInPolygon(latLng, Constants.cityBounds)){
                    mNameTextView.setText(getResources().getString(R.string.please_wait));
                    isCalling = true;
                    new GetLocationFromPlaceId(MyAutoCompleteActivity.this, place.getId(), selectedAddress, latLng).execute();
                    DeviceUtil.hideSoftKeyboard(MyAutoCompleteActivity.this);

                }else{
                    Toast.makeText(MyAutoCompleteActivity.this, "Sorry we do not operate in the location you selected.",Toast.LENGTH_SHORT).show();
                    mLat = "";
                    mLng = "";
                    location = "";
                }
            }else{

                mNameTextView.setText(getResources().getString(R.string.please_wait));
                new GetLocationFromLatLng(MyAutoCompleteActivity.this, place.getLatLng()).execute();
                DeviceUtil.hideSoftKeyboard(MyAutoCompleteActivity.this);
            }



        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
               // Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });



    }
}



