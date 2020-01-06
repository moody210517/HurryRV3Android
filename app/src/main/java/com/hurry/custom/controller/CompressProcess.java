package com.hurry.custom.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.model.ItemModel;
import com.hurry.custom.view.activity.CameraOrderActivity;
import com.hurry.custom.view.activity.HomeActivity;
import com.hurry.custom.view.activity.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;


/**
 * Created by Administrator on 7/21/2016.
 */
 public class CompressProcess extends AsyncTask<Void, Void, Void> {
    Context  mContext;
    ArrayList<Uri> image_uris;

    public CompressProcess(Context con, ArrayList<Uri> uriArrayList)
    {
        super();
        this.mContext = con;
        this.image_uris = uriArrayList;
    }

    @Override
    protected void onPreExecute() {
        if(mContext instanceof MainActivity){
            ((MainActivity)mContext).showProgressDialog(mContext.getString(R.string.wait_awhile));
        }else if(mContext instanceof HomeActivity) {
            ((HomeActivity)mContext).showProgressDialog(mContext.getString(R.string.wait_awhile));
        }else{
            ((CameraOrderActivity)mContext).showProgressDialog(mContext.getString(R.string.wait_awhile));
        }
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... args) {

        for (int i = 0; i < image_uris.size(); i++) {
            if(i <= 9 && Constants.cameraOrderModel.itemModels.size() < 10){
                File file = new File(image_uris.get(i).getPath());
                File compressedImageFile = null;

                long fileSizeInBytes = file.length();
                long fileSizeInKB = fileSizeInBytes / 1024;
                compressedImageFile = file;
                if(fileSizeInKB < 500){
                    compressedImageFile = file;
                }else if(fileSizeInKB < 1024 ){
                    try {
                        compressedImageFile = new Compressor(mContext)
                                .setMaxWidth(300)
                                .setMaxHeight(400)
                                .setCompressFormat(Bitmap.CompressFormat.PNG)
                                .compressToFile(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(fileSizeInKB < 2048) {
                    try {
                        compressedImageFile = new Compressor(mContext)
                                .setMaxWidth(200)
                                .setMaxHeight(300)
                                .setCompressFormat(Bitmap.CompressFormat.PNG)
                                .compressToFile(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        compressedImageFile = new Compressor(mContext)
                                .setMaxWidth(180)
                                .setMaxHeight(250)
                                .setCompressFormat(Bitmap.CompressFormat.PNG)
                                .compressToFile(file);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ItemModel itemModel = new ItemModel();
                itemModel.title = compressedImageFile.getPath();
                itemModel.image = compressedImageFile.getAbsolutePath();
                itemModel.quantity = Constants.quantity.get(0);
                itemModel.weight = Constants.weight.get(0);
                itemModel.weight_value = Constants.weight_value.get(0);
                if(itemModel.image != null && !itemModel.image.isEmpty())
                    Constants.cameraOrderModel.itemModels.add(itemModel);
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void res) {

        if(mContext instanceof MainActivity){
            ((MainActivity)mContext).hideProgressDialog();
            ((MainActivity)mContext).updateFragment(11);

        }else if(mContext instanceof HomeActivity) {

            ((HomeActivity)mContext).hideProgressDialog();
            //Intent cameraOrder = new Intent(mContext, CameraOrderActivity.class);
            ((HomeActivity)mContext).goToCameraPage("");
        }else{
            ((CameraOrderActivity)mContext).hideProgressDialog();
            ((CameraOrderActivity)mContext).initView();
        }
    }

}
