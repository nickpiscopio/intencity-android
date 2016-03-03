package com.intencity.intencity.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
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
    public static final String IS_TERMS = "com.intencity.intencity.is.terms";
    public static final String SHOW_PRIVACY_POLICY = "com.intencity.intencity.show.privacy.policy";

    private final String TERMS_URL = "file:///android_asset/terms.html";
    private final String PRIVACY_POLICY_URL = "file:///android_asset/privacy.html";

    private boolean showPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        Bundle extras = getIntent().getExtras();

        Boolean isTerms = extras.getBoolean(IS_TERMS);
        showPrivacyPolicy = extras.getBoolean(SHOW_PRIVACY_POLICY);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(isTerms ? R.string.title_terms : R.string.title_privacy_policy);
        }

        WebView webview = (WebView)findViewById(R.id.web_view_terms);
        webview.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.page_background));
        webview.loadUrl(isTerms ? TERMS_URL : PRIVACY_POLICY_URL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.privacy_policy:
                Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
                intent.putExtra(TermsActivity.IS_TERMS, false);
                intent.putExtra(TermsActivity.SHOW_PRIVACY_POLICY, false);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (showPrivacyPolicy)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.terms_menu, menu);
        }

        return showPrivacyPolicy;
    }
}