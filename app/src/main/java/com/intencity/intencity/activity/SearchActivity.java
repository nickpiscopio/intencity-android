package com.intencity.intencity.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.RankingListAdapter;
import com.intencity.intencity.helper.doa.UserDao;
import com.intencity.intencity.listener.ServiceListener;
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
public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener
{
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        searchItem.expandActionView();

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.menu_search));
        //        searchView.requestFocus();
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
        String searchTerm = Constant.LIKE_OPERATOR + query.replaceAll(Constant.SPACE_REGEX, "") + Constant.LIKE_OPERATOR;

        new ServiceTask(searchUsersServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                            Constant.generateStoredProcedureParameters(
                                                                    Constant.STORED_PROCEDURE_SEARCH_USERS,
                                                                    email, searchTerm));
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
            return false;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item)
        {
            // Calling finish() here so when the user hits the
            // back button on the search, the activity also closes.
            finish();

            return false;
        }
    };

    @Override
    public void onBackPressed()
    {
        finish();

        super.onBackPressed();
    }

    /**
     * Populates the ranking list.
     *
     * @param users  The list of users.
     */
    private void populateSearchList(ArrayList<User> users)
    {
        ListView listView = (ListView) findViewById(R.id.list_view_search);

        RankingListAdapter arrayAdapter = new RankingListAdapter(
                context,
                R.layout.list_item_ranking,
                users);

        listView.setAdapter(arrayAdapter);
    }

    /**
     * Service listener for searching for a user.
     */
    ServiceListener searchUsersServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            populateSearchList(new UserDao().parseJson(response));
        }

        @Override public void onRetrievalFailed() { }
    };
}