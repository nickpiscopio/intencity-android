package com.intencity.intencity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The Routine Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class RoutineFragment extends android.support.v4.app.Fragment implements ServiceListener
{
    private Spinner spinner;

    private Button start;

    private Context context;

    private ArrayList<String> displayMuscleGroups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_card_routine, container, false);

        spinner = (Spinner) view.findViewById(R.id.spinner_routine);

        start = (Button) view.findViewById(R.id.button_start);
        start.setOnClickListener(startClickListener);

        context = getContext();

        SecurePreferences securePreferences = new SecurePreferences(context);

        String email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        new ServiceTask(this).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.getStoredProcedure(
                                              Constant.STORED_PROCEDURE_GET_ALL_DISPLAY_MUSCLE_GROUPS,
                                              email));

        return view;
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            int recommendedRoutine = 0;

            displayMuscleGroups = new ArrayList<>();

            JSONArray array = new JSONArray(response);

            int length = array.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject object = array.getJSONObject(i);

                String routine = object.getString(Constant.COLUMN_DISPLAY_NAME);
                String recommended = object.getString(Constant.COLUMN_CURRENT_MUSCLE_GROUP);

                // Add all the muscle groups from the database to the array list.
                displayMuscleGroups.add(routine);

                if (routine.equals(recommended))
                {
                    // Get the index of the recommended routine which is
                    // the COLUMN_CURRENT_MUSCLE_GROUP from the database.
                    recommendedRoutine = i;
                }
            }

            populateMuscleGroupSpinner(recommendedRoutine);
        }
        catch (JSONException e)
        {
            Log.e(Constant.TAG, "Error parsing muscle group data " + e.toString());
        }
    }

    @Override
    public void onRetrievalFailed() { }

    /**
     * Pupulates the muscle spinner.
     *
     * @param recommended   Sets the spinner to the recommended selection.
     */
    private void populateMuscleGroupSpinner(int recommended)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner, displayMuscleGroups);

        spinner.setAdapter(adapter);
        spinner.setSelection(recommended);
//
//        // Spinner click listener
//        spinner.setOnItemSelectedListener(startClickListener);
    }

    /**
     * The click listener for the start button.
     */
    private View.OnClickListener startClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String string = displayMuscleGroups.get(spinner.getSelectedItemPosition());
            Toast.makeText(getActivity(), "Selected: " + string, Toast.LENGTH_SHORT).show();
        }
    };
}