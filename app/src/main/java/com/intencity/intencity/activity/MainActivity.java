package com.intencity.intencity.activity;

import android.content.Context;
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
import com.intencity.intencity.listener.ExerciseListListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

import java.util.ArrayList;

/**
 * This is the main activity for Intencity.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class MainActivity extends AppCompatActivity implements ExerciseListListener
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

    private FitnessLogFragment fitnessLogFragment;

    private ArrayList<Exercise> exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String nullString = "";

        SecurePreferences prefs = new SecurePreferences(getApplicationContext());
        if (prefs.getString(Constant.USER_ACCOUNT_EMAIL, nullString).equals(nullString))
        {
            showDemo(DemoActivity.DESCRIPTION_PAGE);
        }
        else
        {
            runIntencity();
        }
    }

    /**
     * Shows the demo screens.
     *
     * @param page  The page to open when the demo starts.
     */
    private void showDemo(int page)
    {
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
        adapter.addFrag(fitnessLogFragment, "");
        adapter.addFrag(new RankingFragment(), "");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.search:
                search();
                return true;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.log_out:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Opens the SearchActivity.
     */
    private void search()
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.BUNDLE_SEARCH_EXERCISES,
                          viewPager.getCurrentItem() == TAB_FITNESS_GURU);
        bundle.putParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST, exercises);
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constant.REQUEST_CODE_SEARCH);
    }

    /**
     * Removes the user's secure preferences and returns him or her to the login page.
     */
    private void logOut()
    {
        Context context = getApplicationContext();
        SecurePreferences securePreferences = new SecurePreferences(context);
        SecurePreferences.Editor editor = securePreferences.edit();
        editor.clear();
        editor.apply();

        showDemo(DemoActivity.LOG_IN_PAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Constant.REQUEST_CODE_SEARCH)
        {
            Bundle extras = data.getExtras();
            Exercise exercise = extras.getParcelable(Constant.BUNDLE_EXERCISE);

            fitnessLogFragment.addExerciseToList(exercise);
        }
    }

    @Override
    public void onNextExercise(ArrayList<Exercise> exercises)
    {
        this.exercises = exercises;
    }
}