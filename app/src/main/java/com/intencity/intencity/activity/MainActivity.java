package com.intencity.intencity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.ViewPagerAdapter;
import com.intencity.intencity.fragment.FitnessLogFragment;
import com.intencity.intencity.fragment.MenuFragment;
import com.intencity.intencity.fragment.RankingFragment;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

/**
 * This is the main activity for Intencity.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class MainActivity extends AppCompatActivity
{
    private final int TAB_RANKING = 0;
    private final int TAB_FITNESS_GURU = 1;
    private final int TAB_MENU = 2;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.tab_icon_ranking,
            R.drawable.tab_icon_fitness_guru,
            R.drawable.tab_icon_menu
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String nullString = "";

        SecurePreferences prefs = new SecurePreferences(getApplicationContext());
        if (prefs.getString(Constant.USER_ACCOUNT_EMAIL, nullString).equals(nullString))
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
        viewPager.setCurrentItem(TAB_FITNESS_GURU);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
    }

    private void setupTabIcons()
    {
        if (tabLayout != null)
        {
            tabLayout.getTabAt(TAB_RANKING).setIcon(tabIcons[TAB_RANKING]);
            tabLayout.getTabAt(TAB_FITNESS_GURU).setIcon(tabIcons[TAB_FITNESS_GURU]);
            tabLayout.getTabAt(TAB_MENU).setIcon(tabIcons[TAB_MENU]);
        }
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new RankingFragment(), "");
        adapter.addFrag(new FitnessLogFragment(), "");
        adapter.addFrag(new MenuFragment(), "");
        viewPager.setAdapter(adapter);
    }
}