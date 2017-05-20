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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * The Login Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class LoginActivity extends AppCompatActivity implements ServiceListener
{
    private EditText email;
    private EditText password;

    private ProgressBar loadingProgressBar;

    private LinearLayout loginForm;

    private TextView terms;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.edit_text_email);
        password = (EditText) findViewById(R.id.edit_text_password);

        loadingProgressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);

        loginForm = (LinearLayout) findViewById(R.id.linear_layout_login_form);

        terms = (TextView) findViewById(R.id.terms);

        Button signIn = (Button) findViewById(R.id.btn_sign_in);
        TextView forgotPassword = (TextView) findViewById(R.id.forgot_password);
        TextView createAccount = (TextView) findViewById(R.id.btn_create_account);

        password.setTypeface(Typeface.DEFAULT);

        signIn.setOnClickListener(signInListener);
        forgotPassword.setOnClickListener(forgotPasswordListener);
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

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    private View.OnClickListener signInListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();

            if (userEmail.length() < 1 || userPassword.length() < 1)
            {
                showMessage(context.getString(R.string.generic_error),
                            getString(R.string.fill_in_fields));
            }
            else
            {

                String encodedEmail = Util.hashValue(userEmail);
                checkCredentials(encodedEmail, userPassword);
            }
        }
    };

    private View.OnClickListener forgotPasswordListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            startActivity(new Intent(context, ForgotPasswordActivity.class));
        }
    };

    private View.OnClickListener createAccountListener = new View.OnClickListener()
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
     * Starts the service to check if the credentials the user typed in are in the web database.
     */
    private void checkCredentials(String email, String password)
    {
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginForm.setVisibility(View.GONE);

        new ServiceTask(this).execute(Constant.SERVICE_VALIDATE_USER_CREDENTIALS,
                                      Constant.getValidateUserCredentialsServiceParameters(
                                              email, Util.replaceApostrophe(password)));
    }

    /**
     * Displays the login error to the user.
     */
    private void showMessage(String title, String message)
    {
        loadingProgressBar.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);

        Util.showMessage(LoginActivity.this, title, message);
    }

    @Override
    public void onServiceResponse(int statusCode, String response)
    {
        switch (statusCode)
        {
            case Constant.STATUS_CODE_SUCCESS_CREDENTIALS_VALID:

                try
                {
                    JSONObject json = new JSONObject(response);

                    int userId = Integer.parseInt(json.getString(Constant.COLUMN_ID));
                    String accountType = json.getString(Constant.COLUMN_ACCOUNT_TYPE);

                    Util.loadIntencity(LoginActivity.this, userId, accountType, 0);
                }
                catch (JSONException e)
                {
                    showLoginError();

                    Log.e(Constant.TAG, "Error parsing login data " + e.toString());
                }

                break;

            case Constant.STATUS_CODE_FAILURE_CREDENTIALS_EMAIL_INVALID:
            case Constant.STATUS_CODE_FAILURE_CREDENTIALS_PASSWORD_INVALID:
            default:

                showLoginError();

                break;
        }
    }

    /**
     * Displays a message to the user telling them a login error.
     */
    private void showLoginError()
    {
        showMessage(context.getString(R.string.login_error_title),
                    context.getString(R.string.login_error_message));

        loadingProgressBar.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);
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
}