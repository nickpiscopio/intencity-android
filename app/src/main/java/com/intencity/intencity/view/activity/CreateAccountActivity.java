package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * This is the create account activity for Intencity.
 *
 * Created by Nick Piscopio on 1/11/16.
 */
public class CreateAccountActivity extends AppCompatActivity implements ServiceListener
{
    public static final String BUNDLE_CREATE_ACCOUNT_FROM_TRIAL = "com.intencity.intencity.create.account.from.trial";

    private LinearLayout loadingLayout;
    private LinearLayout formLayout;

    private ProgressBar progressBar;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText confirmEmailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private TextView terms;

    private Button createAccount;

    private Context context;

    boolean createAccountFromTrial = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        loadingLayout = (LinearLayout) findViewById(R.id.linear_layout_loading);
        formLayout = (LinearLayout) findViewById(R.id.linear_layout_form);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        firstNameEditText = (EditText) findViewById(R.id.edit_text_first_name);
        lastNameEditText = (EditText) findViewById(R.id.edit_text_last_name);
        emailEditText = (EditText) findViewById(R.id.edit_text_email);
        confirmEmailEditText = (EditText) findViewById(R.id.edit_text_confirm_email);
        passwordEditText = (EditText) findViewById(R.id.edit_text_password);
        confirmPasswordEditText = (EditText) findViewById(R.id.edit_text_confirm_password);

        terms = (TextView) findViewById(R.id.terms);

        createAccount = (Button) findViewById(R.id.btn_create_account);

        context = getApplicationContext();

        // Sets the progress bar color.
        Util.setProgressBarColor(context, progressBar);

        passwordEditText.setTypeface(Typeface.DEFAULT);
        confirmPasswordEditText.setTypeface(Typeface.DEFAULT);

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

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            createAccountFromTrial = extras.getBoolean(BUNDLE_CREATE_ACCOUNT_FROM_TRIAL, false);
        }

        String title = context.getString(createAccountFromTrial ? R.string.title_convert_account : R.string.title_create_account);

        createAccount.setOnClickListener(createAccountClickListener);
        createAccount.setText(title);

        // Add the back button to the action bar.
        // Add the title to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }

    /**
     * The click listener for the terms.
     */
    private View.OnClickListener termsClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(context, TermsActivity.class);
            intent.putExtra(TermsActivity.IS_TERMS, true);
            intent.putExtra(TermsActivity.SHOW_PRIVACY_POLICY, true);
            startActivity(intent);
        }
    };

    /**
     * The click listener for the create account button.
     */
    private View.OnClickListener createAccountClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String confirmEmail = confirmEmailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Check if all the fields are filled in.
            if (!Util.checkStringLength(firstName, 1) || !Util.checkStringLength(lastName, 1) ||
                !Util.checkStringLength(email, 1) || !Util.checkStringLength(confirmEmail, 1) ||
                !Util.checkStringLength(password, 1) || !Util.checkStringLength(confirmPassword, 1))
            {
                showErrorMessage(context.getString(R.string.fill_in_fields));
            }
            // Check if the email is valid.
            else if (!Util.isFieldValid(email, Constant.REGEX_EMAIL))
            {
                showErrorMessage(context.getString(R.string.email_validation_error));
            }
            // Check if the first and last name have valid characters.
            else if (!Util.isFieldValid(firstName, Constant.REGEX_FIELD) || !Util.isFieldValid(lastName, Constant.REGEX_FIELD))
            {
                showErrorMessage(context.getString(R.string.field_validation_error));
            }
            // Check to see if the emails match.
            else if (!email.equals(confirmEmail))
            {
                showErrorMessage(context.getString(R.string.email_match_error));
            }
            // Check to see if the password is greater than the password length needed.
            // Check to see if the password is valid.
            else if (!Util.checkStringLength(password, Constant.REQUIRED_PASSWORD_LENGTH) || !Util.isFieldValid(password, Constant.REGEX_FIELD))
            {
                showErrorMessage(context.getString(R.string.password_validation_error));
            }
            // Check to see if the passwords match.
            else if (!password.equals(confirmPassword))
            {
                showErrorMessage(context.getString(R.string.password_match_error));
            }
            else
            {
                loadingLayout.setVisibility(View.VISIBLE);
                formLayout.setVisibility(View.GONE);

                if (createAccountFromTrial)
                {
                    int userId = Util.getSecurePreferencesUserId(context);
                    // Upgrade a trial account to a full account.
                    new ServiceTask(CreateAccountActivity.this).execute(
                            Constant.SERVICE_UPDATE_ACCOUNT,
                            Constant.getUpdateAccountParameters(userId, Util.replaceApostrophe(firstName), Util.replaceApostrophe(
                                    lastName), Util.replacePlus(email), Util.replaceApostrophe(password)));
                }
                else
                {
                    // Create an account.
                    new ServiceTask(CreateAccountActivity.this).execute(
                            Constant.SERVICE_CREATE_ACCOUNT,
                            Constant.getAccountParameters(Util.replaceApostrophe(firstName), Util.replaceApostrophe(
                                    lastName), Util.replacePlus(email), Util.replaceApostrophe(password), Constant.ACCOUNT_TYPE_NORMAL));
                }
            }
        }
    };

    /**
     * Displays the login error to the user.
     *
     * @param message   The message to display to the user.
     */
    private void showErrorMessage(String message)
    {
        CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), message, false);

        new CustomDialog(CreateAccountActivity.this, null, dialog, true);
    }

    /**
     * Displays an error message to the user.
     *
     * @param stringRes     The string resource to populate the error dialog.
     */
    private void showErrorMessage(int stringRes)
    {
        showErrorMessage(context.getString(stringRes));

        loadingLayout.setVisibility(View.GONE);
        formLayout.setVisibility(View.VISIBLE);
    }

    /**
     * The dialog listener for converting an account to a full account.
     */
    private DialogListener accountConvertedDialogListener = new DialogListener()
    {
        @Override
        public void onButtonPressed(int which)
        {
            // There is only one button here, so we aren't switching.
            Util.loadIntencity(CreateAccountActivity.this);
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
    public void onServiceResponse(int statusCode, String response)
    {
        switch (statusCode)
        {
            case Constant.STATUS_CODE_SUCCESS_ACCOUNT_CREATION:

                int userId = Integer.parseInt(response);

                Util.loadIntencity(CreateAccountActivity.this, userId, Constant.ACCOUNT_TYPE_NORMAL, 0);

                break;

            case Constant.STATUS_CODE_SUCCESS_ACCOUNT_UPDATED:

                int storedUserId = Util.getSecurePreferencesUserId(context);
                Util.convertAccount(CreateAccountActivity.this, storedUserId);

                CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.success), context.getString(R.string.account_converted), false);

                new CustomDialog(CreateAccountActivity.this, accountConvertedDialogListener, dialog, false);

                break;

            case Constant.STATUS_CODE_FAILURE_EMAIL_ERROR:

                showErrorMessage(R.string.email_exists);

                break;

            case Constant.STATUS_CODE_FAILURE_ACCOUNT_UPDATE:
            case Constant.STATUS_CODE_FAILURE_ACCOUNT_CREATION:
            default:

                showErrorMessage(R.string.intencity_communication_error);

                break;
        }
    }
}