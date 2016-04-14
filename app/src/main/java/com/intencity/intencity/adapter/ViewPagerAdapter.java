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
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager)
    {
        super(manager);
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return null;
    }

    /**
     * Returns the title of the page.
     *
     * If we want this to be in the tab titles, place in the overridden getPageTitle(int position) method.
     *
     * @param position  The position of the list.
     *
     * @return  The title of the page from the list.
     */
    public CharSequence getTitle(int position)
    {
        return fragmentTitleList.size() > 0 ? fragmentTitleList.get(position) : null;
    }

    /**
     * Adds a fragment to the adapter.
     *
     * @param fragment  The fragment to add.
     * @param title     The title to add.
     */
    public void addFrag(Fragment fragment, String title)
    {
        fragmentList.add(fragment);

        if (!title.equals(""))
        {
            fragmentTitleList.add(title);
        }
    }
}