package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.LeaderboardAdapter;
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
import com.intencity.intencity.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is the search activity for Intencity.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
                                                                 SearchListener
{
    // The milliseconds it takes to search for an exercise.
    // We want this because the user might be typing, and we don't want to flood the server with searches.
    private final long SEARCH_EXECUTE_MILLIS = 1300;

    private LinearLayout connectionIssue;

    private ProgressBar progressBar;

    private View divider;

    private MenuItem searchItem;
    private SearchView searchView;
    private ListView listView;

    private Context context;

    private boolean searchExercises;

    private ArrayList<User> users;

    // This is the list of exercises that the user has already completed.
    private ArrayList<Exercise> exercises;

    private String searchString;

    private ArrayAdapter arrayAdapter = null;

    private Handler searchExecutionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        connectionIssue = (LinearLayout) findViewById(R.id.layout_connection_issue);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        divider = findViewById(R.id.divider);
        divider.setVisibility(View.GONE);

        listView = (ListView) findViewById(R.id.list_view_search);
        listView.setEmptyView(findViewById(R.id.empty_list));

        context = getApplicationContext();

        Bundle bundle = getIntent().getExtras();
        searchExercises = bundle.getBoolean(Constant.BUNDLE_SEARCH_EXERCISES);
        exercises = bundle.getParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST);

        if (!searchExercises)
        {
            listView.setOnItemClickListener(userClickListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        searchItem = menu.findItem(R.id.search);
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
    public boolean onQueryTextChange(final String query)
    {
        progressBar.setVisibility(View.VISIBLE);

        connectionIssue.setVisibility(View.GONE);

        if (searchExecutionHandler != null)
        {
            searchExecutionHandler.removeMessages(0);
        }

        // Get a handler that can be used to post to the main thread
        searchExecutionHandler = new Handler(context.getMainLooper());
        searchExecutionHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                onQueryTextSubmit(query);
            }
        }, SEARCH_EXECUTE_MILLIS);

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        searchString = query;

        SecurePreferences securePreferences = new SecurePreferences(context);
        String email = securePreferences.getString(Constant.USER_ACCOUNT_ID, "");

        // Get all the users from the database with the search query minus the spaces.
        query = searchExercises ? query : query.replaceAll(Constant.REGEX_SPACE, "");

        query = Util.replaceApostrophe(query);

        // Get the stored procedure depending upon what we are searching.
        String urlParameters = searchExercises ?
                Constant.generateStoredProcedureParameters(
                        Constant.STORED_PROCEDURE_SEARCH_EXERCISES, email, query) :
                Constant.generateStoredProcedureParameters(
                        Constant.STORED_PROCEDURE_SEARCH_USERS, email, query);

        new ServiceTask(searchListener).execute(Constant.SERVICE_EXECUTE_STORED_PROCEDURE, urlParameters);

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
     * The click listener for each user clicked in the ListView.
     */
    private AdapterView.OnItemClickListener userClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            User user  = users.get(position);

            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(Constant.BUNDLE_FROM_SEARCH, true);
            intent.putExtra(Constant.BUNDLE_POSITION, position);
            intent.putExtra(Constant.BUNDLE_USER, user);

            startActivityForResult(intent, Constant.REQUEST_CODE_PROFILE);
        }
    };

    /**
     * Service listener for searching for a user.
     */
    ServiceListener searchListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            searchView.clearFocus();

            progressBar.setVisibility(View.GONE);

            if (searchExercises)
            {
                parseExercises(response);
            }
            else
            {
                users = new UserDao().parseJson(response);
                arrayAdapter  = new LeaderboardAdapter(context, R.layout.list_item_ranking, users, true);
                listView.setAdapter(arrayAdapter);
            }

            if (arrayAdapter != null)
            {
                divider.setVisibility(View.GONE);
            }
        }

        @Override
        public void onRetrievalSuccessful(int statusCode, JSONObject response)
        {

        }

        @Override
        public void onRetrievalFailed(int statusCode)
        {
            if (searchExercises)
            {
                parseExercises("");

                showConnectionIssue();
            }
            else
            {
                showConnectionIssue();
            }
        }
    };

    /**
     * Parses the exercises from the response.
     *
     * @param response  The response from the server.
     */
    private void parseExercises(String response)
    {
        ExerciseDao dao = new ExerciseDao();
        try
        {
            ArrayList<Exercise> searchedExerciseResults = dao.parseJson(response, searchString);
            arrayAdapter  = new SearchExerciseListAdapter(context, searchedExerciseResults, exercises, SearchActivity.this);
            listView.setAdapter(arrayAdapter);
        }
        catch (JSONException e)
        {
            showConnectionIssue();
        }
    }

    /**
     * Shows a connection issue to the user.
     */
    private void showConnectionIssue()
    {
        searchView.clearFocus();

        connectionIssue.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        divider.setVisibility(View.VISIBLE);
    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Constant.REQUEST_CODE_PROFILE)
        {
            Bundle extras = data.getExtras();
            int position = extras.getInt(Constant.BUNDLE_POSITION);
            int followId = extras.getInt(Constant.BUNDLE_FOLLOW_ID);

            users.get(position).setFollowingId(followId);
        }
    }
}