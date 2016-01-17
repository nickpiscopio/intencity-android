package com.intencity.intencity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.intencity.intencity.R;

/**
 * This is the settings activity for Intencity.
 *
 * Created by Nick Piscopio on 1/17/15.
 */
public class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String[] settingsList = new String[] { getString(R.string.edit_equipment),
                                                         getString(R.string.change_password) };

        ArrayAdapter<String> settingsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, settingsList);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(settingsAdapter);
        listView.setOnItemClickListener(settingClicked);
    }

    /**
     * The click listener for each item clicked in the settings list.
     */
    private AdapterView.OnItemClickListener settingClicked = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            // This directly correlates with the settingsList indices.
            switch (position)
            {
                case 0: // Change equipment clicked.
                    startActivity(EquipmentActivity.class);
                    break;
                case 1: // Change Password clicked.
                    startActivity(ChangePasswordActivity.class);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Starts an activity.
     *
     * @param cls   The class to start.
     */
    private void startActivity(Class<?> cls)
    {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
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
}