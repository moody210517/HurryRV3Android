package com.hurry.custom.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import com.google.android.gms.maps.model.LatLng;
import com.hurry.custom.R;
import com.hurry.custom.model.AddressModel;
import com.hurry.custom.model.BasicModel;
import com.hurry.custom.model.CarrierModel;
import com.hurry.custom.model.CityModel;
import com.hurry.custom.model.CorporateModel;
import com.hurry.custom.model.DateModel;
import com.hurry.custom.model.OrderCorporateHisModel;
import com.hurry.custom.model.OrderHisModel;
import com.hurry.custom.model.OrderModel;
import com.hurry.custom.model.OrderRescheduleModel;
import com.hurry.custom.model.OrderTrackModel;
import com.hurry.custom.model.PlaceAutocomplete;
import com.hurry.custom.model.PriceType;
import com.hurry.custom.model.QuoteCoperationModel;
import com.hurry.custom.model.QuoteModel;
import com.hurry.custom.model.ServiceModel;
import com.hurry.custom.model.TruckModel;
import com.hurry.custom.view.popup.RecordPopup;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 6/2/2016.
 */

public class Constants {

    //key hash:    rdMpsBlnq1mSIMzaszg0rukmPqw=
    //Package name:  com.hurry.custom
    //Default Activity:  com.hurry.custom.view.activity.MainActivity

    public static final String URL = "https://mobileapi.hurryr.in";//recent
    //public static final String URL = "http://pgollapudi-001-site1.atempurl.com";
    public static final String BASE_URL = URL + "/WebService/";
    public static final String BASE_URL_ORDER = URL + "/Order/";
    public static final String FORGOT  = URL + "/Forgot/";
    public static final String PHOTO_URL = URL + "/uploads/";
    public static final String BASIC_DATA_URL = URL + "/Basic/";
    public static int MODE = 0;
    public static final int PERSONAL = 0;
    public static final int GUEST = 1;
    public static final int CORPERATION = 2;

    public static int ORDER_TYPE = 0;
    public static int CAMERA_OPTION = 1;
    public static int ITEM_OPTION = 2;
    public static int PACKAGE_OPTION = 3;

    public static OrderModel cameraOrderModel = new OrderModel();
    public static OrderModel itemOrderModel = new OrderModel();
    public static OrderModel packageOrderModel = new OrderModel();

    public static AddressModel addressModel = new AddressModel();
    public static DateModel dateModel = new DateModel();
    public static ServiceModel serviceModel = new ServiceModel();
    public static CarrierModel carrierModel = new CarrierModel();
    public static String state;

    public static ArrayList<QuoteModel> quoteModels = new ArrayList<>();
    public static ArrayList<QuoteCoperationModel> quoteCoperationModels = new ArrayList<>();

    public static ArrayList<OrderHisModel> orderHisModels = new ArrayList<>();
    public static ArrayList<OrderCorporateHisModel> orderCorporateHisModels = new ArrayList<>();
    public static ArrayList<OrderHisModel> cancelModels = new ArrayList<>();
    public static ArrayList<OrderRescheduleModel> orderRescheduleModels = new ArrayList<>();

    public static ArrayList<BasicModel> areaLists = new ArrayList<>();
    public static ArrayList<BasicModel> cityLists = new ArrayList<>();
    public static ArrayList<BasicModel> pincodeLists = new ArrayList<>();
    public static ArrayList<BasicModel> itemLists = new ArrayList<>();

    public static ArrayList<TruckModel> truckModels = new ArrayList<>();
    public static OrderTrackModel orderTrackModel = new OrderTrackModel();
    public static ArrayList<CityModel> cityModels = new ArrayList<>();

    public static ArrayList<LatLng> cityBounds = new ArrayList<>();
    public static ArrayList<PlaceAutocomplete> placeAutocompletes = new ArrayList<>();
    public static String cityName = "";

    public static String page_type = "home";
    public static String quote_order_id = "";
    public static String quote_id = "";
    public static String delivery = "";
    public static String loadType = "";
    public static  String trackId = "";
    public static String  paymentType = "Cash on Pick Up";

    public static PriceType priceType = new PriceType();
    public static CorporateModel corporateModel = new CorporateModel();
    public static String guestEmail = "";
    public static String corDetails = "";
    public static String corLoadType = "";

    public static int selectedYear = -1;
    public static int selectedHour = -1, selectedMinute = -1;
    public static int selectedMonth = -1 , selectedDate = -1;
    public static int selectedServiceLevel = 0;

    public  static void makePhoneCall(Context context, String finalPhonenumber){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + finalPhonenumber));
        context.startActivity(callIntent);
    }

    public static String getDimetion(int i){
        try{
            if(!Constants.itemOrderModel.itemModels.get(i).dimension1.isEmpty() && !Constants.itemOrderModel.itemModels.get(i).dimension2.isEmpty() && !Constants.itemOrderModel.itemModels.get(i).dimension3.isEmpty()){
                return Constants.itemOrderModel.itemModels.get(i).dimension1 + "X" + Constants.itemOrderModel.itemModels.get(i).dimension2 + "X" + Constants.itemOrderModel.itemModels.get(i).dimension3;
            }
        }catch (Exception e){};
        return "";
    }


    public static void clearData(){

        cameraOrderModel.itemModels.clear();
        itemOrderModel.itemModels.clear();
        packageOrderModel.itemModels.clear();
        guestEmail = "";
        cameraOrderModel = null;
        itemOrderModel = null;
        packageOrderModel = null;
        addressModel = null;
        dateModel = null;
        serviceModel = null;

        quoteModels = null;
        quoteCoperationModels = null;
      //  orderHisModels = null;
        orderRescheduleModels = null;

        //page_type = "";
        //corporateModel = null;

        cameraOrderModel = new OrderModel();
        itemOrderModel = new OrderModel();
        packageOrderModel = new OrderModel();

        addressModel = new AddressModel();
        dateModel = new DateModel();
        serviceModel = new ServiceModel();
        quoteModels = new ArrayList<>();
        quoteCoperationModels = new ArrayList<>();
     //   orderHisModels = new ArrayList<>();
        orderRescheduleModels = new ArrayList<>();

        //corporateModel = new CorporateModel();

        selectedHour = -1;
        selectedMinute = -1;
        selectedMonth = -1 ;
        selectedDate = -1;
        selectedServiceLevel = 0;

    }


    public static void clearCorporateData(){

        cameraOrderModel.itemModels.clear();
        itemOrderModel.itemModels.clear();
        packageOrderModel.itemModels.clear();

        cameraOrderModel = null;
        itemOrderModel = null;
        packageOrderModel = null;

        dateModel = null;
        serviceModel = null;

        quoteModels = null;
        quoteCoperationModels = null;
        orderHisModels = null;
        orderRescheduleModels = null;

        //page_type = "";
        //corporateModel = null;

        cameraOrderModel = new OrderModel();
        itemOrderModel = new OrderModel();
        packageOrderModel = new OrderModel();

        dateModel = new DateModel();
        serviceModel = new ServiceModel();

        quoteModels = new ArrayList<>();
        quoteCoperationModels = new ArrayList<>();
        orderHisModels = new ArrayList<>();
        orderRescheduleModels = new ArrayList<>();

        //corporateModel = new CorporateModel();

        selectedHour = -1;
        selectedMinute = -1;
        selectedMonth = -1 ;
        selectedDate = -1;
        selectedServiceLevel = 0;
    }


    public static List<String> quantity = new ArrayList<String>() {{
        add("1");
        add("2");
        add("3");
        add("4");
        add("5");
        add("6");
        add("7");
        add("8");
        add("9");
        add("10");
    }};

    public static List<String> weight = new ArrayList<String>() {{
        add("1k");
        add("1 to 3 Kgs");
        add("4 to 6 kgs");
        add("7 to 10 kgs");
        add("11 to 15 kgs");
        add("16 to 20 kgs");
        add("21 to 25 kgs");
        add("26 to 30 kgs");
        add("31 to 40 kgs");
        add("41 to 50 kgs");
    }};

    public static void initWeight(){
        weight.clear();
        weight_value.clear();
        for(int k = 0; k < 100 ; k++){
            weight.add(String.valueOf(k + 1) + "kg");
            weight_value.add(k+1);
        }
    }


    public static List<Integer> weight_value = new ArrayList<Integer>() {{
        add(1);
        add(3);
        add(6);
        add(10);
        add(15);
        add(20);
        add(25);
        add(30);
        add(40);
        add(50);
    }};

    public static List<String> packageLists = new ArrayList<String>() {{
        add("Small size Package");
        add("Medium size Package");
        add("Large size Package");
        add("Extra Large size Package");
        add("Giant size Package");
    }};

    public static List<String> paymentWay = new ArrayList<String>() {{

        add("Cash on Pick up");
        add("Pay using Card");
        add("Net Banking");
        add("COD");
    }};
    public static List<String> quotePaymentWay = new ArrayList<String>() {{
        add("Pay using Card");
        add("Net Banking");
    }};

    public static List<String> securityLists = new ArrayList<String>() {{
        add("What is your favorite Sports team?");
        add("What is your favorite movie?");
        add("What was your favorite sport in high school?");
        add("What was your favorite food?");
        add("What was your childhood nickname?");

        add("What is the name of your favorite childhood friend?");
        add("What was the name of the hospital where you were born?");
        add("What is your favorite Pass Time?");
        add("What is your favorite Car?");
        add("What is your favorite holiday spot?");
    }};

    public static String getTruck(String truck){
        for(int k = 0 ;k < Constants.truckModels.size() ; k++){
            if(Constants.truckModels.get(k).code.equals(truck)){
                return Constants.truckModels.get(k).name;
            }
        }
        return "";
    }
    public static String getDimentionString(String dim1, String dim2, String dim3){
        if(!dim1.isEmpty() && !dim2.isEmpty() && !dim3.isEmpty() && !dim1.equals("null") && !dim2.equals("null") &&  !dim3.equals("null")){
            return dim1 + "X" + dim2 + "X" + dim3;
        }
        return "";
    }

    public static  int getTotalWeight(){
        int total = 0;
        if(Constants.ORDER_TYPE == Constants.CAMERA_OPTION){
            for(int k = 0; k <  Constants.cameraOrderModel.itemModels.size() ; k++){
                total += Constants.cameraOrderModel.itemModels.get(k).weight_value;
            }
        }else if(Constants.ORDER_TYPE == Constants.ITEM_OPTION){
            for(int k = 0; k <  Constants.itemOrderModel.itemModels.size() ; k++){
                total += Constants.itemOrderModel.itemModels.get(k).weight_value;
            }
        }else if(Constants.ORDER_TYPE == Constants.PACKAGE_OPTION){
            for(int k = 0; k <  Constants.packageOrderModel.itemModels.size() ; k++){
                if(Constants.packageOrderModel.itemModels.get(k).weight_value == 0){
                    total += Integer.valueOf(Constants.packageOrderModel.itemModels.get(k).weight);
                }else{
                    total += Constants.packageOrderModel.itemModels.get(k).weight_value;
                }

            }
        }
        return total;
    }



    public static void showImage(Context context,View fragment,  String url, String type) {

        RecordPopup recordPopup = new RecordPopup(context, type, url);
        recordPopup.showAtLocation(fragment, 0, 0, url, type); //DeviceUtil.dipToPixels(this, 50)

    }



    public static String getPrice(Context context, String price){
        if(price == null || price.isEmpty()){
            price = "0";
        }
        return context.getResources().getString(R.string.rupee) + String.format("%.2f", Float.valueOf(price));
    }

    public static void showConfirmDialog(Context context, String title, String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }}).show();
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                }).show();
    }

    public static  ArrayList<LatLng> getGeofences(String geofenc){
        ArrayList<LatLng> latLngs = new ArrayList<>();
        String latlng[] = geofenc.split(":");
        for(int k = 0; k < latlng.length  ; k++){
            String item[] = latlng[k].split(",");
            if(item.length == 2){
                LatLng latLng = new LatLng(Double.valueOf(item[0]), Double.valueOf(item[1]));
                latLngs.add(latLng);
            }
        }
        return latLngs;
    }

}
