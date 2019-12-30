package com.hurry.custom.view.activity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.maps.model.LatLng;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.db.PreferenceUtils;
import com.hurry.custom.controller.CompressProcess;
import com.hurry.custom.controller.GetBasic;
import com.hurry.custom.controller.GetCity;
import com.hurry.custom.controller.GetPhone;
import com.hurry.custom.model.AddressHisModel;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.view.BaseActivity;
import com.hurry.custom.view.adapter.CityAdapter;
import com.hurry.custom.view.adapter.MyViewPagerAdapter;
import com.hurry.custom.view.adapter.NoTouchViewPager;
import com.hurry.custom.view.fragment.AddressDetailsNewFragment;
import com.hurry.custom.view.fragment.DateTimeFragment;
import com.hurry.custom.view.fragment.ItemOrderFragment;
import com.hurry.custom.view.fragment.LocationTrackFragment;
import com.hurry.custom.view.fragment.OrderConfirmFragment;
import com.hurry.custom.view.fragment.OrderHisContainerFragment;
import com.hurry.custom.view.fragment.ReviewFragment;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.skyfishjy.library.RippleBackground;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

import static com.hurry.custom.common.Constants.addressModel;
import static com.hurry.custom.common.Constants.clearData;

/**
 * 测试导航栏控制类
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener{
    public static final int INTENT_REQUEST_GET_IMAGES = 13;
    private final int[] COLORS = {0xFF008080, 0xFF008080, 0xFF008080, 0xFF008080, 0xFF008080};

    public static final int ITEM_ORDER = 1;
    public static final int ADDRESS_DETAILS = 2;
    public static final int DATE_TIME = 3;
    public static final int REVIEW = 4;
    public static final int ORDER_CONFIRM = 5;
    public static final int TRACK = 6;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.txt_title) TextView txtTitle;
    @BindView(R.id.lin_share) LinearLayout linShare;
    @BindView(R.id.lin_rate) LinearLayout linRate;
    @BindView(R.id.lin_location) LinearLayout linLocation;
    @BindView(R.id.fragment) FrameLayout frameLayout;
    @BindView(R.id.content) RippleBackground rippleBackground;
    @BindView(R.id.lin_container) LinearLayout linContainer;

    private NoTouchViewPager mViewPager;
    private PageNavigationView mTab;

    private NavigationController mNavigationController;
    private final List<Integer> mMessageNumberList = new ArrayList<>();

    private int pageSelection = 2;
    private int pageFrameSelection = 0;
    private boolean isViewPager = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        toolbar.setTitle(getResources().getString(R.string.my_account));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.mylogosquare_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(Constants.itemLists.size() == 0 || Constants.cityModels.size() == 0){
            showProgressDialog();
            autoLogin();
        }
        //autoLogin();

    }
    private void autoLogin() {
        new GetBasic(this,"").execute();
    }

    public void getCities(){
        new GetCity(this, "").execute();
    }

    @Override
    public void onResume(){
        super.onResume();

        initView();
        if(Constants.itemLists.size() != 0 && Constants.cityModels.size() != 0){
            initNavigation();
            initEvent();
        }
    }

    public void setTitle(String title){
        toolbar.setTitle(title);
        if(!PreferenceUtils.getCityName(this).isEmpty() && PreferenceUtils.getCityId(this) != -1){
            try{
                String sub[] = PreferenceUtils.getCityName(this).split("-");
                txtTitle.setText(sub[0]);
            }catch (Exception e){};
        }else{
            txtTitle.setText(title);
        }
    }

    private void initView() {

        mViewPager = findViewById(R.id.viewPager);
        mTab = findViewById(R.id.tab);
        linShare.setOnClickListener(this);
        linRate.setOnClickListener(this);
        linLocation.setOnClickListener(this);

        if(PreferenceUtils.getCityId(this) == -1){
            rippleBackground.startRippleAnimation();
        }
    }

    public void setIndex(int index){
        pageSelection = index;
    }
    public void callPhone(){
        Constants.makePhoneCall(HomeActivity.this, PreferenceUtils.getConfPhone(HomeActivity.this));
    }
    public void initNavigation() {
        mNavigationController = mTab.material()
                .addItem(R.mipmap.ic_profile, "Account", COLORS[0])
                .addItem(R.mipmap.ic_history, "History", COLORS[1])
                .addItem(R.mipmap.ic_home, "Home", COLORS[2])
                .addItem(R.mipmap.ic_setting, "Settings", COLORS[3])
                .addItem(getResources().getDrawable(R.drawable.ic_baseline_call_24px), "Call", COLORS[3])
                .enableAnimateLayoutChanges()
                .setDefaultColor(getColor(R.color.black))
                .build();

        MyViewPagerAdapter pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), Math.max(6, mNavigationController.getItemCount()));
        mViewPager.setAdapter(pagerAdapter);
        mNavigationController.setupWithViewPager(mViewPager);
        // 初始化消息数字为0
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            mMessageNumberList.add(0);
        }




        setTitle(getString(R.string.home));
        currentViewPaterFragment = pagerAdapter.getItem(pageSelection);
        if(isViewPager){
            showViewPager(-1);

        }else{
            hideViewPater();
        }


        mTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String click = "ok";
            }
        });

        mNavigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                if(index == 4){
                    if(getResources().getString(R.string.default_phone).equals(PreferenceUtils.getConfPhone(HomeActivity.this))){
                        new GetPhone(HomeActivity.this).execute();
                    }else{
                        Constants.makePhoneCall(HomeActivity.this, PreferenceUtils.getConfPhone(HomeActivity.this));
                    }
                    if(frameLayout.getVisibility() == View.GONE){
                        mNavigationController.setSelect(old);
                        setIndex(old);
                    }
                    return;
                }
            }

            @Override
            public void onRepeat(int index) {

                if(frameLayout.getVisibility() == View.VISIBLE && !Constants.page_type.equals("confirm") && !Constants.page_type.equals("track") && index != 4){
                    new AlertDialog.Builder(HomeActivity.this)
                            .setMessage("Do you wish to discard the changes?")
                            //.setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    showViewPager(-1);
                                    if(index == 2){
                                        linContainer.setVisibility(View.VISIBLE);
                                    }
                                    clearData();
                                }})
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    hideViewPater();
                                }
                            }).show();
                }else{
                    showViewPager(-1);
                    clearData();
                    if(index == 2){
                        linContainer.setVisibility(View.VISIBLE);
                    }
                }


            }
        });


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if(frameLayout.getVisibility() == View.VISIBLE && !Constants.page_type.equals("confirm") && !Constants.page_type.equals("track") && position != 4){

                    new AlertDialog.Builder(HomeActivity.this)
                            .setMessage("Do you wish to discard the changes?")
                            //.setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    showViewPager(-1);
                                    if(position == 2){
                                        linContainer.setVisibility(View.VISIBLE);
                                    }
                                    clearData();

                                }})
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    hideViewPater();
                                }
                            }).show();
                }else{
                    frameLayout.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.VISIBLE);
                    switch (position){
                        case 0:
                            currentViewPaterFragment = pagerAdapter.getItem(0);
                            setTitle(getResources().getString(R.string.my_account));
                            linContainer.setVisibility(View.GONE);
                            setIndex(0);
                            break;

                        case 1:

                            currentViewPaterFragment = pagerAdapter.getItem(1);
                            setTitle(getResources().getString(R.string.order_his));
                            linContainer.setVisibility(View.GONE);
                            setIndex(1);
                            break;

                        case 2:

                            currentViewPaterFragment = pagerAdapter.getItem(2);
                            setTitle("Home");
                            linContainer.setVisibility(View.VISIBLE);
                            setIndex(2);
                            break;

                        case 3:

                            currentViewPaterFragment = pagerAdapter.getItem(3);
                            setTitle(getResources().getString(R.string.setting));
                            linContainer.setVisibility(View.GONE);
                            setIndex(3);
                            break;

                        case 4:
                            currentViewPaterFragment = pagerAdapter.getItem(4);
                            setTitle(getResources().getString(R.string.call_support));
                            linContainer.setVisibility(View.GONE);
                            setIndex(4);
                            break;
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mNavigationController.setSelect(pageSelection);



    }
    private void initEvent() {
        // +
        // 添加导航项
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if (requestCode == INTENT_REQUEST_GET_IMAGES ) {

                for(int k = 0; k < Constants.cameraOrderModel.itemModels.size() ; k++){
                    String image = Constants.cameraOrderModel.itemModels.get(k).image;
                    if(image == null){
                        ItemModel itemModel = Constants.cameraOrderModel.itemModels.get(k);
                        Constants.cameraOrderModel.itemModels.remove(itemModel);
                    }
                }
                final ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                new CompressProcess(HomeActivity.this, image_uris).execute();

            }

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
                    if(currentFragment != null && currentFragment instanceof AddressDetailsNewFragment){
                        ((AddressDetailsNewFragment)currentFragment).showAddressDetails("");
                        if(((AddressDetailsNewFragment)currentFragment).checkInput()){
                            //linContinue.setVisibility(View.VISIBLE);
                            ((AddressDetailsNewFragment)currentFragment).hideReceiver();

                        }
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
                    if(currentFragment != null && currentFragment instanceof AddressDetailsNewFragment){

                        ((AddressDetailsNewFragment)currentFragment).showAddressDetails("");
                        if(    ((AddressDetailsNewFragment)currentFragment).checkInput()){
                            //linContinue.setVisibility(View.VISIBLE);
                            ((AddressDetailsNewFragment)currentFragment).hideReceiver();
                        }
                    }

                }
            }

            if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                    null) {

                TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                        .INTENT_EXTRA_TRANSACTION_RESPONSE);
                ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);
                // Check which object is non-null
                if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                    if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                        //Success Transaction
                        //Toast.makeText(this, "Success",Toast.LENGTH_SHORT).show();
                        //showOrdreConfirm(false);

                        // Response from Payumoney
                        String payuResponse = transactionResponse.getPayuResponse();
                        try {
                            JSONObject jsonObject = new JSONObject(payuResponse);
                            JSONObject  jsonRes = jsonObject.getJSONObject("result");
                            String name_on_car = jsonRes.getString("name_on_card");
                            String paymentId = jsonRes.getString("paymentId");
                            String merchantResponse = transactionResponse.getTransactionDetails();


                            updateFragment(ORDER_CONFIRM, paymentId);

//                            Intent intent = new Intent(mContext, OrderConfirmActivity.class);
//                            intent.putExtra("payment",mPayment);
//                            intent.putExtra("transaction_id", paymentId);
//                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Response from SURl and FURL


                    } else {
                        //Failure Transaction
                        Toast.makeText(this, "failed",Toast.LENGTH_SHORT).show();
                    }

                } else if (resultModel != null && resultModel.getError() != null) {
                    Log.d("", "Error response : " + resultModel.getError().getTransactionResponse());
                    Toast.makeText(this, "Error response : " + resultModel.getError().getTransactionResponse(),Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("", "Both objects are null!");
                    // Toast.makeText(this, "Both objects are null!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                String shareBody = "http://play.google.com/store/apps/details?id=" + getPackageName();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.lin_rate:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }

                break;
            case R.id.lin_location:
                if(Constants.cityModels.size() == 0){
                    new GetCity(this,"show").execute();
                }else{
                    showConfirmDialog();
                }
                break;
        }
    }

    Fragment currentViewPaterFragment = null;
    Fragment currentFragment = null;

    public void stopRipple(){
        if(!PreferenceUtils.getCityName(HomeActivity.this).isEmpty())
            setTitle(PreferenceUtils.getCityName(HomeActivity.this));

        if(rippleBackground != null ){
            rippleBackground.stopRippleAnimation();
        }
    }

    public void updateOrderHis(int selection){
        Constants.orderHisModels.clear();
        Constants.orderCorporateHisModels.clear();
        onResume();
        showViewPager(selection);
    }

    public void callOrderHis(){
        if(currentFragment  instanceof  OrderHisContainerFragment){
            ((OrderHisContainerFragment) currentFragment).orderHistory(100);
        }
    }

    public void updateFragment(int index, String type){

        linContainer.setVisibility(View.GONE);
        hideViewPater();

        Fragment fragment = null;
        pageFrameSelection = index;

        if(index == ITEM_ORDER){
            setTitle(getString(R.string.select_the_item));
            fragment = new ItemOrderFragment(type);
        }
        if(index == ADDRESS_DETAILS){
            setTitle(getString(R.string.address_detail));
            fragment = new AddressDetailsNewFragment(type);
        }
        if(index == DATE_TIME){
            setTitle(getString(R.string.date_time));
            fragment = new DateTimeFragment(type);
        }

        if(index == REVIEW){
            setTitle(getString(R.string.review_your_order));
            fragment = new ReviewFragment();
        }

        if(index == ORDER_CONFIRM){
            setTitle(getString(R.string.order_confirmed));
            fragment = new OrderConfirmFragment(((ReviewFragment)currentFragment).getPaymentType(), type);
        }

        if(index == TRACK){
            setTitle(Constants.cityName);
            linContainer.setVisibility(View.GONE);
            fragment = new LocationTrackFragment();

        }

        currentFragment = fragment;
        if( fragment != null ){
            final Fragment finalFragment = fragment;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                //getFragmentManager().beginTransaction()
                                ft.replace(R.id.fragment, finalFragment); // f2_container is your FrameLayout container
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); //TRANSIT_FRAGMENT_OPEN
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });
                    }catch (Exception e){};
                }
            }, 30);

        }

    }

    public void goToMapSource(){
        if(Constants.cityBounds.size() > 0){
            ((AddressDetailsNewFragment)currentFragment).showAutoCompleteAddress("source");
        }else{
            new GetCity(this, "source").execute();
        }
    }

    public void goToMapDestination(){
        if(Constants.cityBounds.size() > 0){
            ((AddressDetailsNewFragment)currentFragment).showAutoCompleteAddress("destination");
        }else{
            new GetCity(this, "destination").execute();
        }
    }

    public void goToProfileSource(){
        if(currentFragment instanceof  AddressDetailsNewFragment)
            ((AddressDetailsNewFragment)currentFragment).showAddressBook("source");
    }
    public void goToProfileDestination(){
        if(currentFragment instanceof AddressDetailsNewFragment)
            ((AddressDetailsNewFragment)currentFragment).showAddressBook("des");
    }


    public void showViewPager(int index){
        try{
            isViewPager = true;
            frameLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            if( index != -1){
                mNavigationController.setSelect(index);
            }
        }catch (Exception e){};
    }

    public void hideViewPater(){
        isViewPager = false;
        frameLayout.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
        mNavigationController.setSelect(5);
    }


    public void dismissAddressDialog(AddressHisModel model){
        if(currentFragment instanceof AddressDetailsNewFragment){
            ((AddressDetailsNewFragment)currentFragment).dismissAddressDialog(model);
        }
    }

    public void updateLocation(String location, String street, String area, String city, String state, String pincode, LatLng latLng){
        if(currentFragment instanceof AddressDetailsNewFragment){
            ((AddressDetailsNewFragment)currentFragment).updateLocation(location, street, area, city, state, pincode, latLng);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putInt("type", pageSelection);
    }



    public void hideDialog(){
        cityDialog.dismiss();
        stopRipple();
    }

    Dialog cityDialog;
    public void showConfirmDialog(){
        cityDialog = new Dialog(this ,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        cityDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cityDialog.setContentView(R.layout.activity_city);

        RecyclerView recyclerView = (RecyclerView)cityDialog.findViewById(R.id.recyclerview);
        setupRecyclerView(recyclerView);
        // dialog.setTitle("Title...");
        cityDialog.setCanceledOnTouchOutside(false);
        cityDialog.setCancelable(false);
        cityDialog.show();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        if(Constants.cityModels!= null && Constants.cityModels.size() > 0){
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            recyclerView.setAdapter(new CityAdapter(this,
                    Constants.cityModels, recyclerView));
        }else{
            recyclerView.setAdapter(null);
        }
    }

    public void setUpPersonalOrderHis(int index){
        if(currentFragment instanceof OrderHisContainerFragment){
            ((OrderHisContainerFragment)currentFragment).setUpPersonalOrderHis(index);
        }
    }




}
