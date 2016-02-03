package com.intencity.intencity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.MenuAdapter;
import com.intencity.intencity.handler.NotificationHandler;
import com.intencity.intencity.model.MenuItem;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

import java.util.ArrayList;

/**
 * This is the settings activity for Intencity.
 *
 * Created by Nick Piscopio on 1/17/15.
 */
public class MenuActivity extends AppCompatActivity
{
    private ArrayList<MenuItem> menuItems;

    private String logOutTitle;

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

        SecurePreferences securePreferences = new SecurePreferences(getApplicationContext());
        String accountType = securePreferences.getString(Constant.USER_ACCOUNT_TYPE, "");

        int awardTotal = NotificationHandler.getInstance(null).getAwardCount();

        logOutTitle = getString(R.string.title_log_out);

        menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.notifications, "(" + awardTotal + ")"), NotificationActivity.class));
        menuItems.add(new MenuItem(getString(R.string.title_settings), null));
        menuItems.add(new MenuItem(getString(R.string.edit_exclusion), ExclusionActivity.class));
        menuItems.add(new MenuItem(getString(R.string.edit_equipment), EquipmentActivity.class));
        if (!accountType.equals(Constant.ACCOUNT_TYPE_MOBILE_TRIAL))
        {
            menuItems.add(new MenuItem(getString(R.string.change_password), ChangePasswordActivity.class));
        }
        menuItems.add(new MenuItem(logOutTitle, null));
        menuItems.add(new MenuItem(getString(R.string.title_info), null));
        menuItems.add(new MenuItem(getString(R.string.title_about), AboutActivity.class));
        menuItems.add(new MenuItem(getString(R.string.title_terms), TermsActivity.class));
        menuItems.add(new MenuItem(getString(R.string.title_account_settings), null));
        menuItems.add(new MenuItem(getString(R.string.title_delete_account), DeleteAccountActivity.class));

        MenuAdapter settingsAdapter =
                new MenuAdapter(this, R.layout.list_item_header, R.layout.list_item_standard, menuItems);

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
            MenuItem menuItem = menuItems.get(position);

            Class cls = menuItem.getCls();

            if (cls != null)
            {
                startActivity(cls);
            }
            else if (menuItem.getTitle().equals(logOutTitle))
            {
                logOut();
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
        if (cls == DeleteAccountActivity.class)
        {
            startActivityForResult(intent, Constant.REQUEST_CODE_LOG_OUT);
        }
        else
        {
            startActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constant.REQUEST_CODE_LOG_OUT)
        {
            logOut();
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem menuItem)
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