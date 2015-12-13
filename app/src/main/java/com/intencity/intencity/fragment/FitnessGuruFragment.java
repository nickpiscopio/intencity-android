package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        Fragment routineFragment = new RoutineFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.layout_fitness_guru, routineFragment).commit();

        return view;
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            ArrayList<String> displayMuscleGroups = new ArrayList<>();

            JSONArray array = new JSONArray(response);

            int length = array.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject object = array.getJSONObject(i);

                displayMuscleGroups.add(object.getString(Constant.COLUMN_DISPLAY_NAME));
            }
        }
        catch (JSONException e)
        {
            Log.e(Constant.TAG, "Error parsing muscle group data " + e.toString());
        }
    }

    @Override
    public void onRetrievalFailed()
    {

    }
}