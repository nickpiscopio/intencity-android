package com.intencity.intencity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple pager adapter.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter
{
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager)
    {
        super(manager);
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        if (mFragmentTitleList.size() > 0)
        {
            return mFragmentTitleList.get(position);
        }

        return null;
    }

    /**
     * Adds a fragment to the adapter.
     *
     * @param fragment  The fragment to add.
     * @param title     The title to add.
     */
    public void addFrag(Fragment fragment, String title)
    {
        mFragmentList.add(fragment);

        if (!title.equals(""))
        {
            mFragmentTitleList.add(title);
        }
    }
}