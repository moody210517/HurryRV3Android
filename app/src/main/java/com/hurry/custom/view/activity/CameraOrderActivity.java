package com.hurry.custom.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.common.utils.ImageLoaderHelper;
import com.hurry.custom.controller.CompressProcess;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.view.BaseActivity;
import com.hurry.custom.view.BaseBackActivity;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2/12/2018.
 */

public class CameraOrderActivity extends BaseBackActivity implements View.OnClickListener{

    public static int MAX = 5;
    LinearLayout linContainer;
    LinearLayout  linItem;
    TextView txtMore;
    TextView txtExceed;
    Button btnContinue;
    int total = 0;
    public static CameraOrderActivity cameraOrderActivity;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_camera_order);
        ButterKnife.bind(this);
        cameraOrderActivity = this;

        initBackButton(toolbar, "Photo Upload");

        Constants.page_type = "camera";
        if(Constants.cameraOrderModel.itemModels.size() == 0){
            Intent intent = new Intent(this, ImagePickerActivity.class);
            intent.putExtra("limit", CameraOrderActivity.MAX - Constants.cameraOrderModel.itemModels.size());
            startActivityForResult(intent, MainActivity.INTENT_REQUEST_GET_IMAGES);
        }else{
            initView();
        }
    }

    public void initView(){
        linContainer = (LinearLayout)findViewById(R.id.lin_container);
        linItem = (LinearLayout)findViewById(R.id.lin_item);
        linItem.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int k = 0; k < Constants.cameraOrderModel.itemModels.size() ; k++){
            ItemModel itemModel = Constants.cameraOrderModel.itemModels.get(k);
            View v = layoutInflater.inflate(R.layout.row_camera_item, null);

            ImageView img = (ImageView)v.findViewById(R.id.image);
            ImageLoaderHelper.showImageFromLocal(this,itemModel.image, img);
            final int finalK1 = k;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Constants.showImage(CameraOrderActivity.this, linContainer,  Constants.cameraOrderModel.itemModels.get(finalK1).image, "local");
                    }catch (Exception e){};

                }
            });
            TextView txtRemove = (TextView)v.findViewById(R.id.txt_remove);

            txtRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Constants.cameraOrderModel.itemModels.size() > finalK1 ){
                        ItemModel model = Constants.cameraOrderModel.itemModels.get(finalK1);
                        Constants.cameraOrderModel.itemModels.remove(model);
                        initView();
                    }
                }
            });

            final CheckBox chkPackage = (CheckBox)v.findViewById(R.id.chk_package);
            chkPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chkPackage.isChecked()){
                        Constants.cameraOrderModel.itemModels.get(finalK1).mPackage = "1";
                    }else{
                        Constants.cameraOrderModel.itemModels.get(finalK1).mPackage = "0";
                    }
                }
            });

            final NiceSpinner spQuantity = (NiceSpinner)v.findViewById(R.id.sp_quantity);
            //spQuantity.type = 1;
            spQuantity.setPadding(20, 10,10,10);
            final NiceSpinner spWeight = (NiceSpinner)v.findViewById(R.id.sp_weight);
            //spWeight.type = 1;
            spWeight.setPadding(20, 10,10,10);

            spQuantity.attachDataSource(Constants.quantity);
            spWeight.attachDataSource(Constants.weight);

            spQuantity.setText(itemModel.quantity);
            spWeight.setText(itemModel.weight);

            if(itemModel.mPackage.equals("1")){
                chkPackage.setChecked(true);
            }

            final int finalK = k;
            spQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try{
                        Constants.cameraOrderModel.itemModels.get(finalK).quantity = Constants.quantity.get(position);
                    }catch (Exception e){};
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            spWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Constants.cameraOrderModel.itemModels.get(finalK).weight = Constants.weight.get(position);
                    Constants.cameraOrderModel.itemModels.get(finalK).weight_value = Constants.weight_value.get(position);

                    total = 0;
                    for(int k = 0; k <  Constants.cameraOrderModel.itemModels.size() ; k++){
                        total += Constants.cameraOrderModel.itemModels.get(k).weight_value;
                    }
                    if(total > 10){
                        //visibleExceed();
                    }else{

                    }

                    txtExceed.setVisibility(View.GONE);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            linItem.addView(v);
        }

        txtMore = (TextView)findViewById(R.id.txt_upload_more);
        txtMore.setOnClickListener(this);
        btnContinue  = (Button)findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(this);

        if(Constants.cameraOrderModel != null && Constants.cameraOrderModel.itemModels.size() == CameraOrderActivity.MAX){
            txtMore.setVisibility(View.GONE);
        }else{
            txtMore.setVisibility(View.VISIBLE);
        }
        txtExceed = (TextView)findViewById(R.id.txt_exceed);

        total = 0;
        for(int k = 0; k <  Constants.cameraOrderModel.itemModels.size() ; k++){
            total += Constants.cameraOrderModel.itemModels.get(k).weight_value;
        }
        if(total > 10){
            //visibleExceed();
        }else{
            txtExceed.setVisibility(View.GONE);
        }
        txtExceed.setVisibility(View.GONE);
    }

    private  void visibleExceed(){
        txtExceed.setVisibility(View.VISIBLE);
        if(Constants.MODE == Constants.GUEST){
            txtExceed.setText(getResources().getString(R.string.carmera_alert_guest));
        }else{
            txtExceed.setText(getResources().getString(R.string.camera_alert));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_upload_more:


                getImages(new Config());
                break;
            case R.id.btn_continue:
                if(txtExceed.getVisibility() == View.VISIBLE && Constants.MODE == Constants.GUEST){
                    Toast.makeText(CameraOrderActivity.this, "Please Sign In", Toast.LENGTH_SHORT).show();
                }else{
                    if(Constants.cameraOrderModel.itemModels != null &&  Constants.cameraOrderModel.itemModels.size() > 0){
                        if(txtExceed.getVisibility() == View.VISIBLE){
                            Intent intent = new Intent(CameraOrderActivity.this, AddressDetailsNewActivity.class);
                            intent.putExtra("type", "exceed");
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(CameraOrderActivity.this, AddressDetailsNewActivity.class);
                            startActivity(intent);
                        }
                    }else{
                        Toast.makeText(CameraOrderActivity.this, getResources().getString(R.string.input_all), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void getImages(Config config) {

        config.setSelectionLimit(MAX - Constants.cameraOrderModel.itemModels.size());
        //ImagePickerActivity.setConfig(config);
        Intent intent = new Intent(CameraOrderActivity.this, ImagePickerActivity.class);
        intent.putExtra("limit", CameraOrderActivity.MAX - Constants.cameraOrderModel.itemModels.size());
        startActivityForResult(intent, MainActivity.INTENT_REQUEST_GET_IMAGES);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if (requestCode == MainActivity.INTENT_REQUEST_GET_IMAGES ) {

                for(int k = 0; k < Constants.cameraOrderModel.itemModels.size() ; k++){
                    String image = Constants.cameraOrderModel.itemModels.get(k).image;
                    if(image == null){
                        ItemModel itemModel = Constants.cameraOrderModel.itemModels.get(k);
                        Constants.cameraOrderModel.itemModels.remove(itemModel);
                    }
                }


                CameraOrderActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ArrayList<Uri> image_uris;
                        image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                        new CompressProcess(CameraOrderActivity.this, image_uris).execute();

                    }
                });

            }

        }
    }

}
