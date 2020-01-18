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
import com.hurry.custom.view.activity.HomeActivity;
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

    public static int OUT_STATION = 1;
    public static int SAME_CITY = 2;
    public static int INTERNATIONAL = 3;
    public static int DELIVERY_STATUS = 0;
    public static int MAP_HEIGHT = 0;

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
        ORDER_TYPE = 0;

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
        if(Constants.DELIVERY_STATUS == Constants.SAME_CITY){
            for(int k = 0; k < 100 ; k++){
                weight.add(String.valueOf(k + 1) + "kg");
                weight_value.add(k+1);
            }
        }else if(Constants.DELIVERY_STATUS == Constants.OUT_STATION){
            for(int k = 0; k < 70 ; k++){
                weight.add(String.valueOf(k + 1) + "kg");
                weight_value.add(k+1);
            }
        }else if(Constants.DELIVERY_STATUS == Constants.INTERNATIONAL){
            for(int k = 0; k < 70 ; k++){
                weight.add(String.valueOf(k + 1) + "kg");
                weight_value.add(k+1);
            }
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

    public static List<String> paymentWay1 = new ArrayList<String>() {{
        add("Cash on Pick up");
        add("Pay using Card");
        add("Net Banking");
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
                .setCancelable(false)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ((HomeActivity)context).updateOrderHis(2);
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

    public static int getInternational(int weight, int type ,boolean isFood){
        if(type == 1){
            switch (weight){
                case 1:
                    return 1452;
                case 2:
                    return 1914;
                case 3:
                    return 2431;
                case 4:
                    return 2992;
                case 5:
                    return 3553;
                case 6:
                    return 3905;
                case 7:
                    return 4257;
                case 8:
                    return 4609;
                case 9:
                    return 4961;
                case 10:
                    return 5313;
                case 11:
                    return 5607;
                case 12:
                    return 6132;
                case 13:
                    return 6668;
                case 14:
                    return 7193;
                case 15:
                    return 7718;
                case 16:
                    return 8253;
                case 17:
                    return 8778;
                case 18:
                    return 9314;
                case 19:
                    return 9839;
                case 20:
                    return 10374;
                case 21:
                    return 10763;
                case 22:
                    return 11162;
                case 23:
                    return 11550;
                case 24:
                    return 11949;
                case 25:
                    return 12338;
                case 26:
                    return 12737;
                case 27:
                    return 13125;
                case 28:
                    return 13514;
                case 29:
                    return 13913;
                case 30:
                    return 14301;
                case 31:
                    return 3689;
                case 32:
                    return 3760;
                case 33:
                    return 3878;
                case 34:
                    return 3993;
                case 35:
                    return 4111;
                case 36:
                    return 4183;
                case 37:
                    return 4227;
                case 38:
                    return 4342;
                case 39:
                    return 4457;
                case 40:
                    return 4572;
                case 41:
                    return 4612;
                case 42:
                    return 4689;
                case 43:
                    return 4778;
                case 44:
                    return 4890;
                case 45:
                    return 5002;
                case 46:
                    return 5107;
                case 47:
                    return 5222;
                case 48:
                    return 5258;
                case 49:
                    return 5287;
                case 50:
                    return 5396;
                case 51:
                    return 5505;
                case 52:
                    return 5610;
                case 53:
                    return 5719;
                case 54:
                    return 5828;
                case 55:
                    return 5937;
                case 56:
                    return 5957;
                case 57:
                    return 5973;
                case 58:
                    return 6076;
                case 59:
                    return 6181;
                case 60:
                    return 6287;
                case 61:
                    return 6291;
                case 62:
                    return 6298;
                case 63:
                    return 6400;
                case 64:
                    return 6500;
                case 65:
                    return 6602;
                case 66:
                    return 6663;
                case 67:
                    return 6761;
                case 68:
                    return 6863;
                case 69:
                    return 6965;
                case 70:
                    return 7066;
            }
        }else if(type == 2){
            switch (weight){
                case 1:
                    return 1815;
                case 2:
                    return 2299;
                case 3:
                    return 2816;
                case 4:
                    return 3366;
                case 5:
                    return 3916;
                case 6:
                    return 4246;
                case 7:
                    return 4576;
                case 8:
                    return 4906;
                case 9:
                    return 5236;
                case 10:
                    return 5555;
                case 11:
                    return 5768;
                case 12:
                    return 6202;
                case 13:
                    return 6747;
                case 14:
                    return 7303;
                case 15:
                    return 7848;
                case 16:
                    return 8394;
                case 17:
                    return 8949;
                case 18:
                    return 9494;
                case 19:
                    return 10040;
                case 20:
                    return 10585;
                case 21:
                    return 11252;
                case 22:
                    return 11918;
                case 23:
                    return 12595;
                case 24:
                    return 13262;
                case 25:
                    return 13928;
                case 26:
                    return 14595;
                case 27:
                    return 15262;
                case 28:
                    return 15928;
                case 29:
                    return 16595;
                case 30:
                    return 17261;
                case 31:
                    return 3689;
                case 32:
                    return 3760;
                case 33:
                    return 3878;
                case 34:
                    return 3993;
                case 35:
                    return 4111;
                case 36:
                    return 4183;
                case 37:
                    return 4227;
                case 38:
                    return 4342;
                case 39:
                    return 4457;
                case 40:
                    return 4572;
                case 41:
                    return 4612;
                case 42:
                    return 4689;
                case 43:
                    return 4778;
                case 44:
                    return 4890;
                case 45:
                    return 5002;
                case 46:
                    return 5107;
                case 47:
                    return 5222;
                case 48:
                    return 5258;
                case 49:
                    return 5287;
                case 50:
                    return 5396;
                case 51:
                    return 5505;
                case 52:
                    return 5610;
                case 53:
                    return 5719;
                case 54:
                    return 5828;
                case 55:
                    return 5937;
                case 56:
                    return 5957;
                case 57:
                    return 5973;
                case 58:
                    return 6076;
                case 59:
                    return 6181;
                case 60:
                    return 6287;
                case 61:
                    return 6291;
                case 62:
                    return 6298;
                case 63:
                    return 6400;
                case 64:
                    return 6500;
                case 65:
                    return 6602;
                case 66:
                    return 6663;
                case 67:
                    return 6761;
                case 68:
                    return 6863;
                case 69:
                    return 6965;
                case 70:
                    return 7066;
            }
        }else if(type == 3){
            if(isFood){
                switch (weight){
                    case 1:
                        return 1999;
                    case 2:
                        return 2799;
                    case 3:
                        return 2999;
                    case 4:
                        return 3299;
                    case 5:
                        return 3799;
                    case 6:
                        return 4099;
                    case 7:
                        return 4599;
                    case 8:
                        return 4999;
                    case 9:
                        return 5499;
                    case 10:
                        return 7000;
                    case 11:
                        return 7260;
                    case 12:
                        return 7920;
                    case 13:
                        return 8580;
                    case 14:
                        return 9240;
                    case 15:
                        return 9900;
                    case 16:
                        return 10560;
                    case 17:
                        return 11220;
                    case 18:
                        return 11880;
                    case 19:
                        return 12540;
                    case 20:
                        return 13200;
                    case 21:
                        return 13860;
                    case 22:
                        return 14520;
                    case 23:
                        return 15180;
                    case 24:
                        return 15840;
                    case 25:
                        return 16500;
                    case 26:
                        return 17160;
                    case 27:
                        return 17820;
                    case 28:
                        return 18480;
                    case 29:
                        return 19140;
                    case 30:
                        return 19800;
                    case 31:
                        return 3689;
                    case 32:
                        return 3760;
                    case 33:
                        return 3878;
                    case 34:
                        return 3993;
                    case 35:
                        return 4111;
                    case 36:
                        return 4183;
                    case 37:
                        return 4227;
                    case 38:
                        return 4342;
                    case 39:
                        return 4457;
                    case 40:
                        return 4572;
                    case 41:
                        return 4612;
                    case 42:
                        return 4689;
                    case 43:
                        return 4778;
                    case 44:
                        return 4890;
                    case 45:
                        return 5002;
                    case 46:
                        return 5107;
                    case 47:
                        return 5222;
                    case 48:
                        return 5258;
                    case 49:
                        return 5287;
                    case 50:
                        return 5396;
                    case 51:
                        return 5505;
                    case 52:
                        return 5610;
                    case 53:
                        return 5719;
                    case 54:
                        return 5828;
                    case 55:
                        return 5937;
                    case 56:
                        return 5957;
                    case 57:
                        return 5973;
                    case 58:
                        return 6076;
                    case 59:
                        return 6181;
                    case 60:
                        return 6287;
                    case 61:
                        return 6291;
                    case 62:
                        return 6298;
                    case 63:
                        return 6400;
                    case 64:
                        return 6500;
                    case 65:
                        return 6602;
                    case 66:
                        return 6663;
                    case 67:
                        return 6761;
                    case 68:
                        return 6863;
                    case 69:
                        return 6965;
                    case 70:
                        return 7066;
                }
            }else{
                switch (weight){
                    case 1:
                        return 1844;
                    case 2:
                        return 2514;
                    case 3:
                        return 2751;
                    case 4:
                        return 3173;
                    case 5:
                        return 3585;
                    case 6:
                        return 3976;
                    case 7:
                        return 4368;
                    case 8:
                        return 4759;
                    case 9:
                        return 5150;
                    case 10:
                        return 5532;
                    case 11:
                        return 5985;
                    case 12:
                        return 6428;
                    case 13:
                        return 6871;
                    case 14:
                        return 7324;
                    case 15:
                        return 7767;
                    case 16:
                        return 8210;
                    case 17:
                        return 8652;
                    case 18:
                        return 9106;
                    case 19:
                        return 9549;
                    case 20:
                        return 9991;
                    case 21:
                        return 10455;
                    case 22:
                        return 10918;
                    case 23:
                        return 11382;
                    case 24:
                        return 11845;
                    case 25:
                        return 12299;
                    case 26:
                        return 12762;
                    case 27:
                        return 13226;
                    case 28:
                        return 13689;
                    case 29:
                        return 14153;
                    case 30:
                        return 14616;
                    case 31:
                        return 3689;
                    case 32:
                        return 3760;
                    case 33:
                        return 3878;
                    case 34:
                        return 3993;
                    case 35:
                        return 4111;
                    case 36:
                        return 4183;
                    case 37:
                        return 4227;
                    case 38:
                        return 4342;
                    case 39:
                        return 4457;
                    case 40:
                        return 4572;
                    case 41:
                        return 4612;
                    case 42:
                        return 4689;
                    case 43:
                        return 4778;
                    case 44:
                        return 4890;
                    case 45:
                        return 5002;
                    case 46:
                        return 5107;
                    case 47:
                        return 5222;
                    case 48:
                        return 5258;
                    case 49:
                        return 5287;
                    case 50:
                        return 5396;
                    case 51:
                        return 5505;
                    case 52:
                        return 5610;
                    case 53:
                        return 5719;
                    case 54:
                        return 5828;
                    case 55:
                        return 5937;
                    case 56:
                        return 5957;
                    case 57:
                        return 5973;
                    case 58:
                        return 6076;
                    case 59:
                        return 6181;
                    case 60:
                        return 6287;
                    case 61:
                        return 6291;
                    case 62:
                        return 6298;
                    case 63:
                        return 6400;
                    case 64:
                        return 6500;
                    case 65:
                        return 6602;
                    case 66:
                        return 6663;
                    case 67:
                        return 6761;
                    case 68:
                        return 6863;
                    case 69:
                        return 6965;
                    case 70:
                        return 7066;
                }
            }

        }else if(type == 4){
            if(isFood){
                switch (weight){
                    default:
                        return 800 * weight;
                    case 1:
                        return 1799;
                    case 2:
                        return 1999;
                    case 3:
                        return 2299;
                    case 4:
                        return 2599;
                    case 5:
                        return 2666;
                    case 6:
                        return 3999;
                    case 7:
                        return 4299;
                    case 8:
                        return 4799;
                    case 9:
                        return 5099;
                    case 10:
                        return 545 * weight;
                    case 11:
                        return 545 * weight;
                    case 12:
                        return 545 * weight;
                    case 13:
                        return 545 * weight;
                    case 14:
                        return 545 * weight;
                    case 15:
                        return 545 * weight;
                    case 16:
                        return 545 * weight;
                    case 17:
                        return 545 * weight;
                    case 18:
                        return 545 * weight;
                    case 19:
                        return 545 * weight;
                    case 20:
                        return 545 * weight;
                    case 21:
                        return 545 * weight;
                    case 22:
                        return 545 * weight;
                    case 23:
                        return 545 * weight;
                    case 24:
                        return 545 * weight;
                    case 25:
                        return 545 * weight;
                    case 26:
                        return 545 * weight;
                    case 27:
                        return 545 * weight;
                    case 28:
                        return 545 * weight;
                    case 29:
                        return 545 * weight;
                    case 30:
                        return 545 * weight;
                }
            }else{
                switch (weight){
                    case 1:
                        return 1455;
                    case 2:
                        return 1780;
                    case 3:
                        return 2054;
                    case 4:
                        return 2360;
                    case 5:
                        return 2666;
                    case 6:
                        return 3910;
                    case 7:
                        return 4266;
                    case 8:
                        return 4622;
                    case 9:
                        return 4978;
                    case 10:
                        return 5334;
                    case 11:
                        return 5757;
                    case 12:
                        return 6180;
                    case 13:
                        return 6602;
                    case 14:
                        return 7025;
                    case 15:
                        return 7448;
                    case 16:
                        return 7871;
                    case 17:
                        return 8294;
                    case 18:
                        return 8716;
                    case 19:
                        return 9139;
                    case 20:
                        return 9562;
                    case 21:
                        return 9988;
                    case 22:
                        return 10416;
                    case 23:
                        return 10844;
                    case 24:
                        return 11273;
                    case 25:
                        return 11701;
                    case 26:
                        return 12130;
                    case 27:
                        return 12558;
                    case 28:
                        return 12987;
                    case 29:
                        return 13415;
                    case 30:
                        return 13844;
                    case 31:
                        return 3689;
                    case 32:
                        return 3760;
                    case 33:
                        return 3878;
                    case 34:
                        return 3993;
                    case 35:
                        return 4111;
                    case 36:
                        return 4183;
                    case 37:
                        return 4227;
                    case 38:
                        return 4342;
                    case 39:
                        return 4457;
                    case 40:
                        return 4572;
                    case 41:
                        return 4612;
                    case 42:
                        return 4689;
                    case 43:
                        return 4778;
                    case 44:
                        return 4890;
                    case 45:
                        return 5002;
                    case 46:
                        return 5107;
                    case 47:
                        return 5222;
                    case 48:
                        return 5258;
                    case 49:
                        return 5287;
                    case 50:
                        return 5396;
                    case 51:
                        return 5505;
                    case 52:
                        return 5610;
                    case 53:
                        return 5719;
                    case 54:
                        return 5828;
                    case 55:
                        return 5937;
                    case 56:
                        return 5957;
                    case 57:
                        return 5973;
                    case 58:
                        return 6076;
                    case 59:
                        return 6181;
                    case 60:
                        return 6287;
                    case 61:
                        return 6291;
                    case 62:
                        return 6298;
                    case 63:
                        return 6400;
                    case 64:
                        return 6500;
                    case 65:
                        return 6602;
                    case 66:
                        return 6663;
                    case 67:
                        return 6761;
                    case 68:
                        return 6863;
                    case 69:
                        return 6965;
                    case 70:
                        return 7066;
                }
            }

        }

        return 0;
    }

    public static  int getRate(int weight, int type){
        if(Constants.DELIVERY_STATUS == Constants.OUT_STATION){
            if(type == 1){
                switch (weight){
                    case 1:
                        return 298;
                    case 2:
                        return 592;
                    case 3:
                        return 888;
                    case 4:
                        return 1182;
                    case 5:
                        return 1476;
                    case 6:
                        return 1774;
                    case 7:
                        return 2068;
                    case 8:
                        return 2364;
                    case 9:
                        return 2658;
                    case 10:
                        return 2952;
                    case 11:
                        return 3250;
                    case 12:
                        return 3544;
                    case 13:
                        return 3840;
                    case 14:
                        return 4134;
                    case 15:
                        return 4428;
                    case 16:
                        return 4726;
                    case 17:
                        return 5020;
                    case 18:
                        return 5316;
                    case 19:
                        return 5610;
                    case 20:
                        return 5904;
                    case 21:
                        return 6202;
                    case 22:
                        return 6496;
                    case 23:
                        return 6792;
                    case 24:
                        return 7086;
                    case 25:
                        return 7380;
                    case 26:
                        return 7678;
                    case 27:
                        return 7972;
                    case 28:
                        return 8268;
                    case 29:
                        return 8562;
                    case 30:
                        return 8856;
                    case 31:
                        return 9154;
                    case 32:
                        return 9448;
                    case 33:
                        return 9744;
                    case 34:
                        return 10038;
                    case 35:
                        return 10332;
                    case 36:
                        return 10630;
                    case 37:
                        return 10924;
                    case 38:
                        return 11220;
                    case 39:
                        return 11514;
                    case 40:
                        return 11808;
                    case 41:
                        return 12106;
                    case 42:
                        return 12400;
                    case 43:
                        return 12696;
                    case 44:
                        return 12990;
                    case 45:
                        return 13284;
                    case 46:
                        return 13582;
                    case 47:
                        return 13876;
                    case 48:
                        return 14172;
                    case 49:
                        return 14466;
                    case 50:
                        return 14760;
                    case 51:
                        return 15058;
                    case 52:
                        return 15352;
                    case 53:
                        return 15648;
                    case 54:
                        return 15942;
                    case 55:
                        return 16236;
                    case 56:
                        return 16534;
                    case 57:
                        return 16828;
                    case 58:
                        return 17124;
                    case 59:
                        return 17418;
                    case 60:
                        return 17712;
                    case 61:
                        return 18010;
                    case 62:
                        return 18304;
                    case 63:
                        return 18600;
                    case 64:
                        return 18894;
                    case 65:
                        return 19188;
                    case 66:
                        return 19486;
                    case 67:
                        return 19780;
                    case 68:
                        return 20076;
                    case 69:
                        return 20370;
                    case 70:
                        return 20664;
                }
            }else if(type == 2){
                switch (weight){
                    case 1:
                        return 185;
                    case 2:
                        return 368;
                    case 3:
                        return 548;
                    case 4:
                        return 733;
                    case 5:
                        return 915;
                    case 6:
                        return 1095;
                    case 7:
                        return 1278;
                    case 8:
                        return 1460;
                    case 9:
                        return 1643;
                    case 10:
                        return 1825;
                    case 11:
                        return 2010;
                    case 12:
                        return 2190;
                    case 13:
                        return 2375;
                    case 14:
                        return 2558;
                    case 15:
                        return 2738;
                    case 16:
                        return 2920;
                    case 17:
                        return 3105;
                    case 18:
                        return 3285;
                    case 19:
                        return 3468;
                    case 20:
                        return 3648;
                    case 21:
                        return 3833;
                    case 22:
                        return 4018;
                    case 23:
                        return 4198;
                    case 24:
                        return 4380;
                    case 25:
                        return 4565;
                    case 26:
                        return 4745;
                    case 27:
                        return 4928;
                    case 28:
                        return 5110;
                    case 29:
                        return 5293;
                    case 30:
                        return 5475;
                    case 31:
                        return 5660;
                    case 32:
                        return 5840;
                    case 33:
                        return 6023;
                    case 34:
                        return 6208;
                    case 35:
                        return 6388;
                    case 36:
                        return 6570;
                    case 37:
                        return 6750;
                    case 38:
                        return 6935;
                    case 39:
                        return 7118;
                    case 40:
                        return 7298;
                    case 41:
                        return 7483;
                    case 42:
                        return 7668;
                    case 43:
                        return 7848;
                    case 44:
                        return 8030;
                    case 45:
                        return 8213;
                    case 46:
                        return 8395;
                    case 47:
                        return 8578;
                    case 48:
                        return 8760;
                    case 49:
                        return 8940;
                    case 50:
                        return 9125;
                    case 51:
                        return 9305;
                    case 52:
                        return 9490;
                    case 53:
                        return 9673;
                    case 54:
                        return 9858;
                    case 55:
                        return 10038;
                    case 56:
                        return 10220;
                    case 57:
                        return 10400;
                    case 58:
                        return 10583;
                    case 59:
                        return 10768;
                    case 60:
                        return 10948;
                    case 61:
                        return 11133;
                    case 62:
                        return 11315;
                    case 63:
                        return 11498;
                    case 64:
                        return 11680;
                    case 65:
                        return 11863;
                    case 66:
                        return 12043;
                    case 67:
                        return 12228;
                    case 68:
                        return 12410;
                    case 69:
                        return 12590;
                    case 70:
                        return 12773;
                }
            }else if(type == 3){
                switch (weight){
                    case 1:
                        return 149;
                    case 2:
                        return 199;
                    case 3:
                        return 249;
                    case 4:
                        return 399;
                    case 5:
                        return 499;
                    case 6:
                        return 599;
                    case 7:
                        return 699;
                    case 8:
                        return 799;
                    case 9:
                        return 899;
                    case 10:
                        return 1272;
                    case 11:
                        return 1400;
                    case 12:
                        return 1528;
                    case 13:
                        return 1652;
                    case 14:
                        return 1780;
                    case 15:
                        return 1908;
                    case 16:
                        return 2032;
                    case 17:
                        return 2160;
                    case 18:
                        return 2288;
                    case 19:
                        return 2412;
                    case 20:
                        return 2540;
                    case 21:
                        return 2602;
                    case 22:
                        return 2723;
                    case 23:
                        return 2847;
                    case 24:
                        return 2976;
                    case 25:
                        return 3097;
                    case 26:
                        return 3139;
                    case 27:
                        return 3261;
                    case 28:
                        return 3379;
                    case 29:
                        return 3500;
                    case 30:
                        return 3622;
                    case 31:
                        return 3689;
                    case 32:
                        return 3760;
                    case 33:
                        return 3878;
                    case 34:
                        return 3993;
                    case 35:
                        return 4111;
                    case 36:
                        return 4183;
                    case 37:
                        return 4227;
                    case 38:
                        return 4342;
                    case 39:
                        return 4457;
                    case 40:
                        return 4572;
                    case 41:
                        return 4612;
                    case 42:
                        return 4689;
                    case 43:
                        return 4778;
                    case 44:
                        return 4890;
                    case 45:
                        return 5002;
                    case 46:
                        return 5107;
                    case 47:
                        return 5222;
                    case 48:
                        return 5258;
                    case 49:
                        return 5287;
                    case 50:
                        return 5396;
                    case 51:
                        return 5505;
                    case 52:
                        return 5610;
                    case 53:
                        return 5719;
                    case 54:
                        return 5828;
                    case 55:
                        return 5937;
                    case 56:
                        return 5957;
                    case 57:
                        return 5973;
                    case 58:
                        return 6076;
                    case 59:
                        return 6181;
                    case 60:
                        return 6287;
                    case 61:
                        return 6291;
                    case 62:
                        return 6298;
                    case 63:
                        return 6400;
                    case 64:
                        return 6500;
                    case 65:
                        return 6602;
                    case 66:
                        return 6663;
                    case 67:
                        return 6761;
                    case 68:
                        return 6863;
                    case 69:
                        return 6965;
                    case 70:
                        return 7066;
                }
            }

        }else if(Constants.DELIVERY_STATUS ==  Constants.INTERNATIONAL){



        }

        return 0;
    }

}
