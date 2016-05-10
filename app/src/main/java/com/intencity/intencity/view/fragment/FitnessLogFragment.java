package com.intencity.intencity.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.handler.FragmentHandler;
import com.intencity.intencity.listener.ExerciseListListener;
import com.intencity.intencity.listener.LoadingListener;
import com.intencity.intencity.listener.NotificationListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.RoutineSection;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * The Fitness Log Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class FitnessLogFragment extends android.support.v4.app.Fragment implements
                                                                                   LoadingListener,
                                                                                   ExerciseListListener
{
    private LinearLayout connectionIssue;

    private TextView tryAgain;

    private ProgressBar progressBar;

    private Context context;

    private int routineState;
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

    private ArrayList<RoutineSection> sections;

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

        initRoutineFragment();


        return view;
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
//            getMuscleGroups();
        }
    };

    private void initRoutineFragment()
    {
        onFinishedLoading(Constant.ID_FRAGMENT_ROUTINE);
    }



    /**
     * Repopulates the RoutineFragment's spinner
     *
     * @param values    The values to add to the spinner
     *
     */
//    private void repopulateSpinner(ArrayList<String> values)
//    {
//        routineFragment.setRoutineName(routineName);
//        routineFragment.setDisplayMuscleGroups(values);
//        routineFragment.setRecommendedRoutine(recommendedRoutine);
//        routineFragment.populateMuscleGroupSpinner();
//    }

    /**
     * Pushes the RoutineFragment on the stack.
     *
     * @param sections  The routine sections to display in the routine list view.
     */
    private void pushRoutineFragment(ArrayList<RoutineSection> sections)
    {
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList(Constant.BUNDLE_ROUTINE_SECTIONS, sections);
//        bundle.putString(Constant.BUNDLE_ROUTINE_NAME, routineName);
//        bundle.putParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST, previousExercises);
//        bundle.putInt(Constant.BUNDLE_EXERCISE_LIST_INDEX, index);
//        bundle.putInt(Constant.BUNDLE_RECOMMENDED_ROUTINE, recommendedRoutine);

        routineFragment = new RoutineFragment();
        routineFragment.setListener(this);

        // Pushes the routine fragment onto the stack when everything has finished loading.
        FragmentManager manager = getFragmentManager();
        FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.pushFragment(manager, R.id.layout_fitness_log, routineFragment, Constant.FRAGMENT_ROUTINE, false,
                                     null, false);

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
//                repopulateSpinner(displayMuscleGroups);
            }
            else
            {
                // Only add the saved exercises to the spinner because of the network issue.
                pushRoutineFragment(sections);
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
    public void onStartLoading()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishedLoading(int which)
    {
        switch (which)
        {
            case Constant.ID_FRAGMENT_ROUTINE:
                pushRoutineFragment(null);
                break;
            case Constant.ID_FRAGMENT_EXERCISE_LIST:

                Bundle bundle = new Bundle();
                bundle.putString(Constant.BUNDLE_ROUTINE_NAME, routineFragment.getRoutineName());
                bundle.putParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST,
                                              routineFragment.getPreviousExercises());
                bundle.putInt(Constant.BUNDLE_EXERCISE_LIST_INDEX, routineFragment.getIndex());
                bundle.putInt(Constant.BUNDLE_ROUTINE_TYPE, routineFragment.getRoutineState());

                exerciseListFragment = new ExerciseListFragment();
                exerciseListFragment.setLoadingListener(FitnessLogFragment.this);
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
            // We don't need anything to happen.
            // We just need the progress bar to stop.
            case Constant.ID_SAVE_EXERCISE_LIST:
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