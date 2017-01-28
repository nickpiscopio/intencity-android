package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import com.intencity.intencity.adapter.SelectableListItemAdapter;
import com.intencity.intencity.helper.doa.FitnessLocationDao;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.EquipmentMetaData;
import com.intencity.intencity.model.SelectableListItem;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SelectionType;
import com.intencity.intencity.util.Util;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * This is the activity that shows the fitness location names.
 *
 * Created by Nick Piscopio on 11/20/16.
 */
public class FitnessLocationActivity extends AppCompatActivity implements ServiceListener
{
    private class Location
    {
        private int row;
        private String location = "";

        public int getRow()
        {
            return row;
        }

        public void setRow(int row)
        {
            this.row = row;
        }

        public String getLocation()
        {
            return location;
        }

        public void setLocation(String location)
        {
            this.location = location;
        }
    }

    private enum ActivityState
    {
        STATE_SELECT,
        STATE_EDIT,
        STATE_REMOVE
    }

    private Context context;

    private MenuItem menuItemEdit;
    private MenuItem menuItemRemove;
    private MenuItem menuItemSave;
    private MenuItem menuItemDone;

    private ProgressBar progressBar;

    private View header;
    private TextView description;

    private ListView listView;

    private FloatingActionButton floatingActionButton;

    private LinearLayout layoutAdd;

    private ArrayList<SelectableListItem> locations;
    private ArrayList<String> locationsToRemove;

    private String email;

    private SelectableListItemAdapter adapter;

    private ActivityState activityState;

    private boolean selectingFitnessLocation = false;

    private ArrayList<SelectableListItem> alreadyRetrievedLocations;

    private FitnessLocationDao fitnessLocationDao;

    private Location selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_location_edit);

        ActionBar actionBar = getSupportActionBar();

        context = getApplicationContext();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.button_add);
        floatingActionButton.setOnClickListener(fabClickListener);

        layoutAdd = (LinearLayout) findViewById(R.id.layout_add);

        email = Util.getSecurePreferencesEmail(context);

        locations = new ArrayList<>();

        adapter = new SelectableListItemAdapter(context, R.layout.list_item_standard_selectable, locations);

        header = getLayoutInflater().inflate(R.layout.list_item_header_title_description, null);
        header.findViewById(R.id.title).setVisibility(View.GONE);

        description = (TextView) header.findViewById(R.id.description);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.addHeaderView(header, null, false);
        listView.setEmptyView(findViewById(R.id.empty_list));
        listView.setOnItemClickListener(locationClickListener);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            selectingFitnessLocation = extras.getBoolean(Constant.BUNDLE_FITNESS_LOCATION_SELECT);
            alreadyRetrievedLocations = extras.getParcelable(Constant.BUNDLE_FITNESS_LOCATIONS);
        }

        fitnessLocationDao = new FitnessLocationDao(this, email);

        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);

            if (selectingFitnessLocation)
            {
                actionBar.setTitle(getString(R.string.select_fitness_locations));

                activityState = ActivityState.STATE_SELECT;

                selectedLocation = new Location();
            }
            else
            {
                actionBar.setTitle(getString(R.string.edit_fitness_locations));

                activityState = ActivityState.STATE_EDIT;
            }
        }

        if (alreadyRetrievedLocations != null && alreadyRetrievedLocations.size() > 0)
        {
            progressBar.setVisibility(View.VISIBLE);

            setLocations(alreadyRetrievedLocations);
        }
        else
        {
            getUserFitnessLocations();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (hasValidFitnessLocation())
        {
            setResult();
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fitness_location_menu, menu);

        menuItemEdit = menu.findItem(R.id.edit);
        menuItemRemove = menu.findItem(R.id.remove);
        menuItemSave = menu.findItem(R.id.save);
        menuItemDone = menu.findItem(R.id.done);

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
                activityState = ActivityState.STATE_EDIT;
                applyActivityState();
                return true;
            case R.id.remove:
                activityState = ActivityState.STATE_REMOVE;
                applyActivityState();
                return true;
            case R.id.save:

                if (locationsToRemove != null && locationsToRemove.size() > 0)
                {
                    removeLocations();
                }

                // Fall through here because we do the same as 'DONE'
            case R.id.done:
                activityState = selectingFitnessLocation ? ActivityState.STATE_SELECT : ActivityState.STATE_EDIT;
                applyActivityState();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * Sets the menu item based on the state the view is in.
     *
     */
    private void applyActivityState()
    {
        if(selectingFitnessLocation)
        {
            setSelection(SelectionType.DESELECT, selectedLocation.getRow());
            selectedLocation = new Location();
        }

        if (userHasFitnessLocations())
        {
            switch (activityState)
            {
                case STATE_EDIT:
                    menuItemEdit.setVisible(false);
                    menuItemRemove.setVisible(!selectingFitnessLocation);
                    menuItemSave.setVisible(false);
                    menuItemDone.setVisible(selectingFitnessLocation);

                    header.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);

                    adapter.setListItemType(SelectableListItem.ListItemType.TYPE_IMAGE_VIEW);

                    break;

                case STATE_REMOVE:
                    menuItemEdit.setVisible(false);
                    menuItemRemove.setVisible(false);
                    menuItemSave.setVisible(true);
                    menuItemDone.setVisible(false);

                    description.setText(context.getString(R.string.edit_equipment_description_save));
                    description.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);

                    adapter.setListItemType(SelectableListItem.ListItemType.TYPE_CHECKBOX);

                    break;

                case STATE_SELECT:
                default:
                    menuItemEdit.setVisible(true);
                    menuItemRemove.setVisible(true);
                    menuItemSave.setVisible(false);
                    menuItemDone.setVisible(false);

                    description.setText(context.getString(R.string.edit_equipment_description_select_location));
                    description.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);

                    adapter.setListItemType(SelectableListItem.ListItemType.TYPE_RADIO_BUTTON);

                    break;
            }
        }
        else
        {
            header.setVisibility(View.GONE);

            menuItemEdit.setVisible(false);
            menuItemRemove.setVisible(false);
            menuItemSave.setVisible(false);
            menuItemDone.setVisible(false);
        }

        setFloatingActionButton();
    }

    /**
     * Sets the floating action button images and layouts.
     */
    private void setFloatingActionButton()
    {
        if (hasValidFitnessLocation())
        {
            floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.next_light));
            floatingActionButton.setOnLongClickListener(fabLongClickListener);

            layoutAdd.setVisibility(View.VISIBLE);
        }
        else
        {
            floatingActionButton.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_plus_white));
            floatingActionButton.setOnLongClickListener(null);

            layoutAdd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            SelectableListItem.ListItemType listItemType = selectingFitnessLocation ?
                    SelectableListItem.ListItemType.TYPE_RADIO_BUTTON : SelectableListItem.ListItemType.TYPE_IMAGE_VIEW;

            setLocations(fitnessLocationDao.parseJson(response, null, listItemType));
        }
        catch (JSONException exception)
        {
            Log.e(Constant.TAG, "Couldn't parse user locations. " + exception.toString());

            setLocations(null);
        }

        applyActivityState();
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
            response = response.replaceAll("\"", "");
            if (response.equals(Constant.SUCCESS))
            {
                getUserFitnessLocations();
            }
            else
            {
                showConnectionIssue();
            }
        }

        @Override
        public void onRetrievalFailed()
        {
            showConnectionIssue();
        }
    };

    /**
     * Sets the result, and finishes the activity.
     */
    private void setResult()
    {
        Intent intent = new Intent();
        intent.putExtra(Constant.BUNDLE_FITNESS_LOCATION, selectedLocation.getLocation());

        setResult(Constant.REQUEST_CODE_FITNESS_LOCATION, intent);
        finish();
    }

    /**
     * Opens the EquipmentActivity
     *
     * @param displayName   The display name of the location the user exercises.
     * @param location      The location (address or long/lat of the fitness location the user exercises.
     */
    private void openEquipmentActivity(String displayName, String location)
    {
        Intent intent = new Intent(context, EquipmentActivity.class);
        intent.putExtra(Constant.BUNDLE_EQUIPMENT_META_DATA, new EquipmentMetaData(displayName, location));

        startActivityForResult(intent, Constant.REQUEST_CODE_EQUIPMENT_SAVED);
    }

    /**
     * Gets the user's fitness locations if they have any.
     */
    private void getUserFitnessLocations()
    {
        progressBar.setVisibility(View.VISIBLE);

        fitnessLocationDao.getFitnessLocations();
    }

    /**
     * Sets the fitness locations for the screen.
     *
     * @param locations     The locations that we retrieved from the database.
     */
    private void setLocations(ArrayList<SelectableListItem> locations)
    {
        this.locations.clear();
        locationsToRemove = new ArrayList<>();

        if (locations != null)
        {
            this.locations.addAll(locations);
        }

        adapter.notifyDataSetChanged();

        progressBar.setVisibility(View.GONE);
    }

    /**
     * Displays a generic error to the user stating Intencity couldn't connect to the server.
     */
    private void showConnectionIssue()
    {
        CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), context.getString(R.string.intencity_communication_error), false);

        new CustomDialog(FitnessLocationActivity.this, dialogListener, dialog, false);

        progressBar.setVisibility(View.GONE);
    }

    /**
     * Sends the data to the server to remove locations.
     */
    private void removeLocations()
    {
        if (userHasFitnessLocations())
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
     * Sets the flag to select or deselect a row.
     *
     * @param type      The type of selection we want to do.
     * @param position  The position to select or deselect a row.
     */
    private void setSelection(SelectionType type, int position)
    {
        if (locations.size() > 0)
        {
            SelectableListItem selectedItem = locations.get(position);
            selectedItem.setSelected(type == SelectionType.SELECT);
        }
    }

    /**
     * Checks to see if the user has fitness locations.
     *
     * @return  Boolean value telling whether the user has fitness locations or not.
     */
    private boolean userHasFitnessLocations()
    {
        return locations != null && locations.size() > 0;
    }


    /**
     * Checks to see if the user has selected a fitness location.
     *
     * @return  Boolean value of whether the user has selected a fitness location.
     */
    private boolean hasValidFitnessLocation()
    {
        return selectingFitnessLocation && !selectedLocation.getLocation().equals("");
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
            int listItemPosition = position - 1;
            SelectableListItem location = locations.get(listItemPosition);

            switch (activityState)
            {
                case STATE_EDIT:
                    // We are editing, so open the equipment activity.
                    openEquipmentActivity(location.getTitle(), location.getDescription());

                    break;

                case STATE_REMOVE:
                    // The address is located in the description.
                    // We only allow 1 address per person, so we are removing on the user's email/location (address).
                    String address = location.getDescription();

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

                    break;

                case STATE_SELECT:
                default:

                    // We deselect the old row.
                    setSelection(SelectionType.DESELECT, selectedLocation.getRow());

                    // We select the new row.
                    selectedLocation.setRow(listItemPosition);
                    selectedLocation.setLocation(location.getDescription());
                    setSelection(SelectionType.SELECT, selectedLocation.getRow());

                    adapter.notifyDataSetChanged();

                    setFloatingActionButton();

                    break;
            }
        }
    };

    /**
     * The click listener for adding a fitness location.
     */
    private View.OnClickListener fabClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (hasValidFitnessLocation())
            {
                setResult();
            }
            else
            {
                openEquipmentActivity("", "");
            }
        }
    };

    /**
     * The long click listener for the floating action button for when we have the next button and the add button active.
     */
    private View.OnLongClickListener fabLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view)
        {
            openEquipmentActivity("", "");

            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constant.REQUEST_CODE_EQUIPMENT_SAVED)
        {
            getUserFitnessLocations();
        }
    }
}