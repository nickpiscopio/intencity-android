package com.intencity.intencity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.RankingListAdapter;
import com.intencity.intencity.adapter.SearchExerciseListAdapter;
import com.intencity.intencity.helper.doa.ExerciseDao;
import com.intencity.intencity.helper.doa.UserDao;
import com.intencity.intencity.listener.SearchListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.User;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

import java.util.ArrayList;

/**
 * This is the search activity for Intencity.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
                                                                 SearchListener
{
    private SearchView searchView;
    private ListView listView;

    private Context context;

    private boolean searchExercises;

    // This is the list of exercises that the user has already completed.
    private ArrayList<Exercise> exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = (ListView) findViewById(R.id.list_view_search);

        context = getApplicationContext();

        Bundle bundle = getIntent().getExtras();
        searchExercises = bundle.getBoolean(Constant.BUNDLE_SEARCH_EXERCISES);
        exercises = bundle.getParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.expandActionView();

        searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(searchExercises ?
                                        getString(R.string.search_exercise) :
                                        getString(R.string.search_user));
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchItem, actionExpandListener);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        SecurePreferences securePreferences = new SecurePreferences(context);
        String email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        // Get all the users from the database with the search query minus the spaces.
        // Need to add % before and after the search term, so we can get back the proper
        // values from the database.
        query = searchExercises ? query : query.replaceAll(Constant.SPACE_REGEX, "");
        String searchTerm = Constant.LIKE_OPERATOR + query + Constant.LIKE_OPERATOR;

        // Get the stored procedure depending upon what we are searching.
        String urlParameters = searchExercises ?
                Constant.generateStoredProcedureParameters(
                        Constant.STORED_PROCEDURE_SEARCH_EXERCISES, email, searchTerm) :
                Constant.generateStoredProcedureParameters(
                        Constant.STORED_PROCEDURE_SEARCH_USERS, email, searchTerm);

        new ServiceTask(searchListener).execute(Constant.SERVICE_STORED_PROCEDURE, urlParameters);

        return false;
    }

    /**
     * The expand listener for the search view.
     */
    private MenuItemCompat.OnActionExpandListener actionExpandListener = new MenuItemCompat.OnActionExpandListener()
    {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item)
        {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item)
        {
            // Calling finish() here so when the user hits the
            // back button on the search, the activity also closes.
            onBackPressed();
            return true;
        }
    };

    @Override
    public void onBackPressed()
    {
        if (!searchExercises)
        {
            setResult(null);
        }
        else
        {
            finish();
        }

        super.onBackPressed();
    }

    /**
     * Service listener for searching for a user.
     */
    ServiceListener searchListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            searchView.clearFocus();

            ArrayAdapter arrayAdapter;

            if (searchExercises)
            {
                ArrayList<Exercise> searchedExerciseResults = new ExerciseDao().parseJson(response);
                arrayAdapter  = new SearchExerciseListAdapter(context, R.layout.list_item_search_exercise, searchedExerciseResults, exercises, SearchActivity.this);
            }
            else
            {
                ArrayList<User> users = new UserDao().parseJson(response);
                arrayAdapter  = new RankingListAdapter(context, users);
            }

            listView.setAdapter(arrayAdapter);
        }

        @Override
        public void onRetrievalFailed()
        {
            searchView.clearFocus();
        }
    };

    /**
     * Sets the result for this activity and goes back.
     *
     * @param exercise  The exercise that was added if we are searching exercises.
     */
    private void setResult(Exercise exercise)
    {
        // Send the exercise back to the main activity
        // so we can send it to the fragment to get added to the list.
        Intent intent = new Intent();

        if (exercise != null)
        {
            intent.putExtra(Constant.BUNDLE_EXERCISE, exercise);
        }

        intent.putExtra(Constant.BUNDLE_SEARCH_EXERCISES, searchExercises);
        setResult(Constant.REQUEST_CODE_SEARCH, intent);
        finish();
    }

    @Override
    public void onExerciseAdded(Exercise exercise)
    {
        setResult(exercise);
    }
}