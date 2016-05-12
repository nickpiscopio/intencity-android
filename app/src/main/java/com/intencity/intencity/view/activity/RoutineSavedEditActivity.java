package com.intencity.intencity.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.RoutineSavedAdapter;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.RoutineRow;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.Util;

import java.util.ArrayList;

/**
 * This is the Intencity Routine activity.
 *
 * Created by Nick Piscopio on 5/6/16.
 */
public class RoutineSavedEditActivity extends AppCompatActivity implements ServiceListener
{
    private Context context;

    private ProgressBar progressBar;

    private LinearLayout description;

    private View divider;

    private ListView listView;

    private RoutineSavedAdapter adapter;

    private ArrayList<RoutineRow> routines;
    private ArrayList<String> routinesToRemove;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_saved_edit);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        context = getApplicationContext();

        divider = findViewById(R.id.divider);
        description = (LinearLayout) findViewById(R.id.layout_description);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        email = Util.getSecurePreferencesEmail(context);

        routines = getIntent().getParcelableArrayListExtra(Constant.BUNDLE_ROUTINE_ROWS);
        routinesToRemove = new ArrayList<>();

        populateListView(routines);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.save:
                removeRoutines();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        setResult(Constant.REQUEST_SAVED_ROUTINE_UPDATED);
        finish();
    }

    @Override
    public void onRetrievalFailed()
    {
        showConnectionIssue();
    }

    /**
     * Displays a generic error to the user stating Intencity couldn't connect to the server.
     */
    private void showConnectionIssue()
    {
        CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), context.getString(R.string.intencity_communication_error), false);

        new CustomDialog(RoutineSavedEditActivity.this, dialogListener, dialog, false);

        progressBar.setVisibility(View.GONE);
    }

    /**
     * Populates the list.
     *
     * @param routines      ArrayList of routines to add to the list view.
     */
    private void populateListView(ArrayList<RoutineRow> routines)
    {
        adapter = new RoutineSavedAdapter(context, R.layout.list_item_standard_checkbox, routines);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(routineClickListener);

        progressBar.setVisibility(View.GONE);

        divider.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
    }

    /**
     * Sends the data to the server to remove routines.
     */
    private void removeRoutines()
    {
        if (routinesToRemove.size() > 0)
        {
            progressBar.setVisibility(View.VISIBLE);

            new ServiceTask(this).execute(Constant.SERVICE_UPDATE_USER_ROUTINE, Constant.generateServiceListVariables(email, routinesToRemove, false));
        }
        else
        {
            onBackPressed();
        }
    }

    /**
     * The dialog listener for when the connection to Intencity fails.
     */
    private DialogListener dialogListener = new DialogListener()
    {
        @Override
        public void onButtonPressed(int which)
        {
            // There is only 1 button that can be pressed, so we aren't going to switch on it.
            finish();
        }
    };

    /**
     * The click listener for when a routine is clicked in the ListView.
     */
    private AdapterView.OnItemClickListener routineClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            RoutineRow routine = routines.get(position);

            String title = routine.getTitle();

            // Add or remove muscle groups from the list
            // if he or she clicks on a list item.
            if (routinesToRemove.contains(title))
            {
                routine.setChecked(true);
                routinesToRemove.remove(title);
            }
            else
            {
                routine.setChecked(false);
                routinesToRemove.add(title);
            }

            adapter.notifyDataSetChanged();
        }
    };
}