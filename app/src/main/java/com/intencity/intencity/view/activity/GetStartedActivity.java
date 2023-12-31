package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * The initial activity for Intencity.
 *
 * This was created to make the getting started process quicker because people don't like logging into applications.
 *
 * Created by Nick Piscopio on 8/13/16.
 */
public class GetStartedActivity extends AppCompatActivity implements ServiceListener
{
    private ProgressBar loadingProgressBar;

    private LinearLayout loginForm;

    private TextView terms;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        loadingProgressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        loginForm = (LinearLayout) findViewById(R.id.linear_layout_login_form);

        terms = (TextView) findViewById(R.id.terms);

        Button getStarted = (Button) findViewById(R.id.btn_get_started);
        TextView login = (TextView) findViewById(R.id.btn_login);
        TextView createAccount = (TextView) findViewById(R.id.btn_create_account);

        getStarted.setOnClickListener(getStartedClickListener);
        login.setOnClickListener(loginClickListener);
        createAccount.setOnClickListener(createAccountListener);

        context = getApplicationContext();

        // Sets the progress bar color.
        Util.setProgressBarColor(context, loadingProgressBar);

        SpannableStringBuilder builder = new SpannableStringBuilder();

        String[] checkBoxString = terms.getText().toString().split("@");
        int termsStringLength = checkBoxString.length;

        for (int i = 0; i < termsStringLength; i++)
        {
            if (i % 2 != 0)
            {
                String termsSnippet = checkBoxString[i];

                SpannableString spannable = new SpannableString(termsSnippet);
                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary)),
                                  0, termsSnippet.length(), 0);

                builder.append(spannable);
            }
            else
            {
                builder.append(checkBoxString[i]);
            }

            terms.setText(builder, TextView.BufferType.SPANNABLE);
            terms.setOnClickListener(termsClickListener);
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

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            JSONObject json = new JSONObject(response);

            String email = json.getString(Constant.COLUMN_EMAIL);
            String accountType = json.getString(Constant.COLUMN_ACCOUNT_TYPE);

            Util.loadIntencity(GetStartedActivity.this, email, accountType, 0);
        }
        catch (JSONException e)
        {
            loadingProgressBar.setVisibility(View.GONE);
            loginForm.setVisibility(View.VISIBLE);

            showMessage(context.getString(R.string.login_error_title),
                        context.getString(R.string.login_error_message));
            Log.e(Constant.TAG, "Error parsing login data " + e.toString());
        }
    }

    @Override
    public void onRetrievalFailed()
    {
        loadingProgressBar.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);

        showMessage(context.getString(R.string.login_error_title),
                    context.getString(R.string.login_error_message));
    }

    /**
     * The click listener for the get started button.
     *
     * This creates an account for the user without asking them for their information.
     */
    View.OnClickListener getStartedClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            createTrialAccount();
        }
    };

    /**
     * The click listener for the login button.
     */
    View.OnClickListener loginClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            startActivity(new Intent(context, LoginActivity.class));
        }
    };

    /**
     * The click listener for the create account button.
     */
    View.OnClickListener createAccountListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            startActivity(new Intent(context, CreateAccountActivity.class));
        }
    };

    /**
     * The click listener for the terms.
     */
    private View.OnClickListener termsClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            openTerms();
        }
    };

    /**
     * Displays the login error to the user.
     */
    private void showMessage(String title, String message)
    {
        loadingProgressBar.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);

        Util.showMessage(GetStartedActivity.this, title, message);
    }

    /**
     * Opens the terms of use.
     */
    private void openTerms()
    {
        Intent intent = new Intent(context, TermsActivity.class);
        intent.putExtra(TermsActivity.IS_TERMS, true);
        intent.putExtra(TermsActivity.SHOW_PRIVACY_POLICY, true);
        startActivity(intent);
    }

    /**
     * Creates a trial account for the user.
     */
    private void createTrialAccount()
    {
        final long createdDate = new Date().getTime();

        String firstName = "Anonymous";
        String lastName = "User";
        final String email = lastName + createdDate + "@intencity.fit";
        String password = String.valueOf(createdDate);

        loadingProgressBar.setVisibility(View.VISIBLE);
        loginForm.setVisibility(View.GONE);

        new ServiceTask(new ServiceListener() {
            @Override
            public void onRetrievalSuccessful(String response)
            {
                response = response.replaceAll("\"", "");

                if (response.equalsIgnoreCase(Constant.ACCOUNT_CREATED))
                {
                    Util.loadIntencity(GetStartedActivity.this, email, Constant.ACCOUNT_TYPE_MOBILE_TRIAL, createdDate);
                }
                else
                {
                    showMessage(context.getString(R.string.login_error_title),
                                context.getString(
                                        R.string.intencity_communication_error));
                }
            }

            @Override
            public void onRetrievalFailed() { }
        }).execute(Constant.SERVICE_CREATE_ACCOUNT,
                   Constant.getAccountParameters(firstName, lastName, email, password,
                                                 Constant.ACCOUNT_TYPE_MOBILE_TRIAL));
    }
}