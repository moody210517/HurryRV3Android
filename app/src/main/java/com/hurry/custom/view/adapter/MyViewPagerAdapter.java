package com.hurry.custom.view.adapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.hurry.custom.view.fragment.AccountFragment;
import com.hurry.custom.view.fragment.BaseFragment;
import com.hurry.custom.view.fragment.FeedBackFragment;
import com.hurry.custom.view.fragment.HomeFragment;
import com.hurry.custom.view.fragment.OrderHisContainerFragment;
import com.hurry.custom.view.fragment.SettingFragment;


public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

    private int size;

    public MyViewPagerAdapter(FragmentManager fm, int size) {
        super(fm);
        this.size = size;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AccountFragment();
            case 1:

                return new OrderHisContainerFragment();

            case 2:
                return new HomeFragment();

            case 3:
                return new SettingFragment();

            case 4:
                return new HomeFragment();
            case 5:
                return new FeedBackFragment();

            default:
                return new AccountFragment();
        }

    }

    @Override
    public int getCount()     {
        return size;
    }



    @Override
    public int getItemPosition(Object item) {
        BaseFragment fragment = (BaseFragment)item;

        int position = fragment.position;

        if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }
    }


}
