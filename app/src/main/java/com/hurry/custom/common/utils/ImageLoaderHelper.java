package com.hurry.custom.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hurry.custom.common.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;

import com.hurry.custom.R;

/**
 * Created by Administrator on 6/2/2016.
 */
public class ImageLoaderHelper {
    public static final int ROUND_200 = 200;
    private static DisplayImageOptions mDefaultOptions;

    protected static DisplayImageOptions getDisplayOptionsForDefault(boolean round) {
        if (round) {
            mDefaultOptions = new DisplayImageOptions.Builder()
                    .displayer(new SimpleBitmapDisplayer())
                    .cacheInMemory(true).cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageOnLoading(null)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.mipmap.ic_launcher)
                    .displayer(new RoundedBitmapDisplayer(1000))
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        } else {

            mDefaultOptions = new DisplayImageOptions.Builder()
                    .displayer(new SimpleBitmapDisplayer())
                    .cacheInMemory(true).cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageOnLoading(null)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.mipmap.ic_launcher)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }
        return mDefaultOptions;
    }

    /**
     * display image with default options
     */
    public static void displayImageRound(ImageLoader imageLoader, String urlImage, ImageView imageView,
                                         ImageLoadingListener imageLoadingListener) {
        if (urlImage == null) {
            return;
        }
        DisplayImageOptions options = getDisplayOptionsForDefault(false);
        if (imageLoader == null)
            imageLoader = ImageLoader.getInstance();

        if (imageLoadingListener == null) {
            imageLoader.displayImage(urlImage, imageView, options);
        } else {
            imageLoader.displayImage(urlImage, imageView, options, imageLoadingListener);
        }
    }

    public static void displayImageRound(ImageLoader imageLoader, String urlImage, ImageView imageView, int round,
                                         ImageLoadingListener imageLoadingListener) {
        if (urlImage == null) {
            return;
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new SimpleBitmapDisplayer())
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(null)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .displayer(new RoundedBitmapDisplayer(round))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        if (imageLoader == null)
            imageLoader = ImageLoader.getInstance();

        if (imageLoadingListener == null) {
            imageLoader.displayImage(urlImage, imageView, options);
        } else {
            imageLoader.displayImage(urlImage, imageView, options, imageLoadingListener);
        }
    }

    public static void displayImage(ImageLoader imageLoader, String urlImage, ImageView imageView,
                                    ImageLoadingListener imageLoadingListener) {
        if (urlImage == null) {
            return;
        }

        DisplayImageOptions options = getDisplayOptionsForDefault(false);
        if (imageLoader == null)
            imageLoader = ImageLoader.getInstance();

        if (imageLoadingListener == null) {
            imageLoader.displayImage(urlImage, imageView, options);
        } else {
            imageLoader.displayImage(urlImage, imageView, options, imageLoadingListener);
        }
    }

    public static void loadImage(ImageLoader imageLoader, String urlImage, ImageLoadingListener imageLoadingListener) {
        if (urlImage == null) {
            return;
        }

        DisplayImageOptions options = getDisplayOptionsForDefault(false);
        if (imageLoader == null)
            imageLoader = ImageLoader.getInstance();

        if (imageLoadingListener == null) {
            imageLoadingListener = new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }
            };
        }
        imageLoader.loadImage(urlImage, options, imageLoadingListener);
    }

    /**
     * display image with custom options
     *
     * @param imageScaleType
     */
    protected static void displayImage(String urlImage, ImageView imageView,
                                       ImageLoadingListener imageLoadingListener, int stubImageId, int failImageId,
                                       ImageScaleType imageScaleType) {
        if (urlImage == null) {
            imageView.setImageResource(failImageId);
            return;
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(stubImageId).showImageOnFail(failImageId)
                .showImageForEmptyUri(failImageId).cacheInMemory(true)
                .imageScaleType(imageScaleType).cacheOnDisk(true).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (imageLoadingListener == null) {
            imageLoader.displayImage(urlImage, imageView, options);
        } else {
            imageLoader.displayImage(urlImage, imageView, options,
                    imageLoadingListener);
        }
    }


    protected static void displayImageRound(String urlImage, ImageView imageView,
                                            ImageLoadingListener imageLoadingListener, int stubImageId, int failImageId,
                                            ImageScaleType imageScaleType) {
        if (urlImage == null) {
            imageView.setImageResource(failImageId);
            return;
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(stubImageId).showImageOnFail(failImageId)
                .showImageForEmptyUri(failImageId).cacheInMemory(true)
                .imageScaleType(imageScaleType).cacheOnDisk(true).displayer(new RoundedBitmapDisplayer(1)).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (imageLoadingListener == null) {
            imageLoader.displayImage(urlImage, imageView, options);
        } else {
            imageLoader.displayImage(urlImage, imageView, options,
                    imageLoadingListener);
        }
    }

    public static void cancelLoading(ImageAware imageView) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.cancelDisplayTask(imageView);
    }

    public static void removeImage(ImageLoader imageLoader, String urlImage) {
        if (urlImage == null) {
            return;
        }
        if (imageLoader == null)
            imageLoader = ImageLoader.getInstance();

        File imageFile = imageLoader.getDiscCache().get(urlImage);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        MemoryCacheUtils.removeFromCache(urlImage, imageLoader.getMemoryCache());
    }

    public static void removeAllImage(ImageLoader imageLoader) {
        if (imageLoader == null)
            imageLoader = ImageLoader.getInstance();
        imageLoader.clearDiskCache();
        imageLoader.clearMemoryCache();
    }


    public static void showImageFromLocal(Context context, String path, ImageView imageView){
        if(path != null){
            try{
                File file = new File(path);

                if(file.isFile()){
                    Glide.with(context)
                            .load(path)
                            .dontAnimate()
                            .centerCrop()
                            .error(com.gun0912.tedpicker.R.drawable.no_image)
                            .into(imageView);
                    //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    //imageView.setImageBitmap(myBitmap);
                }


            }catch (Exception e){}
        }
    }



    public static void showImage(Context context, String path, ImageView imageView){
        if(path != null){
            try{
                Glide.with(context)
                        .load(Constants.PHOTO_URL  + "products/"+  path)
                        //   .override(selected_bottom_size, selected_bottom_size)
                        .dontAnimate()
                        .centerCrop()
                        .error(com.gun0912.tedpicker.R.drawable.no_image)
                        .into(imageView);
                //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                //imageView.setImageBitmap(myBitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void showImageFromUrl(Context context, String path, ImageView imageView){
        if(path != null){
            try{

                    Glide.with(context)
                            .load(path)
                            //   .override(selected_bottom_size, selected_bottom_size)
                            .dontAnimate()
                            .centerCrop()
                            .error(com.gun0912.tedpicker.R.drawable.no_image)
                            .into(imageView);

                    //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    //imageView.setImageBitmap(myBitmap);

            }catch (Exception e){}
        }
    }

}
