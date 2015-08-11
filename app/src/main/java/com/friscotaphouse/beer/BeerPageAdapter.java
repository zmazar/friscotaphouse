package com.friscotaphouse.beer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zach on 8/8/2015.
 */
public class BeerPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private List<String> titles;

    public BeerPageAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<Fragment>();
        titles = new ArrayList<String>();
    }

    public void addFragments(List<Fragment> fl, List<String> tl) {
        if(fragments != null && titles != null) {
            // Clear out the fragments, and add the new ones.
            fragments.clear();
            titles.clear();
            fragments.addAll(fl);
            titles.addAll(tl);
        }

        // Notify the adapter that the fragments have changed so it can refresh.
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment ret = null;

        if(position <= fragments.size()) {
            ret = fragments.get(position);
        }

        return ret;
    }

    @Override
    public int getCount() {
        int ret = 0;

        if(fragments != null) {
            ret = fragments.size();
        }

        return ret;
    }

    @Override
    public int getItemPosition(Object obj) {
        int ret = POSITION_NONE;

        if(fragments.contains(obj)) {
            ret = POSITION_UNCHANGED;
        }

        return ret;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
