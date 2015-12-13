package com.intencity.intencity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.MainActivity;
import com.intencity.intencity.dialog.CustomDialog;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

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
        View view = inflater.inflate(R.layout.fragment_demo_login, container, false);

        email = (EditText) view.findViewById(R.id.edit_text_email);
        password = (EditText) view.findViewById(R.id.edit_text_password);

        Button signIn = (Button) view.findViewById(R.id.btn_sign_in);
        TextView tryIntencity = (TextView) view.findViewById(R.id.btn_try_intencity);

        signIn.setOnClickListener(signInListener);
        tryIntencity.setOnClickListener(tryIntencityListener);

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
            new ServiceTask(LoginFragment.this).execute(Constant.SERVICE_TRIAL_ACCOUNT,
                                                        Constant.getTrialAccountParameters(
                                                                new Date().getTime()));
        }
    };

    private void checkCredentials()
    {
        new ServiceTask(this).execute(Constant.SERVICE_VALIDATE_USER_CREDENTIALS,
                                      Constant.getValidateUserCredentialsServiceParameters(
                                              email.getText().toString(),
                                              password.getText().toString()));
    }

    /**
     * Saves the login information for Intencity.
     *
     * @param email         The email of the user.
     * @param accountType   The account type of the user.
     */
    private void loadIntencity(String email, String accountType)
    {
        SecurePreferences securePreferences = new SecurePreferences(context);
        SecurePreferences.Editor editor = securePreferences.edit();
        editor.putString(Constant.USER_ACCOUNT_EMAIL, email);
        editor.putString(Constant.USER_ACCOUNT_TYPE, accountType);
        editor.apply();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        getActivity().finish();
    }

    private void showErrorMessage()
    {
        new CustomDialog(context, this, context.getString(R.string.login_error_title), context.getString(R.string.login_error_message), false);
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            JSONObject json = new JSONObject(response);

            String email = json.getString(Constant.JSON_EMAIL);
            String accountType = json.getString(Constant.JSON_ACCOUNT_TYPE);

            loadIntencity(email, accountType);
        }
        catch (JSONException e)
        {
            showErrorMessage();
            Log.e(Constant.TAG, "Error parsing login data " + e.toString());
        }
    }

    @Override
    public void onRetrievalFailed()
    {
        showErrorMessage();
    }

    @Override
    public void onPositiveButtonPressed() { }

    @Override
    public void onNegativeButtonPressed() { }
}