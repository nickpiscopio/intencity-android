package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;

import org.json.JSONObject;

/**
 * The Fitness Guru Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class FitnessGuruFragment extends android.support.v4.app.Fragment implements ServiceListener
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_intencity_fitness_guru, container, false);

//        new ServiceTask(this).execute(Constant.SERVICE_VALIDATE_USER_CREDENTIALS,
//                                      Constant.getValidateUserCredentialsServiceParameters(
//                                              email.getText().toString(),
//                                              password.getText().toString()));

        return view;
    }

    @Override
    public void onRetrievalSuccessful(JSONObject response)
    {

    }

    @Override
    public void onRetrievalFailed()
    {

    }
}