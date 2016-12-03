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
import com.intencity.intencity.model.EquipmentMetaData;
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
 * This is the activity that shows the equipment location names.
 *
 * Created by Nick Piscopio on 11/20/16.
 */
public class EquipmentEditActivity extends AppCompatActivity implements ServiceListener
{
    private Context context;

    private MenuItem menuItemRemove;
    private MenuItem menuItemSave;

    private ProgressBar progressBar;

    private TextView description;

    private ListView listView;

    private FloatingActionButton add;

    private ArrayList<SelectableListItem> locations;
    private ArrayList<String> locationsToRemove;

    private String email;

    private CheckboxAdapter adapter;

    private boolean hasMoreFitnessLocations = false;
    private boolean inRemovingState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_edit);

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

        locations = new ArrayList<>();

        adapter = new CheckboxAdapter(context, R.layout.list_item_standard_checkbox, locations);

        View header = getLayoutInflater().inflate(R.layout.list_item_header_title_description, null);

        TextView title = (TextView) header.findViewById(R.id.title);
        description = (TextView) header.findViewById(R.id.description);

        title.setText(context.getString(R.string.edit_equipment_description1));
        description.setText(context.getString(R.string.edit_equipment_description2));

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.addHeaderView(header, null, false);
        listView.setEmptyView(findViewById(R.id.empty_list));
        listView.setOnItemClickListener(locationClickListener);

        getUserFitnessLocations();
    }

    @Override
    public void onBackPressed()
    {
        if (hasMoreFitnessLocations)
        {
            setResult(Constant.REQUEST_CODE_ROUTINE_UPDATED);
            finish();
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.remove_menu, menu);

        menuItemRemove = menu.findItem(R.id.remove);
        menuItemSave = menu.findItem(R.id.save);

        setMenuItems(false);

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
            case R.id.remove:
                setMenuItems(!this.inRemovingState);
                return true;
            case R.id.save:
                setMenuItems(!this.inRemovingState);
                removeLocations();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * Sets the menu item based on the state the view is in.
     *
     * @param inRemovingState   Boolean value for whether we are in the removing state or not.
     */
    private void setMenuItems(boolean inRemovingState)
    {
        this.inRemovingState = inRemovingState;

        menuItemRemove.setVisible(!this.inRemovingState);
        menuItemSave.setVisible(this.inRemovingState);

        description.setVisibility(this.inRemovingState ? View.VISIBLE : View.GONE);

        // Tell the adapter that we are enabling or disabling the deletion flag.
        adapter.setDeletionEnabled(this.inRemovingState);
    }

    /**
     * The click listener for adding a fitness location.
     */
    private View.OnClickListener addClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            openEquipmentActivity("", "");
        }
    };

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            locations.clear();
            locationsToRemove = new ArrayList<>();

            JSONArray array = new JSONArray(response);

            int length = array.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject object = array.getJSONObject(i);

                String name = object.getString(Constant.COLUMN_DISPLAY_NAME);
                String location = object.getString(Constant.COLUMN_LOCATION);

                SelectableListItem listItem = new SelectableListItem(name, location);
                listItem.setDeletionEnabled(false);

                // Add all the locations to the array.
                locations.add(listItem);
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
    private ServiceListener saveLocationsServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
//            setResult(Constant.REQUEST_CODE_ROUTINE_UPDATED);
            finish();
        }

        @Override
        public void onRetrievalFailed()
        {
            showConnectionIssue();
        }
    };

    /**
     * Opens the EquipmentActivity
     *
     * @param displayName   The display name of the location the user exercises.
     * @param location      The location (address or long/lat of the fitness location the user exercises.
     */
    private void openEquipmentActivity(String displayName, String location)
    {
        Intent intent = new Intent(context, EquipmentActivity.class);

        if (!displayName.equals("") || !location.equals(""))
        {
            intent.putExtra(Constant.BUNDLE_EQUIPMENT_META_DATA, new EquipmentMetaData(displayName, location));
        }

        startActivityForResult(intent, Constant.REQUEST_CODE_EQUIPMENT_SAVED);
    }

    /**
     * Gets the user's fitness locations if they have any.
     */
    private void getUserFitnessLocations()
    {
        progressBar.setVisibility(View.VISIBLE);

        new ServiceTask(this).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_USER_FITNESS_LOCATIONS, email));
    }

    /**
     * Displays a generic error to the user stating Intencity couldn't connect to the server.
     */
    private void showConnectionIssue()
    {
        CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), context.getString(R.string.intencity_communication_error), false);

        new CustomDialog(EquipmentEditActivity.this, dialogListener, dialog, false);

        progressBar.setVisibility(View.GONE);
    }

    /**
     * Sends the data to the server to remove locations.
     */
    private void removeLocations()
    {
        if (locationsToRemove.size() > 0)
        {
            progressBar.setVisibility(View.VISIBLE);

            new ServiceTask(saveLocationsServiceListener).execute(Constant.SERVICE_UPDATE_USER_FITNESS_LOCATION,
                                                                  Constant.generateServiceListVariables(email,
                                                                                                      locationsToRemove, false));
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
    private AdapterView.OnItemClickListener locationClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            SelectableListItem location = locations.get(position - 1);

            // The address is located in teh description.
            // We only allow 1 address per person, so we are removing on the user's email/location (address).
            String address = location.getDescription();

            if (inRemovingState)
            {
                // Add or remove muscle groups from the list
                // if he or she clicks on a list item.
                if (locationsToRemove.contains(address))
                {
                    location.setChecked(true);
                    locationsToRemove.remove(address);
                }
                else
                {
                    location.setChecked(false);
                    locationsToRemove.add(address);
                }

                adapter.notifyDataSetChanged();
            }
            else
            {
                // We are editing, so open the equipment activity.
                openEquipmentActivity(address, location.getDescription());
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constant.REQUEST_CODE_EQUIPMENT_SAVED)
        {
            hasMoreFitnessLocations = true;

            getUserFitnessLocations();
        }
    }
}