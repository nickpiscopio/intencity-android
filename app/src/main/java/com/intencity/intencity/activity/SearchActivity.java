package com.intencity.intencity.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;

/**
 * This is the search activity for Intencity.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
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
        new ServiceTask(searchUsersServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                        storedProcedureParatmeters);
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

    ServiceListener searchUsersServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {

        }

        @Override public void onRetrievalFailed()
        {

        }
    };
}