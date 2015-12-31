package com.intencity.intencity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.ViewPagerAdapter;
import com.intencity.intencity.fragment.FitnessLogFragment;
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
    private final int TAB_FITNESS_GURU = 0;
    private final int TAB_RANKING = 1;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.tab_icon_fitness_guru,
            R.drawable.tab_icon_ranking,
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

    /**
     * Creates the tab icons for the layout.
     */
    private void setupTabIcons()
    {
        if (tabLayout != null)
        {
            tabLayout.getTabAt(TAB_FITNESS_GURU).setIcon(tabIcons[TAB_FITNESS_GURU]);
            tabLayout.getTabAt(TAB_RANKING).setIcon(tabIcons[TAB_RANKING]);
        }
    }

    /**
     * Sets up the view pager.
     *
     * @param viewPager     The view pager to add the adapter to.
     */
    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FitnessLogFragment(), "");
        adapter.addFrag(new RankingFragment(), "");
        viewPager.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.search:
                startActivity(SearchActivity.class);
                return true;
            case R.id.about:
                startActivity(AboutActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Starts an activity.
     *
     * @param cls   The class to start.
     */
    private void startActivity(Class<?> cls)
    {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}