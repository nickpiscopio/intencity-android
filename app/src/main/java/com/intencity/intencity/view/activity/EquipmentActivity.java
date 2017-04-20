package com.intencity.intencity.view.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.intencity.intencity.R;
import com.intencity.intencity.adapter.SelectableListItemAdapter;
import com.intencity.intencity.helper.GoogleGeocode;
import com.intencity.intencity.listener.DialogFitnessLocationListener;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.GeocodeListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.EquipmentMetaData;
import com.intencity.intencity.model.SelectableListItem;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.notification.FitnessLocationDialog;
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
 * Created by Nick Piscopio on 1/17/16.
 */
public class EquipmentActivity extends AppCompatActivity implements GeocodeListener
{
    private final int REQUEST_CODE_FITNESS_DIALOG = 10;
    private final int REQUEST_CODE_ADDRESS = 20;
    private final int REQUEST_CODE_LOCATION_VALIDITY = 30;

    private ProgressBar progressBar;

    private CardView cardFitnessLocation;

    private TextView textViewDisplayName;
    private TextView textViewLocation;

    private ListView listView;

    private MenuItem menuCheckBox;

    private String email;
    private String defaultLocationString;

    // This is the location that was already saved in the DB.
    // It will be unique to each user.
    private String savedDisplayName = "";
    private String savedLocation = "";

    private ArrayList<SelectableListItem> equipmentList;
    private ArrayList<String> userEquipment;

    private Context context;

    private SelectableListItemAdapter adapter;

    private GoogleGeocode googleGeocode;

    private boolean selectAll = false;

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

        progressBar = (ProgressBar)findViewById(R.id.progress_bar_loading);

        progressBar.setVisibility(View.VISIBLE);

        cardFitnessLocation = (CardView)findViewById(R.id.card_fitness_location);

        textViewDisplayName = (TextView)findViewById(R.id.text_view_display_name);
        textViewLocation = (TextView)findViewById(R.id.text_view_location);

        context = getApplicationContext();

        email = Util.getSecurePreferencesUserId(context);
        defaultLocationString = getString(R.string.fitness_location_default);

        cardFitnessLocation.setOnClickListener(fitnessLocationClickListener);

        googleGeocode = new GoogleGeocode(EquipmentActivity.this, this);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            EquipmentMetaData metaData = extras.getParcelable(Constant.BUNDLE_EQUIPMENT_META_DATA);
            if (metaData != null)
            {
                savedDisplayName = metaData.getDisplayName();
                setDisplayName(savedDisplayName);

                savedLocation = metaData.getLocation();
                setLocation(savedLocation);
            }
        }

        new ServiceTask(getEquipmentServiceListener).execute(Constant.SERVICE_EXECUTE_STORED_PROCEDURE,
                                                             Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_USER_EQUIPMENT, email, savedLocation));
    }

    @Override
    protected void onDestroy()
    {
        googleGeocode.onDestroy();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checkbox_menu, menu);

        menuCheckBox = menu.findItem(R.id.checkbox);

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
            case R.id.checkbox:
                setCheckboxTick(selectAll = !selectAll);

                updateEquipmentList();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        googleGeocode.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onBackPressed()
    {
        String textViewDisplayNameString = textViewDisplayName.getText().toString();
        String textViewLocationString = textViewLocation.getText().toString();

        if (userEquipment != null && userEquipment.size() == 0)
        {
            CustomDialogContent dialog = new CustomDialogContent(getString(R.string.edit_equipment_error_title), getString(R.string.edit_equipment_error_description), true);
            dialog.setNegativeButtonStringRes(R.string.invalid_fitness_location_negative_button);

            new CustomDialog(EquipmentActivity.this, invalidFitnessEquipmentDialogListener, dialog, true);
        }
        else if ((userEquipment != null && userEquipment.size() > 0) ||
            (!textViewLocationString.equals(savedLocation) && !textViewLocationString.equals(defaultLocationString)) ||
             !textViewDisplayNameString.equals(savedDisplayName))
        {
            progressBar.setVisibility(View.VISIBLE);

            // We check if the location is valid here.
            // The GeocodeListener will return what gets executed next.
            googleGeocode.checkLocationValidity(REQUEST_CODE_LOCATION_VALIDITY, textViewLocationString);
        }
        else
        {
            // We finish the activity because there isn't anything to update.
            super.onBackPressed();
        }
    }

    /**
     * The click listener for each item clicked in the equipment list.
     */
    private AdapterView.OnItemClickListener equipmentClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            SelectableListItem equipment = equipmentList.get(position - 1);

            updateEquipmentListItem(equipment);

            // We set the checkbox to checked if the user has all the equipment in the equipment list.
            setCheckboxTick(userEquipment.size() == equipmentList.size());

            adapter.notifyDataSetChanged();
        }
    };

    /**
     * The service listener for checking if a user already has fitness equipment in a specifed location.
     */
    private ServiceListener getLocationServiceListener = new ServiceListener()
    {
        @Override public void onRetrievalSuccessful(String response)
        {
            if (response.equals(Constant.RETURN_NULL))
            {
                // We failed to retrieve equipment from a specified location.
                // This means the location wasn't used before.
                // This is what we want, so we are updating the equipment.
                updateEquipment();
            }
            else
            {
                // We already have a fitness location saved.
                // Notify the user.
                CustomDialogContent dialog = new CustomDialogContent(getString(R.string.duplicate_location_title), getString(R.string.duplicate_location_description), true);
                dialog.setPositiveButtonStringRes(R.string.overwrite);
                dialog.setNegativeButtonStringRes(R.string.invalid_fitness_location_negative_button);

                new CustomDialog(EquipmentActivity.this, overwriteFitnessLocationDialogListener, dialog, true);
            }
        }

        @Override
        public void onServiceResponse(int statusCode, String response)
        {

        }

        @Override
        public void onRetrievalFailed(int statusCode)
        {
            displayCommunicationError();
        }
    };

    /**
     * The ServiceListener for getting the user's equipment.
     */
    private ServiceListener getEquipmentServiceListener = new ServiceListener()
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

                // We set the checkbox to checked if the user has all the equipment in the equipment list.
                setCheckboxTick(userEquipment.size() == equipmentList.size());

                populateEquipmentListView();
            }
            catch (JSONException exception)
            {
                Log.e(Constant.TAG, "Couldn't parse equipment " + exception.toString());

                progressBar.setVisibility(View.GONE);

                displayCommunicationError();
            }
        }

        @Override
        public void onServiceResponse(int statusCode, String response)
        {

        }

        @Override
        public void onRetrievalFailed(int statusCode)
        {
            progressBar.setVisibility(View.GONE);

            displayCommunicationError();
        }
    };

    /**
     * The dialog listener for the equipment activity.
     */
    private DialogFitnessLocationListener dialogListener = new DialogFitnessLocationListener()
    {
        @Override
        public void onSaveFitnessLocation(String displayName, String location)
        {
            setDisplayName(displayName);

            setLocation(location);
        }
    };

    /**
     * The dialog listener for the invalid fitness dialog.
     */
    private DialogListener overwriteFitnessLocationDialogListener = new DialogListener()
    {
        @Override
        public void onButtonPressed(int which)
        {
            switch (which)
            {
                // Overwrite fitness location.
                case Constant.POSITIVE_BUTTON:
                    updateEquipment();
                    break;

                // Just go back because we don't want to save anything.
                case Constant.NEGATIVE_BUTTON:
                    EquipmentActivity.super.onBackPressed();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * The dialog listener for the invalid fitness dialog.
     */
    private DialogListener invalidFitnessLocationDialogListener = new DialogListener()
    {
        @Override
        public void onButtonPressed(int which)
        {
            switch (which)
            {
                // Set the fitness location.
                case Constant.POSITIVE_BUTTON:
                    googleGeocode.checkLocationPermission(REQUEST_CODE_FITNESS_DIALOG);
                    break;

                // Just go back because we don't want to save anything.
                case Constant.NEGATIVE_BUTTON:
                    EquipmentActivity.super.onBackPressed();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * The dialog listener for the invalid fitness equipment.
     */
    private DialogListener invalidFitnessEquipmentDialogListener = new DialogListener()
    {
        @Override
        public void onButtonPressed(int which)
        {
            switch (which)
            {
                // Just go back because we don't want to save anything.
                case Constant.NEGATIVE_BUTTON:
                    EquipmentActivity.super.onBackPressed();
                    break;

                // Allow the user to set equipment.
                case Constant.POSITIVE_BUTTON:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * The dialog listener for a connection issue.
     */
    private DialogListener dialogConnectionIssueListener = new DialogListener()
    {
        @Override
        public void onButtonPressed(int which)
        {
            // This only gets called when there is a connection issue.
            // There is only 1 button that can be pressed, so we aren't going to switch on it.
            finish();
        }
    };

    /**
     * The click listener for editing the fitness location.
     */
    private View.OnClickListener fitnessLocationClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            googleGeocode.checkLocationPermission(REQUEST_CODE_FITNESS_DIALOG);
        }
    };

    /**
     * The ServiceListener for updating the user's equipment.
     */
    private ServiceListener updateEquipmentServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {

        }

        @Override
        public void onServiceResponse(int statusCode, String response)
        {
            setResult(Constant.REQUEST_CODE_EQUIPMENT_SAVED);
            finish();
        }

        @Override
        public void onRetrievalFailed(int statusCode)
        {
            displayCommunicationError();
        }
    };

    /**
     * Sets the text of the display name.
     *
     * @param displayName   The text to set the display name.
     */
    private void setDisplayName(String displayName)
    {
        textViewDisplayName.setVisibility(displayName.length() > 0 ? View.VISIBLE : View.GONE);
        textViewDisplayName.setText(displayName);
    }

    /**
     * Sets the text of the fitness location.
     *
     * @param location   The text to set the location.
     */
    private void setLocation(String location)
    {
        if (!location.equals(""))
        {
            textViewLocation.setText(location);
            textViewLocation.setTextColor(ContextCompat.getColor(context, R.color.secondary_light));
            textViewLocation.setTypeface(Typeface.DEFAULT);
        }
        else
        {
            // Get location of user since there wasn't one already.
            googleGeocode.checkLocationPermission(REQUEST_CODE_ADDRESS);
            textViewLocation.setTextColor(ContextCompat.getColor(context, R.color.card_button_delete_deselect));
            textViewLocation.setTypeface(textViewLocation.getTypeface(), Typeface.ITALIC);
        }
    }

    /**
     * Selects or deselects all of the equipment based on the checkbox that is checked in the menu.
     *
     * @param selectAll Boolean value of whether or not we should select all of the equipment.
     */
    private void setCheckboxTick(boolean selectAll)
    {
        this.selectAll = selectAll;

        if (menuCheckBox != null)
        {
            menuCheckBox.setIcon(this.selectAll ? R.mipmap.ic_checkbox_marked_white : R.mipmap.ic_checkbox_blank_outline_white);
        }
    }

    /**
     * Searches the equipment list and checks or unchecks items based on whether we are selecting or deselecting the menu checkbox.
     */
    private void updateEquipmentList()
    {
        for (SelectableListItem listItem : equipmentList)
        {
            if (selectAll && !listItem.isChecked())
            {
                updateEquipmentListItem(listItem);
            }
            else if (!selectAll && listItem.isChecked())
            {
                updateEquipmentListItem(listItem);
            }
        }

        // We set the checkbox to checked if the user has all the equipment in the equipment list.
        setCheckboxTick(userEquipment.size() == equipmentList.size());

        adapter.notifyDataSetChanged();
    }

    /**
     * Populates the equipment list.
     */
    private void populateEquipmentListView()
    {
        adapter = new SelectableListItemAdapter(this, R.layout.list_item_standard_selectable, equipmentList);

        View header = getLayoutInflater().inflate(R.layout.list_item_header_title_description, null);

        TextView title = (TextView)header.findViewById(R.id.title);
        TextView description = (TextView)header.findViewById(R.id.description);

        title.setText(context.getString(R.string.edit_equipment_title));
        description.setText(context.getString(R.string.edit_equipment_description));

        listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.addHeaderView(header, null, false);
        listView.setOnItemClickListener(equipmentClickListener);

        progressBar.setVisibility(View.GONE);
    }

    /**
     * Calls the service to update the user's equipment.
     */
    private void updateEquipment()
    {
        String params = Constant.generateEquipmentListVariables(email, textViewDisplayName.getText().toString(), savedLocation, textViewLocation.getText().toString(), userEquipment);
        new ServiceTask(updateEquipmentServiceListener).execute(Constant.SERVICE_UPDATE_EQUIPMENT, params);
    }

    /**
     * Updates a specified equipment list item.
     *
     * This adds or removes it from the list of equipment that the user currently has.
     *
     * @param equipment The item to add or remove from the user's list of equipment.
     */
    private void updateEquipmentListItem(SelectableListItem equipment)
    {
        String equipmentName = equipment.getTitle();

        // Add or remove equipment from the list
        // if he or she selects on a list item.
        if (userEquipment.contains(equipmentName))
        {
            equipment.setChecked(false);
            userEquipment.remove(equipmentName);
        }
        else
        {
            equipment.setChecked(true);
            userEquipment.add(equipmentName);
        }
    }

    /**
     * Displays a communication error to the user.
     */
    private void displayCommunicationError()
    {
        CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), context.getString(R.string.intencity_communication_error), false);

        new CustomDialog(EquipmentActivity.this, dialogConnectionIssueListener, dialog, false);

        progressBar.setVisibility(View.GONE);
    }

    /**
     * This is the callback from the GeocodeListener.
     * It is called when we receive the callback for GoogleApiClient being connected.
     */
    @Override
    public void onGoogleApiClientConnected(int requestCode, GoogleApiClient googleApiClient, Location location)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_FITNESS_DIALOG:
                new FitnessLocationDialog(EquipmentActivity.this, textViewDisplayName.getText().toString(), textViewLocation.getText().toString(), googleApiClient, location, dialogListener);
                break;
            case REQUEST_CODE_ADDRESS:
                googleGeocode.getLastLocationAddress(REQUEST_CODE_ADDRESS, location);
                break;
            default:
                break;
        }
    }

    /**
     * This is the callback from the GeocodeListener.
     * It is called when we receive the callback that the location that was looked up is valid.
     */
    @Override
    public void onGeocodeRetrievalSuccessful(int requestCode, Object obj)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_ADDRESS:
                textViewLocation.setText((String)obj);
                break;

            // We got a response back with a status of "OK".
            // This tells us that the location we typed is valid according to Google.
            case REQUEST_CODE_LOCATION_VALIDITY:

                String location = (String)obj;

                textViewLocation.setText(location);

                // If the location and saved location equal, that means we are editing a fitness equipment.
                if (location.equals(savedLocation))
                {
                    updateEquipment();
                }
                else
                {
                    // If the location and saved location don't equal, then we should check if the location already exists
                    // because the user might not know he or she already has equipment at the designated location
                    new ServiceTask(getLocationServiceListener).execute(Constant.SERVICE_EXECUTE_STORED_PROCEDURE,
                                                                        Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_CHECK_IF_FITNESS_LOCATION_EXISTS, email, location));
                }

                break;

            default:
                break;
        }
    }

    /**
     * This is the callback from the GeocodeListener.
     * It is called when we receive the callback that the location that was looked up is invalid.
     */
    @Override
    public void onGeocodeRetrievalFailed(int requestCode)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_LOCATION_VALIDITY:
                String title = getString(R.string.invalid_fitness_location_title);
                String message = getString(R.string.invalid_fitness_location_description);
                CustomDialogContent dialog = new CustomDialogContent(title, message, true);
                dialog.setPositiveButtonStringRes(R.string.invalid_fitness_location_positive_button);
                dialog.setNegativeButtonStringRes(R.string.invalid_fitness_location_negative_button);

                new CustomDialog(EquipmentActivity.this, invalidFitnessLocationDialogListener, dialog, true);

                progressBar.setVisibility(View.GONE);
                break;

            case REQUEST_CODE_ADDRESS:
                break;

            default:
                displayCommunicationError();
                break;
        }
    }

    @Override
    public void onLocationServiceEnabled() { }

    @Override
    public void onLocationServiceNotEnabled(int requestCode)
    {
        switch (requestCode)
        {
            case GoogleGeocode.REQUEST_CODE_CANCELED:
                EquipmentActivity.super.onBackPressed();
            case GoogleGeocode.LOCATION_NOT_AVAILABLE:
            case GoogleGeocode.REQUEST_CODE_PERMISSION_NEEDED:
            default:
                progressBar.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * This is the callback from the GeocodeListener.
     * It is called when we receive the callback that the user needs to grant permission.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults, FragmentActivity activity)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}