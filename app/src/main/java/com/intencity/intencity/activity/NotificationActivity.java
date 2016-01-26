package com.intencity.intencity.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.NotificationAdapter;
import com.intencity.intencity.dialog.AwardDialogContent;
import com.intencity.intencity.handler.NotificationHandler;

import java.util.ArrayList;

/**
 * This is the settings activity for Intencity.
 *
 * Created by Nick Piscopio on 1/17/15.
 */
public class NotificationActivity extends AppCompatActivity
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

        ArrayList<AwardDialogContent> awards = NotificationHandler.getInstance(null).getAwards();

        NotificationAdapter settingsAdapter =
                new NotificationAdapter(getApplicationContext(), R.layout.list_item_award, awards);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(settingsAdapter);
        listView.setEmptyView(findViewById(R.id.text_view_no_notifications));
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