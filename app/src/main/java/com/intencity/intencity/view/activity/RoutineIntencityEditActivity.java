package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.intencity.intencity.adapter.CheckboxAdapter;
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

/**
 * This is the Intencity Routine activity.
 *
 * Created by Nick Piscopio on 5/6/16.
 */
public class RoutineIntencityEditActivity extends AppCompatActivity implements ServiceListener
{
    private Context context;

    private ProgressBar progressBar;

    private ListView listView;

    private FloatingActionButton add;

    private ArrayList<SelectableListItem> routines;
    private ArrayList<String> routinesToRemove;

    private String email;

    private CheckboxAdapter adapter;

    private boolean hasMoreExercises = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_intencity_edit);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        context = getApplicationContext();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        add = (FloatingActionButton) findViewById(R.id.button_add);
        add.setOnClickListener(addClickListener);

        email = Util.getSecurePreferencesEmail(context);

        routines = new ArrayList<>();

        adapter = new CheckboxAdapter(context, R.layout.list_item_standard_checkbox, routines);

        View header = getLayoutInflater().inflate(R.layout.list_item_header_title_description, null);

        TextView title = (TextView) header.findViewById(R.id.title);
        TextView description = (TextView) header.findViewById(R.id.description);

        title.setText(context.getString(R.string.edit_routines_description1));
        description.setText(context.getString(R.string.edit_routines_description2));

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.addHeaderView(header, null, false);
        listView.setEmptyView(findViewById(R.id.empty_list));
        listView.setOnItemClickListener(routineClickListener);

        getRoutines();
    }

    @Override
    public void onBackPressed()
    {
        if (hasMoreExercises)
        {
            setResult(Constant.REQUEST_ROUTINE_UPDATED);
            finish();
        }

        super.onBackPressed();
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

    /**
     * The click listener for adding an Intencity Routine.
     */
    private View.OnClickListener addClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(context, RoutineIntencityAddActivity.class);
            startActivityForResult(intent, Constant.REQUEST_ROUTINE_UPDATED);
        }
    };

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            routines.clear();
            routinesToRemove = new ArrayList<>();

            JSONArray array = new JSONArray(response);

            int length = array.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject object = array.getJSONObject(i);

                String name = object.getString(Constant.COLUMN_DISPLAY_NAME);

                // Add all the equipment to the array.
                routines.add(new SelectableListItem(name));
            }

            adapter.notifyDataSetChanged();

            progressBar.setVisibility(View.GONE);
        }
        catch (JSONException exception)
        {
            Log.e(Constant.TAG, "Couldn't parse custom Intencity routine list. " + exception.toString());

            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRetrievalFailed()
    {
        showConnectionIssue();
    }

    /**
     * The service listener for saving a routine.
     */
    private ServiceListener saveRoutinesServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            setResult(Constant.REQUEST_ROUTINE_UPDATED);
            finish();
        }

        @Override
        public void onRetrievalFailed()
        {
            showConnectionIssue();
        }
    };

    /**
     * Gets the routines from the server.
     */
    private void getRoutines()
    {
        progressBar.setVisibility(View.VISIBLE);

        new ServiceTask(this).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_USER_MUSCLE_GROUP_ROUTINE, email));
    }

    /**
     * Displays a generic error to the user stating Intencity couldn't connect to the server.
     */
    private void showConnectionIssue()
    {
        CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), context.getString(R.string.intencity_communication_error), false);

        new CustomDialog(RoutineIntencityEditActivity.this, dialogListener, dialog, false);

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

            new ServiceTask(saveRoutinesServiceListener).execute(Constant.SERVICE_UPDATE_USER_MUSCLE_GROUP_ROUTINE,
                                                                Constant.generateServiceListVariables(email, routinesToRemove, false));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constant.REQUEST_ROUTINE_UPDATED)
        {
            hasMoreExercises = true;

            getRoutines();
        }
    }
}