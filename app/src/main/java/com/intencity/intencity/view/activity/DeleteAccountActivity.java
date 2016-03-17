package com.intencity.intencity.view.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;
import com.intencity.intencity.util.Util;

/**
 * This is the change password activity for Intencity.
 *
 * Created by Nick Piscopio on 1/17/15.
 */
public class DeleteAccountActivity extends AppCompatActivity implements DialogListener
{
    private EditText password;

    private Button deleteAccount;

    private Context context;

    private ProgressBar loadingProgressBar;

    private String email;

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

        deleteAccount = (Button) findViewById(R.id.btn_change_password);
        deleteAccount.setOnClickListener(deleteAccountListener);

        // Sets the password field typefaces to default.
        password.setTypeface(Typeface.DEFAULT);

        context = getApplicationContext();

        SecurePreferences securePreferences = new SecurePreferences(getApplicationContext());
        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");
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
     * The service listener for the user credentials.
     */
    private ServiceListener userCredentialsListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            response = response.replaceAll("\"", "");

            if (response.equalsIgnoreCase(Constant.INVALID_PASSWORD))
            {
                loadingProgressBar.setVisibility(View.GONE);

                showMessage(context.getString(R.string.generic_error),
                            context.getString(R.string.invalid_password));
            }
            else if (response.equalsIgnoreCase(Constant.COULD_NOT_FIND_EMAIL))
            {
                logOut();
            }
            else
            {
                // Service to delete the user's account.
                new ServiceTask(removeAccountListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                                 Constant.generateStoredProcedureParameters(
                                                                         Constant.STORED_PROCEDURE_REMOVE_ACCOUNT,
                                                                         email));
            }
        }

        @Override
        public void onRetrievalFailed()
        {
            loadingProgressBar.setVisibility(View.GONE);

            showMessage(context.getString(R.string.generic_error),
                        context.getString(R.string.intencity_communication_error));
        }
    };

    /**
     * The service listener for the removing a user's account.
     */
    private ServiceListener removeAccountListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            logOut();
        }

        @Override
        public void onRetrievalFailed()
        {
            loadingProgressBar.setVisibility(View.GONE);

            showMessage(context.getString(R.string.generic_error),
                        context.getString(R.string.intencity_communication_error));
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

            new ServiceTask(userCredentialsListener).execute(
                    Constant.SERVICE_VALIDATE_USER_CREDENTIALS,
                    Constant.getValidateUserCredentialsServiceParameters(email,
                                                                         Util.replaceApostrophe(currentPassword)));
        }
    }
}