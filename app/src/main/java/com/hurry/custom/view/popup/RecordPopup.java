package com.hurry.custom.view.popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.hurry.custom.R;
import com.hurry.custom.common.utils.ImageLoaderHelper;


public class RecordPopup implements View.OnClickListener{
    public View parent;
    public PopupWindow popupWindow;
    Context context;
    public View vview;


    ImageView imgClose;
    ImageView img;
    String url;
    String type;

    public RecordPopup(Context paramContext, String type, String url)
    {
        context = paramContext;
        this.type = type;
        this.url = url;
        this.parent = ((LayoutInflater)paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_image, null);
        this.popupWindow = new PopupWindow(this.parent, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,true);
        initView(this.parent);
    }
    public void showAtLocation(View pView,int left,int top, String  url, String type)
    {
        vview = pView;
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.update();
        popupWindow.setWidth((int) (pView.getWidth()));
        popupWindow.setHeight((int) (pView.getHeight()));
        popupWindow.showAtLocation(pView, Gravity.CENTER, left, top);
        this.url = url;
        this.type = type;
    }
    public void hide()
    {
        this.popupWindow.dismiss();
    }
    public boolean isVisible()
    {
        return this.popupWindow.isShowing();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.img_close:
                hide();
                break;
        }
    }

    public void initView(View view){

        imgClose = (ImageView)view.findViewById(R.id.img_close);
        img = (ImageView)view.findViewById(R.id.image);

        imgClose.setOnClickListener(this);
        img.setOnClickListener(this);

        if(type.equals("url")){
            ImageLoaderHelper.showImage(context,url , img);
        }else{
            ImageLoaderHelper.showImageFromLocal(context,url , img);
        }

    }
}