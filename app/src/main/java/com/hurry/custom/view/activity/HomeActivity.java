package com.hurry.custom.view.activity;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.gun0912.tedpicker.ImagePickerActivity;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.controller.CompressProcess;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.view.BaseActivity;
import com.hurry.custom.view.adapter.MyViewPagerAdapter;
import com.hurry.custom.view.adapter.NoTouchViewPager;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;


/**
 * 测试导航栏控制类
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener{
    public static final int INTENT_REQUEST_GET_IMAGES = 13;
    private final int[] COLORS = {0xFF008080, 0xFF008080, 0xFF008080, 0xFF008080, 0xFF008080};


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.txt_title) TextView txtTitle;
    @BindView(R.id.lin_share) LinearLayout linShare;
    @BindView(R.id.lin_rate) LinearLayout linRate;

    private NoTouchViewPager mViewPager;
    private PageNavigationView mTab;


    private NavigationController mNavigationController;

    private final List<Integer> mMessageNumberList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.my_account));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.mylogosquare_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initNavigation();
        initEvent();

    }

    public void setTitle(String title){
        toolbar.setTitle(title);
        txtTitle.setText(title);
    }

    private void initView() {

        mViewPager = findViewById(R.id.viewPager);
        mTab = findViewById(R.id.tab);
        linShare.setOnClickListener(this);
        linRate.setOnClickListener(this);
    }


    private void initNavigation() {
        mNavigationController = mTab.material()
                .addItem(R.mipmap.ic_profile, "Account", COLORS[0])
                .addItem(R.mipmap.ic_history, "Order His", COLORS[1])
                .addItem(R.mipmap.ic_home, "Home", COLORS[2])
                .addItem(R.mipmap.ic_setting, "Setting", COLORS[3])
                .addItem(getResources().getDrawable(R.drawable.ic_baseline_call_24px), "Call", COLORS[3])
                .enableAnimateLayoutChanges()
                .build();

        MyViewPagerAdapter pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), Math.max(4, mNavigationController.getItemCount()));
        mViewPager.setAdapter(pagerAdapter);
        mNavigationController.setupWithViewPager(mViewPager);
        // 初始化消息数字为0
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            mMessageNumberList.add(0);
        }

        if(getValue("page").equals("")){
            mNavigationController.setSelect(2);
        }else{
            mNavigationController.setSelect(1);
        }


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        setTitle(getResources().getString(R.string.my_account));
                        break;
                    case 1:
                        setTitle(getResources().getString(R.string.order_his));
                        break;
                    case 2:
                        setTitle("Home");
                        break;
                    case 3:
                        setTitle(getResources().getString(R.string.quote));
                        break;
                    case 4:
                        setTitle(getResources().getString(R.string.setting));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Here is the share content body";
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
        }
    }
}
