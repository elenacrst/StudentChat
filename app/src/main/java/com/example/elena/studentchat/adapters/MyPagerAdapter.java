package com.example.elena.studentchat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.example.elena.studentchat.fragments.PhotosFragment;
import com.example.elena.studentchat.fragments.WelcomeFragment;

/**
 * Created by Elena on 10/25/2017.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    public MyPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new WelcomeFragment();

            case 1:
                return new PhotosFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
