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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.common.utils.ImageLoaderHelper;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.model.PolyLineUtils;
import com.hurry.custom.model.Route;
import com.hurry.custom.model.Step;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.map.LocationTrackMapActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.hurry.custom.view.activity.HomeActivity.TRACK;

public class LocationTrackFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    Context mContext;
    public static String UNCLICK = "0";
    // Layout part
    public static LatLng userPosition;
    public static LatLng sourcePosition;
    public static LatLng destinationPosition;
    public static LatLng packageLocation;
    @BindView(R.id.mapView) MapView mapView;
    @BindView(R.id.img_phone) ImageView imgPhone;
    @BindView(R.id.img_back_icon) ImageView imgBack;

    GoogleMap map;
    Bundle mBundle;

    String mClick = UNCLICK;
    Marker userMarker;
    Marker sourceMarker;
    Marker destinationMarker;
    OrderModel orderModel;
    private LocationManager locationManager;
    private int zoom_out_in = 16;
    Handler mHandler = new Handler()
    ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(
                R.layout.activity_order_map, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);
        Constants.page_type = "track";

        initView(view);
        initGps();
        mBundle = savedInstanceState;
        initMap(savedInstanceState);
        return view;
    }



    public void initView(View view) {


        TextView txtFirst = (TextView) view.findViewById(R.id.txt_first);
        TextView txtSecond = (TextView) view.findViewById(R.id.txt_second);
        TextView txtEmail = (TextView) view.findViewById(R.id.txt_email);
        TextView txtPhone = (TextView) view.findViewById(R.id.txt_phone);

        txtPhone.setOnClickListener(this);
        imgPhone.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        txtFirst.setText(Constants.orderTrackModel.first_name);
        txtSecond.setText(Constants.orderTrackModel.last_name);
        txtEmail.setText(Constants.orderTrackModel.email);
        if (Constants.orderTrackModel.phone != null && !Constants.orderTrackModel.phone.isEmpty()) {
            SpannableString content = new SpannableString(Constants.orderTrackModel.phone);
            content.setSpan(new UnderlineSpan(), 0, Constants.orderTrackModel.phone.length(), 0);
            txtPhone.setText(content);
        }

        ImageView img = (ImageView) view.findViewById(R.id.image);
        Glide.with(this)
                .load(Constants.PHOTO_URL + "employer/" + Constants.orderTrackModel.picture)
                //   .override(selected_bottom_size, selected_bottom_size)
                .dontAnimate()
                .centerCrop()
                .error(com.gun0912.tedpicker.R.drawable.no_image)
                .into(img);

        if (Constants.orderTrackModel.pickup != null && Constants.orderTrackModel.location != null) {
            String[] str_location = Constants.orderTrackModel.location.split(",");
            String[] str_pickup = Constants.orderTrackModel.pickup.split(",");
            try {
                LatLng pickup = new LatLng(Double.valueOf(str_pickup[0]), Double.valueOf(str_pickup[1]));
                sourcePosition = pickup;
            } catch (Exception e) {
            }
            try {
                LatLng location = new LatLng(Double.valueOf(str_location[0]), Double.valueOf(str_location[1]));
                packageLocation = location;
            } catch (Exception e) {

            }
        }
        if (Constants.orderTrackModel.location != null) {
            try{
                String[] str_location = Constants.orderTrackModel.location.split(",");
                packageLocation = new LatLng(Double.valueOf(str_location[0]), Double.valueOf(str_location[1]));
            }catch (Exception e){};
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void setUpMap(GoogleMap mmap) {
        this.map = mmap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(1));
        Location lastKnownLocation = getLastKnownLocation();
        double latt = 35.89093;
        double lng = -106.326907;
        if (lastKnownLocation != null) {
            lng = lastKnownLocation.getLongitude();
            latt = lastKnownLocation.getLatitude();
        }
        userPosition = new LatLng(latt, lng);
        configureNormal();
        MapsInitializer.initialize(mContext);


        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                try {
                    marker.getTitle();
                    String index = marker.getSnippet();
                    if (marker.isVisible()) {
                        marker.hideInfoWindow();
                        //linConfirm.setVisibility(View.GONE);
                        // btnConfirm.setText(getResources().getString(R.string.muti_request));
                    }
                } catch (Exception ex) {

                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getSnippet().equals("1")) {
                    map.setInfoWindowAdapter(new CustomInfoWindowAdapter(1)); // show source and des location
                } else {
                    map.setInfoWindowAdapter(new CustomInfoWindowAdapter(2));
                }
                return false;
            }
        });
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //Your code where exception occurs goes here...

            }
        });


        showMarkers(true);
        MapsInitializer.initialize(mContext);
    }


    private void showMarkers(boolean updateFlag) {
        if (Constants.orderTrackModel.pickup != null && Constants.orderTrackModel.location != null) {
            String[] str_location = Constants.orderTrackModel.location.split(",");
            String[] str_pickup = Constants.orderTrackModel.pickup.split(",");
            try {
                LatLng pickup = new LatLng(Double.valueOf(str_pickup[0]), Double.valueOf(str_pickup[1]));
                sourcePosition = pickup;
            } catch (Exception e) {
            }
            try {
                LatLng location = new LatLng(Double.valueOf(str_location[0]), Double.valueOf(str_location[1]));
                packageLocation = location;
            } catch (Exception e) {

            }
        }
        if (Constants.orderTrackModel.location != null) {
            try{
                String[] str_location = Constants.orderTrackModel.location.split(",");
                packageLocation = new LatLng(Double.valueOf(str_location[0]), Double.valueOf(str_location[1]));
            }catch (Exception e){};
        }

        //map.clear();

        if(userMarker != null){
            userMarker.remove();
        }

        sourcePosition = new LatLng(Constants.addressModel.sourceLat, Constants.addressModel.sourceLng);
        destinationPosition = new LatLng(Constants.addressModel.desLat, Constants.addressModel.desLng);

        sourceMarker = map.addMarker(new MarkerOptions().position(sourcePosition) //position
                .title("source")
                .snippet("1")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.org_set)));

        destinationMarker = map.addMarker(new MarkerOptions().position(destinationPosition) //position
                .title("des")
                .snippet("1")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.dest_set)));

        if (packageLocation != null) {
            //Use map.animateCamera(update) if you want moving effect
            userMarker = map.addMarker(new MarkerOptions().position(packageLocation) //position
                    .title("Package Location")
                    .snippet("2")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.box)));
        }
        CameraUpdate update = null;



        if(updateFlag){
            if (packageLocation != null) {
                update = CameraUpdateFactory.newLatLngZoom(packageLocation, zoom_out_in);
                if (packageLocation != null && packageLocation.latitude != 0)
                    map.moveCamera(update);
            } else {
                if (userPosition != null) {
                    update = CameraUpdateFactory.newLatLngZoom(userPosition, zoom_out_in);
                    if (userPosition != null && userPosition.latitude != 0)
                        map.moveCamera(update);
                }
            }
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) mContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
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

    public void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpMap(googleMap);
                recursive();
                drawPath(sourcePosition, destinationPosition);
            }
        });
    }


    boolean tracking_flag = true;
    private void recursive() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                orderTracking(Constants.orderTrackModel.order_id, Constants.orderTrackModel.employer_id);
                if (tracking_flag) {
                    recursive();
                }
            }
        }, 10000);
    }

    @Override
    public void onResume() {
        super.onResume();
        tracking_flag = true;
        if (mapView != null) {
            mapView.onResume();
        }
        mClick = UNCLICK;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null) {
        }
        tracking_flag = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }
        tracking_flag = false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
        System.gc();
    }


    private void configureNormal() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

            }
        });
    }

    private void initGps() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
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



    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        //    if(locationManager!=null) locationManager.removeUpdates(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.txt_phone:
                makePhoneCall(Constants.orderTrackModel.phone);
                break;
            case R.id.img_phone:
                makePhoneCall(Constants.orderTrackModel.phone);
                break;
            case R.id.img_back_icon:
                ((HomeActivity)mContext).showViewPager(-1);
                break;
        }
    }

    private void makePhoneCall(String finalPhonenumber) {
        ((HomeActivity)mContext).setIndex(TRACK);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + finalPhonenumber));
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
        }
    }

    //https://maps.googleapis.com/maps/api/geocode/json?latlng=%@&sensor=false&language=en
    // get gps location from zip code
    //http://maps.googleapis.com/maps/api/geocode/json?address=55309&sensor=false

    private void showItemLists(LinearLayout linColumn, LinearLayout linItem) {

        if (Constants.ORDER_TYPE == Constants.CAMERA_OPTION) {
            orderModel = Constants.cameraOrderModel;
            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewCol = inflater.inflate(R.layout.column_camera, null);
            viewCol.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linColumn.addView(viewCol);
            if (orderModel != null) {
                for (int k = 0; k < orderModel.itemModels.size(); k++) {

                    View view = inflater.inflate(R.layout.row_review_camera, null);
                    ImageView img = (ImageView) view.findViewById(R.id.image);
                    TextView txtQuantity = (TextView) view.findViewById(R.id.txt_quantity);
                    TextView txtWeight = (TextView) view.findViewById(R.id.txt_weight);
                    ImageLoaderHelper.showImage(mContext, orderModel.itemModels.get(k).image, img);

                    txtQuantity.setText(orderModel.itemModels.get(k).quantity);
                    txtWeight.setText(orderModel.itemModels.get(k).weight);
                    linItem.addView(view);
                }
            }


        } else if (Constants.ORDER_TYPE == Constants.ITEM_OPTION) {
            orderModel = Constants.itemOrderModel;

            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewCol = inflater.inflate(R.layout.column_item, null);
            viewCol.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linColumn.addView(viewCol);

            if (orderModel != null) {
                for (int k = 0; k < orderModel.itemModels.size(); k++) {

                    View view = inflater.inflate(R.layout.row_review_item, null);
                    TextView txtItem = (TextView) view.findViewById(R.id.txt_item);
                    TextView txtDimension = (TextView) view.findViewById(R.id.txt_dimension);
                    TextView txtQuantity = (TextView) view.findViewById(R.id.txt_quantity);
                    TextView txtWeight = (TextView) view.findViewById(R.id.txt_weight);

                    txtItem.setText(orderModel.itemModels.get(k).title);
                    txtDimension.setText( Constants.getDimentionString(orderModel.itemModels.get(k).dimension1 , orderModel.itemModels.get(k).dimension2 ,orderModel.itemModels.get(k).dimension3) );
                    txtQuantity.setText(orderModel.itemModels.get(k).quantity);
                    txtWeight.setText(orderModel.itemModels.get(k).weight);
                    linItem.addView(view);

                }
            }


        } else if (Constants.ORDER_TYPE == Constants.PACKAGE_OPTION) {
            orderModel = Constants.packageOrderModel;

            linColumn.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewCol = inflater.inflate(R.layout.row_review_package, null);
            viewCol.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linColumn.addView(viewCol);

            if (orderModel != null) {
                for (int k = 0; k < orderModel.itemModels.size(); k++) {

                    View view = inflater.inflate(R.layout.row_review_package, null);
                    TextView txtItem = (TextView) view.findViewById(R.id.txt_item);
                    TextView txtQuantity = (TextView) view.findViewById(R.id.txt_quantity);
                    TextView txtWeight = (TextView) view.findViewById(R.id.txt_weight);
                    txtItem.setText(orderModel.itemModels.get(k).title);
                    txtQuantity.setText(orderModel.itemModels.get(k).quantity);
                    txtWeight.setText(orderModel.itemModels.get(k).weight);
                    linItem.addView(view);

                }
            }
        }


    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        int type;
        private View view;

        public CustomInfoWindowAdapter(int type) {
            this.type = type;
            if (type == 1) {
                view = getLayoutInflater().inflate(R.layout.pop_infowindow_product,
                        null);
            } else {
                view = getLayoutInflater().inflate(R.layout.pop_infowindow_package,
                        null);
            }

        }

        @Override
        public View getInfoContents(Marker marker) {

            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            if (type == 1) { // source and desination
                final String snippet = marker.getSnippet();
                if (snippet != null) {
                    if (snippet.equals("")) {
                        return null;
                    }
                }
                try {
                    //currentIndex = Integer.valueOf(snippet);
                    final String title = marker.getTitle();
                    final TextView txtTitle = (TextView) view.findViewById(R.id.txt_title);
                    final TextView txtAddress = ((TextView) view.findViewById(R.id.txt_address));
                    final TextView txtArea = ((TextView) view.findViewById(R.id.txt_area));
                    final TextView txtCity = ((TextView) view.findViewById(R.id.txt_city));
                    if (title.equals("source")) {
                        txtTitle.setText("Source Location");
                        txtAddress.setText(Constants.addressModel.sourceAddress);
                        txtArea.setText(Constants.addressModel.sourceArea);
                        txtCity.setText(Constants.addressModel.sourceCity);
                    } else {
                        txtTitle.setText("Destination Location");
                        txtAddress.setText(Constants.addressModel.desAddress);
                        txtArea.setText(Constants.addressModel.desArea);
                        txtCity.setText(Constants.addressModel.desCity);
                    }

                } catch (Exception e) {
                    return null;
                }
            } else {
                final String snippet = marker.getSnippet();
                if (snippet != null) {
                    if (snippet.equals("")) {
                        return null;
                    }
                }

                try {
                    //currentIndex = Integer.valueOf(snippet);
                    final String title = marker.getTitle();
                    //final TextView company= ((TextView) view.findViewById(R.id.txt_employer));

                    final TextView status = ((TextView) view.findViewById(R.id.txt_status));
                    LinearLayout linItem = (LinearLayout) view.findViewById(R.id.lin_item);
                    LinearLayout linColumn = (LinearLayout) view.findViewById(R.id.lin_column);
                    LinearLayout linDetails = (LinearLayout) view.findViewById(R.id.lin_details);

                    //status.setText(Constants.addressModel.desPinCode);
                    if (Constants.state.equals("0")) {
                        status.setText("Status: " + "Cancel");
                    } else if (Constants.state.equals("1")) {
                        status.setText("Status: " + "Progressing");
                    } else if (Constants.state.equals("2")) {
                        status.setText("Status: " + "On the way for pickup");
                    } else if (Constants.state.equals("3")) {
                        status.setText("Status: " + "On the way to destination");
                    } else if (Constants.state.equals("4")) {
                        status.setText("Status: " + "Order Delivered");
                    } else if (Constants.state.equals("5")) {
                        status.setText("Status: " + "Order on hold");
                    } else if (Constants.state.equals("6")) {
                        status.setText("Status: " + "Returned Order");
                    }

                    if (Constants.MODE != Constants.CORPERATION) {
                        showItemLists(linColumn, linItem);
                    } else {
                        linItem.setVisibility(View.GONE);
                        linColumn.setVisibility(View.GONE);
                        linDetails.setVisibility(View.VISIBLE);
                        TextView txtDelivery = (TextView) view.findViewById(R.id.txt_deliver);
                        TextView txtLoadType = (TextView) view.findViewById(R.id.txt_load_type);
                        txtDelivery.setText(Constants.corDetails);
                        txtLoadType.setText(Constants.getTruck(Constants.corLoadType));
                    }
                } catch (Exception e) {
                    return null;
                }
            }
            return view;
        }
    }



    private void orderTracking(final String orderId, String employer_id) {
        RequestParams params = new RequestParams();
        params.put( "order_id", orderId );
        params.put( "employer_id", employer_id );
        params.put("type", Constants.MODE);
        WebClient.post(Constants.URL  + "/Track/order_track", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        //((MainActivity)context).showProgressDialog();
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
                                    JSONObject orderObj = response.getJSONObject("orders");
                                    Constants.orderTrackModel.id = orderObj.getString("id");
                                    Constants.orderTrackModel.order_id = orderObj.getString("order_id");
                                    Constants.orderTrackModel.pickup = orderObj.getString("pickup");
                                    Constants.orderTrackModel.location = orderObj.getString("location");
                                    Constants.orderTrackModel.employer_id = orderObj.getString("employer_id");
                                    Constants.orderTrackModel.dt = orderObj.getString("dt");
                                    Constants.orderTrackModel.first_name = orderObj.getString("first_name");
                                    Constants.orderTrackModel.last_name = orderObj.getString("last_name");
                                    Constants.orderTrackModel.email = orderObj.getString("email");
                                    Constants.orderTrackModel.phone = orderObj.getString("phone");
                                    Constants.orderTrackModel.picture = orderObj.getString("picture");
                                    Constants.orderTrackModel.type = results;

                                    showMarkers(false);
                                    MapsInitializer.initialize(mContext);
                                    //Toast.makeText(LocationTrackMapActivity.this, "tracking" + Constants.orderTrackModel.location, Toast.LENGTH_SHORT).show();

                                }else if(results.equals("300")){

                                    return;

                                }
                            }else if(response.getString("result").equals("400")){
                                //Toast.makeText(this , context.getResources().getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    };
                    public void onFinish() {
                        //((MainActivity)context).hideProgressDialog();
                    }
                    ;
                });
    }


    private void drawPath(LatLng source, LatLng destination) {
        if (source == null || destination == null) {
            return;
        }
        try{
            if (destination.latitude != 0) {
                //setDestinationMarker(destination);
                //boundLatLang();
                HashMap<String, String> map = new HashMap<String, String>();

                //params.add(new BasicNameValuePair("key", mContext.getResources().getString(R.string.gecode_api_key2)));
                getPath("https://maps.googleapis.com/maps/api/directions/json?origin="
                        + source.latitude + "," + source.longitude
                        + "&destination=" + destination.latitude + ","
                        + destination.longitude + "&mode=driving&sensor=false&key=" + getString(R.string.gecode_api_key2));
            }else{
                LatLngBounds.Builder bld = new LatLngBounds.Builder();
                if(sourceMarker.getPosition().latitude  != 0){
                    bld.include(sourceMarker.getPosition());
                }
                if(destinationMarker.getPosition().latitude != 0)
                    bld.include(destinationMarker.getPosition());

                //bld.include(userMarker.getPosition());
                LatLngBounds latLngBounds = bld.build();
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(
                        latLngBounds, 20));
            }
        }catch (Exception e){};

    }

    ArrayList<LatLng> points;
    PolylineOptions lineOptions;
    Polyline polyLine;

    private void getPath(String url) {
        RequestParams params = new RequestParams();
        WebClient.get(url, params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                        //showProgressDialog();

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

                        String disValue = null;
                        if(sourceMarker != null && destinationMarker != null){
                            try{
                                //final JSONObject json = new JSONObject(response);
                                JSONArray routeArray = response.getJSONArray("routes");
                                JSONObject routes = routeArray.getJSONObject(0);

                                JSONArray legsArray = routes.getJSONArray("legs");
                                JSONObject distanceObject = legsArray.getJSONObject(0);
                                JSONObject distanceObj = distanceObject.getJSONObject("distance");

                                String endAddress = distanceObject.getString("end_address");
                                String startAddress = distanceObject.getString("start_address");

                                disValue = distanceObj.getString("value");

                                Route route = new Route();
                                parseRoute(response, route);
                                final ArrayList<Step> step = route.getListStep();
                                System.out.println("step size=====> " + step.size());
                                points = new ArrayList<LatLng>();
                                lineOptions = new PolylineOptions();

                                for (int i = 0; i < step.size(); i++) {
                                    List<LatLng> path = step.get(i).getListPoints();
                                    System.out.println("step =====> " + i + " and "
                                            + path.size());
                                    points.addAll(path);
                                }
                                if (polyLine != null){
                                    polyLine.remove();
                                    polyLine = null;
                                }

                                lineOptions.addAll(points);
                                lineOptions.width(10);
                                lineOptions.color(Color.rgb( 5 ,177, 251)); // #00008B rgb(0,0,139)

                                if ( lineOptions != null && map != null) { //
                                    polyLine = map.addPolyline(lineOptions);
                                }


                            }catch (Exception e){

                            }
                        }
                    };
                    public void onFinish() {

                        //hideProgressDialog();

                    }
                    ;
                });
    }

    public Route parseRoute(JSONObject jObject, Route routeBean) {

        try {
            Step stepBean;
            //JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("routes");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject innerjObject = jArray.getJSONObject(i);
                if (innerjObject != null) {
                    JSONArray innerJarry = innerjObject.getJSONArray("legs");
                    for (int j = 0; j < innerJarry.length(); j++) {


                        JSONObject jObjectLegs = innerJarry.getJSONObject(j);
                        routeBean.setDistanceText(jObjectLegs.getJSONObject(
                                "distance").getString("text"));
                        routeBean.setDistanceValue(jObjectLegs.getJSONObject(
                                "distance").getInt("value"));

                        routeBean.setDurationText(jObjectLegs.getJSONObject(
                                "duration").getString("text"));
                        routeBean.setDurationValue(jObjectLegs.getJSONObject(
                                "duration").getInt("value"));

                        routeBean.setStartAddress(jObjectLegs
                                .getString("start_address"));
                        routeBean.setEndAddress(jObjectLegs
                                .getString("end_address"));

                        routeBean.setStartLat(jObjectLegs.getJSONObject(
                                "start_location").getDouble("lat"));
                        routeBean.setStartLon(jObjectLegs.getJSONObject(
                                "start_location").getDouble("lng"));

                        routeBean.setEndLat(jObjectLegs.getJSONObject(
                                "end_location").getDouble("lat"));
                        routeBean.setEndLon(jObjectLegs.getJSONObject(
                                "end_location").getDouble("lng"));

                        JSONArray jstepArray = jObjectLegs
                                .getJSONArray("steps");
                        if (jstepArray != null) {
                            for (int k = 0; k < jstepArray.length(); k++) {
                                stepBean = new Step();
                                JSONObject jStepObject = jstepArray
                                        .getJSONObject(k);
                                if (jStepObject != null) {

                                    stepBean.setHtml_instructions(jStepObject
                                            .getString("html_instructions"));
                                    stepBean.setStrPoint(jStepObject
                                            .getJSONObject("polyline")
                                            .getString("points"));
                                    stepBean.setStartLat(jStepObject
                                            .getJSONObject("start_location")
                                            .getDouble("lat"));
                                    stepBean.setStartLon(jStepObject
                                            .getJSONObject("start_location")
                                            .getDouble("lng"));
                                    stepBean.setEndLat(jStepObject
                                            .getJSONObject("end_location")
                                            .getDouble("lat"));
                                    stepBean.setEndLong(jStepObject
                                            .getJSONObject("end_location")
                                            .getDouble("lng"));

                                    stepBean.setListPoints(new PolyLineUtils()
                                            .decodePoly(stepBean.getStrPoint()));


                                    routeBean.getListStep().add(stepBean);
                                }

                            }
                        }
                    }

                }

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return routeBean;
    }





}
