package com.intencity.intencity.view.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
    private String rateTitle;

    private Context context;

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

        context = getApplicationContext();

        SecurePreferences securePreferences = new SecurePreferences(context);
        String accountType = securePreferences.getString(Constant.USER_ACCOUNT_TYPE, "");

        int awardTotal = NotificationHandler.getInstance(null).getAwardCount();

        logOutTitle = getString(R.string.title_log_out);
        rateTitle = getString(R.string.title_rate_intencity);

        menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(getString(R.string.notifications, "(" + awardTotal + ")"), NotificationActivity.class));
        menuItems.add(new MenuItem(getString(R.string.title_settings), null));
        menuItems.add(new MenuItem(getString(R.string.edit_priority), ExercisePriorityActivity.class));
        menuItems.add(new MenuItem(getString(R.string.edit_equipment), EquipmentActivity.class));
        if (!accountType.equals(Constant.ACCOUNT_TYPE_MOBILE_TRIAL))
        {
            menuItems.add(new MenuItem(getString(R.string.change_password), ChangePasswordActivity.class));
        }
        menuItems.add(new MenuItem(logOutTitle, null));
        menuItems.add(new MenuItem(getString(R.string.title_app), null));
        menuItems.add(new MenuItem(getString(R.string.title_about), AboutActivity.class));
        menuItems.add(new MenuItem(getString(R.string.title_terms), TermsActivity.class, getTermsBundle(true)));
        menuItems.add(new MenuItem(getString(R.string.title_privacy_policy), TermsActivity.class, getTermsBundle(false)));
        menuItems.add(new MenuItem(rateTitle, null));

        if (!accountType.equals(Constant.ACCOUNT_TYPE_MOBILE_TRIAL))
        {
            menuItems.add(new MenuItem(getString(R.string.title_account_settings), null));
            menuItems.add(new MenuItem(getString(R.string.title_delete_account), DeleteAccountActivity.class));
        }

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
                startActivity(cls, menuItem.getBundle());
            }
            else if (menuItem.getTitle().equals(logOutTitle))
            {
                logOut();
            }
            else if (menuItem.getTitle().equals(rateTitle))
            {
                String packageName = context.getPackageName();

                try
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                }
                catch (ActivityNotFoundException e)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                                             Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                }
            }
        }
    };

    /**
     * Starts an activity.
     *
     * @param cls       The class to start.
     * @param bundle    The bundle that is included with the intent if there is one.
     */
    private void startActivity(Class<?> cls, Bundle bundle)
    {
        Intent intent = new Intent(this, cls);

        if (bundle != null)
        {
            intent.putExtras(bundle);
        }

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
     * Gets the bundle for the terms activity.
     *
     * @param isTerms   Boolean value of whether the activity should show the terms or privacy policy.
     *
     * @return  The bundle.
     */
    private Bundle getTermsBundle(boolean isTerms)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(TermsActivity.IS_TERMS, isTerms);
        bundle.putBoolean(TermsActivity.SHOW_PRIVACY_POLICY, false);
        return bundle;
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