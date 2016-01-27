package com.intencity.intencity.activity;

import android.content.Context;
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
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
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
public class ExclusionActivity extends AppCompatActivity
{
    private LinearLayout connectionIssue;

    private TextView tryAgain;

    private ProgressBar progressBar;

    private ListView listView;

    private View divider;

    private String email;

    private ArrayList<String> exclusionList;
    private ArrayList<String> newExclusionList;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_items);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        divider = findViewById(R.id.divider);

        connectionIssue = (LinearLayout) findViewById(R.id.image_view_connection_issue);
        tryAgain = (TextView) findViewById(R.id.btn_try_again);
        tryAgain.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        progressBar.setVisibility(View.VISIBLE);

        context = getApplicationContext();

        SecurePreferences securePreferences = new SecurePreferences(context);
        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        new ServiceTask(getExclusionService).execute(Constant.SERVICE_STORED_PROCEDURE,
                Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_EXCLUSION, email));
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
                exclusionList = new ArrayList<>();

                if (!response.equals(Constant.RETURN_NULL))
                {
                    JSONArray array = new JSONArray(response);

                    int length = array.length();

                    for (int i = 0; i < length; i++)
                    {
                        JSONObject object = array.getJSONObject(i);

                        String exerciseName = object.getString(Constant.COLUMN_EXCLUSION_NAME);

                        // Add all the excluded exercises to the array.
                        exclusionList.add(exerciseName);
                    }
                }

                populateExclusionListView();
            }
            catch (JSONException exception)
            {
                Log.e(Constant.TAG, "Couldn't parse exclusion list " + exception.toString());

                connectionIssue.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                divider.setVisibility(View.GONE);
            }
        }

        @Override
        public void onRetrievalFailed()
        {
            connectionIssue.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            divider.setVisibility(View.GONE);
        }
    };

    /**
     * The ServiceListener for updating the user's equipment.
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

            new CustomDialog(ExclusionActivity.this, dialogListener, dialog);
        }
    };

    @Override
    public void onBackPressed()
    {
        if (newExclusionList != null)
        {
            updateExclusion();
        }
        else
        {
            // We finish the activity manually if we have equipment to update.
            super.onBackPressed();
        }
    }

    /**
     * Populates the equipment list.
     */
    private void populateExclusionListView()
    {
        newExclusionList = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, exclusionList);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.text_view_hidden_exercises));

        for (String exerciseName : exclusionList)
        {
            listView.setItemChecked(exclusionList.indexOf(exerciseName), true);

            newExclusionList.add(exerciseName);
        }

        listView.setOnItemClickListener(settingClicked);

        progressBar.setVisibility(View.GONE);

        divider.setVisibility(View.VISIBLE);
    }

    /**
     * Calls the service to update the user's exclusion list.
     */
    private void updateExclusion()
    {
        new ServiceTask(updateExclusionServiceListener).execute(Constant.SERVICE_UPDATE_EXCLUSION,
                                                                Constant.generateListVariables(
                                                                        email, newExclusionList));
    }

    /**
     * The click listener for each item clicked in the list.
     */
    private AdapterView.OnItemClickListener settingClicked = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            // Add or remove equipment from the user's list of equipment
            // if he or she clicks on a list item.
            String equipment = exclusionList.get(position);
            if (newExclusionList.contains(equipment))
            {
                newExclusionList.remove(equipment);
            }
            else
            {
                newExclusionList.add(equipment);
            }
        }
    };

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
}