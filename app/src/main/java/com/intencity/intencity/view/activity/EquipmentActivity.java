package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.CheckboxAdapter;
import com.intencity.intencity.listener.DialogFitnessLocationListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.SelectableListItem;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.notification.FitnessLocationDialog;
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

    private CardView cardFitnessLocation;

    private TextView textViewDisplayName;
    private TextView textViewLocation;

    private ListView listView;

    private MenuItem menuCheckBox;

    private String email;

    private ArrayList<SelectableListItem> equipmentList;
    private ArrayList<String> userEquipment;

    private Context context;

    private CheckboxAdapter adapter;

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

        connectionIssue = (LinearLayout) findViewById(R.id.layout_connection_issue);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        progressBar.setVisibility(View.VISIBLE);

        cardFitnessLocation = (CardView) findViewById(R.id.card_fitness_location);

        textViewDisplayName = (TextView) findViewById(R.id.text_view_display_name);
        textViewLocation = (TextView) findViewById(R.id.text_view_location);

        context = getApplicationContext();

        email = Util.getSecurePreferencesEmail(context);

        cardFitnessLocation.setOnClickListener(fitnessLocationClickListener);

        new ServiceTask(getEquipmentServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_EQUIPMENT, email));

        setUserSetEquipment();
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
     * Selects or deselects all of the equipment based on the checkbox that is checked in the menu.
     *
     * @param selectAll   Boolean value of whether or not we should select all of the equipment.
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

        TextView title = (TextView) header.findViewById(R.id.title);
        TextView description = (TextView) header.findViewById(R.id.description);

        title.setText(context.getString(R.string.edit_equipment_title));
        description.setText(context.getString(R.string.edit_equipment_description));

        listView = (ListView) findViewById(R.id.list_view);
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

        new ServiceTask(updateEquipmentServiceListener).execute(Constant.SERVICE_UPDATE_EQUIPMENT,
                                                                Constant.generateServiceListVariables(
                                                                        email, userEquipment, true));
    }

    /**
     * The click listener for editing the fitness location.
     */
    private View.OnClickListener fitnessLocationClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            new FitnessLocationDialog(EquipmentActivity.this, textViewDisplayName.getText().toString(), textViewLocation.getText().toString(), dialogListener);
        }
    };

    /**
     * The click listener for each item clicked in the settings list.
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
     * Updates a specified equipment list item.
     *
     * This adds or removes it from the list of equipment that the user currently has.
     *
     * @param equipment     The item to add or remove from the user's list of equipment.
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
     * The dialog listener for the equipment activity.
     */
    private DialogFitnessLocationListener dialogListener = new DialogFitnessLocationListener()
    {
        @Override
        public void onSaveFitnessLocation(String displayName, String location)
        {
            textViewDisplayName.setVisibility(displayName.length() > 0 ? View.VISIBLE : View.GONE);
            textViewDisplayName.setText(displayName);
            textViewLocation.setText(location);
        }

        @Override
        public void onButtonPressed(int which)
        {
            // This only gets called when there is a connection issue.
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