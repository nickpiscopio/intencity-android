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
import com.intencity.intencity.handler.NotificationHandler;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

/**
 * This is the settings activity for Intencity.
 *
 * Created by Nick Piscopio on 1/17/15.
 */
public class MenuActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_list);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String[] settingsList;

        SecurePreferences securePreferences = new SecurePreferences(getApplicationContext());
        String accountType = securePreferences.getString(Constant.USER_ACCOUNT_TYPE, "");

        int awardTotal = NotificationHandler.getInstance(null).getAwardCount();

        // TODO: This needs to be fixed for trial accounts.
        // Do not add the change password if we are using a trial account.
        if (!accountType.equals(Constant.ACCOUNT_TYPE_TRIAL))
        {
            settingsList = new String[] { getString(R.string.notifications, "(" + awardTotal + ")"),
                                          getString(R.string.edit_exclusion),
                                          getString(R.string.edit_equipment),
                                          getString(R.string.change_password),
                                          getString(R.string.title_about),
                                          getString(R.string.title_terms),
                                          getString(R.string.title_log_out)
            };
        }
        else
        {
            settingsList = new String[] { getString(R.string.edit_equipment) };
        }

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
                case 0: // Change exclusion clicked.
                    startActivity(NotificationActivity.class);
                    break;

                case 1: // Change exclusion clicked.
                    startActivity(ExclusionActivity.class);
                    break;
                case 2: // Change equipment clicked.
                    startActivity(EquipmentActivity.class);
                    break;
                case 3: // Change Password clicked.
                    startActivity(ChangePasswordActivity.class);
                    break;
                case 4: // About clicked.
                    startActivity(AboutActivity.class);
                    break;
                case 5: // Terms clicked.
                    startActivity(TermsActivity.class);
                    break;
                case 6: // Logout clicked.
                    logOut();
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

    /**
     * Dismisses the activity and tells the MainActivity to log out.
     */
    private void logOut()
    {
        setResult(Constant.REQUEST_CODE_LOG_OUT);
        finish();
    }
}