package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is the Exercise Search activity for Intencity.
 *
 * Created by Nick Piscopio on 1/22/15.
 */
public class ExerciseSearchActivity extends AppCompatActivity implements ServiceListener
{
    private LinearLayout connectionIssue;

    private TextView tryAgain;

    private ProgressBar progressBar;

    private ListView listView;

    private View divider;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_search);

        connectionIssue = (LinearLayout) findViewById(R.id.layout_connection_issue);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(clickListener);

        divider = findViewById(R.id.divider);
        divider.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        String type = bundle.getString(Constant.BUNDLE_EXERCISE_TYPE);
        String displayMuscleGroup = bundle.getString(Constant.BUNDLE_ROUTINE_NAME);

        progressBar.setVisibility(View.VISIBLE);

        String routineName = displayMuscleGroup.equalsIgnoreCase(Constant.ROUTINE_CARDIO) ?
                                Constant.ROUTINE_LEGS_AND_LOWER_BACK :
                                displayMuscleGroup;

        new ServiceTask(this).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(
                                              Constant.STORED_PROCEDURE_GET_INJURY_PREVENTION_WORKOUTS,
                                              type, routineName.replace("&", "%26")));

        context = getApplicationContext();

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);

            String title = "";
            if (type != null && type.equalsIgnoreCase(Constant.EXERCISE_TYPE_WARM_UP))
            {
                title = context.getString(R.string.warm_ups_title, displayMuscleGroup);
            }
            else
            {
                title = context.getString(R.string.stretches_title, displayMuscleGroup);
            }

            actionBar.setTitle(title);
        }
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

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            ArrayList<String> exercises = new ArrayList<>();

            JSONArray array = new JSONArray(response);

            int length = array.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject object = array.getJSONObject(i);

                String exerciseName = object.getString(Constant.COLUMN_EXERCISE_NAME);
                exercises.add(exerciseName);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exercises);
            listView.setAdapter(adapter);

            divider.setVisibility(View.VISIBLE);
        }
        catch (JSONException e)
        {
            connectionIssue.setVisibility(View.VISIBLE);

            Log.e(Constant.TAG, "Error parsing muscle group data " + e.toString());
        }

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRetrievalFailed()
    {
        connectionIssue.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * The item click listener for the ListView.
     */
    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            TextView textView = (TextView) view.findViewById(android.R.id.text1);

            Intent intent = new Intent(context, Direction.class);
            intent.putExtra(Constant.BUNDLE_EXERCISE_NAME, textView.getText().toString());
            startActivity(intent);
        }
    };
}