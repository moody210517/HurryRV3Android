package com.hurry.custom.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
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
import com.hurry.custom.view.BaseBackActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 3/18/2017.
 */

public class DateTimeActivity extends BaseBackActivity implements View.OnClickListener, WheelPicker.OnItemSelectedListener{

    public static final int DATE_PICKER_ID = 1111;
    int year, month, day, hour, minute;


    SupportMapFragment mapView;
    GoogleMap map;
    Marker sourceMarker, destinationMarker;
    LatLng sourcePosition, destinationPosition;
    ArrayList<LatLng> points;
    PolylineOptions lineOptions;
    Polyline polyLine;

    EditText edtDate;
    EditText edtTime;

    LinearLayout linEmail;
    TextView txtEstimated, txtWeight, txtDistance;
    EditText edtEmail;


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_header) TextView txtHeader;
    @BindView(R.id.img_sender) ImageView imgSender;
    @BindView(R.id.img_receiver) ImageView imgReceiver;
    @BindView(R.id.lin_date_time) LinearLayout linDateTime;
    @BindView(R.id.txt_source) TextView txtSource;
    @BindView(R.id.txt_destination) TextView txtDestination;
    @BindView(R.id.lin_service) LinearLayout linService;


    @BindView(R.id.wheel_date_picker) WheelDatePicker wheelDatePicker;
    @BindView(R.id.wheel_hour) WheelPicker wheelHour;
    @BindView(R.id.wheel_minute) WheelPicker wheelMinute;
    @BindView(R.id.wheel_ap) WheelPicker wheelAp;
    @BindView(R.id.scrollview) ScrollView scrollView;
    @BindView(R.id.lin_level_container) LinearLayout linLevelContainer;

    int selectedYear, selectedMonth, selectedDay;
    int selectedHour, selectedMinute;
    String status = "AM";

    ArrayList<String> hours = new ArrayList<String>();
    ArrayList<String> minutes = new ArrayList<String>();
    ArrayList<String> aps = new ArrayList<String>();

    String pageType = "level";

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_date_time);
        ButterKnife.bind(this);
        initBackButton(toolbar, "Date & Time");

        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.locale = locale;
        getResources().updateConfiguration(config,
        getResources().getDisplayMetrics());

        initView();
        initDateTimePickers();

        if(Constants.MODE != Constants.CORPERATION ){
            addService();
        }else{
            if(PreferenceUtils.getQuote(this)){
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
                        if(DateTimeActivity.this.hour > Constants.selectedHour || (DateTimeActivity.this.hour == Constants.selectedHour && DateTimeActivity.this.minute > Constants.selectedMinute)){

                            edtTime.setText(TimeHelper.getTime(DateTimeActivity.this.hour, DateTimeActivity.this.minute));
                            //Constants.dateModel.time = TimeHelper.getTwoDigital(hour_of_12_hour_format )+ " : " + TimeHelper.getTwoDigital(selectedMinute )+ " " + status;
                            Constants.dateModel.time = TimeHelper.getTime(DateTimeActivity.this.hour, DateTimeActivity.this.minute);
                            Constants.selectedHour = DateTimeActivity.this.hour;
                            Constants.selectedMinute = DateTimeActivity.this.minute;
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
                    Toast.makeText(DateTimeActivity.this, "Pickup time should not be in the past", Toast.LENGTH_SHORT).show();
                    chooseDate = false;
                    imgReceiver.setVisibility(View.GONE);
                }
            }
        });
        wheelDatePicker.setSelectedItemTextColor(getColor(R.color.red_pop));
        wheelDatePicker.setItemTextColor(getColor(R.color.wheel_text_color));
        wheelDatePicker.setCurved(true);
        wheelDatePicker.setItemTextSize(42);

        for(int k = 1 ; k <= 12 ; k++){
            hours.add(String.valueOf(k));
        }
        wheelHour.setData(hours);
        wheelHour.setSelectedItemTextColor(getColor(R.color.red_pop));
        wheelHour.setItemTextColor(getColor(R.color.wheel_text_color));
        wheelHour.setCurved(true);
        wheelHour.setItemTextSize(42);
        int hour = calendar.get(Calendar.HOUR);
        wheelHour.setSelectedItemPosition( hour ); //hour > 12 ? hour - 12 :
        wheelHour.setSelected(true);
        wheelHour.setOnItemSelectedListener(this);



        for(int k = 1 ; k <= 60 ; k++){
            minutes.add(String.valueOf(k));
        }

        wheelMinute.setData(minutes);
        wheelMinute.setSelectedItemTextColor(getColor(R.color.red_pop));
        wheelMinute.setItemTextColor(getColor(R.color.wheel_text_color));
        wheelMinute.setCurved(true);
        wheelMinute.setItemTextSize(42);
        wheelMinute.setSelectedItemPosition(calendar.get(Calendar.MINUTE));
        wheelMinute.setSelected(true);
        wheelMinute.setOnItemSelectedListener(this);



        aps.add("AM");
        aps.add("PM");
        wheelAp.setData(aps);
        wheelAp.setSelectedItemTextColor(getColor(R.color.red_pop));
        wheelAp.setItemTextColor(getColor(R.color.wheel_text_color));
        wheelAp.setCurved(true);
        wheelAp.setItemTextSize(42);
        int ap = calendar.get(Calendar.AM_PM);
        wheelAp.setSelectedItemPosition(ap); //hour > 12 ? 1:0
        wheelAp.setSelected(true);
        wheelAp.setOnItemSelectedListener(this);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wheelHour.setSelectedItemPosition( hour ); //hour > 12 ? hour - 12 :
                wheelMinute.setSelectedItemPosition(calendar.get(Calendar.MINUTE) );
                wheelAp.setSelectedItemPosition(ap); //hour > 12 ? 1:0
            }
        },800);
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


    private  void initView(){

        imgReceiver.setOnClickListener(this);

        imgSender.setOnClickListener(this);
        txtSource.setText(Constants.addressModel.sourceAddress);
        txtDestination.setText(Constants.addressModel.desAddress);

        linLevelContainer.setVisibility(View.VISIBLE);
        linDateTime.setVisibility(View.GONE);

        imgReceiver.setVisibility(View.GONE);
        imgSender.setVisibility(View.GONE);

        txtEstimated = (TextView)findViewById(R.id.txt_estimated);
        edtDate = (EditText)findViewById(R.id.edt_date);
        edtDate.setHint("Input Date");

        edtTime = (EditText)findViewById(R.id.edt_time);
        txtDistance = (TextView)findViewById(R.id.txt_distance);
        txtWeight = (TextView)findViewById(R.id.txt_weight);

        if(Constants.priceType.distance == 0 ){
            txtDistance.setText( String.format("%.2f", distance(Constants.addressModel.sourceLat, Constants.addressModel.desLat, Constants.addressModel.sourceLng, Constants.addressModel.desLng,0,0)/1000 + 8) + "Km(s)");
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


        if(DeviceUtil.isTablet(this)){
            edtDate.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            edtTime.setBackgroundDrawable(getResources().getDrawable(R.drawable.broder_noe));
            findViewById(R.id.line_date).setVisibility(View.VISIBLE);
            findViewById(R.id.line_time).setVisibility(View.VISIBLE);
        }
        linEmail = (LinearLayout)findViewById(R.id.lin_email);
        edtEmail = (EditText)findViewById(R.id.edt_email);
        if(Constants.MODE == Constants.GUEST){
            linEmail.setVisibility(View.VISIBLE);
        }


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

    private void showReviewButton(){

        imgReceiver.setVisibility(View.VISIBLE);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }


    private void setUpMapView(GoogleMap mmap) {
        this.map = mmap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


            //getPath("https://maps.googleapis.com/maps/api/directions/json", source, destination);
            //new GetRoute(this, source, destination).execute();
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

            }catch (Exception e){

            }
        }
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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, pickerListener1, year, month , day);
                showFlag = false;
                return datePickerDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener1 = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            showFlag = false;
            if((selectedYear == year && selectedMonth > month )|| ( selectedYear == year && selectedMonth == month && selectedDay >= day) || selectedYear > year){
                if(selectedDay == day){
                    if(DateTimeActivity.this.hour > Constants.selectedHour || (DateTimeActivity.this.hour == Constants.selectedHour && DateTimeActivity.this.minute > Constants.selectedMinute)){

                        edtTime.setText(TimeHelper.getTime(DateTimeActivity.this.hour, DateTimeActivity.this.minute));
                        //Constants.dateModel.time = TimeHelper.getTwoDigital(hour_of_12_hour_format )+ " : " + TimeHelper.getTwoDigital(selectedMinute )+ " " + status;
                        Constants.dateModel.time = TimeHelper.getTime(DateTimeActivity.this.hour, DateTimeActivity.this.minute);
                        Constants.selectedHour = DateTimeActivity.this.hour;
                        Constants.selectedMinute = DateTimeActivity.this.minute;


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
                Toast.makeText(DateTimeActivity.this, "Pickup time should not be in the past", Toast.LENGTH_SHORT).show();
                chooseDate = false;
                DateTimeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.postDelayed(runnable, 300);
                    }
                });
            }
        }
    };

    Handler  mHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            showDialog(DATE_PICKER_ID);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.img_back:

                if(!PreferenceUtils.getQuote(DateTimeActivity.this)){
                    openAddressDetaisl();
                }
                finish();
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
                pageType = "level";
                txtHeader.setText(getString(R.string.select_service_level));
                linLevelContainer.setVisibility(View.VISIBLE);
                linDateTime.setVisibility(View.GONE);

                imgReceiver.setVisibility(View.VISIBLE);
                imgSender.setVisibility(View.GONE);


                break;
            case R.id.img_receiver:
            case R.id.txt_next:
                if(chooseService){
                    if(pageType.equals("date")){
                        if(Constants.MODE != Constants.CORPERATION || PreferenceUtils.getQuote(this)){
                            if(chooseService){
                                if(Constants.MODE == Constants.GUEST){
                                    String email = edtEmail.getText().toString();
                                    Constants.guestEmail = email;
                                    if(!ValidationHelper.isValidEmail(email)){
                                        Toast.makeText(DateTimeActivity.this, "Check Email",Toast.LENGTH_SHORT).show();
                                        return ;
                                    }
                                }
                                Intent review = new Intent(DateTimeActivity.this, ReviewActivity.class);
                                startActivity(review);

                            }else{
                                Toast.makeText(DateTimeActivity.this, getString(R.string.choose_service), Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            order_corporate();
                        }
                    }else{
                        pageType = "date";
                        txtHeader.setText(getString(R.string.select_pickup_date_time));
                        linLevelContainer.setVisibility(View.GONE);
                        linDateTime.setVisibility(View.VISIBLE);
                        imgReceiver.setVisibility(View.GONE);
                        imgSender.setVisibility(View.VISIBLE);
                    }
                }else{
                    Toast.makeText(DateTimeActivity.this, getString(R.string.choose_service), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private  void openAddressDetaisl(){
        Intent intent = new Intent(this, AddressDetailsNewActivity.class);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showTimePicker(){
        showFlag = true;

     //   Calendar calendar = Calendar.getInstance();
        Date dt = new Date();
        final int hour = dt.getHours(); //calendar.get(Calendar.HOUR_OF_DAY);
        int minute =  dt.getMinutes();///calendar.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker = null;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String status = "AM";
                if(selectedHour > 11)
                {
                    status = "PM";
                }
                int hour_of_12_hour_format;
                if(selectedHour > 11){
                    hour_of_12_hour_format = selectedHour - 12;
                }
                else {
                    hour_of_12_hour_format = selectedHour;
                }

                String current = new StringBuilder().append(day)
                        .append("-").append(month + 1).append("-").append(year)
                        .append(" ").toString();
                if(Constants.dateModel.date.equals(current)){
                    if(DateTimeActivity.this.hour > selectedHour || (DateTimeActivity.this.hour == selectedHour && DateTimeActivity.this.minute > selectedMinute)){
                        //Toast.makeText(DateTimeActivity.this, "Pickup date should not be past date", Toast.LENGTH_SHORT).show();
                        Toast.makeText(DateTimeActivity.this, "Pickup time should not be in the past", Toast.LENGTH_SHORT).show();
                        showTimePicker();
                        return;
                    }
                }
                Constants.selectedHour = selectedHour;
                Constants.selectedMinute = selectedMinute;

                showFlag = false;
                edtTime.setText( TimeHelper.getTwoDigital( hour_of_12_hour_format , true)+ " : " + TimeHelper.getTwoDigital(selectedMinute , false)+ " " + status);
                Constants.dateModel.time =TimeHelper.getTwoDigital(hour_of_12_hour_format , true)+ " : " + TimeHelper.getTwoDigital(selectedMinute, false)+ " " + status;
                showEstmiatedDate();

            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
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
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.row_service_leve, null);
                linExpedited = (LinearLayout)view.findViewById(R.id.lin_expedited);
                linExpress = (LinearLayout)view.findViewById(R.id.lin_express);
                linEconomy = (LinearLayout)view.findViewById(R.id.lin_economy);

                txtExpedited = (TextView)view.findViewById(R.id.txt_expedited);
                txtExpeditedPrice = (TextView)view.findViewById(R.id.txt_expedited_price);
                txtExpeditedDuration = (TextView)view.findViewById(R.id.txt_expedited_duration);

                txtExpeditedPrice.setText(getResources().getString(R.string.rupee) +  String.format("%.2f", Float.valueOf(Constants.priceType.expeditied_price)));
                txtExpeditedDuration.setText(Constants.priceType.getDuration(1, Constants.DELIVERY_STATUS));

                txtExpress = (TextView)view.findViewById(R.id.txt_express);
                txtExpressPrice = (TextView)view.findViewById(R.id.txt_express_price);
                txtExpressDuration = (TextView)view.findViewById(R.id.txt_express_duration);

                txtExpressPrice.setText(getResources().getString(R.string.rupee) + String.format("%.2f", Float.valueOf(Constants.priceType.express_price)) );
                txtExpressDuration.setText(Constants.priceType.getDuration(2,Constants.DELIVERY_STATUS));

                txtEconomy = (TextView)view.findViewById(R.id.txt_economy);
                txtEconomyPrice = (TextView)view.findViewById(R.id.txt_economy_price);
                txtEconomyDuration = (TextView)view.findViewById(R.id.txt_economy_duration);

                txtEconomyPrice.setText(getResources().getString(R.string.rupee) +  String.format("%.2f", Float.valueOf(Constants.priceType.economy_price)));
                txtEconomyDuration.setText(Constants.priceType.getDuration(3, Constants.DELIVERY_STATUS));

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
        imgReceiver.setVisibility(View.VISIBLE);
        linExpedited.setBackgroundColor(Color.WHITE);
        linExpress.setBackgroundColor(Color.WHITE);
        linEconomy.setBackgroundColor(Color.WHITE);



        switch (n){
            case 1:
                linExpedited.setBackgroundDrawable(getResources().getDrawable(R.drawable.service_level_back));

                Constants.serviceModel.name = Constants.priceType.expedited_name;
                Constants.serviceModel.price = Constants.priceType.expeditied_price;
                Constants.serviceModel.time_in = Constants.priceType.getDuration(1,Constants.DELIVERY_STATUS);
                break;

            case 2:
                linExpress.setBackgroundDrawable(getResources().getDrawable(R.drawable.service_level_back));
                Constants.serviceModel.name = Constants.priceType.express_name;
                Constants.serviceModel.price = Constants.priceType.express_price;
                Constants.serviceModel.time_in = Constants.priceType.getDuration(2,Constants.DELIVERY_STATUS);
                break;

            case 3:
                linEconomy.setBackgroundDrawable(getResources().getDrawable(R.drawable.service_level_back));

                Constants.serviceModel.name = Constants.priceType.economy_name;
                Constants.serviceModel.price = Constants.priceType.economy_price;
                Constants.serviceModel.time_in = Constants.priceType.getDuration(3,Constants.DELIVERY_STATUS);
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
        params.put("id", PreferenceUtils.getCorOrderId(this));
        params.put("user_id", PreferenceUtils.getCorporateUserId(this));
        params.put("name", Constants.corporateModel.name);
        params.put("address", Constants.corporateModel.address);
        params.put("phone", Constants.corporateModel.phone);
        params.put("description", Constants.corporateModel.details);
        params.put("truck", Constants.corporateModel.truck);
        params.put("date", Constants.dateModel.date);
        params.put("time", Constants.dateModel.time);

        params.put("device_id", DeviceUtil.getDeviceId(this));
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
                                if(response.getString("result").equals("400")){
                                    Toast.makeText(DateTimeActivity.this, getResources().getString(R.string.quote_message), Toast.LENGTH_LONG).show();
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
                        hideProgressDialog();
                    }
                    ;
                });
    }

    private void showConfirmDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        //builder1.setMessage("Write your message here.");
        builder1.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                finish();
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
            if(DateTimeActivity.this.hour > selectedHour || (DateTimeActivity.this.hour == selectedHour && DateTimeActivity.this.minute > selectedMinute)){
                //Toast.makeText(DateTimeActivity.this, "Pickup date should not be past date", Toast.LENGTH_SHORT).show();
                Toast.makeText(DateTimeActivity.this, "Pickup time should not be in the past", Toast.LENGTH_SHORT).show();
                imgReceiver.setVisibility(View.GONE);
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
