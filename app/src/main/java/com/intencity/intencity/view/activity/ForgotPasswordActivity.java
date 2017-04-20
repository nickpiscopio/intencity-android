package com.intencity.intencity.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.Util;

import org.json.JSONObject;

/**
 * This is the forgot password activity for Intencity.
 *
 * Created by Nick Piscopio on 1/11/16.
 */
public class ForgotPasswordActivity extends AppCompatActivity implements ServiceListener
{
    private ProgressBar loadingProgressBar;

    private LinearLayout form;

    private EditText email;

    private Button resetPassword;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        loadingProgressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        form = (LinearLayout) findViewById(R.id.linear_layout_form);

        email = (EditText) findViewById(R.id.edit_text_email);

        resetPassword = (Button) findViewById(R.id.btn_reset_password);
        resetPassword.setOnClickListener(resetPasswordListener);

        context = getApplicationContext();

        // Sets the progress bar color.
        Util.setProgressBarColor(context, loadingProgressBar);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * The click listener for the terms checkbox.
     */
    private View.OnClickListener resetPasswordListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String emailText = email.getText().toString();
            if (Util.isFieldValid(emailText, Constant.REGEX_EMAIL))
            {
                loadingProgressBar.setVisibility(View.VISIBLE);
                form.setVisibility(View.GONE);

                String emailString = Util.replacePlus(email.getText().toString());

                new ServiceTask(ForgotPasswordActivity.this).execute(
                        Constant.SERVICE_FORGOT_PASSWORD,
                        Constant.getStandardServiceUrlParams(emailString));
            }
            else
            {
                Util.showMessage(ForgotPasswordActivity.this, context.getString(R.string.generic_error), context.getString(R.string.email_validation_error));
            }
        }
    };

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
        loadingProgressBar.setVisibility(View.GONE);
        form.setVisibility(View.VISIBLE);

        Util.showMessage(ForgotPasswordActivity.this, context.getString(R.string.forgot_password_email_sent_title),
                         context.getString(R.string.forgot_password_email_sent));
    }

    @Override
    public void onServiceResponse(int statusCode, String response)
    {

    }

    @Override
    public void onRetrievalFailed(int statusCode)
    {
        loadingProgressBar.setVisibility(View.GONE);
        form.setVisibility(View.VISIBLE);

        Util.showCommunicationErrorMessage(ForgotPasswordActivity.this);
    }
}