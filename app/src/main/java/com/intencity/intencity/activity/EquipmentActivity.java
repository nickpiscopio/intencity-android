package com.intencity.intencity.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.intencity.intencity.R;
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
public class EquipmentActivity extends AppCompatActivity
{
    private ListView listView;

    private String email;

    private ArrayList<String> equipmentList;
    private ArrayList<String> userEquipment;

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

        SecurePreferences securePreferences = new SecurePreferences(getApplicationContext());
        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

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
            }
        }

        @Override
        public void onRetrievalFailed()
        {

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

        }

        @Override
        public void onRetrievalFailed()
        {

        }
    };

    @Override
    public void onBackPressed()
    {
        updateEquipment();

        super.onBackPressed();
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
    }

    /**
     * Calls the service to update the user's equipment.
     */
    private void updateEquipment()
    {
        new ServiceTask(updateEquipmentServiceListener).execute(Constant.SERVICE_UPDATE_EQUIPMENT,
                                                                Constant.generateEquipmentListVariables(
                                                                        email, userEquipment));
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
}