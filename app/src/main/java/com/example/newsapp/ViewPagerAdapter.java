package com.example.newsapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment[] childFragments;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);

    }


//
    @Override
    public Fragment getItem(int position) {
        Log.i("in getItem", String.valueOf(position));
        return Section.getInstance(position);
    }



    @Override
    public int getCount() {
        return 6; //6 items
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        if(position==0)
//            return "World";
//        if(position==1)
//            return "Business";
//        if(position==2)
//            return "Politics";
//        if(position==3)
//            return "Sports";
//        if(position==4)
//            return "Technology";
//        if(position==5)
//            return "Science";

        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return "World";
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return "Business";
            case 2: // Fragment # 1 - This will show SecondFragment
                return "Politics";
            case 3:
                return "Sports";
            case 4:
                return "Technology";
            case 5:
                return "Science";
            default:
                return null;

        }


    }
}
