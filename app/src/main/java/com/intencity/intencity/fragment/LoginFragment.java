package com.intencity.intencity.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.CreateAccountActivity;
import com.intencity.intencity.activity.ForgotPasswordActivity;
import com.intencity.intencity.activity.TermsActivity;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * The Login Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class LoginFragment extends android.support.v4.app.Fragment implements ServiceListener
{
    private EditText email;
    private EditText password;

    private ProgressBar loadingProgressBar;

    private LinearLayout loginForm;

    private CheckBox termsCheckBox;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email = (EditText) view.findViewById(R.id.edit_text_email);
        password = (EditText) view.findViewById(R.id.edit_text_password);

        loadingProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_loading);

        loginForm = (LinearLayout) view.findViewById(R.id.linear_layout_login_form);

        termsCheckBox = (CheckBox) view.findViewById(R.id.check_box_terms);

        Button signIn = (Button) view.findViewById(R.id.btn_sign_in);
        TextView tryIntencity = (TextView) view.findViewById(R.id.btn_try_intencity);
        TextView forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        TextView createAccount = (TextView) view.findViewById(R.id.btn_create_account);

        password.setTypeface(Typeface.DEFAULT);

        signIn.setOnClickListener(signInListener);
        tryIntencity.setOnClickListener(tryIntencityListener);
        forgotPassword.setOnClickListener(forgotPasswordListener);
        createAccount.setOnClickListener(createAccountListener);

        context = getContext();

        // Sets the progress bar color.
        Util.setProgressBarColor(context, loadingProgressBar);

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

        return view;
    }

    View.OnClickListener signInListener = new View.OnClickListener()
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
            else if (!termsCheckBox.isChecked())
            {
                showMessage(context.getString(R.string.generic_error),
                            getString(R.string.accept_terms));
            }
            else
            {
                checkCredentials(userEmail, userPassword);
            }
        }
    };

    View.OnClickListener tryIntencityListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (termsCheckBox.isChecked())
            {
                new CustomDialog(context, trialDialogListener, new CustomDialogContent(getString(R.string.trial_account_title), getString(R.string.trial_account_message), true), true);
            }
            else
            {
               showMessage(context.getString(R.string.generic_error),
                           getString(R.string.accept_terms));
            }
        }
    };

    View.OnClickListener forgotPasswordListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            startActivity(new Intent(context, ForgotPasswordActivity.class));
        }
    };

    View.OnClickListener createAccountListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            startActivity(new Intent(context, CreateAccountActivity.class));
        }
    };

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
                startActivity(new Intent(LoginFragment.this.getContext(), TermsActivity.class));
            }
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
                                              email, password));
    }

    /**
     * Displays the login error to the user.
     */
    private void showMessage(String title, String message)
    {
        loadingProgressBar.setVisibility(View.GONE);
        loginForm.setVisibility(View.VISIBLE);

        Util.showMessage(context, title, message);
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            JSONObject json = new JSONObject(response);

            String email = json.getString(Constant.COLUMN_EMAIL);
            String accountType = json.getString(Constant.COLUMN_ACCOUNT_TYPE);

            Util.loadIntencity(LoginFragment.this.getActivity(), email, accountType, 0);
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
     * The dialog listener for the "Try Intencity" button.
     */
    private DialogListener trialDialogListener = new DialogListener()
    {
        @Override
        public void onButtonPressed(int which)
        {
            switch (which)
            {
                case Constant.POSITIVE_BUTTON: // Create Trial Account
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
                                Util.loadIntencity(LoginFragment.this.getActivity(), email, Constant.ACCOUNT_TYPE_MOBILE_TRIAL, createdDate);
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
                    break;
                default:
                    break;
            }
        }
    };
}