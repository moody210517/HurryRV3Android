package com.hurry.custom.view.fragment;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.hurry.custom.view.activity.map.TouchMapActivity;


/**
 * Created by Administrator on 8/18/2017.
 */
public class TouchableWrapper extends FrameLayout {

    Context mContext;
    public TouchableWrapper(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                TouchMapActivity.mMapIsTouched = true;
                //Toast.makeText(mContext, "click", Toast.LENGTH_SHORT).show();
                ((TouchMapActivity)mContext).hideInformation();
                break;

            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_UP:
                TouchMapActivity.mMapIsTouched = false;
                //Toast.makeText(mContext, "up", Toast.LENGTH_SHORT).show();
                ((TouchMapActivity)mContext).showInformation();
                break;

        }
        return super.dispatchTouchEvent(event);
    }
}