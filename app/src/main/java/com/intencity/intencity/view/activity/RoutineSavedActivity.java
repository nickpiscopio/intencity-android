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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.RoutineAdapter;
import com.intencity.intencity.helper.doa.ExerciseDao;
import com.intencity.intencity.helper.doa.UserRoutineDao;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.SelectableListItem;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SelectionType;
import com.intencity.intencity.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is the Intencity Routine activity.
 *
 * Created by Nick Piscopio on 5/6/16.
 */
public class RoutineSavedActivity extends AppCompatActivity implements ServiceListener
{
    private enum DialogType
    {
        COMMUNICATION_ERROR,
        NO_ROUTINES
    }

    private Context context;

    private ProgressBar progressBar;

    private LinearLayout connectionIssueLayout;

    private ListView listView;

    private FloatingActionButton start;

    private String email;

    private RoutineAdapter adapter;

    private ArrayList<SelectableListItem> rows;

    private int routineSelected;

    private boolean hasMoreExercises = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_saved);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        context = getApplicationContext();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);
        connectionIssueLayout = (LinearLayout) findViewById(R.id.layout_connection_issue);
        start = (FloatingActionButton) findViewById(R.id.button_add);
        start.setOnClickListener(startExerciseClickListener);

        Bundle bundle = getIntent().getExtras();

        rows = bundle.getParcelableArrayList(Constant.BUNDLE_ROUTINE_ROWS);

        email = Util.getSecurePreferencesUserId(context);

        View header = getLayoutInflater().inflate(R.layout.list_item_header_title_description, null);

        TextView title = (TextView) header.findViewById(R.id.title);
        TextView description = (TextView) header.findViewById(R.id.description);

        title.setText(context.getString(R.string.title_routine));
        description.setText(context.getString(R.string.user_routine_description));

        listView = (ListView) findViewById(R.id.list_view);
        listView.addHeaderView(header, null, false);
        listView.setOnItemClickListener(routineClickListener);

        populateRoutineList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routine_menu, menu);

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
            case R.id.edit:
                Intent intent = new Intent(context, RoutineSavedEditActivity.class);
                intent.putExtra(Constant.BUNDLE_ROUTINE_ROWS, rows);
                startActivityForResult(intent, Constant.REQUEST_CODE_ROUTINE_UPDATED);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * Gets the routines from the server.
     */
    private void getRoutines()
    {
        showLoading();

        new ServiceTask(this).execute(Constant.SERVICE_EXECUTE_STORED_PROCEDURE, Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_USER_ROUTINE, email));
    }

    /**
     * Populates the routine list.
     */
    private void populateRoutineList()
    {
        adapter = new RoutineAdapter(context, R.layout.list_item_header, R.layout.list_item_standard_radio_button, rows);

        listView.setAdapter(adapter);
    }

    /**
     * Shows the loading view.
     */
    private void showLoading()
    {
        progressBar.setVisibility(View.VISIBLE);
        connectionIssueLayout.setVisibility(View.GONE);
    }

    /**
     * Hides the loading view.
     */
    private void hideLoading()
    {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Shows the connection issue view.
     */
    private void showConnectionIssue()
    {
        hideLoading();

        connectionIssueLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Displays a dialog to the user.
     *
     * @param type  The type of dialog to display.
     */
    private void showDialog(DialogType type)
    {
        int title = R.string.generic_error;
        int message = R.string.intencity_communication_error;

        if (type == DialogType.NO_ROUTINES)
        {
            title = R.string.no_saved_routine_title;
            message = R.string.no_saved_routine_message;
        }

        CustomDialogContent dialog = new CustomDialogContent(context.getString(title), context.getString(message), false);

        new CustomDialog(RoutineSavedActivity.this, dialogListener, dialog, false);

        progressBar.setVisibility(View.GONE);
    }

    /**
     * Sets the flag to select or deselect a row.
     *
     * @param type      The type of selection we want to do.
     * @param position  The position to select or deselect a row.
     */
    private void setSelection(SelectionType type, int position)
    {
        SelectableListItem selectedItem = rows.get(position);

        selectedItem.setSelected(type == SelectionType.SELECT);
    }

    /**
     * The click listener for when a routine is clicked in the ListView.
     */
    private AdapterView.OnItemClickListener routineClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            // We deselect the old row.
            setSelection(SelectionType.DESELECT, routineSelected);

            // We select the new row.
            routineSelected = position - 1;
            setSelection(SelectionType.SELECT, routineSelected);

            adapter.notifyDataSetChanged();

            start.setVisibility(View.VISIBLE);
        }
    };

    /**
     * The start exercising click listener.
     */
    private View.OnClickListener startExerciseClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            showLoading();

            SelectableListItem row = rows.get(routineSelected);

            String routine = String.valueOf(row.getRowNumber());
            String storedProcedureParameters = Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_USER_ROUTINE_EXERCISES, email, routine);

            new ServiceTask(routineServiceListener).execute(Constant.SERVICE_EXECUTE_STORED_PROCEDURE, storedProcedureParameters);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constant.REQUEST_CODE_SAVED_ROUTINE_UPDATED)
        {
            getRoutines();
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
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed()
    {
        if (hasMoreExercises)
        {
            Intent intent = new Intent();
            intent.putExtra(Constant.BUNDLE_ROUTINE_ROWS, rows);

            setResult(Constant.REQUEST_CODE_SAVED_ROUTINE_UPDATED, intent);
        }

        super.onBackPressed();
    }

    /**
     * The service listener for getting the exercise list.
     */
    public ServiceListener routineServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            ExerciseDao dao = new ExerciseDao(context);
            ArrayList<Exercise> exercises = new ArrayList<>();

            try
            {
                // We are adding a warm-up to the exercise list.
                exercises.add(dao.getInjuryPreventionExercise(ExerciseDao.ExerciseType.WARM_UP));
                exercises.addAll(dao.parseJson(response, ""));
                exercises.add(dao.getInjuryPreventionExercise(ExerciseDao.ExerciseType.STRETCH));

                SelectableListItem row = rows.get(routineSelected);

                Intent intent = new Intent();
                intent.putExtra(Constant.BUNDLE_ROUTINE_NAME, row.getTitle());
                intent.putExtra(Constant.BUNDLE_EXERCISE_LIST, exercises);

                setResult(Constant.REQUEST_CODE_START_EXERCISING, intent);
                finish();
            }
            catch (JSONException e)
            {
                Log.e(Constant.TAG, e.getMessage());

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
    };

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            rows.clear();

            if (response.equalsIgnoreCase(Constant.RETURN_NULL))
            {
                showDialog(DialogType.NO_ROUTINES);
            }
            else
            {
                rows.addAll(new UserRoutineDao().parseJson(response));
            }

            adapter.notifyDataSetChanged();

            hasMoreExercises = true;
        }
        catch (JSONException e)
        {
            showDialog(DialogType.COMMUNICATION_ERROR);
        }

        hideLoading();
    }

    @Override
    public void onRetrievalSuccessful(int statusCode, JSONObject response)
    {

    }

    @Override
    public void onRetrievalFailed()
    {
        showDialog(DialogType.COMMUNICATION_ERROR);

        hideLoading();
    }
}