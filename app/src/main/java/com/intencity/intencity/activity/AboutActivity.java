package com.intencity.intencity.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.intencity.intencity.R;

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

        TextView versionTextView = (TextView) findViewById(R.id.text_view_version);

        try
        {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionTextView.setText(versionName);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.i("Couldn't find version", "Couldn't find version " + e.toString());
        }
    }
}