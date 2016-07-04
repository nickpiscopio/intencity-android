package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import com.intencity.intencity.util.SecurePreferences;
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
    private LinearLayout connectionIssue;

    private ProgressBar progressBar;

    private ListView listView;

    private String email;

    private ArrayList<SelectableListItem> equipmentList;
    private ArrayList<String> userEquipment;

    private Context context;

    private CheckboxAdapter adapter;

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

        connectionIssue = (LinearLayout) findViewById(R.id.layout_connection_issue);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        progressBar.setVisibility(View.VISIBLE);

        context = getApplicationContext();

        email = Util.getSecurePreferencesEmail(context);

        new ServiceTask(getEquipmentServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_EQUIPMENT, email));

        setUserSetEquipment();
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
                userEquipment = new ArrayList<>();

                JSONArray array = new JSONArray(response);

                int length = array.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject object = array.getJSONObject(i);

                    String equipmentName = object.getString(Constant.COLUMN_EQUIPMENT_NAME);
                    boolean hasEquipment = object.getString(Constant.COLUMN_HAS_EQUIPMENT).equalsIgnoreCase(Constant.TRUE);

                    SelectableListItem listItem = new SelectableListItem(equipmentName);
                    listItem.setChecked(hasEquipment);

                    // Add all the equipment to the array.
                    equipmentList.add(listItem);

                    // Add the list item to the user's equipment list if he or she currently has it.
                    if (hasEquipment)
                    {
                        userEquipment.add(equipmentName);
                    }
                }

                populateEquipmentListView();
            }
            catch (JSONException exception)
            {
                Log.e(Constant.TAG, "Couldn't parse equipment " + exception.toString());

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

            progressBar.setVisibility(View.GONE);
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
     */
    private void populateEquipmentListView()
    {
        adapter = new CheckboxAdapter(this, R.layout.list_item_standard_checkbox, equipmentList);

        View header = getLayoutInflater().inflate(R.layout.list_item_header_title_description, null);

        TextView title = (TextView) header.findViewById(R.id.title);
        TextView description = (TextView) header.findViewById(R.id.description);

        title.setText(context.getString(R.string.edit_equipment_title));
        description.setText(context.getString(R.string.edit_equipment_description));

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.addHeaderView(header, null, false);
        listView.setOnItemClickListener(settingClicked);

        progressBar.setVisibility(View.GONE);
    }

    /**
     * Calls the service to update the user's equipment.
     */
    private void updateEquipment()
    {
        progressBar.setVisibility(View.VISIBLE);

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
            SelectableListItem equipment = equipmentList.get(position - 1);

            String title = equipment.getTitle();

            // Add or remove muscle groups from the list
            // if he or she clicks on a list item.
            if (userEquipment.contains(title))
            {
                equipment.setChecked(false);
                userEquipment.remove(title);
            }
            else
            {
                equipment.setChecked(true);
                userEquipment.add(title);
            }

            adapter.notifyDataSetChanged();
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

    /**
     * Saves the user set the equipment.
     */
    private void setUserSetEquipment()
    {
        SecurePreferences sp = new SecurePreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(Constant.USER_SET_EQUIPMENT, true);
        editor.apply();
    }
}