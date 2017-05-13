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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.SelectableListItemAdapter;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.SelectableListItem;
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

    private ListView listView;

    private SelectableListItemAdapter adapter;

    private ArrayList<SelectableListItem> routines;
    private ArrayList<String> routinesToRemove;

    private int userId;

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

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        userId = Util.getSecurePreferencesUserId(context);

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
    public void onServiceResponse(int statusCode, String response)
    {
        switch (statusCode)
        {
            case Constant.STATUS_CODE_SUCCESS_USER_ROUTINE_UPDATED:

                setResult(Constant.REQUEST_CODE_SAVED_ROUTINE_UPDATED);
                finish();

                break;

            case Constant.STATUS_CODE_FAILURE_USER_ROUTINE_UPDATE:
            default:

                showConnectionIssue();
                break;
        }
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
    private void populateListView(ArrayList<SelectableListItem> routines)
    {
        adapter = new SelectableListItemAdapter(context, R.layout.list_item_standard_selectable, routines);

        View header = getLayoutInflater().inflate(R.layout.list_item_header_title_description, null);

        TextView title = (TextView) header.findViewById(R.id.title);
        TextView description = (TextView) header.findViewById(R.id.description);

        title.setText(context.getString(R.string.edit_routines_description1));
        description.setText(context.getString(R.string.edit_routines_description2));

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.addHeaderView(header, null, false);
        listView.setOnItemClickListener(routineClickListener);

        progressBar.setVisibility(View.GONE);
    }

    /**
     * Sends the data to the server to remove routines.
     */
    private void removeRoutines()
    {
        if (routinesToRemove.size() > 0)
        {
            progressBar.setVisibility(View.VISIBLE);

            new ServiceTask(this).execute(Constant.SERVICE_UPDATE_USER_ROUTINE, Constant.generateServiceListVariables(
                    userId, routinesToRemove, false));
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
            SelectableListItem routine = routines.get(position - 1);

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