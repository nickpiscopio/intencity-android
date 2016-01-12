package com.intencity.intencity.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.CreateAccountActivity;
import com.intencity.intencity.activity.ForgotPasswordActivity;
import com.intencity.intencity.dialog.CustomDialog;
import com.intencity.intencity.dialog.Dialog;
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
public class LoginFragment extends android.support.v4.app.Fragment implements ServiceListener,
                                                                              DialogListener
{
    private EditText email;
    private EditText password;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email = (EditText) view.findViewById(R.id.edit_text_email);
        password = (EditText) view.findViewById(R.id.edit_text_password);

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

        return view;
    }

    View.OnClickListener signInListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            checkCredentials();
        }
    };

    View.OnClickListener tryIntencityListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            long uniqueNumber = new Date().getTime();

            String firstName = "Anonymous";
            String lastName = "User";
            final String email = lastName + uniqueNumber + "@intencityapp.com";
            String password = String.valueOf(uniqueNumber);

            new ServiceTask(new ServiceListener() {
                @Override
                public void onRetrievalSuccessful(String response)
                {
                    response = response.replaceAll("\"", "");
                    
                    if (response.equalsIgnoreCase(Constant.ACCOUNT_CREATED))
                    {
                        Util.loadIntencity(LoginFragment.this.getActivity(), email, Constant.ACCOUNT_TYPE_TRIAL);
                    }
                    else
                    {
                        showErrorMessage(context.getString(R.string.intencity_communication_error));
                    }
                }

                @Override
                public void onRetrievalFailed() { }
            }).execute(Constant.SERVICE_CREATE_ACCOUNT,
                                                        Constant.getAccountParameters(firstName,
                                                                                      lastName,
                                                                                      email,
                                                                                      password,
                                                                                      Constant.ACCOUNT_TYPE_TRIAL));
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
     * Starts the service to check if the credentials the user typed in are in the web database.
     */
    private void checkCredentials()
    {
        new ServiceTask(this).execute(Constant.SERVICE_VALIDATE_USER_CREDENTIALS,
                                      Constant.getValidateUserCredentialsServiceParameters(
                                              email.getText().toString(),
                                              password.getText().toString()));
    }

    /**
     * Displays the login error to the user.
     */
    private void showErrorMessage(String message)
    {
        Dialog dialog = new Dialog(context.getString(R.string.login_error_title),
                                   message,
                                   false);

        new CustomDialog(context, this, dialog);
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            JSONObject json = new JSONObject(response);

            String email = json.getString(Constant.COLUMN_EMAIL);
            String accountType = json.getString(Constant.COLUMN_ACCOUNT_TYPE);

            Util.loadIntencity(LoginFragment.this.getActivity(), email, accountType);
        }
        catch (JSONException e)
        {
            showErrorMessage(context.getString(R.string.login_error_message));
            Log.e(Constant.TAG, "Error parsing login data " + e.toString());
        }
    }

    @Override
    public void onRetrievalFailed()
    {
        showErrorMessage(context.getString(R.string.login_error_message));
    }

    @Override
    public void onButtonPressed(int which) { }
}