package com.intencity.intencity.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.intencity.intencity.activity.DemoActivity;
import com.intencity.intencity.fragment.FitnessGuruFragment;
import com.intencity.intencity.fragment.LoginFragment;
import com.intencity.intencity.fragment.MenuFragment;
import com.intencity.intencity.fragment.PagerFragment;
import com.intencity.intencity.fragment.RankingFragment;

/**
 * A simple pager adapter that represents pages for the demo.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class PagerAdapter extends FragmentStatePagerAdapter
{
    public static final int NUM_PAGES_DEMO = 4;
    public static final int NUM_PAGES_INTENCITY = 3;

    private int calledFrom;

    /**
     * Constructor for the Pager Adapter.
     *
     * @param fm        An instance of the fragment manager.
     * @param classId   The class ID that created this adapter.
     */
    public PagerAdapter(FragmentManager fm, int classId)
    {
        super(fm);

        this.calledFrom = classId;
    }

    @Override
    public Fragment getItem(int position)
    {
        if (calledFrom == DemoActivity.CLASS_ID)
        {
            if (position == NUM_PAGES_DEMO - 1)
            {
                return new LoginFragment();
            }
            else
            {
                Bundle bundle = new Bundle();
                bundle.putInt(PagerFragment.FRAGMENT_PAGE, position);

                PagerFragment sliderFragment = new PagerFragment();
                sliderFragment.setArguments(bundle);

                return sliderFragment;
            }
        }
        else
        {
            switch (position)
            {
                case 0:
                    return new RankingFragment();
                case 2:
                    return new MenuFragment();
                case 1:
                default:
                    return new FitnessGuruFragment();
            }
        }
    }

    @Override
    public int getCount()
    {
        if (calledFrom == DemoActivity.CLASS_ID)
        {
            return NUM_PAGES_DEMO;
        }
        else
        {
            return NUM_PAGES_INTENCITY;
        }
    }
}