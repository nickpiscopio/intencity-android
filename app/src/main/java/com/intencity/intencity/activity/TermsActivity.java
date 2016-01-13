package com.intencity.intencity.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.intencity.intencity.R;

/**
 * This is the terms activity for Intencity.
 *
 * Created by Nick Piscopio on 1/11/16.
 */
public class TermsActivity extends AppCompatActivity
{
    private final String TERMS_URL = "file:///android_asset/terms.html";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        WebView webview = (WebView)findViewById(R.id.web_view_terms);
        webview.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.page_background));
        webview.loadUrl(TERMS_URL);
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