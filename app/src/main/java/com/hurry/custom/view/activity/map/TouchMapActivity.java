package com.hurry.custom.view.activity.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
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
import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by Administrator on 8/18/2017.
 */

//https://developers.google.com/maps/documentation/android-sdk/styling
public class TouchMapActivity extends FragmentBaseActivity implements View.OnClickListener , GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationNameAdapter.ContactsAdapterListener   {

    int CAMERA_UPDATE = 16;
    public static boolean mMapIsTouched = false;
    LocationManager locationManager;
    ImageView markerImage;
    GoogleMap map;
    SupportMapFragment mapView;
    LatLng userPosition;
    String location = "";
    TextView edtSearch;
    ImageView imgClose;
    Button btnDone;
    boolean first_load = true;
    boolean pickup_auto_complete = false;
    public static boolean isCalling = false;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.autoCompleteTextView) AutoCompleteTextView mAutocompleteTextView;

    private LocationNameAdapter contactsAdapter;
    ArrayList<LocationNameAdapter.PlaceAutocomplete> contactList;
    private PlacesClient mGoogleApiClient;
    String selectedAddress;


    private void hidekeyboard(){

        InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE );
        View f = getCurrentFocus();
        if( null != f && null != f.getWindowToken() && EditText.class.isAssignableFrom( f.getClass() ) )
            imm.hideSoftInputFromWindow( f.getWindowToken(), 0 );
        else
            getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        first_load = true;
        setContentView(R.layout.activity_touch_map);
        DeviceUtil.setStatusBarColor(this);
        ButterKnife.bind(this);
        initBackButton(toolbar, "");
        DeviceUtil.hideSoftKeyboard(this);
        hidekeyboard();
        FragmentManager fmanager = getSupportFragmentManager();
        Fragment fragment = fmanager.findFragmentById(R.id.mapFragment);
        mapView = (SupportMapFragment) fragment;
        markerImage = (ImageView) findViewById(R.id.img_marker);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpMapView(googleMap);
            }
        });

        Places.initialize(getApplicationContext(), getString(R.string.gecode_api_key2));
        mGoogleApiClient = Places.createClient(this);

        initview();
        pickup_auto_complete = false;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void initview() {
        ImageView imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edtSearch = (TextView) findViewById(R.id.edt_search);
        imgClose = (ImageView) findViewById(R.id.img_close);
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TouchMapActivity.this, MyAutoCompleteActivity.class);
                //Intent intent = new Intent(TouchMapActivity.this, PickupLocationActivity.class);
                intent.putExtra("type", getValue("type"));
                startActivityForResult(intent, 100);
            }
        });
        ImageView imgSearch = (ImageView) findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(TouchMapActivity.this, MyAutoCompleteActivity.class);
//                intent.putExtra("type", getValue("type"));
//                startActivityForResult(intent, 100);
                if(isCalling == false){
                    if(choosedLatLng != null && choosedLatLng.latitude != 0){
                        if(Constants.MODE != Constants.CORPERATION){
                            if(isPointInPolygon(choosedLatLng, Constants.cityBounds)){
                                if(location != null && !location.isEmpty()){
                                    clickDone(String.valueOf(choosedLatLng.latitude), String.valueOf(choosedLatLng.longitude));
                                }
                                finish();
                            }else{
                                Toast.makeText(TouchMapActivity.this, "Sorry we do not operate in the location you selected.",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            if(location != null && !location.isEmpty()){
                                clickDone(String.valueOf(choosedLatLng.latitude), String.valueOf(choosedLatLng.longitude));
                            }
                            finish();
                        }
                    }
                }else{
                    Toast.makeText(TouchMapActivity.this, "Please Wait a sec", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnDone = (Button) findViewById(R.id.btn_done);
        btnDone.setOnClickListener(this);


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
                contactList, this, recyclerView, mGoogleApiClient,"");
        recyclerView.setAdapter(contactsAdapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setUpMapView(GoogleMap mmap) {
        this.map = mmap;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        //map.setMyLocationEnabled(true);
        //Map Settings
        //  map.getUiSettings().setAllGesturesEnabled(true);
        //  map.getUiSettings().setMyLocationButtonEnabled(true);
        // map.getUiSettings().setCompassEnabled(true);
        //map.setInfoWindowAdapter(new CustomInfoWindowAdapter(1));
        boolean success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        if (!success) {
            Log.e("TAG", "Style parsing failed.");
        }

        Location lastKnownLocation = getLastKnownLocation();
        double latt = 35.89093;
        double lng = -106.326907;
        if (lastKnownLocation != null) {
            lng = lastKnownLocation.getLongitude();
            latt = lastKnownLocation.getLatitude();
        }
        userPosition = new LatLng(latt, lng);
        MapsInitializer.initialize(this);

        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                try {
                    marker.getTitle();
                    String index = marker.getSnippet();
                    if (marker.isVisible()) {
                        marker.hideInfoWindow();
                    }
                } catch (Exception ex) {
                }
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (map != null) {
                    map.clear();
                }
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getSnippet().equals("1")) {
                    //map.setInfoWindowAdapter(new CustomInfoWindowAdapter(1));
                } else {
                    //map.setInfoWindowAdapter(new CustomInfoWindowAdapter(2));
                }
                return false;
            }
        });
        if(mapView!=null){
            mapView.onResume();
        }
        setUpMap(userPosition);
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
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


    LatLng center;
    String pinCode;
    String state;
    String city;
    String area;
    String address;

    private void setUpMap(LatLng latLong) {
        try {

            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    if(Constants.MODE == Constants.PERSONAL || Constants.MODE == Constants.GUEST){

                        if(isPointInPolygon(userPosition, Constants.cityBounds)){
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userPosition, CAMERA_UPDATE);
                            map.moveCamera(update);
                            MapsInitializer.initialize(TouchMapActivity.this);
                        }else{
                            if(Constants.cityBounds.size() > 0){
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (int k = 0 ; k < Constants.cityBounds.size(); k++) {
                                    builder.include(Constants.cityBounds.get(k));
                                }
                                LatLngBounds bounds = builder.build();
                                first_load = true;
                                map.moveCamera(CameraUpdateFactory.newLatLngBounds(
                                        bounds, 0));
                            }
                        }
                    }else{
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userPosition, CAMERA_UPDATE);
                        map.moveCamera(update);
                        MapsInitializer.initialize(TouchMapActivity.this);
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
                    if(first_load){
                        first_load = false;
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_UPDATE);
                        map.moveCamera(update);
                    }

                    if(!pickup_auto_complete){
                        try {
                            edtSearch.setText("Waiting...");
                            new GetLocationFromLatLng(TouchMapActivity.this, latLng).execute();
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == 100) {
                try{
                    location = data.getStringExtra("location");
                    double lan = Double.valueOf(data.getStringExtra("lat"));
                    double lon = Double.valueOf(data.getStringExtra("lng"));
                    pinCode = data.getStringExtra("pinCode");
                    state = data.getStringExtra("state");
                    area = data.getStringExtra("area");
                    city = data.getStringExtra("city");
                    if(area == null || area.isEmpty()){
                        area = city;
                    }
                    address = data.getStringExtra("address");
                    choosedLatLng = new LatLng(Double.valueOf(lan), Double.valueOf(lon));
                    LatLng latLng = new LatLng(lan, lon);
                    edtSearch.setText(location);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng).zoom(CAMERA_UPDATE).build();//.tilt(70)
                    map.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                    pickup_auto_complete = true;
                }catch (Exception e){};
            }
        }
    }

    LatLng choosedLatLng;

    public void updateLocation(String location, String street,String area, String city, String state,  String pincode, LatLng ltlng){
        this.location = location;

        if(location.isEmpty() || street.isEmpty() || area.isEmpty() || city.isEmpty() || state.isEmpty() ){
            //mNameTextView.setText("Choose Correct Location, Can not get location info");
            //Toast.makeText(this, "Choose Correct Location, Can not get location info",Toast.LENGTH_SHORT).show();
        }else{

            edtSearch.setText(location);
            this.location = location;
            this.address = street;
            this.pinCode = pincode;
            this.state = state;
            this.city = city;
            this.area = area;
            this.choosedLatLng = ltlng;
            //latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        }
    }


    public void updateLocation2(String location, String street,String area, String city, String state,  String pincode){
        isCalling = false;
        //this.location = location;
        if(location.isEmpty() || area.isEmpty() || city.isEmpty() || state.isEmpty() ){
            Toast.makeText(this, "Choose Correct Location, Can not get location info",Toast.LENGTH_SHORT).show();
        }else{
            try{
                this.location = location;
                this.address = street;
                this.pinCode = pincode;
                this.state = state;
                this.city = city;
                this.area = area;
                mAutocompleteTextView.setText(location);

            }catch (Exception e){};
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btn_done:
                if(isCalling == false){
                    if(choosedLatLng != null && choosedLatLng.latitude != 0){
                        if(Constants.MODE != Constants.CORPERATION){
                            if(isPointInPolygon(choosedLatLng, Constants.cityBounds)){
                                if(location != null && !location.isEmpty()){
                                    clickDone(String.valueOf(choosedLatLng.latitude), String.valueOf(choosedLatLng.longitude));
                                }
                                finish();
                            }else{
                                Toast.makeText(TouchMapActivity.this, "Sorry we do not operate in the location you selected.",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            if(location != null && !location.isEmpty()){
                                clickDone(String.valueOf(choosedLatLng.latitude), String.valueOf(choosedLatLng.longitude));
                            }
                            finish();
                        }
                    }
                }else{
                    Toast.makeText(TouchMapActivity.this, "Please Wait a sec", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
        if(address == null || address.isEmpty()){
            address = area;
        }
        returnIntent.putExtra("area", area);
        returnIntent.putExtra("address", address);
        returnIntent.putExtra("type", getValue("type"));

        setResult(RESULT_OK, returnIntent);
        finish();
    }


    public void setLocation(String location){
        this.location = location;
        edtSearch.setText(location);
    }

    public void hideInformation(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if(linInfo.getVisibility() == View.VISIBLE ){ //&& mMapIsTouched == true
//                    DeviceUtil.collapse(linInfo);
//                }
            }
        }, 300);
    }

    public void showInformation(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if(linInfo.getVisibility() == View.GONE ){ //&& mMapIsTouched == false
//                    DeviceUtil.expand(linInfo);
//                }
            }
        }, 200);
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


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        contactsAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        contactsAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onContactSelected(LocationNameAdapter.PlaceAutocomplete contact) {
        final LocationNameAdapter.PlaceAutocomplete item = contact;
        final String placeId = String.valueOf(item.placeId);
        selectedAddress = String.valueOf(item.description);

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS, Place.Field.PLUS_CODE);
        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();

        mGoogleApiClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            //Log.i(TAG, "Place found: " + place.getName());
            //mNameTextView.setText(Html.fromHtml(place.getName().toString() + ""));
            //mLat = String.valueOf(place.getLatLng().latitude);
            //mLng  = String.valueOf(place.getLatLng().longitude);
            choosedLatLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            this.location = place.getAddress() + "";
            edtSearch.setText(location);
            String[] addresses = location.split(",");
            AddressComponents temp = place.getAddressComponents();
            PlusCode temp1 = place.getPlusCode();
            pinCode = addresses[addresses.length - 1];
            //mNameTextView.setText("Please Waiting....");
            DeviceUtil.hideSoftKeyboard(TouchMapActivity.this);
            LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            if(Constants.MODE != Constants.CORPERATION){
                if(GpsUtil.isPointInPolygon(latLng, Constants.cityBounds)){
                   // mNameTextView.setText(getResources().getString(R.string.please_wait));
                    isCalling = true;
                    new GetLocationFromPlaceId(TouchMapActivity.this, place.getId(), selectedAddress, latLng).execute();
                    DeviceUtil.hideSoftKeyboard(TouchMapActivity.this);
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(choosedLatLng, CAMERA_UPDATE);
                    map.moveCamera(update);
                }else{
                    Toast.makeText(TouchMapActivity.this, "Sorry we do not operate in the location you selected.",Toast.LENGTH_SHORT).show();
                    location = "";
                }
            }else{
                new GetLocationFromLatLng(TouchMapActivity.this, place.getLatLng()).execute();
                DeviceUtil.hideSoftKeyboard(TouchMapActivity.this);
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



