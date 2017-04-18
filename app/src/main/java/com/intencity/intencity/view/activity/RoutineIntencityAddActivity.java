package com.intencity.intencity.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This is the Intencity Routine activity.
 *
 * Created by Nick Piscopio on 5/6/16.
 */
public class RoutineIntencityAddActivity extends AppCompatActivity implements ServiceListener
{
    private Context context;

    private ProgressBar progressBar;

    private ListView listView;

    private String email;

    private ArrayList<SelectableListItem> muscleGroups;
    private ArrayList<String> routineMuscleGroups;

    private SelectableListItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_intencity_add);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);
        progressBar.setVisibility(View.VISIBLE);

        context = getApplicationContext();

        email = Util.getSecurePreferencesUserId(context);

        progressBar.setVisibility(View.VISIBLE);

        new ServiceTask(this).execute(Constant.SERVICE_EXECUTE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_CUSTOM_ROUTINE_MUSCLE_GROUP, null));
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
                saveRoutine();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            muscleGroups = new ArrayList<>();
            routineMuscleGroups = new ArrayList<>();

            JSONArray array = new JSONArray(response);

            int length = array.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject object = array.getJSONObject(i);

                String name = object.getString(Constant.COLUMN_DISPLAY_NAME);

                SelectableListItem muscleGroup = new SelectableListItem(name);
                muscleGroup.setChecked(false);

                // Add all the equipment to the array.
                muscleGroups.add(muscleGroup);
            }

            populateListView(muscleGroups);
        }
        catch (JSONException exception)
        {
            Log.e(Constant.TAG, "Couldn't parse equipment " + exception.toString());

            showConnectionIssue();
        }
    }

    @Override
    public void onRetrievalSuccessful(int statusCode, JSONObject response)
    {

    }

    @Override
    public void onRetrievalFailed()
    {
        showConnectionIssue();
    }

    private ServiceListener saveRoutineServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            setResult(Constant.REQUEST_CODE_ROUTINE_UPDATED);
            finish();
        }

        @Override
        public void onRetrievalSuccessful(int statusCode, JSONObject response)
        {

        }

        @Override
        public void onRetrievalFailed()
        {
            showConnectionIssue();
        }
    };

    /**
     * Displays a generic error to the user stating Intencity couldn't connect to the server.
     */
    private void showConnectionIssue()
    {
        CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), context.getString(R.string.intencity_communication_error), false);

        new CustomDialog(RoutineIntencityAddActivity.this, dialogListener, dialog, false);

        progressBar.setVisibility(View.GONE);
    }

    /**
     * Populates the list.
     *
     * @param rows  The ArrayList of muscle groups to add to the list view.
     */
    private void populateListView(ArrayList<SelectableListItem> rows)
    {
        adapter = new SelectableListItemAdapter(context, R.layout.list_item_standard_selectable, rows);

        View header = getLayoutInflater().inflate(R.layout.list_item_header_title_description, null);

        TextView title = (TextView) header.findViewById(R.id.title);
        header.findViewById(R.id.description).setVisibility(View.GONE);

        title.setText(context.getString(R.string.add_routines_description));

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.addHeaderView(header, null, false);
        listView.setOnItemClickListener(routineClickListener);

        progressBar.setVisibility(View.GONE);
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
     * Sends the data to the server to save teh routine.
     */
    private void saveRoutine()
    {
        if (routineMuscleGroups.size() > 0)
        {
            progressBar.setVisibility(View.VISIBLE);

            Collections.sort(routineMuscleGroups, String.CASE_INSENSITIVE_ORDER);

            new ServiceTask(saveRoutineServiceListener).execute(Constant.SERVICE_SET_USER_MUSCLE_GROUP_ROUTINE,
                                                                Constant.generateServiceListVariables(email, routineMuscleGroups, true));
        }
        else
        {
            CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.muscle_group_limit_title), context.getString(R.string.muscle_group_limit_description), false);

            new CustomDialog(RoutineIntencityAddActivity.this, null, dialog, false);
        }
    }

    /**
     * The click listener for when a routine is clicked in the ListView.
     */
    private AdapterView.OnItemClickListener routineClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            SelectableListItem routine = muscleGroups.get(position - 1);

            String title = routine.getTitle();

            // Add or remove muscle groups from the list
            // if he or she clicks on a list item.
            if (routineMuscleGroups.contains(title))
            {
                routine.setChecked(false);
                routineMuscleGroups.remove(title);
            }
            else
            {
                routine.setChecked(true);
                routineMuscleGroups.add(title);
            }

            adapter.notifyDataSetChanged();
        }
    };
}