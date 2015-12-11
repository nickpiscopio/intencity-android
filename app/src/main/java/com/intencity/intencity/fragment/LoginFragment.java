package com.intencity.intencity.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.Date;

public class LoginFragment extends android.support.v4.app.Fragment implements ServiceListener,
                                                                              DialogListener
{
    private EditText email;
    private EditText password;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_demo_sign_in, container, false);

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

    private void loadIntencity()
    {
        SharedPreferences prefs = context.getSharedPreferences(Constant.SHARED_PREFERENCES,
                                                               Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constant.DEMO_FINISHED, true);
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
        String[] credentialsResponse = response.replaceAll("\"|\n","").split(Constant.RESPONSE_DELIMITER);

        if (credentialsResponse[0].equals(Constant.LOG_IN_VALID) ||
            credentialsResponse[0].equals(Constant.ACCOUNT_CREATED))
        {
            loadIntencity();
        }
        else
        {
            showErrorMessage();
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