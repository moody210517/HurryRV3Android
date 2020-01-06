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
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.aigestudio.wheelpicker.WheelPicker;
import com.aigestudio.wheelpicker.widgets.WheelDatePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.common.utils.DeviceUtil;
import com.hurry.custom.common.utils.TimeHelper;
import com.hurry.custom.common.utils.ValidationHelper;
import com.hurry.custom.controller.GetRoute;
import com.hurry.custom.controller.WebClient;
import com.hurry.custom.model.AddressModel;
import com.hurry.custom.model.CorporateModel;
import com.hurry.custom.model.DateModel;
import com.hurry.custom.model.PolyLineUtils;
import com.hurry.custom.model.Route;
import com.hurry.custom.model.Step;
import com.hurry.custom.view.activity.AddressDetailsNewActivity;
import com.hurry.custom.view.activity.HomeActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DateTimeFragment extends Fragment implements View.OnClickListener, WheelPicker.OnItemSelectedListener{


    Context mContext;
    int year, month, day, hour, minute;


    SupportMapFragment mapView;
    GoogleMap map;
    Marker sourceMarker, destinationMarker;
    LatLng sourcePosition, destinationPosition;
    ArrayList<LatLng> points;
    PolylineOptions lineOptions;
    Polyline polyLine;


    @BindView(R.id.edt_date) EditText edtDate;
    @BindView(R.id.edt_time) EditText edtTime;
    @BindView(R.id.lin_email) LinearLayout linEmail;
    @BindView(R.id.txt_estimated) TextView txtEstimated;
    @BindView(R.id.txt_distance) TextView txtDistance;
    @BindView(R.id.txt_weight) TextView txtWeight;

    EditText edtEmail;


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_header) TextView txtHeader;
    @BindView(R.id.img_sender)
    ImageView imgSender;
    @BindView(R.id.img_receiver) ImageView imgReceiver;
    @BindView(R.id.lin_date_time) LinearLayout linDateTime;
    @BindView(R.id.txt_source) TextView txtSource;
    @BindView(R.id.txt_destination) TextView txtDestination;
    @BindView(R.id.lin_service) LinearLayout linService;


    @BindView(R.id.wheel_date_picker) WheelDatePicker wheelDatePicker;
    @BindView(R.id.wheel_hour) WheelPicker wheelHour;
    @BindView(R.id.wheel_minute) WheelPicker wheelMinute;
    @BindView(R.id.wheel_ap) WheelPicker wheelAp;
    @BindView(R.id.wheel_date) WheelPicker wheelDate;
//    @BindView(R.id.scrollview)
//    ScrollView scrollView;
    @BindView(R.id.txt_back) TextView txtBack;
    @BindView(R.id.txt_next) TextView txtNext;
    @BindView(R.id.lin_level_container) LinearLayout linLevelContainer;
    @BindView(R.id.rl_map) RelativeLayout relativeMap;
    @BindView(R.id.lin_plan_container) LinearLayout linPlanContainer;
    @BindView(R.id.lin_distance_container) LinearLayout linDistanceContainer;
    @BindView(R.id.lin_estimated_pickup) LinearLayout linEstimatedPickup;


    int selectedYear, selectedMonth, selectedDay;
    int selectedHour, selectedMinute;
    String status = "AM";

    ArrayList<String> hours = new ArrayList<String>();
    ArrayList<String> minutes = new ArrayList<String>();
    ArrayList<String> aps = new ArrayList<String>();
    ArrayList<String> dates = new ArrayList<String>();
    ArrayList<Calendar> dateCalendars = new ArrayList<Calendar>();
    int todayPosition;

    String pageType = "level";

    public DateTimeFragment(String type){
        if(type.equals("review")){
            pageType = "date";
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(
                R.layout.activity_date_time, container, false);
        mContext = getActivity();
        ButterKnife.bind(this, view);

        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.locale = locale;
        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());

        initView(view);
        initDateTimePickers();

        if(Constants.MODE != Constants.CORPERATION ){
            addService();
            if(Constants.DELIVERY_STATUS == Constants.INTERNATIONAL){
                chooseService(1);
            }

        }else{
            if(PreferenceUtils.getQuote(mContext)){
                addService();
                Constants.page_type = "quote";
            }else{

            }
        }

        if(Constants.dateModel != null && Constants.dateModel.time != null){
            edtTime.setText(Constants.dateModel.time);
            showEstmiatedDate();
        }else{
            if(Constants.dateModel == null)
                Constants.dateModel = new DateModel();
            Constants.dateModel.time = edtTime.getText().toString();
        }

        if(Constants.dateModel != null && Constants.dateModel.date != null){
            edtDate.setText(Constants.dateModel.date);
            showEstmiatedDate();
        }else{
            if(Constants.dateModel == null)
                Constants.dateModel = new DateModel();
            Constants.dateModel.date = edtDate.getText().toString();
        }


        if(pageType.equals("date")){

            txtHeader.setText(getString(R.string.select_pickup_date_time));
            linLevelContainer.setVisibility(View.GONE);
            linDateTime.setVisibility(View.VISIBLE);

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(Constants.selectedHour > 12 ){
                        wheelHour.setSelectedItemPosition( Constants.selectedHour -12 -1 );
                        wheelAp.setSelectedItemPosition(1); //hour > 12 ? 1:0
                    }else{
                        wheelHour.setSelectedItemPosition( Constants.selectedHour -1 );
                        wheelAp.setSelectedItemPosition(0); //hour > 12 ? 1:0
                    }
                    wheelMinute.setSelectedItemPosition(Constants.selectedMinute -1);
                    wheelDate.setSelectedItemPosition(getPos(Constants.selectedYear, Constants.selectedMonth, Constants.selectedDate));
                    Constants.dateModel.date = TimeHelper.getDate(Constants.selectedYear, Constants.selectedMonth, Constants.selectedDate);
                    Constants.dateModel.time = TimeHelper.getTime( Constants.selectedHour, Constants.selectedMinute);
                    showNext();
                }
            },200);


        }


        return view;
    }



    private int getPos(int yearc, int monthc, int datec){

        Calendar calendarFrom = Calendar.getInstance();
        Calendar calendarUntil = Calendar.getInstance();

        calendarFrom.add(Calendar.MONTH,-1); //setTime(dateFormat.parse(s1));
        calendarUntil.add(Calendar.MONTH ,  6); //dateFormat.parse(s2)

        int position = 0;
        while (calendarFrom.compareTo(calendarUntil) != 0) {
            //System.out.println(dateFormat.format(calendarFrom.getTime()));

            int year = calendarFrom.get(Calendar.YEAR);
            int month = calendarFrom.get(Calendar.MONTH);
            int day = calendarFrom.get(Calendar.DATE);

            int toyear = calendarUntil.get(Calendar.YEAR);
            int tomonth = calendarUntil.get(Calendar.MONTH);
            int today = calendarUntil.get(Calendar.DATE);

            if(year == toyear && tomonth == month && today == day){
                break;
            }

            if(yearc == year && monthc  == month && datec == day){
                return position;
            }else{
            }
            calendarFrom.add(Calendar.DATE, 1);
            position++;
        }

        return  0;

    }



    @Override
    public void onResume(){
        super.onResume();
        if(!Constants.guestEmail.isEmpty()){
            edtEmail.setText(Constants.guestEmail);
        }
    }

    private void initDateTimePickers(){
        Calendar calendar = Calendar.getInstance();
        wheelDatePicker.setSelectedYear(calendar.get(Calendar.YEAR));
        wheelDatePicker.setSelectedMonth(calendar.get(Calendar.MONTH) + 1);
        wheelDatePicker.setSelectedDay(calendar.get(Calendar.DATE));
        wheelDatePicker.setOnDateSelectedListener(new WheelDatePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(WheelDatePicker picker, Date date) {

                selectedMonth = picker.getCurrentMonth() - 1;
                selectedYear = picker.getCurrentYear();
                selectedDay = picker.getCurrentDay();

                showFlag = false;
                if((selectedYear == year && selectedMonth > month )|| ( selectedYear == year && selectedMonth == month && selectedDay >= day) || selectedYear > year){
                    if(selectedDay == day){
                        if(hour > Constants.selectedHour || (hour == Constants.selectedHour && minute > Constants.selectedMinute)){

                            edtTime.setText(TimeHelper.getTime(hour, minute));
                            //Constants.dateModel.time = TimeHelper.getTwoDigital(hour_of_12_hour_format )+ " : " + TimeHelper.getTwoDigital(selectedMinute )+ " " + status;
                            Constants.dateModel.time = TimeHelper.getTime(hour, minute);
                            Constants.selectedHour = hour;
                            Constants.selectedMinute = minute;
                            showEstmiatedDate();
                        }
                    }
                    edtDate.setText(TimeHelper.getDate(selectedYear, selectedMonth, selectedDay));
                    Constants.dateModel.date = TimeHelper.getDate(selectedYear, selectedMonth, selectedDay);
                    chooseDate = true;
                    Constants.selectedYear = selectedYear;
                    Constants.selectedMonth = selectedMonth;
                    Constants.selectedDate = selectedDay;
                    showEstmiatedDate();
                    showReviewButton();
                }else{
                    //Toast.makeText(DateTimeActivity.this, "Pickup date should not be past date", Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContext, "Pickup time should not be in the past", Toast.LENGTH_SHORT).show();
                    chooseDate = false;

                    hideNext();
                }
            }
        });


        wheelDatePicker.setSelectedItemTextColor(mContext.getColor(R.color.red_pop));
        wheelDatePicker.setItemTextColor(mContext.getColor(R.color.wheel_text_color));
        wheelDatePicker.setCurved(true);
        wheelDatePicker.setItemTextSize((int)DeviceUtil.dipToPixels(mContext, 18));
        wheelDatePicker.setCyclic(true);


        String s1 = "05.05.2019";
        String s2 = "11.05.2040";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat myDateFormat = new SimpleDateFormat("EEE, MMM dd");

        Calendar calendarFrom = Calendar.getInstance();
        Calendar calendarUntil = Calendar.getInstance();
        Calendar current = Calendar.getInstance();

        calendarFrom.add(Calendar.MONTH,-1); //setTime(dateFormat.parse(s1));
        calendarUntil.add(Calendar.MONTH ,  6); //dateFormat.parse(s2)

        int position = 0;
        while (calendarFrom.compareTo(calendarUntil) != 0) {
            //System.out.println(dateFormat.format(calendarFrom.getTime()));

            int year = calendarFrom.get(Calendar.YEAR);
            int month = calendarFrom.get(Calendar.MONTH);
            int day = calendarFrom.get(Calendar.DATE);

            int toyear = calendarUntil.get(Calendar.YEAR);
            int tomonth = calendarUntil.get(Calendar.MONTH);
            int today = calendarUntil.get(Calendar.DATE);

            if(year == toyear && tomonth == month && today == day){
                break;
            }

            if(current.get(Calendar.YEAR) == year && current.get(Calendar.MONTH) == month && current.get(Calendar.DATE) == day){
                todayPosition = position ;
                dates.add("Today");
            }else{
                dates.add(myDateFormat.format(calendarFrom.getTime()));
            }
            Calendar newItem = Calendar.getInstance();
            newItem.set(calendarFrom.get(Calendar.YEAR) , calendarFrom.get(Calendar.MONTH), calendarFrom.get(Calendar.DATE));
            dateCalendars.add(newItem);
            calendarFrom.add(Calendar.DATE, 1);
            position++;
        }

        wheelDate.setData(dates);
        wheelDate.setSelectedItemTextColor(mContext.getColor(R.color.black));
        wheelDate.setItemTextColor(mContext.getColor(R.color.wheel_text_color));
        wheelDate.setCurved(true);
        wheelDate.setCyclic(true);
        wheelDate.setItemTextSize((int)DeviceUtil.dipToPixels(mContext, 18));
        wheelDate.setOnItemSelectedListener(this);


        int hour = calendar.get(Calendar.HOUR);
        for(int k = 1 ; k <= 12 ; k++){
            hours.add(String.valueOf(k));
        }
        wheelHour.setData(hours);
        wheelHour.setSelectedItemTextColor(mContext.getColor(R.color.red_pop));
        wheelHour.setItemTextColor(mContext.getColor(R.color.wheel_text_color));
        wheelHour.setCurved(true);
        wheelHour.setCyclic(true);
        wheelHour.setItemTextSize((int)DeviceUtil.dipToPixels(mContext, 18));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wheelHour.setLayoutParams(params);
        final ViewGroup.MarginLayoutParams lpt =(ViewGroup.MarginLayoutParams)wheelHour.getLayoutParams();
        lpt.setMargins((int)DeviceUtil.dipToPixels(mContext, 20),lpt.topMargin,lpt.rightMargin,lpt.bottomMargin);
        wheelHour.setOnItemSelectedListener(this);
        //wheelHour.setSelectedItemPosition( 4 ); //hour > 12 ? hour - 12 :


        for(int k = 1 ; k <= 60 ; k++){
            minutes.add(String.valueOf(k));
        }

        wheelMinute.setData(minutes);
        wheelMinute.setSelectedItemTextColor(mContext.getColor(R.color.red_pop));
        wheelMinute.setItemTextColor(mContext.getColor(R.color.wheel_text_color));
        wheelMinute.setCurved(true);
        wheelMinute.setCyclic(true);
        wheelMinute.setItemTextSize((int)DeviceUtil.dipToPixels(mContext, 18));

      //  wheelMinute.setSelectedItemPosition(calendar.get(Calendar.MINUTE) - 1);
        wheelMinute.setSelected(true);
        wheelMinute.setOnItemSelectedListener(this);
        wheelMinute.setLayoutParams(params);
        final ViewGroup.MarginLayoutParams lptm =(ViewGroup.MarginLayoutParams)wheelMinute.getLayoutParams();
        lptm.setMargins((int)DeviceUtil.dipToPixels(mContext, 20),lpt.topMargin,lpt.rightMargin,lpt.bottomMargin);

        aps.add("AM");
        aps.add("PM");
        wheelAp.setData(aps);
        wheelAp.setSelectedItemTextColor(mContext.getColor(R.color.red_pop));
        wheelAp.setItemTextColor(mContext.getColor(R.color.wheel_text_color));
        wheelAp.setCurved(true);
        //wheelAp.setCyclic(true);
        wheelAp.setItemTextSize((int)DeviceUtil.dipToPixels(mContext, 18));
        int ap = calendar.get(Calendar.AM_PM);
        //wheelAp.setSelectedItemPosition(ap); //hour > 12 ? 1:0
        wheelAp.setSelected(true);
        wheelAp.setOnItemSelectedListener(this);
        wheelAp.setLayoutParams(params);
        final ViewGroup.MarginLayoutParams lptap =(ViewGroup.MarginLayoutParams)wheelAp.getLayoutParams();
        lptap.setMargins((int)DeviceUtil.dipToPixels(mContext, 20),lpt.topMargin,lpt.rightMargin,lpt.bottomMargin);

        wheelHour.setSelectedItemPosition( hour -1 ); //hour > 12 ? hour - 12 :
        wheelMinute.setSelectedItemPosition(calendar.get(Calendar.MINUTE) -1 );
        wheelAp.setSelectedItemPosition(ap); //hour > 12 ? 1:0
        wheelDate.setSelectedItemPosition(todayPosition); //hour > 12 ? 1:0

        linDateTime.setVisibility(View.GONE);
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


    public void hideNext(){
        imgReceiver.setVisibility(View.GONE);
        txtNext.setVisibility(View.GONE);
    }
    public void showNext(){
        imgReceiver.setVisibility(View.VISIBLE);
        txtNext.setVisibility(View.GONE);
    }

    private  void initView(View view){

        imgReceiver.setOnClickListener(this);
        imgSender.setOnClickListener(this);
        txtNext.setOnClickListener(this);
        txtNext.setVisibility(View.GONE);
        txtBack.setOnClickListener(this);

        txtSource.setText(Constants.addressModel.sourceAddress);
        txtDestination.setText(Constants.addressModel.desAddress);


        linLevelContainer.setVisibility(View.VISIBLE);


        hideNext();

        edtDate.setHint("Input Date");
        if(Constants.priceType.distance == 0 ){
            txtDistance.setText( String.format("%.2f", distance(Constants.addressModel.sourceLat, Constants.addressModel.desLat,
                    Constants.addressModel.sourceLng, Constants.addressModel.desLng,0,0)/1000 + 8) + "Km(s)");
        }else{
            txtDistance.setText( Constants.priceType.distance + "Km(s)");
        }

        txtWeight.setText(String.valueOf(Constants.getTotalWeight()) + "kg(s)");
        //edtTime.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        year = calendar.get(Calendar.YEAR);
        Constants.selectedYear = year;

        Date dt = new Date();
        hour = dt.getHours();
        minute = dt.getMinutes();

        edtDate.setText(TimeHelper.getDate(year, month, day));
        edtTime.setText(TimeHelper.getTime());

        if(Constants.selectedHour  == -1){
            Constants.selectedHour = hour;
        }

        if(Constants.selectedMinute == -1){
            Constants.selectedMinute = minute;
        }

        if(DeviceUtil.isTablet(mContext)){
            edtDate.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtTime.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            view.findViewById(R.id.line_date).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line_time).setVisibility(View.VISIBLE);
        }

        edtEmail = (EditText)view.findViewById(R.id.edt_email);
        if(Constants.MODE == Constants.GUEST){
            linEmail.setVisibility(View.VISIBLE);
        }

        if(Constants.MAP_HEIGHT != 0 && Constants.MAP_HEIGHT > 400){
            LinearLayout.LayoutParams rel_btn = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, Constants.MAP_HEIGHT);
            relativeMap.setLayoutParams(rel_btn);
        }

        FragmentManager fmanager = getChildFragmentManager();
        Fragment fragment = fmanager.findFragmentById(R.id.mapFragment);
        mapView = (SupportMapFragment) fragment;
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpMapView(googleMap);
            }
        });

        if(Constants.DELIVERY_STATUS == Constants.INTERNATIONAL){
            linDistanceContainer.setVisibility(View.GONE);
            linPlanContainer.setVisibility(View.VISIBLE);
            linEstimatedPickup.setVisibility(View.GONE);
        }else if(Constants.DELIVERY_STATUS == Constants.OUT_STATION){
            linEstimatedPickup.setVisibility(View.GONE);
        }else{
            linEstimatedPickup.setVisibility(View.VISIBLE);
        }
    }

    private void showReviewButton(){
        showNext();

    }


    private void setUpMapView(GoogleMap mmap) {
        this.map = mmap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);

//        boolean success = map.setMapStyle(
//                MapStyleOptions.loadRawResourceStyle(
//                        this, R.raw.style_json));
//        if (!success) {
//            Log.e("TAG", "Style parsing failed.");
//        }

        if(map != null){
            sourcePosition = new LatLng(Constants.addressModel.sourceLat, Constants.addressModel.sourceLng);
            destinationPosition = new LatLng(Constants.addressModel.desLat, Constants.addressModel.desLng);

            sourceMarker = map.addMarker(new MarkerOptions().position(sourcePosition) //position
                    .title("source")
                    .snippet("")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.org_set)));

            destinationMarker = map.addMarker(new MarkerOptions().position(destinationPosition) //position
                    .title("destination")
                    .snippet("")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.dest_set)));
//                                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(sourcePosition, 8);
//                                            map.moveCamera(update);
            drawPath(sourcePosition, destinationPosition);


        }

        if(mapView!=null){
            mapView.onResume();
        }
    }

    private void drawPath(LatLng source, LatLng destination) {
        if (source == null || destination == null) {
            return;
        }
        if (destination.latitude != 0) {
            //setDestinationMarker(destination);
            //boundLatLang();
            HashMap<String, String> map = new HashMap<String, String>();

            //params.add(new BasicNameValuePair("key", mContext.getResources().getString(R.string.gecode_api_key2)));
            //getPath("https://maps.googleapis.com/maps/api/directions/json", source, destination);
            new GetRoute(mContext,  this,  source, destination).execute();
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
    }

    public void drawRoute(JSONObject response){
        if(sourceMarker != null && destinationMarker != null){
            try{
                //final JSONObject json = new JSONObject(response);
                JSONArray routeArray = response.getJSONArray("routes");
                if(routeArray.length() > 0){
                    JSONObject routes = routeArray.getJSONObject(0);

                    JSONArray legsArray = routes.getJSONArray("legs");
                    JSONObject distanceObject = legsArray.getJSONObject(0);
                    JSONObject distanceObj = distanceObject.getJSONObject("distance");

                    String endAddress = distanceObject.getString("end_address");
                    String startAddress = distanceObject.getString("start_address");


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

                    LatLngBounds.Builder bld = new LatLngBounds.Builder();
                    bld.include(sourceMarker.getPosition());
                    bld.include(destinationMarker.getPosition());
                    //bld.include(userPosition);
                    LatLngBounds latLngBounds = bld.build();
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(
                            latLngBounds, 20));
                }else{
                    drawDotLine(sourcePosition, destinationPosition);
                }

            }catch (Exception e){

            }
        }
    }


    public void drawDotLine(LatLng sourcePosition, LatLng destinationPosition){
        points = new ArrayList<LatLng>();
        lineOptions = new PolylineOptions();
//        for (int i = 0; i < step.size(); i++) {
//
//            System.out.println("step =====> " + i + " and "
//                    + path.size());
//            points.addAll(path);
//        }
        List<LatLng> path = new ArrayList<>();

        double difLat = destinationPosition.latitude - sourcePosition.latitude;
        double difLng = destinationPosition.longitude - sourcePosition.longitude;

        double zoom = map.getCameraPosition().zoom;

        double divLat = difLat / (zoom * 5);
        double divLng = difLng / (zoom * 5);

        LatLng tmpLatOri = sourcePosition;
        for(int i = 0; i < (zoom * 5); i++){
            LatLng loopLatLng = tmpLatOri;
            if(i > 0){
                loopLatLng = new LatLng(tmpLatOri.latitude + (divLat * 0.25f), tmpLatOri.longitude + (divLng * 0.25f));
            }

//            Polyline polyline = map.addPolyline(new PolylineOptions()
//                    .add(loopLatLng)
//                    .add(new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng))
//                    .color(color)
//                    .width(5f));

            points.clear();
            points.add(loopLatLng);
            points.add(new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng));

            PolylineOptions options = new PolylineOptions();
            options.addAll(points);
            options.width(10);
            options.color(Color.rgb( 5 ,177, 251)); // #00008B rgb(0,0,139)
            if ( options != null && map != null) { //
                polyLine = map.addPolyline(options);
            }

            tmpLatOri = new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng);
        }


        LatLngBounds.Builder bld = new LatLngBounds.Builder();
        bld.include(sourceMarker.getPosition());
        bld.include(destinationMarker.getPosition());
        //bld.include(userPosition);
        LatLngBounds latLngBounds = bld.build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(
                latLngBounds, 20));
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



    boolean showFlag = false;
    boolean chooseDate = false;



    private DatePickerDialog.OnDateSetListener pickerListener1 = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            showFlag = false;
            if((selectedYear == year && selectedMonth > month )|| ( selectedYear == year && selectedMonth == month && selectedDay >= day) || selectedYear > year){
                if(selectedDay == day){
                    if( hour > Constants.selectedHour || ( hour == Constants.selectedHour && minute > Constants.selectedMinute)){

                        edtTime.setText(TimeHelper.getTime( hour, minute));
                        //Constants.dateModel.time = TimeHelper.getTwoDigital(hour_of_12_hour_format )+ " : " + TimeHelper.getTwoDigital(selectedMinute )+ " " + status;
                        Constants.dateModel.time = TimeHelper.getTime( hour, minute);
                        Constants.selectedHour = hour;
                        Constants.selectedMinute = minute;

                        showEstmiatedDate();
                    }
                }
                edtDate.setText(TimeHelper.getDate(selectedYear, selectedMonth, selectedDay));
                Constants.dateModel.date = TimeHelper.getDate(selectedYear, selectedMonth, selectedDay);
                chooseDate = true;
                Constants.selectedYear = selectedYear;
                Constants.selectedMonth = selectedMonth;
                Constants.selectedDate = selectedDay;
                showEstmiatedDate();

            }else{
                //Toast.makeText(DateTimeActivity.this, "Pickup date should not be past date", Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "Pickup time should not be in the past", Toast.LENGTH_SHORT).show();
                chooseDate = false;

            }
        }
    };



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.img_back:
                if(!PreferenceUtils.getQuote(mContext)){
                    openAddressDetaisl();
                }

                break;

            case R.id.lin_expedited:
                chooseService(1);
                Constants.selectedServiceLevel = 1;
                showEstmiatedDate();
                break;
            case R.id.lin_express:
                chooseService(2);
                Constants.selectedServiceLevel = 2;
                showEstmiatedDate();
                break;
            case R.id.lin_economy:
                chooseService(3);
                Constants.selectedServiceLevel = 3;
                showEstmiatedDate();
                break;

            case R.id.img_sender:
            case R.id.txt_back:

                if(pageType.equals("level")){
                    ((HomeActivity)mContext).updateFragment(HomeActivity.ADDRESS_DETAILS,"datetime");
                }else{
                    pageType = "level";
                    txtHeader.setText(getString(R.string.select_service_level));
                    linLevelContainer.setVisibility(View.VISIBLE);
                    linDateTime.setVisibility(View.GONE);
                    showNext();
                }

                break;
            case R.id.img_receiver:
            case R.id.txt_next:
                if(chooseService){
                    if(pageType.equals("date")){
                        if(Constants.MODE != Constants.CORPERATION || PreferenceUtils.getQuote(mContext)){
                            if(chooseService){
                                if(Constants.MODE == Constants.GUEST){
                                    String email = edtEmail.getText().toString();
                                    Constants.guestEmail = email;
                                    if(!ValidationHelper.isValidEmail(email)){
                                        Toast.makeText(mContext, "Check Email",Toast.LENGTH_SHORT).show();
                                        return ;
                                    }
                                }

                                ((HomeActivity)mContext).updateFragment(HomeActivity.REVIEW, "");


                            }else{
                                Toast.makeText(mContext, getString(R.string.choose_service), Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            order_corporate();
                        }
                    }else{
                        pageType = "date";
                        txtHeader.setText(getString(R.string.select_pickup_date_time));
                        linLevelContainer.setVisibility(View.GONE);
                        linDateTime.setVisibility(View.VISIBLE);

                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Calendar calendar = Calendar.getInstance();
                                int ap = calendar.get(Calendar.AM_PM);
                                wheelHour.setSelectedItemPosition( hour -1 ); //hour > 12 ? hour - 12 :
                                wheelMinute.setSelectedItemPosition(calendar.get(Calendar.MINUTE) -1 );
                                wheelAp.setSelectedItemPosition(ap); //hour > 12 ? 1:0
                                wheelDate.setSelectedItemPosition(todayPosition);
                            }
                        },200);

                        showNext();

                    }
                }else{
                    Toast.makeText(mContext, getString(R.string.choose_service), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private  void openAddressDetaisl(){
        Intent intent = new Intent(mContext, AddressDetailsNewActivity.class);
        startActivity(intent);
    }



    LinearLayout linExpedited;
    LinearLayout linExpress;
    LinearLayout linEconomy;
    TextView txtExpedited;
    TextView txtExpeditedPrice;
    TextView txtExpeditedDuration;
    TextView txtExpress;
    TextView txtExpressPrice;
    TextView txtExpressDuration;
    TextView txtEconomy;
    TextView txtEconomyPrice;
    TextView txtEconomyDuration;

    private  void addService(){
        try{
            if(Constants.serviceModel != null){
                linService.removeAllViews();
                LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.row_service_leve, null);
                linExpedited = (LinearLayout)view.findViewById(R.id.lin_expedited);
                linExpress = (LinearLayout)view.findViewById(R.id.lin_express);
                linEconomy = (LinearLayout)view.findViewById(R.id.lin_economy);

                txtExpedited = (TextView)view.findViewById(R.id.txt_expedited);
                txtExpeditedPrice = (TextView)view.findViewById(R.id.txt_expedited_price);
                txtExpeditedDuration = (TextView)view.findViewById(R.id.txt_expedited_duration);

                txtExpeditedPrice.setText(getResources().getString(R.string.rupee) +  String.format("%.2f", Float.valueOf(Constants.priceType.expeditied_price)));
                txtExpeditedDuration.setText(Constants.priceType.getDuration(1,Constants.DELIVERY_STATUS));

                txtExpress = (TextView)view.findViewById(R.id.txt_express);
                txtExpressPrice = (TextView)view.findViewById(R.id.txt_express_price);
                txtExpressDuration = (TextView)view.findViewById(R.id.txt_express_duration);

                if(Constants.priceType.express_price.isEmpty()){
                    linExpress.setVisibility(View.GONE);
                }else{
                    txtExpressPrice.setText(getResources().getString(R.string.rupee) + String.format("%.2f", Float.valueOf(Constants.priceType.express_price)) );
                    txtExpressDuration.setText(Constants.priceType.getDuration(2, Constants.DELIVERY_STATUS));
                }

                txtEconomy = (TextView)view.findViewById(R.id.txt_economy);
                txtEconomyPrice = (TextView)view.findViewById(R.id.txt_economy_price);
                txtEconomyDuration = (TextView)view.findViewById(R.id.txt_economy_duration);

                if(Constants.priceType.economy_price.isEmpty()){
                    linEconomy.setVisibility(View.GONE);
                }else{
                    txtEconomyPrice.setText(getResources().getString(R.string.rupee) +  String.format("%.2f", Float.valueOf(Constants.priceType.economy_price)));
                    txtEconomyDuration.setText(Constants.priceType.getDuration(3, Constants.DELIVERY_STATUS));
                }

                linExpedited.setOnClickListener(this);
                linExpress.setOnClickListener(this);
                linEconomy.setOnClickListener(this);
                linService.addView(view);

                if(Constants.serviceModel != null && Constants.serviceModel.name != null ){
                    if(Constants.serviceModel.name.equals("Expedited")){
                        Constants.selectedServiceLevel = 1;
                        chooseService(1);
                        showEstmiatedDate();
                    }else if(Constants.serviceModel.name.equals("Express")){
                        Constants.selectedServiceLevel = 2;
                        chooseService(2);
                        showEstmiatedDate();
                    }else if(Constants.serviceModel.name.equals("Economy")){
                        Constants.selectedServiceLevel = 3;
                        chooseService(3);
                        showEstmiatedDate();
                    }
                }
            }
        }catch (Exception e){};

    }

    boolean chooseService = false;
    private void chooseService(int n){
        if(Constants.selectedMonth == -1){
            Constants.selectedMonth = month;
        }
        if(Constants.selectedDate == -1){
            Constants.selectedDate = day;
        }

        chooseService = true;
        showNext();

        linExpedited.setBackgroundColor(getResources().getColor(R.color.white_background));
        linExpress.setBackgroundColor(getResources().getColor(R.color.white_background));
        linEconomy.setBackgroundColor(getResources().getColor(R.color.white_background));

        switch (n){
            case 1:
                linExpedited.setBackgroundDrawable(getResources().getDrawable(R.drawable.service_level_back));

                Constants.serviceModel.name = Constants.priceType.expedited_name;
                Constants.serviceModel.price = Constants.priceType.expeditied_price;
                Constants.serviceModel.time_in = Constants.priceType.getDuration(n, Constants.DELIVERY_STATUS);
                break;

            case 2:
                linExpress.setBackgroundDrawable(getResources().getDrawable(R.drawable.service_level_back));
                Constants.serviceModel.name = Constants.priceType.express_name;
                Constants.serviceModel.price = Constants.priceType.express_price;
                Constants.serviceModel.time_in = Constants.priceType.getDuration(n, Constants.DELIVERY_STATUS);
                break;

            case 3:
                linEconomy.setBackgroundDrawable(getResources().getDrawable(R.drawable.service_level_back));

                Constants.serviceModel.name = Constants.priceType.economy_name;
                Constants.serviceModel.price = Constants.priceType.economy_price;
                Constants.serviceModel.time_in = Constants.priceType.getDuration(n, Constants.DELIVERY_STATUS);
                break;
        }
    }

    private void showEstmiatedDate(){

        if(Constants.selectedServiceLevel != 0){
            int currentTime  = Constants.selectedHour;
            int differentTime = 0;
            switch (Constants.selectedServiceLevel){
                case  1:
                    differentTime = 4;
                    break;
                case 2:
                    differentTime = 8;
                    break;
                case 3:
                    differentTime = 16;
                    break;
            }
            int i = 0;
            int minute = Constants.selectedMinute;
            if(currentTime > 19 ){
                i = 1;
                currentTime = 6;
                minute = 0;
            }else if(currentTime < 6){
                currentTime = 6;
                minute = 0;
            }

            while (currentTime + differentTime > 19){
                differentTime = currentTime + differentTime - 19 -1;
                if(differentTime == 0 && Constants.selectedMinute == 0){
                    currentTime = 20;
                    break;
                }else{
                    currentTime = 6;
                }
                i++;
            }

            if(i == 0){
                txtEstimated.setText(TimeHelper.getDate(Constants.selectedYear, Constants.selectedMonth, Constants.selectedDate) + "  " + TimeHelper.getTime(currentTime + differentTime  , minute)); //getString(R.string.estimated_time) + " " +
            }else{
                Calendar calendar = Calendar.getInstance();
                calendar.set(Constants.selectedYear, Constants.selectedMonth, Constants.selectedDate + i );
                txtEstimated.setText(TimeHelper.getDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) + "  " + TimeHelper.getTime(6 + differentTime , minute));//getString(R.string.estimated_time) + " " +
            }

        }else{

        }
    }

    private void order_corporate() {
        RequestParams params = new RequestParams();
        params.put("id", PreferenceUtils.getCorOrderId(mContext));
        params.put("user_id", PreferenceUtils.getCorporateUserId(mContext));
        params.put("name", Constants.corporateModel.name);
        params.put("address", Constants.corporateModel.address);
        params.put("phone", Constants.corporateModel.phone);
        params.put("description", Constants.corporateModel.details);
        params.put("truck", Constants.corporateModel.truck);
        params.put("date", Constants.dateModel.date);
        params.put("time", Constants.dateModel.time);

        params.put("device_id", DeviceUtil.getDeviceId(mContext));
        params.put("device_type", DeviceUtil.getDeviceName());

        // order address
        JSONArray addressArray = new JSONArray();
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("s_address", Constants.addressModel.sourceAddress);
        jsonMap.put("s_area", Constants.addressModel.sourceArea);
        jsonMap.put("s_city", Constants.addressModel.sourceCity);
        jsonMap.put("s_state", Constants.addressModel.sourceState);
        jsonMap.put("s_pincode", Constants.addressModel.sourcePinCode);
        jsonMap.put("s_phone", Constants.addressModel.sourcePhonoe + ":" + Constants.addressModel.senderName);
        jsonMap.put("s_landmark", Constants.addressModel.sourceLandMark);
        jsonMap.put("s_instruction", Constants.addressModel.sourceInstruction);
        jsonMap.put("s_lat", String.valueOf(Constants.addressModel.sourceLat));
        jsonMap.put("s_lng", String.valueOf(Constants.addressModel.sourceLng));

        jsonMap.put("d_address", Constants.addressModel.desAddress);
        jsonMap.put("d_area", Constants.addressModel.desArea);
        jsonMap.put("d_city", Constants.addressModel.desCity);
        jsonMap.put("d_state", Constants.addressModel.desState);
        jsonMap.put("d_pincode", Constants.addressModel.desPinCode);
        jsonMap.put("d_landmark", Constants.addressModel.desLandMark);
        jsonMap.put("d_instruction", Constants.addressModel.desInstruction);
        jsonMap.put("d_lat", String.valueOf(Constants.addressModel.desLat));
        jsonMap.put("d_lng", String.valueOf(Constants.addressModel.desLng));
        jsonMap.put("d_phone", String.valueOf(Constants.addressModel.desPhone));
        jsonMap.put("d_name", String.valueOf(Constants.addressModel.desName));

        JSONObject addressJson = new JSONObject(jsonMap);
        addressArray.put(addressJson);
        params.put("orderaddress", addressArray.toString());

        WebClient.post(Constants.BASE_URL_ORDER + "order_corporate", params,
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
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(mContext, getResources().getString(R.string.quote_message), Toast.LENGTH_LONG).show();
                                }else if(response.getString("result").equals("200")){
                                    showConfirmDialog();
                                    //Constants.page_type = "";
                                    Constants.corporateModel = null;
                                    Constants.corporateModel = new CorporateModel();
                                    Constants.addressModel = null;
                                    Constants.addressModel = new AddressModel();
                                }
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

    private void showConfirmDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
        //builder1.setMessage("Write your message here.");
        builder1.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (View)inflater.inflate(com.gun0912.tedpicker.R.layout.dialog_forgot, null);
        builder1.setView(view);
        final AlertDialog alert11 = builder1.create();
        TextView txtTitle = (TextView)view.findViewById(com.gun0912.tedpicker.R.id.txt_title);
        TextView txtContent  = (TextView)view.findViewById(com.gun0912.tedpicker.R.id.txt_content);
        txtTitle.setText(""); //getResources().getString(R.string.text_message)
        txtContent.setText(getResources().getString(R.string.email_corporation));

        Button btnOk = (Button)view.findViewById(com.gun0912.tedpicker.R.id.btn_ok);
        btnOk.setText("I Agree");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert11.hide();
            }
        });
        alert11.show();
    }



    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        switch (picker.getId()){
            case R.id.wheel_hour:
                selectedHour = Integer.valueOf(hours.get(wheelHour.getCurrentItemPosition()));
                updateTime(status);
                break;
            case R.id.wheel_minute:
                selectedMinute = Integer.valueOf(minutes.get(wheelMinute.getCurrentItemPosition()));
                updateTime(status);
                break;
            case R.id.wheel_ap:
                status = aps.get(wheelAp.getCurrentItemPosition());
                updateTime(status);
                break;
            case R.id.wheel_date:

                selectedMonth = dateCalendars.get(wheelDate.getCurrentItemPosition()).get(Calendar.MONTH);// - 1;
                selectedYear = dateCalendars.get(wheelDate.getCurrentItemPosition()).get(Calendar.YEAR);
                selectedDay = dateCalendars.get(wheelDate.getCurrentItemPosition()).get(Calendar.DATE);

                showFlag = false;
                if((selectedYear == year && selectedMonth > month )|| ( selectedYear == year && selectedMonth == month && selectedDay >= day) || selectedYear > year){
                    if(selectedDay == day){
                        if(hour > Constants.selectedHour || (hour == Constants.selectedHour && minute >= Constants.selectedMinute)){

                            edtTime.setText(TimeHelper.getTime(hour, minute));
                            //Constants.dateModel.time = TimeHelper.getTwoDigital(hour_of_12_hour_format )+ " : " + TimeHelper.getTwoDigital(selectedMinute )+ " " + status;
                            Constants.dateModel.time = TimeHelper.getTime(hour, minute);
                            Constants.selectedHour = hour;
                            Constants.selectedMinute = minute;
                            showEstmiatedDate();
                        }
                    }
                    edtDate.setText(TimeHelper.getDate(selectedYear, selectedMonth, selectedDay));
                    Constants.dateModel.date = TimeHelper.getDate(selectedYear, selectedMonth, selectedDay);
                    chooseDate = true;
                    Constants.selectedYear = selectedYear;
                    Constants.selectedMonth = selectedMonth;
                    Constants.selectedDate = selectedDay;
                    showEstmiatedDate();
                    showReviewButton();
                }else{
                    //Toast.makeText(DateTimeActivity.this, "Pickup date should not be past date", Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContext, "Pickup time should not be in the past", Toast.LENGTH_SHORT).show();
                    chooseDate = false;

                    hideNext();
                }


                break;

        }
    }

    private void updateTime(String status){

        int hour_of_12_hour_format;
        if(selectedHour > 11){
            hour_of_12_hour_format = selectedHour - 12;
        }
        else {
            hour_of_12_hour_format = selectedHour;
        }

        if(status.equals("PM")){
            selectedHour = selectedHour + 12;
        }

        String current = new StringBuilder().append(day)
                .append("-").append(month + 1).append("-").append(year)
                .append(" ").toString();
        if(Constants.dateModel.date.equals(current)){
            if(hour > selectedHour || ( hour == selectedHour && minute > selectedMinute)){
                //Toast.makeText(DateTimeActivity.this, "Pickup date should not be past date", Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext , "Pickup time should not be in the past", Toast.LENGTH_SHORT).show();
                hideNext();
                return;
            }
        }

        Constants.selectedHour = selectedHour;
        Constants.selectedMinute = selectedMinute;

        showFlag = false;
        edtTime.setText( TimeHelper.getTwoDigital( hour_of_12_hour_format , true)+ " : " + TimeHelper.getTwoDigital(selectedMinute , false)+ " " + status);
        Constants.dateModel.time =TimeHelper.getTwoDigital(hour_of_12_hour_format , true)+ " : " + TimeHelper.getTwoDigital(selectedMinute, false)+ " " + status;
        showEstmiatedDate();
        showReviewButton();

    }





}
