package com.intencity.intencity.view.activity;

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
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
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
 * This is the activity to edit the user's equipment for Intencity.
 *
 * Created by Nick Piscopio on 1/17/15.
 */
public class EquipmentActivity extends AppCompatActivity
{
    private LinearLayout description;
    private LinearLayout connectionIssue;

    private TextView tryAgain;

    private ProgressBar progressBar;

    private ListView listView;

    private View divider;

    private String email;

    private ArrayList<String> equipmentList;
    private ArrayList<String> userEquipment;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        divider = findViewById(R.id.divider);
        description = (LinearLayout) findViewById(R.id.layout_description);

        connectionIssue = (LinearLayout) findViewById(R.id.layout_connection_issue);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        progressBar.setVisibility(View.VISIBLE);

        context = getApplicationContext();

        email = Util.getSecurePreferencesEmail(context);

        new ServiceTask(getEquipmentServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_EQUIPMENT, email));
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
    private ServiceListener getEquipmentServiceListener =  new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            try
            {
                equipmentList = new ArrayList<>();
                ArrayList<Integer> userEquipment = new ArrayList<>();

                JSONArray array = new JSONArray(response);

                int length = array.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject object = array.getJSONObject(i);

                    String equipmentName = object.getString(Constant.COLUMN_EQUIPMENT_NAME);
                    boolean hasEquipment = object.getString(Constant.COLUMN_HAS_EQUIPMENT).equalsIgnoreCase(Constant.TRUE);

                    // Add all the equipment to the array.
                    equipmentList.add(equipmentName);

                    // Add all the equipment to the user's equipment list
                    // if it is returned from the web database. from the database.
                    if (hasEquipment)
                    {
                        userEquipment.add(i);
                    }
                }

                populateEquipmentListView(userEquipment);
            }
            catch (JSONException exception)
            {
                Log.e(Constant.TAG, "Couldn't parse equipment " + exception.toString());

                connectionIssue.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                divider.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
            }
        }

        @Override
        public void onRetrievalFailed()
        {
            connectionIssue.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            divider.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        }
    };

    /**
     * The ServiceListener for updating the user's equipment.
     */
    private ServiceListener updateEquipmentServiceListener =  new ServiceListener()
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

            new CustomDialog(EquipmentActivity.this, dialogListener, dialog, false);
        }
    };

    @Override
    public void onBackPressed()
    {
        if (userEquipment != null)
        {
            updateEquipment();
        }
        else
        {
            // We finish the activity manually if we have equipment to update.
            super.onBackPressed();
        }
    }

    /**
     * Populates the equipment list.
     *
     * @param userEquipment     ArrayList of indices the current equipment the user has.
     *                          This directly correlates to the equipmentList indices.
     */
    private void populateEquipmentListView(ArrayList<Integer> userEquipment)
    {
        this.userEquipment = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, equipmentList);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        for (int equipmentIndex : userEquipment)
        {
            listView.setItemChecked(equipmentIndex, true);

            this.userEquipment.add(equipmentList.get(equipmentIndex));
        }

        listView.setOnItemClickListener(settingClicked);

        progressBar.setVisibility(View.GONE);

        divider.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
    }

    /**
     * Calls the service to update the user's equipment.
     */
    private void updateEquipment()
    {
        new ServiceTask(updateEquipmentServiceListener).execute(Constant.SERVICE_UPDATE_EQUIPMENT,
                                                                Constant.generateServiceListVariables(
                                                                        email, userEquipment, true));
    }

    /**
     * The click listener for each item clicked in the settings list.
     */
    private AdapterView.OnItemClickListener settingClicked = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            // Add or remove equipment from the user's list of equipment
            // if he or she clicks on a list item.
            String equipment = equipmentList.get(position);
            if (userEquipment.contains(equipment))
            {
                userEquipment.remove(equipment);
            }
            else
            {
                userEquipment.add(equipment);
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