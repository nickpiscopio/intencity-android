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

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.FragmentHandler;
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
public class RoutineFragment extends android.support.v4.app.Fragment
{
    private Spinner spinner;

    private Button start;

    private Context context;

    private ArrayList<String> displayMuscleGroups;

    private String email;

    private String routineName;
    private ArrayList<Exercise> previousExercises;
    private int index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        spinner = (Spinner) view.findViewById(R.id.spinner_routine);

        start = (Button) view.findViewById(R.id.button_start);
        start.setOnClickListener(startClickListener);

        context = getContext();

        SecurePreferences securePreferences = new SecurePreferences(context);

        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        Bundle bundle = getArguments();

        if (bundle != null)
        {
            routineName = bundle.getString(Constant.BUNDLE_ROUTINE_NAME);
            previousExercises = bundle.getParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST);
            index = bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX);
        }

        new ServiceTask(spinnerServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                        Constant.getStoredProcedure(
                                                                Constant.STORED_PROCEDURE_GET_ALL_DISPLAY_MUSCLE_GROUPS,
                                                                email));

        return view;
    }

    /**
     * The service listener for populating the spinner with routine names.
     *
     * The routine name that gets set is the recommended routine name.
     */
    public ServiceListener spinnerServiceListener = new ServiceListener()
    {
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

                // Add the previous exercise to the list.
                if (previousExercises != null && previousExercises.size() > 0)
                {
                    displayMuscleGroups.add(getString(R.string.routine_continue));
                    recommendedRoutine = displayMuscleGroups.size() - 1;
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
    };

    /**
     * The service listener for setting the routine.
     */
    public ServiceListener routineServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            new ServiceTask(exerciseServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                             Constant.getStoredProcedure(
                                                                     Constant.STORED_PROCEDURE_GET_EXERCISES_FOR_TODAY,
                                                                     email));
        }

        @Override
        public void onRetrievalFailed() { }
    };

    /**
     * The service listener for getting the exercise list.
     */
    public ServiceListener exerciseServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            ArrayList<Exercise> exercises = new ArrayList<>();

            try
            {
                JSONArray array = new JSONArray(response);

                int length = array.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject object = array.getJSONObject(i);

                    String name = object.getString(Constant.COLUMN_EXERCISE_NAME);
                    String weight = object.getString(Constant.COLUMN_EXERCISE_WEIGHT);
                    String reps = object.getString(Constant.COLUMN_EXERCISE_REPS);
                    String duration = object.getString(Constant.COLUMN_EXERCISE_DURATION);
                    String difficulty = object.getString(Constant.COLUMN_EXERCISE_DIFFICULTY);

                    Set set = new Set();
                    set.setWeight(
                            weight.equalsIgnoreCase(Constant.RETURN_NULL) ? Constant.CODE_FAILED :
                                    Integer.valueOf(weight));
                    set.setReps(reps.equalsIgnoreCase(Constant.RETURN_NULL) ? Constant.CODE_FAILED :
                                        Integer.valueOf(reps));
                    set.setDuration(duration);
                    set.setDifficulty(difficulty.equalsIgnoreCase(Constant.RETURN_NULL) ?
                                              Constant.CODE_FAILED : Integer.valueOf(difficulty));

                    ArrayList<Set> sets = new ArrayList<>();
                    sets.add(set);

                    Exercise exercise = new Exercise();
                    exercise.setName(name);
                    exercise.setSets(sets);

                    // Add all the exercises from the database to the array list.
                    exercises.add(exercise);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            pushCardFragmentExercise(exercises, 0);
        }

        @Override
        public void onRetrievalFailed() { }
    };

    private void pushCardFragmentExercise(ArrayList<Exercise> exercises, int index)
    {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.BUNDLE_ROUTINE_NAME, routineName);
        bundle.putParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST, exercises);
        bundle.putInt(Constant.BUNDLE_EXERCISE_LIST_INDEX, index);

        FragmentHandler.getInstance().pushFragment(getFragmentManager(),
                                                   R.id.layout_fitness_log,
                                                   new ExerciseListFragment(), "", false, bundle,
                                                   true);
    }

    /**
     * Populates the muscle spinner.
     *
     * @param recommended   Sets the spinner to the recommended selection.
     */
    private void populateMuscleGroupSpinner(int recommended)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner, displayMuscleGroups);

        spinner.setAdapter(adapter);
        spinner.setSelection(recommended);
    }

    /**
     * The click listener for the start button.
     */
    private View.OnClickListener startClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int spinnerPosition = spinner.getSelectedItemPosition();

            String routineSelection = spinner.getItemAtPosition(spinnerPosition).toString();

            // If the user selects to continue from the last routine he or she chose.
            if (routineSelection.equals(getString(R.string.routine_continue)))
            {
                pushCardFragmentExercise(previousExercises, index);
            }
            else
            {
                routineName = routineSelection;

                String routine = String.valueOf(spinnerPosition + 1);
                String storedProcedureParameters = Constant.getStoredProcedure(
                        Constant.STORED_PROCEDURE_SET_CURRENT_MUSCLE_GROUP, email, routine);

                new ServiceTask(routineServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                                storedProcedureParameters);
            }
        }
    };
}