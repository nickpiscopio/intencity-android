package com.intencity.intencity.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.PriorityListAdapter;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ExercisePriorityListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.ExercisePriorityUtil;
import com.intencity.intencity.util.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is the activity to edit the user's equipment for Intencity.
 *
 * Created by Nick Piscopio on 1/17/15.
 */
public class ExercisePriorityActivity extends AppCompatActivity implements ExercisePriorityListener
{
    private LinearLayout connectionIssue;

    private ProgressBar progressBar;

    private ListView listView;

    private int userId;

    private ArrayList<Integer> exerciseIds;
    private ArrayList<String> exerciseNames;
    private ArrayList<String> exercisePriorities;

    private Context context;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_priority);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        connectionIssue = (LinearLayout) findViewById(R.id.layout_connection_issue);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        progressBar.setVisibility(View.VISIBLE);

        context = getApplicationContext();

        SecurePreferences securePreferences = new SecurePreferences(context);
        userId = securePreferences.getInt(Constant.USER_ACCOUNT_ID, 0);

        new ServiceTask(getExclusionServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                             Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_EXERCISE_PRIORITIES, userId));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * The ServiceListener for getting the user's exercise priorities.
     */
    private ServiceListener getExclusionServiceListener =  new ServiceListener()
    {
        @Override
        public void onServiceResponse(int statusCode, String response)
        {
            switch (statusCode)
            {
                case Constant.STATUS_CODE_SUCCESS_STORED_PROCEDURE:

                    try
                    {
                        exerciseIds = new ArrayList<>();
                        exerciseNames = new ArrayList<>();
                        exercisePriorities = new ArrayList<>();

                        if (!response.equals(Constant.RETURN_NULL))
                        {
                            JSONArray array = new JSONArray(response);

                            int length = array.length();

                            for (int i = 0; i < length; i++)
                            {
                                JSONObject object = array.getJSONObject(i);

                                int exerciseId = object.getInt(Constant.COLUMN_ID);
                                String name = object.getString(Constant.COLUMN_EXERCISE_NAME);
                                String priority = object.getString(Constant.COLUMN_PRIORITY);

                                exerciseIds.add(exerciseId);
                                exerciseNames.add(name);
                                exercisePriorities.add(priority);
                            }
                        }

                        populatePriorityListView();
                    }
                    catch (JSONException exception)
                    {
                        Log.e(Constant.TAG, "Couldn't parse priority list " + exception.toString());

                        connectionIssue.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                    break;

                case Constant.STATUS_CODE_FAILURE_STORED_PROCEDURE:
                default:

                    connectionIssue.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    break;
            }
        }
    };

    /**
     * The ServiceListener for updating the user's exercise priorities.
     */
    private ServiceListener updateExclusionServiceListener =  new ServiceListener()
    {
        @Override
        public void onServiceResponse(int statusCode, String response)
        {
            switch (statusCode)
            {
                case Constant.STATUS_CODE_SUCCESS_EXERCISE_PRIORITY_UPDATED:

                    finish();

                    break;

                case Constant.STATUS_CODE_FAILURE_EXERCISE_PRIORITY_UPDATE:
                default:

                    CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), context.getString(R.string.intencity_communication_error), false);

                    new CustomDialog(ExercisePriorityActivity.this, dialogListener, dialog, false);

                    progressBar.setVisibility(View.GONE);

                    break;
            }
        }
    };

    @Override
    public void onBackPressed()
    {
        if (exerciseIds != null && exerciseIds.size() > 0)
        {
            updateExercisePriorities();
        }
        else
        {
            super.onBackPressed();
        }
    }

    /**
     * Populates the exercise priority list.
     */
    private void populatePriorityListView()
    {
        adapter = new PriorityListAdapter(context, this, R.layout.list_item_exercise_priority, exerciseNames, exercisePriorities);

        View header = getLayoutInflater().inflate(R.layout.list_item_header_title_description, null);

        TextView title = (TextView) header.findViewById(R.id.title);
        TextView description = (TextView) header.findViewById(R.id.description);

        title.setText(context.getString(R.string.edit_priority_title));
        description.setText(context.getString(R.string.edit_priority_description));

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.addHeaderView(header, null, false);
        listView.setEmptyView(findViewById(R.id.text_view_no_priorities));

        progressBar.setVisibility(View.GONE);
    }

    /**
     * Calls the service to update the user's exercise priorities.
     */
    private void updateExercisePriorities()
    {
        progressBar.setVisibility(View.VISIBLE);

        new ServiceTask(updateExclusionServiceListener).execute(Constant.SERVICE_UPDATE_EXERCISE_PRIORITY,
                                                                Constant.generateExercisePriorityListVariables(
                                                                        userId, exerciseIds, exercisePriorities));
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

    @Override
    public void onSetExercisePriority(int position, int priority, boolean increment)
    {
        priority = ExercisePriorityUtil.getExercisePriority(priority, increment);

        exercisePriorities.set(position, String.valueOf(priority));

        adapter.notifyDataSetChanged();
    }
}