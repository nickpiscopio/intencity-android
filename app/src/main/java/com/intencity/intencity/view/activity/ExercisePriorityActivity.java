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

    private String email;

    private ArrayList<String> exerciseNames;
    private ArrayList<String> exercisePriorities;

    private Context context;

    private ArrayAdapter<String> adapter;

    private ExercisePriorityUtil exercisePriorityUtil;

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
        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        exercisePriorityUtil = new ExercisePriorityUtil();

        new ServiceTask(getExclusionService).execute(Constant.SERVICE_STORED_PROCEDURE,
                Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_EXERCISE_PRIORITIES, email));
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
     * The ServiceListener for getting the user's equipment.
     */
    private ServiceListener getExclusionService =  new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            try
            {
                exerciseNames = new ArrayList<>();
                exercisePriorities = new ArrayList<>();

                if (!response.equals(Constant.RETURN_NULL))
                {
                    JSONArray array = new JSONArray(response);

                    int length = array.length();

                    for (int i = 0; i < length; i++)
                    {
                        JSONObject object = array.getJSONObject(i);

                        String exerciseName = object.getString(Constant.COLUMN_EXERCISE_NAME);
                        String priority = object.getString(Constant.COLUMN_PRIORITY);

                        exerciseNames.add(exerciseName);
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
        }

        @Override
        public void onRetrievalFailed()
        {
            connectionIssue.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    };

    /**
     * The ServiceListener for updating the user's exercise priorities.
     */
    private ServiceListener updateExclusionServiceListener =  new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            finish();
        }

        @Override
        public void onRetrievalFailed()
        {
            CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), context.getString(R.string.intencity_communication_error), false);

            new CustomDialog(ExercisePriorityActivity.this, dialogListener, dialog, false);
        }
    };

    @Override
    public void onBackPressed()
    {
        if (exerciseNames != null && exerciseNames.size() > 0)
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
        new ServiceTask(updateExclusionServiceListener).execute(Constant.SERVICE_UPDATE_EXERCISE_PRIORITY,
                                                                Constant.generateExercisePriorityListVariables(
                                                                        email, exerciseNames, exercisePriorities));
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
        priority = exercisePriorityUtil.getExercisePriority(priority, increment);

        exercisePriorities.set(position, String.valueOf(priority));

        adapter.notifyDataSetChanged();
    }
}