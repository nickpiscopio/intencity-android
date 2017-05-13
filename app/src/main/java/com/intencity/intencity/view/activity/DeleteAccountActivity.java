package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.Util;

/**
 * This is the change password activity for Intencity.
 *
 * Created by Nick Piscopio on 1/17/15.
 */
public class DeleteAccountActivity extends AppCompatActivity implements ServiceListener, DialogListener
{
    private EditText password;

    private TextView forgotPassword;

    private Button deleteAccount;

    private Context context;

    private ProgressBar loadingProgressBar;

    private int userId;

    private String currentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        loadingProgressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        password = (EditText) findViewById(R.id.edit_text_current_password);

        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(forgotPasswordListener);

        deleteAccount = (Button) findViewById(R.id.btn_change_password);
        deleteAccount.setOnClickListener(deleteAccountListener);

        // Sets the password field typefaces to default.
        password.setTypeface(Typeface.DEFAULT);

        context = getApplicationContext();

        userId = Util.getSecurePreferencesUserId(context);
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

    /**
     * The click listener for forgot password.
     */
    private View.OnClickListener forgotPasswordListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            startActivity(new Intent(context, ForgotPasswordActivity.class));
        }
    };

    /**
     * The click listener for the change password button.
     */
    private View.OnClickListener deleteAccountListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            currentPassword = password.getText().toString();

            // Check if all the fields are filled in.
            if (!Util.checkStringLength(currentPassword, 1))
            {
                showMessage(context.getString(R.string.generic_error),
                            context.getString(R.string.fill_in_password));
            }
            else
            {
                CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.title_delete_account),
                                                                     context.getString(R.string.delete_account_dialog_message),
                                                                     true);
                dialog.setNegativeButtonStringRes(R.string.title_delete_account);
                dialog.setPositiveButtonStringRes(android.R.string.cancel);

                new CustomDialog(DeleteAccountActivity.this, DeleteAccountActivity.this, dialog, true);
            }
        }
    };

    /**
     * Sets a result to log out the user.
     */
    private void logOut()
    {
        setResult(Constant.REQUEST_CODE_LOG_OUT);
        finish();
    }

    /**
     * Show a message to the user.
     *
     * @param title     The title of the message.
     * @param message   The message to display.
     */
    private void showMessage(String title, String message)
    {
        Util.showMessage(DeleteAccountActivity.this, title, message);
    }

    @Override
    public void onButtonPressed(int which)
    {
        // The "Delete account" button is the negative button.
        // We do this to make it harder for the user to reach for it,
        // so they don't just blindly click.
        if (which == Constant.NEGATIVE_BUTTON)
        {
            loadingProgressBar.setVisibility(View.VISIBLE);

            new ServiceTask(this).execute(
                    Constant.SERVICE_VALIDATE_USER_CREDENTIALS,
                    Constant.getValidateUserCredentialsServiceParameters(userId,
                                                                         Util.replaceApostrophe(currentPassword)));
        }
    }

    @Override
    public void onServiceResponse(int statusCode, String response)
    {
        switch (statusCode)
        {
            case Constant.STATUS_CODE_SUCCESS_CREDENTIALS_VALID:

                // The credentials are valid so call the service to delete the account.
                new ServiceTask(this).execute(Constant.SERVICE_STORED_PROCEDURE,
                                              Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_REMOVE_ACCOUNT,
                                                                                         String.valueOf(userId)));

                break;

            case Constant.STATUS_CODE_SUCCESS_STORED_PROCEDURE:
                // We log out here because we couldn't find the user's account, so we don't need to delete anything.
            case Constant.STATUS_CODE_FAILURE_CREDENTIALS_EMAIL_INVALID:

                logOut();

                break;

            case Constant.STATUS_CODE_FAILURE_CREDENTIALS_PASSWORD_INVALID:

                loadingProgressBar.setVisibility(View.GONE);

                showMessage(context.getString(R.string.generic_error),
                            context.getString(R.string.invalid_password));

                break;

            default:

                loadingProgressBar.setVisibility(View.GONE);

                showMessage(context.getString(R.string.generic_error),
                            context.getString(R.string.intencity_communication_error));

                break;
        }
    }
}