package com.hurry.custom.view.adapter;

/**
 * Created by Administrator on 6/30/2015.
 */

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;


public class StartPagerAdapter extends PagerAdapter {

    private ArrayList<View> mListView;
    private  Activity activity;

    public StartPagerAdapter(Activity act, ArrayList<View> mListView) {
        this.mListView = mListView;
        activity = act;
    }

    @Override
    public int getCount() {
        /*if(mListView.size() == 3){
            Intent intent = new Intent(activity , AgreeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        }*/
        return mListView != null && !mListView.isEmpty() ? mListView.size() : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        container.addView(mListView.get(position % mListView.size()));
        return mListView.get(position % mListView.size());

    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mListView.get(position % mListView.size()));
        container.removeView((View)object);
    }

    public class ViewHolder{

    }
}
