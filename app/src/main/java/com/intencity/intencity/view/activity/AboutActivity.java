package com.intencity.intencity.view.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.intencity.intencity.BuildType;
import com.intencity.intencity.R;
import com.intencity.intencity.util.Build;

/**
 * This is the about activity for Intencity.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class AboutActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView versionTextView = (TextView) findViewById(R.id.text_view_version);

        try
        {
            String buildType = (BuildType.type == Build.Type.DEBUG) ? " (" + BuildType.type.toString() + ")" : "";
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName + buildType;
            versionTextView.setText(versionName);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.i("Couldn't find version", "Couldn't find version " + e.toString());
        }
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