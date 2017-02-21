package com.example.he.jockbook.View;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by he on 2017/2/19.
 */

public class MyViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mList;
    private List<String> mTitleList;

    public MyViewPagerAdapter(FragmentManager fm,List<Fragment> list,List<String> title) {
        super(fm);
        mList=list;
        mTitleList=title;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }




}
