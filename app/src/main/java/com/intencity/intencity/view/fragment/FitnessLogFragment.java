package com.intencity.intencity.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.DatabaseListener;
import com.intencity.intencity.listener.ExerciseListListener;
import com.intencity.intencity.listener.LoadingListener;
import com.intencity.intencity.listener.NotificationListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.task.GetExerciseTask;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.handler.FragmentHandler;
import com.intencity.intencity.util.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The Fitness Log Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class FitnessLogFragment extends android.support.v4.app.Fragment implements DatabaseListener,
                                                                                   LoadingListener,
                                                                                   ExerciseListListener
{
    private LinearLayout connectionIssue;

    private TextView tryAgain;

    private ProgressBar progressBar;

    private Context context;

    private String routineName;
    private ArrayList<Exercise> previousExercises;
    private int index;
    private int recommendedRoutine;

    private RoutineFragment routineFragment;

    private String email;

    private boolean pushedTryAgain = false;

    private NotificationListener notificationListener;

    private ExerciseListFragment exerciseListFragment;

    private ExerciseListListener mainActivityExerciseListListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_intencity_fitness_log, container, false);

        connectionIssue = (LinearLayout) view.findViewById(R.id.image_view_connection_issue);
        tryAgain = (TextView) view.findViewById(R.id.btn_try_again);
        tryAgain.setOnClickListener(tryConnectingAgainListener);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_loading);
        onStartLoading();

        context = getContext();

        new GetExerciseTask(context, this).execute();

        return view;
    }

    /**
     * Starts the service to get the muscle groups from the web server.
     */
    private void getMuscleGroups()
    {
        new ServiceTask(muscleGroupServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                            Constant.generateStoredProcedureParameters(
                                                                    Constant.STORED_PROCEDURE_GET_ALL_DISPLAY_MUSCLE_GROUPS,
                                                                    email));
    }

    /**
     * The listener to try to get the exercise data again.
     */
    private View.OnClickListener tryConnectingAgainListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            pushedTryAgain = true;
            onStartLoading();
            getMuscleGroups();
        }
    };

    /**
     * The service listener for populating the spinner with routine names.
     *
     * The routine name that gets set is the recommended routine name.
     */
    public ServiceListener muscleGroupServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            try
            {
                recommendedRoutine = 0;

                ArrayList<String> displayMuscleGroups = new ArrayList<>();

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
                    // Need the context here because we haven't started this fragment yet.
                    displayMuscleGroups.add(getString(R.string.routine_continue));
                    recommendedRoutine = displayMuscleGroups.size() - 1;
                }

                if (pushedTryAgain)
                {
                    // Repopulate the spinner if the user gets their connection back
                    repopulateSpinner(displayMuscleGroups);

                    removeConnectionIssueMessage();
                    stopLoading();
                }
                else
                {
                    pushRoutineFragment(displayMuscleGroups);
                }
            }
            catch (JSONException e)
            {
                Log.e(Constant.TAG, "Error parsing muscle group data " + e.toString());

                onFinishedLoading(
                        pushedTryAgain ? Constant.CODE_FAILED_REPOPULATE : (int) Constant.CODE_FAILED);
            }
        }

        @Override
        public void onRetrievalFailed()
        {
            onFinishedLoading(pushedTryAgain ? Constant.CODE_FAILED_REPOPULATE : (int) Constant.CODE_FAILED);
        }
    };

    /**
     * Repopulates the RoutineFragment's spinner
     *
     * @param values    The values to add to the spinner
     *
     */
    private void repopulateSpinner(ArrayList<String> values)
    {
        routineFragment.setRoutineName(routineName);
        routineFragment.setDisplayMuscleGroups(values);
        routineFragment.setRecommendedRoutine(recommendedRoutine);
        routineFragment.populateMuscleGroupSpinner();
    }

    /**
     * Pushes the RoutineFragment on the stack.
     *
     * @param displayMuscleGroups   The display muscle groups to add to the spinner
     *                              in the RoutineFragment.
     */
    private void pushRoutineFragment(ArrayList<String> displayMuscleGroups)
    {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constant.BUNDLE_DISPLAY_MUSCLE_GROUPS, displayMuscleGroups);
        bundle.putString(Constant.BUNDLE_ROUTINE_NAME, routineName);
        bundle.putParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST, previousExercises);
        bundle.putInt(Constant.BUNDLE_EXERCISE_LIST_INDEX, index);
        bundle.putInt(Constant.BUNDLE_RECOMMENDED_ROUTINE, recommendedRoutine);

        routineFragment = new RoutineFragment();
        routineFragment.setListener(this);

        // Pushes the routine fragment onto the stack when everything has finished loading.
        FragmentManager manager = getFragmentManager();
        FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.pushFragment(manager, R.id.layout_fitness_log, routineFragment, Constant.FRAGMENT_ROUTINE, false,
                                     bundle, false);

        stopLoading();
    }

    /**
     * Checks if there are previous exercises and repopulates the spinner.
     *
     * @param hasRoutineFragment    Boolean value of if the RoutineFragment has already been added.
     */
    private void checkPreviousExercises(Boolean hasRoutineFragment)
    {
        if (previousExercises != null && previousExercises.size() > 0)
        {
            ArrayList<String> displayMuscleGroups = new ArrayList<>();
            // Need the context here because we haven't started this fragment yet.
            displayMuscleGroups.add(getString(R.string.routine_continue));
            recommendedRoutine = displayMuscleGroups.size() - 1;

            if (hasRoutineFragment)
            {
                // Repopulate the spinner since we lost the connection
                repopulateSpinner(displayMuscleGroups);
            }
            else
            {
                // Only add the saved exercises to the spinner because of the network issue.
                pushRoutineFragment(displayMuscleGroups);
            }
        }
    }

    /**
     * Removes the message to the user about the connection issue.
     */
    private void removeConnectionIssueMessage()
    {
        connectionIssue.setVisibility(View.GONE);
    }

    /**
     * Removes the progress bar from the UI.
     */
    private void stopLoading()
    {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRetrievalSuccessful(String routineName, ArrayList<?> results, int index)
    {
        this.routineName = routineName;
        previousExercises = (ArrayList<Exercise>)results;
        this.index = index;

        SecurePreferences securePreferences = new SecurePreferences(context);
        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        getMuscleGroups();
    }

    @Override
    public void onStartLoading()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishedLoading(int which)
    {
        switch (which)
        {
            case Constant.ID_FRAGMENT_EXERCISE_LIST:

                Bundle bundle = new Bundle();
                bundle.putString(Constant.BUNDLE_ROUTINE_NAME, routineFragment.getRoutineName());
                bundle.putParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST,
                                              routineFragment.getPreviousExercises());
                bundle.putInt(Constant.BUNDLE_EXERCISE_LIST_INDEX, routineFragment.getIndex());

                exerciseListFragment = new ExerciseListFragment();
                exerciseListFragment.setFitnessLogListener(FitnessLogFragment.this);

                FragmentHandler.getInstance().pushFragment(getFragmentManager(),
                                                           R.id.layout_fitness_log,
                                                           exerciseListFragment, "", false,
                                                           bundle, true);

                removeConnectionIssueMessage();

                break;
            case Constant.CODE_FAILED_REPOPULATE:
                connectionIssue.setVisibility(View.VISIBLE);
                checkPreviousExercises(true);
                break;
            case (int) Constant.CODE_FAILED:
                connectionIssue.setVisibility(View.VISIBLE);
                checkPreviousExercises(false);
                break;
            default:
                break;
        }

        stopLoading();
    }

    /**
     * Sets the MainActivity's ExerciseListListener,
     * so we can call it when the workout is completed.
     *
     * @param listener  The listener to set the MainActivity's ExerciseListListener to.
     */
    public void setMainActivityExerciseListListener(ExerciseListListener listener)
    {
        this.mainActivityExerciseListListener = listener;
    }

    /**
     * Sets the MainActivity's NotificationListener.
     *
     * @param notificationListener  The listener to set the MainActivity's NotificationListener to.
     */
    public void setNotificationListener(NotificationListener notificationListener)
    {
        this.notificationListener = notificationListener;
    }

    @Override
    public void onCompletedWorkout()
    {
        mainActivityExerciseListListener.onCompletedWorkout();
    }
}