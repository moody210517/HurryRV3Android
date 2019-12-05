package com.hurry.custom.view.custom;

/**
 * Created by Administrator on 7/5/2016.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.hurry.custom.R;


public class IconTextTabLayout extends TabLayout {

    //TypedArray YourIcons = getResources().obtainTypedArray(R.array.random_imgs);
    int [] YourIcons = {R.drawable.ic_dashboard, R.drawable.ic_dashboard, R.drawable.ic_dashboard};
    String [] title  = {"Completed", "Pending", "Returned"};
    Context mContext;

    public IconTextTabLayout(Context context) {
        super(context);
        mContext = context;
    }

    public IconTextTabLayout(Context context, AttributeSet attrs) {

        super(context, attrs);
        mContext  =  context;
    }

    public IconTextTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setTabsFromPagerAdapter(@NonNull PagerAdapter adapter) {
        this.removeAllTabs();
        int i = 0;
        for (int count = adapter.getCount(); i < count; ++i) {
//            this.addTab(this.newTab().setCustomView(R.layout.custom_tab)
//                    .setIcon(YourIcons[i])
//                    .setText(adapter.getPageTitle(i)));
            View view = generateView();
            TextView txtTitle = (TextView)view.findViewById(R.id.txt_title);
            //ImageView imgIcon = (ImageView)view.findViewById(R.id.icon);
            txtTitle.setText(title[i]);
            //imgIcon.setBackgroundResource(YourIcons[i]);
            this.addTab(this.newTab().setCustomView(view));
        }
    }
    public View generateView(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.custom_tab, null);
        return view;
    }


}