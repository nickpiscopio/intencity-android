package com.intencity.intencity.activity;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intencity.intencity.R;
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
    private LinearLayout loadingLayout;
    private LinearLayout formLayout;

    private ProgressBar progressBar;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText confirmEmailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private CheckBox termsCheckBox;

    private Button createAccount;

    private Context context;

    @Override protected void onCreate(Bundle savedInstanceState)
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

        termsCheckBox = (CheckBox) findViewById(R.id.check_box_terms);

        createAccount = (Button) findViewById(R.id.btn_create_account);

        context = getApplicationContext();

        // Sets the progress bar color.
        Util.setProgressBarColor(context, progressBar);

        passwordEditText.setTypeface(Typeface.DEFAULT);
        confirmPasswordEditText.setTypeface(Typeface.DEFAULT);

        SpannableStringBuilder builder = new SpannableStringBuilder();

        String[] checkBoxString = termsCheckBox.getText().toString().split("@");
        String termsString = checkBoxString[1];
        SpannableString redSpannable = new SpannableString(termsString);
        redSpannable
                .setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary)),
                         0, termsString.length(), 0);
        builder.append(checkBoxString[0]);
        builder.append(redSpannable);
        builder.append(checkBoxString[2]);

        termsCheckBox.setText(builder, TextView.BufferType.SPANNABLE);
        termsCheckBox.setOnClickListener(termsClickListener);

        createAccount.setOnClickListener(createAccountClickListener);

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
    private View.OnClickListener termsClickListener = new View.OnClickListener()
    {
        @Override public void onClick(View v)
        {
            // The terms checkbox gets checked first, then the click listener gets called,
            // so we want to start the TermsActivity only if the checkbox is checked.
            if (termsCheckBox.isChecked())
            {
                startActivity(new Intent(CreateAccountActivity.this, TermsActivity.class));
            }
        }
    };

    /**
     * The click listener for the create account button.
     */
    private View.OnClickListener createAccountClickListener = new View.OnClickListener()
    {
        @Override public void onClick(View v)
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
            // Check to see if the user has accepted the terms.
            else if (!termsCheckBox.isChecked())
            {
                showErrorMessage(context.getString(R.string.accept_terms));
            }
            else
            {
                loadingLayout.setVisibility(View.VISIBLE);
                formLayout.setVisibility(View.GONE);

                new ServiceTask(CreateAccountActivity.this).execute(
                        Constant.SERVICE_CREATE_ACCOUNT,
                        Constant.getAccountParameters(firstName, lastName, replacePlus(email), password, Constant.ACCOUNT_TYPE_NORMAL));
            }
        }
    };

    /**
     * Replaces the '+' character in a String of text.
     * This is so we can create an account on the server with an email that has a '+' in it.
     *
     * @param text  The text to search.
     *
     * @return  The new String with its replaced character.
     */
    private String replacePlus(String text)
    {
        return text.replaceAll("\\+", "%2B");
    }

    /**
     * Displays the login error to the user.
     */
    private void showErrorMessage(String message)
    {
        CustomDialogContent
                dialog = new CustomDialogContent(context.getString(R.string.generic_error), message, false);

        new CustomDialog(CreateAccountActivity.this, null, dialog, true);
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
        response = response.replaceAll("\"", "");

        if (response.equalsIgnoreCase(Constant.EMAIL_EXISTS))
        {
            loadingLayout.setVisibility(View.GONE);
            formLayout.setVisibility(View.VISIBLE);

            showErrorMessage(context.getString(R.string.email_exists));
        }
        else if (response.equalsIgnoreCase(Constant.ACCOUNT_CREATED))
        {
            Util.loadIntencity(this, emailEditText.getText().toString(), Constant.ACCOUNT_TYPE_NORMAL, 0);
        }
        else
        {
            loadingLayout.setVisibility(View.GONE);
            formLayout.setVisibility(View.VISIBLE);

            showErrorMessage(context.getString(R.string.intencity_communication_error));
        }
    }

    @Override
    public void onRetrievalFailed()
    {
        loadingLayout.setVisibility(View.GONE);
        formLayout.setVisibility(View.VISIBLE);

        showErrorMessage(context.getString(R.string.intencity_communication_error));
    }
}