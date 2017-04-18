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
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;
import com.intencity.intencity.util.Util;

import org.json.JSONObject;

/**
 * This is the change password activity for Intencity.
 *
 * Created by Nick Piscopio on 1/17/15.
 */
public class ChangePasswordActivity extends AppCompatActivity implements ServiceListener,
                                                                         DialogListener
{
    private TextView forgotPassword;

    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmNewPasswordEditText;

    private Button changePassword;

    private Context context;

    private ProgressBar loadingProgressBar;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        loadingProgressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(forgotPasswordListener);
        currentPasswordEditText = (EditText) findViewById(R.id.edit_text_current_password);
        newPasswordEditText = (EditText) findViewById(R.id.edit_text_new_password);
        confirmNewPasswordEditText = (EditText) findViewById(R.id.edit_text_confirm_new_password);

        changePassword = (Button) findViewById(R.id.btn_change_password);
        changePassword.setOnClickListener(changePasswordListener);

        // Sets the password field typefaces to default.
        currentPasswordEditText.setTypeface(Typeface.DEFAULT);
        newPasswordEditText.setTypeface(Typeface.DEFAULT);
        confirmNewPasswordEditText.setTypeface(Typeface.DEFAULT);

        context = getApplicationContext();

        SecurePreferences securePreferences = new SecurePreferences(getApplicationContext());
        userId = securePreferences.getInt(Constant.USER_ACCOUNT_ID, 0);
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
    private View.OnClickListener changePasswordListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

            // Check if all the fields are filled in.
            if (!Util.checkStringLength(currentPassword, 1) || !Util.checkStringLength(newPassword, 1))
            {
                showMessage(context.getString(R.string.generic_error),
                                 context.getString(R.string.fill_in_fields));
            }
            // Check to see if the password is greater than the password length needed.
            // Check to see if the password is valid.
            else if (!Util.checkStringLength(newPassword, Constant.REQUIRED_PASSWORD_LENGTH) || !Util.isFieldValid(newPassword, Constant.REGEX_FIELD))
            {
                showMessage(context.getString(R.string.generic_error),
                                 context.getString(R.string.password_validation_error));
            }
            // Check to see if the passwords match.
            else if (!newPassword.equals(confirmNewPassword))
            {
                showMessage(context.getString(R.string.generic_error),
                                 context.getString(R.string.password_match_error));
            }
            else
            {
                loadingProgressBar.setVisibility(View.VISIBLE);

                new ServiceTask(ChangePasswordActivity.this).execute(
                        Constant.SERVICE_CHANGE_PASSWORD,
                        Constant.generateChangePasswordVariables(userId,
                                                                 Util.replaceApostrophe(currentPassword),
                                                                 Util.replaceApostrophe(newPassword)));
            }
        }
    };

    @Override
    public void onRetrievalSuccessful(String response)
    {
////        response = response.replaceAll("\"", "");
//
////        try
////        {
////            JSONObject obj = new JSONObject(response);
//            boolean success = Boolean.parseBoolean(response.getString(Constant.SUCCESS));
//            if (success)
//            {
//                int userId = Integer.parseInt(response.getString(Constant.DATA));
//
//                Util.loadIntencity(GetStartedActivity.this, userId, Constant.ACCOUNT_TYPE_MOBILE_TRIAL, createdDate);
//            }
//            else
//            {
//                showFailureMessage();
//            }
//        }
////        catch (JSONException e)
////        {
////            showFailureMessage();
////        }
//
//        if (response.equalsIgnoreCase(Constant.INVALID_PASSWORD))
//        {
//            showMessage(context.getString(R.string.generic_error),
//                        context.getString(R.string.invalid_password));
//        }
//        else if (response.equalsIgnoreCase(Constant.SUCCESS))
//        {
//            CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.password_changed_title),
//                                                         context.getString(R.string.password_changed),
//                                                         false);
//
//            new CustomDialog(ChangePasswordActivity.this, ChangePasswordActivity.this, dialog, true);
//        }
//        else
//        {
//            showMessage(context.getString(R.string.generic_error),
//                        context.getString(R.string.intencity_communication_error));
//        }
//
//        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRetrievalSuccessful(int statusCode, JSONObject response)
    {

    }

    @Override
    public void onRetrievalFailed()
    {
        loadingProgressBar.setVisibility(View.GONE);

        showMessage(context.getString(R.string.generic_error),
                    context.getString(R.string.intencity_communication_error));
    }

    /**
     * Show a message to the user.
     *
     * @param title     The title of the message.
     * @param message   The message to display.
     */
    private void showMessage(String title, String message)
    {
        Util.showMessage(ChangePasswordActivity.this, title, message);
    }

    @Override
    public void onButtonPressed(int which)
    {
        // The only button that can be pressed is the positive one,
        // so we are not going to put a switch here.

        // Finish the activity when we know the password was changed successfully.
        finish();
    }
}