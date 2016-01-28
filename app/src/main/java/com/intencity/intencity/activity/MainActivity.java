package com.intencity.intencity.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
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
import com.intencity.intencity.handler.NotificationHandler;
import com.intencity.intencity.listener.ExerciseListListener;
import com.intencity.intencity.listener.NotificationListener;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;
import com.intencity.intencity.util.Util;

import java.util.Date;

/**
 * This is the main activity for Intencity.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class MainActivity extends AppCompatActivity implements ExerciseListListener,
                                                               NotificationListener
{
    private final int MENU_ID = R.id.menu;

    private final int TAB_FITNESS_GURU = 0;
    private final int TAB_RANKING = 1;

    private Toolbar toolbar;

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private MenuItem menuItem;

    private int[] tabIcons = {
            R.drawable.tab_icon_fitness_guru,
            R.drawable.tab_icon_ranking
    };

    private FitnessLogFragment fitnessLogFragment;
    private RankingFragment rankingFragment;

    private Context context;

    private SecurePreferences securePreferences;

    boolean userHasLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String nullString = "";

        context = getApplicationContext();

        securePreferences = new SecurePreferences(context);
        if (securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, nullString).equals(nullString))
        {
            userHasLoggedIn = false;

            showDemo(DemoActivity.DESCRIPTION);
        }
        else
        {
            userHasLoggedIn = true;

            runIntencity();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (userHasLoggedIn)
        {
            rewardUserForUsingIntencity();
        }
    }

    /**
     * Shows the demo screens.
     *
     * @param page  The page to open when the demo starts.
     */
    private void showDemo(int page)
    {
        invalidateOptionsMenu();

        Intent intent = new Intent(this, DemoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.EXTRA_DEMO_PAGE, page);
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
        setupViewPager();
        viewPager.setCurrentItem(TAB_FITNESS_GURU);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        // Instantiate the NotificationHandler.
        // We reset it because it won't work properly if we log out and log back in.
        NotificationHandler.getInstance(this).resetInstance();
        NotificationHandler.getInstance(this);
    }

    /**
     * Reward the user for using Intencity every 12 hours the user comes back to the app.
     *
     * This is not in UTC, but I don't believe we need to be that specific for this.
     * We grant users points in a relaxed way.
     */
    private void rewardUserForUsingIntencity()
    {
        // Set the email and account type in the SecurePreferences.
        SecurePreferences securePreferences = new SecurePreferences(context);
        long lastLogin = securePreferences.getLong(Constant.USER_LAST_LOGIN, 0);
        long now = new Date().getTime();

        SecurePreferences.Editor editor = securePreferences.edit();

        if ((now - lastLogin) >= Constant.LOGIN_POINTS_THRESHOLD)
        {
            String email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");
            Util.grantPointsToUser(email, Constant.POINTS_LOGIN, context.getString(R.string.award_login_description));

            editor.putLong(Constant.USER_LAST_LOGIN, now);
            editor.apply();
        }

        if (lastLogin == 0)
        {
            CustomDialogContent content = new CustomDialogContent(context.getString(R.string.welcome_title), context.getString(R.string.welcome_description), false);
            content.setPositiveButtonStringRes(R.string.get_started);
            new CustomDialog(MainActivity.this, null, content);
        }
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
     */
    private void setupViewPager()
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        fitnessLogFragment = new FitnessLogFragment();
        fitnessLogFragment.setMainActivityExerciseListListener(this);
        fitnessLogFragment.setNotificationListener(this);
        adapter.addFrag(fitnessLogFragment, "");

        rankingFragment = new RankingFragment();
        adapter.addFrag(rankingFragment, "");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        menuItem = menu.findItem(MENU_ID);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case MENU_ID:
                startActivityForResult(new Intent(this, MenuActivity.class),
                                       Constant.REQUEST_CODE_LOG_OUT);

                menuItem.setIcon(ContextCompat.getDrawable(context, R.mipmap.menu));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Removes the user's secure preferences and returns him or her to the login page.
     */
    private void logOut()
    {
        SecurePreferences.Editor editor = securePreferences.edit();
        editor.clear();
        editor.apply();

        showDemo(DemoActivity.LOG_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constant.REQUEST_CODE_LOG_OUT)
        {
            logOut();
        }
    }

    @Override
    public void onCompletedWorkout()
    {
        setupViewPager();
    }

    @Override
    public void onNotificationAdded()
    {
        if (menuItem != null)
        {
            menuItem.setIcon(ContextCompat.getDrawable(context, R.drawable.menu_notification));

            ((AnimationDrawable)menuItem.getIcon()).start();
        }
    }
}