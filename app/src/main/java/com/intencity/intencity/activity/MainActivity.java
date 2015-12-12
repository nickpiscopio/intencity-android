package com.intencity.intencity.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.PagerAdapter;
import com.intencity.intencity.util.Constant;

public class MainActivity extends FragmentActivity
{
    public static final int CLASS_ID = 0;

    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = this.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (!prefs.getBoolean(Constant.DEMO_FINISHED, false))
        {
            showDemo();
        }
        else
        {
            runIntencity();
        }
    }

    /**
     * Shows the demo screens.
     */
    private void showDemo()
    {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Runs the normal functionality of Intencity.
     */
    private void runIntencity()
    {
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), CLASS_ID);

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(pageChangeListener);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageSelected(int position)
        {
            switch (position)
            {
                case 0:

                    break;
                case 1:

                    break;
                case 2:

                    break;
                case 3:
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) { }
    };

    @Override
    public void onBackPressed()
    {
        if (pager.getCurrentItem() == 0)
        {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        }
        else
        {
            // Otherwise, select the previous step.
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }
}
