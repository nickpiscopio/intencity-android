package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.Util;

import java.util.Date;

/**
 * The initial activity for Intencity.
 *
 * This was created to make the getting started process quicker because people don't like logging into applications.
 *
 * Created by Nick Piscopio on 8/13/16.
 */
public class GetStartedActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private final int RC_SIGN_IN = 10;

    private ProgressBar loadingProgressBar;

    private LinearLayout loginForm;

    private TextView terms;

    private Context context;

    private GoogleApiClient mGoogleApiClient;

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
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        TextView login = (TextView) findViewById(R.id.btn_login);
        TextView createAccount = (TextView) findViewById(R.id.btn_create_account);

        getStarted.setOnClickListener(getStartedClickListener);
        signInButton.setOnClickListener(signInWithGoogleClickListener);
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

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(GetStartedActivity.this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        Status code = result.getStatus();
        if (result.isSuccess())
        {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            // sign in.
        }
        else
        {
            showFailureMessage();
        }
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
     * The click listener for signing in with google.
     */
    View.OnClickListener signInWithGoogleClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
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
     *
     * @param title     The title of the message to display.
     * @param message   The message to display to the user.
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
        String email = lastName + createdDate + "@intencity.fit";
        final String encodedEmail = Util.hashValue(email);
        String password = String.valueOf(createdDate);

        new ServiceTask(new ServiceListener()
        {
            @Override
            public void onServiceResponse(int statusCode, String response)
            {
                switch (statusCode)
                {
                    case Constant.STATUS_CODE_SUCCESS_ACCOUNT_CREATION:
                        int userId = Integer.parseInt(response);

                        Util.loadIntencity(GetStartedActivity.this, userId, Constant.ACCOUNT_TYPE_MOBILE_TRIAL, createdDate);
                        break;

                    case Constant.STATUS_CODE_FAILURE_ACCOUNT_CREATION:
                    default:

                        showFailureMessage();
                        break;
                }
            }
        }).execute(Constant.SERVICE_CREATE_ACCOUNT,
                   Constant.getAccountParameters(firstName, lastName, encodedEmail, password,
                                                 Constant.ACCOUNT_TYPE_MOBILE_TRIAL));
    }

    /**
     * Displays a dialog to the user telling them the account couldn't be created.
     */
    private void showFailureMessage()
    {
        showMessage(context.getString(R.string.login_error_title),
                    context.getString(
                            R.string.intencity_communication_error));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        showFailureMessage();
    }
}