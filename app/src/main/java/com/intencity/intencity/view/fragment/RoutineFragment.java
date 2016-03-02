package com.intencity.intencity.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.LoadingListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;
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
public class RoutineFragment extends android.support.v4.app.Fragment
{
    private Spinner spinner;

    private Button start;

    private Context context;

    private ArrayList<String> displayMuscleGroups;

    private String routineName;
    private ArrayList<Exercise> previousExercises;
    private int index;
    private int recommendedRoutine;

    private LoadingListener listener;

    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        spinner = (Spinner) view.findViewById(R.id.spinner_routine);

        start = (Button) view.findViewById(R.id.button_start);
        start.setOnClickListener(startClickListener);

        Bundle bundle = getArguments();

        if (bundle != null)
        {
            displayMuscleGroups = bundle.getStringArrayList(Constant.BUNDLE_DISPLAY_MUSCLE_GROUPS);
            routineName = bundle.getString(Constant.BUNDLE_ROUTINE_NAME);
            previousExercises = bundle.getParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST);
            index = bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX);
            recommendedRoutine = bundle.getInt(Constant.BUNDLE_RECOMMENDED_ROUTINE);
        }

        context = getContext();

        SecurePreferences securePreferences = new SecurePreferences(context);
        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        populateMuscleGroupSpinner();

        return view;
    }

    /**
     * The service listener for setting the routine.
     */
    public ServiceListener routineServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            new ServiceTask(exerciseServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                             Constant.generateStoredProcedureParameters(
                                                                     Constant.STORED_PROCEDURE_GET_EXERCISES_FOR_TODAY,
                                                                     email));
        }

        @Override
        public void onRetrievalFailed()
        {
            listener.onFinishedLoading(Constant.CODE_FAILED_REPOPULATE);
        }
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

            // We are adding a warm-up to the exercise list.
            Exercise warmUp = getNewExercise(context.getString(R.string.warm_up),
                                             Constant.RETURN_NULL,
                                             Constant.RETURN_NULL,
                                             Constant.RETURN_NULL,
                                             Constant.RETURN_NULL);
            warmUp.setDescription(context.getString(R.string.warm_up_description));
            exercises.add(warmUp);

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

                    // Add all the exercises from the database to the array list.
                    exercises.add(getNewExercise(name, weight, reps, duration, difficulty));
                }

                previousExercises = exercises;
                index = 0;

                listener.onFinishedLoading(Constant.ID_FRAGMENT_EXERCISE_LIST);
            }
            catch (JSONException e)
            {
                e.printStackTrace();

                listener.onFinishedLoading(Constant.CODE_FAILED_REPOPULATE);
            }

            // We are adding a stretch to the exercise list.
            Exercise stretch = getNewExercise(context.getString(R.string.stretch),
                                              Constant.RETURN_NULL,
                                              Constant.RETURN_NULL,
                                              Constant.RETURN_NULL,
                                              Constant.RETURN_NULL);
            stretch.setDescription(context.getString(R.string.stretch_description));
            exercises.add(stretch);
        }

        @Override
        public void onRetrievalFailed()
        {
            listener.onFinishedLoading(Constant.CODE_FAILED_REPOPULATE);
        }
    };

    /**
     * Get a new exercise.
     *
     * @param name          The name of the exercise.
     * @param weight        The weight the user did last time.
     * @param reps          The amount of reps the user did.
     * @param duration      The duration the user did.
     *                      Usually in 00:00:00 format.
     * @param difficulty    The difficulty from 1-10.
     *
     * @return  The new exercise.
     */
    private Exercise getNewExercise(String name, String weight, String reps, String duration, String difficulty)
    {
        Set set = new Set();
        set.setWeight(weight.equalsIgnoreCase(Constant.RETURN_NULL) ? (int) Constant.CODE_FAILED :
                              Float.valueOf(weight));
        set.setReps(reps.equalsIgnoreCase(Constant.RETURN_NULL) ? 0 : Integer.valueOf(reps));
        set.setDuration(duration);
        set.setDifficulty(difficulty.equalsIgnoreCase(Constant.RETURN_NULL) ? (int) Constant.CODE_FAILED :
                                  Integer.valueOf(difficulty));

        ArrayList<Set> sets = new ArrayList<>();
        sets.add(set);

        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setSets(sets);

        return exercise;
    }

    /**
     * Populates the muscle spinner.
     */
    public void populateMuscleGroupSpinner()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner, displayMuscleGroups);

        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setSelection(recommendedRoutine);
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
                listener.onFinishedLoading(Constant.ID_FRAGMENT_EXERCISE_LIST);
            }
            else
            {
                routineName = routineSelection;

                String routine = String.valueOf(spinnerPosition + 1);
                String storedProcedureParameters = Constant.generateStoredProcedureParameters(
                        Constant.STORED_PROCEDURE_SET_CURRENT_MUSCLE_GROUP, email, routine);

                listener.onStartLoading();

                new ServiceTask(routineServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                                storedProcedureParameters);
            }
        }
    };

    /**
     * Setters and getters for the RoutineFragment.
     */
    public void setListener(LoadingListener listener)
    {
        this.listener = listener;
    }

    public String getRoutineName()
    {
        return routineName;
    }

    public void setRoutineName(String routineName)
    {
        this.routineName = routineName;
    }

    public ArrayList<Exercise> getPreviousExercises()
    {
        return previousExercises;
    }

    public int getIndex()
    {
        return index;
    }

    public void setRecommendedRoutine(int recommendedRoutine)
    {
        this.recommendedRoutine = recommendedRoutine;
    }

    public void setDisplayMuscleGroups(ArrayList<String> displayMuscleGroups)
    {
        this.displayMuscleGroups = displayMuscleGroups;
    }
}