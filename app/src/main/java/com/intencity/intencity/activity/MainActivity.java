package com.intencity.intencity.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.ViewPagerAdapter;
import com.intencity.intencity.fragment.FitnessGuruFragment;
import com.intencity.intencity.fragment.MenuFragment;
import com.intencity.intencity.fragment.RankingFragment;
import com.intencity.intencity.util.Constant;

/**
 * This is the main activity for Intencity.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class MainActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            android.R.drawable.btn_star,
            android.R.drawable.btn_star,
            android.R.drawable.btn_star
    };

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons()
    {
        if (tabLayout != null)
        {
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new RankingFragment(), getString(R.string.title_rankings));
        adapter.addFrag(new FitnessGuruFragment(), getString(R.string.title_fitness_guru));
        adapter.addFrag(new MenuFragment(), getString(R.string.title_menu));
        viewPager.setAdapter(adapter);
    }

    // Might need later.
//    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener()
//    {
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
//
//        @Override
//        public void onPageSelected(int position)
//        {
//            switch (position)
//            {
//                case 0:
//
//                    break;
//                case 1:
//
//                    break;
//                case 2:
//
//                    break;
//                case 3:
//                default:
//                    break;
//            }
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) { }
//    };

    @Override
    public void onBackPressed()
    {
        if (viewPager.getCurrentItem() == 0)
        {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        }
        else
        {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
}
