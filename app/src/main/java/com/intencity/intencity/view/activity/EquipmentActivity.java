package com.intencity.intencity.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.intencity.intencity.R;
import com.intencity.intencity.adapter.CheckboxAdapter;
import com.intencity.intencity.listener.DialogFitnessLocationListener;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.EquipmentMetaData;
import com.intencity.intencity.model.SelectableListItem;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.notification.FitnessLocationDialog;
import com.intencity.intencity.notification.ToastDialog;
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
 * <p>
 * Created by Nick Piscopio on 1/17/15.
 */
public class EquipmentActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final int GOOGLE_API_CLIENT_ID = 0;

    private LinearLayout connectionIssue;

    private ProgressBar progressBar;

    private CardView cardFitnessLocation;

    private TextView textViewDisplayName;
    private TextView textViewLocation;

    private ListView listView;

    private MenuItem menuCheckBox;

    private String email;

    // This is the location that was already saved in teh DB.
    // It will be unique to each user.
    private String savedLocation = "";

    private ArrayList<SelectableListItem> equipmentList;
    private ArrayList<String> userEquipment;

    private Context context;

    private CheckboxAdapter adapter;

    private GoogleApiClient mGoogleApiClient;

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

        connectionIssue = (LinearLayout)findViewById(R.id.layout_connection_issue);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_loading);

        progressBar.setVisibility(View.VISIBLE);

        cardFitnessLocation = (CardView)findViewById(R.id.card_fitness_location);

        textViewDisplayName = (TextView)findViewById(R.id.text_view_display_name);
        textViewLocation = (TextView)findViewById(R.id.text_view_location);

        context = getApplicationContext();

        email = Util.getSecurePreferencesEmail(context);

        cardFitnessLocation.setOnClickListener(fitnessLocationClickListener);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            EquipmentMetaData metaData = extras.getParcelable(Constant.BUNDLE_EQUIPMENT_META_DATA);
            if (metaData != null)
            {
                setDisplayName(metaData.getDisplayName());

                savedLocation = metaData.getLocation();
                textViewLocation.setText(!savedLocation.equals("") ? savedLocation : getString(R.string.fitness_location_default));
            }
        }

        new ServiceTask(getEquipmentServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE, Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_EQUIPMENT, email));

        setUserSetEquipment();
    }

    @Override
    protected void onDestroy()
    {
        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.disconnect();
        }

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
        switch (requestCode)
        {
            case Constant.REQUEST_CODE_PERMISSION:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    requestGoogleApiClient();
                }
                else
                {
                    CustomDialogContent content = new CustomDialogContent(getString(R.string.edit_equipment_location_permission_needed));
                    content.setPositiveButtonStringRes(R.string.button_preferences);

                    new ToastDialog(EquipmentActivity.this, content, dialogListener);

                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
            default:
                break;
        }
    }

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
     * The dialog listener for the equipment activity.
     */
    private DialogFitnessLocationListener dialogListener = new DialogFitnessLocationListener()
    {
        @Override
        public void onSaveFitnessLocation(String displayName, String location)
        {
            setDisplayName(displayName);

            textViewLocation.setText(location);
        }

        @Override
        public void onButtonPressed(int which)
        {
            switch (which)
            {
                case Constant.POSITIVE_BUTTON:
                    startInstalledAppDetailsActivity();
                    break;
                default:
                    break;
            }
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
            // This is for Android M+
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                // Permission was already granted, so show the fitness location dialog.
                requestGoogleApiClient();
            }
            else
            {
                requestLocationPermission();
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
     * The ServiceListener for updating the user's equipment.
     */
    private ServiceListener updateEquipmentServiceListener = new ServiceListener()
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

            new CustomDialog(EquipmentActivity.this, dialogConnectionIssueListener, dialog, false);

            progressBar.setVisibility(View.GONE);
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
     * Selects or deselects all of the equipment based on the checkbox that is checked in the menu.
     *
     * @param selectAll Boolean value of whether or not we should select all of the equipment.
     */
    private void setCheckboxTick(boolean selectAll)
    {
        this.selectAll = selectAll;

        menuCheckBox.setIcon(this.selectAll ? R.mipmap.ic_checkbox_marked_white : R.mipmap.ic_checkbox_blank_outline_white);
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
        adapter = new CheckboxAdapter(this, R.layout.list_item_standard_checkbox, equipmentList);

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
        progressBar.setVisibility(View.VISIBLE);

        String params = Constant.generateEquipmentListVariables(email, textViewDisplayName.getText().toString(), savedLocation, textViewLocation.getText().toString(), userEquipment);
        new ServiceTask(updateEquipmentServiceListener).execute(Constant.SERVICE_UPDATE_EQUIPMENT, params);
    }

    /**
     * Displays the fitness location dialog to the user.
     */
    private void requestGoogleApiClient()
    {
        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(Places.GEO_DATA_API).addApi(LocationServices.API).enableAutoManage(this, GOOGLE_API_CLIENT_ID, this).addConnectionCallbacks(this).build();
        }

        if (mGoogleApiClient.isConnected())
        {
            // We're already connected to google's service, so just open the dialog.
            openFitnessLocationDialog();
        }
        else
        {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Requests the location permission.
     */
    private void requestLocationPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(EquipmentActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION))
        {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(EquipmentActivity.this)
                    .setMessage(context.getResources().getString(R.string.edit_equipment_location_permission_needed))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    ActivityCompat.requestPermissions(EquipmentActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, Constant.REQUEST_CODE_PERMISSION);
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                }
            }).show();
        }
        else
        {
            // Permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(EquipmentActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, Constant.REQUEST_CODE_PERMISSION);
        }
    }

    /**
     * Updates a specified equipment list item.
     * <p>
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
     * Opens the fitness location dialog.
     */
    // We suppress this warning because at this point in the code, we already have the permission to use the user's location.
    @SuppressWarnings({ "MissingPermission" })
    private void openFitnessLocationDialog()
    {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        new FitnessLocationDialog(EquipmentActivity.this, textViewDisplayName.getText().toString(), textViewLocation.getText().toString(), mGoogleApiClient, location, dialogListener);
    }

    /**
     * Starts the app settings screen.
     */
    private void startInstalledAppDetailsActivity()
    {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(intent);

        Toast.makeText(context, context.getString(R.string.directions_set_permissions), Toast.LENGTH_LONG).show();
    }

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

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        // We've connected to the Google Maps API, so open the dialog.
        openFitnessLocationDialog();
    }

    @Override
    public void onConnectionSuspended(int i){ }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(context, getString(R.string.location_issue), Toast.LENGTH_SHORT).show();
    }
}