package com.intencity.intencity.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.intencity.intencity.R;
import com.intencity.intencity.dialog.CustomDialog;
import com.intencity.intencity.dialog.Dialog;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.Util;

/**
 * This is the forgot password activity for Intencity.
 *
 * Created by Nick Piscopio on 1/11/16.
 */
public class ForgotPasswordActivity extends AppCompatActivity implements DialogListener,
                                                                         ServiceListener
{
    private EditText email;

    private Button resetPassword;

    private Context context;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = (EditText) findViewById(R.id.edit_text_email);

        resetPassword = (Button) findViewById(R.id.btn_reset_password);
        resetPassword.setOnClickListener(resetPasswordListener);

        context = getApplicationContext();

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
                new ServiceTask(ForgotPasswordActivity.this).execute(
                        Constant.SERVICE_FORGOT_PASSWORD,
                        Constant.getNewPassword(email.getText().toString()));
            }
            else
            {
                showMessage(context.getString(R.string.generic_error), context.getString(R.string.email_validation_error));
            }
        }
    };

    /**
     * Displays the login error to the user.
     *
     * @param title     The title the dialog should display.
     * @param message   The message the dialog should display.
     */
    private void showMessage(String title, String message)
    {
        Dialog dialog = new Dialog(title , message, false);

        new CustomDialog(ForgotPasswordActivity.this, this, dialog);
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
    public void onButtonPressed(int which){ }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        showMessage(context.getString(R.string.forgot_password_email_sent_title),
                    context.getString(R.string.forgot_password_email_sent));
    }

    @Override public void onRetrievalFailed() { }
}