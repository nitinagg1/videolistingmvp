package com.mn2square.videolistingmvp.activity.viewpageradapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mn2square.videolistingmvp.swipetabfragments.ListFragement.presenter.ListFragmentImpl;
import com.mn2square.videolistingmvp.swipetabfragments.SavedListFragment.presenter.SavedListFragmentImpl;
import com.mn2square.videolistingmvp.swipetabfragments.folderlistfragment.presenter.FolderListFragmentImpl;

/**
 * Created by nitinagarwal on 3/15/17.
 */


public class ViewPagerAdapter extends FragmentPagerAdapter {

    Context mContext;
    CharSequence[] mTitles;

    public ViewPagerAdapter(FragmentManager fm, CharSequence[] titles)
    {
        super(fm);
        mTitles = titles;
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    static final int NUM_ITEMS = 3;

    @Override
    public Fragment getItem(int position) {

        return new com.mn2square.videolistingmvp.mvvm.swipetabfragments.ListFragment.ListFragmentImpl();
//        return new Fragment();
//        switch (position) {
//            case 0:
//                return new FolderListFragmentImpl();
//            case 1:
//                return new ListFragmentImpl();
//            case 2:
//                return new SavedListFragmentImpl();
//            default:
//                return new ListFragmentImpl();

//        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}