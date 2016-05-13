package com.intencity.intencity.view.fragment;

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
import com.intencity.intencity.listener.LoadingListener;
import com.intencity.intencity.listener.NotificationListener;
import com.intencity.intencity.util.Constant;

/**
 * The Fitness Log Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class FitnessLogFragment extends android.support.v4.app.Fragment implements LoadingListener
{
    private final int LAYOUT_FITNESS_LOG = R.id.layout_fitness_log;

    private LinearLayout connectionIssue;

    private TextView tryAgain;

    private ProgressBar progressBar;

    private NotificationListener notificationListener;

    private RoutineFragment routineFragment;

    private FragmentManager manager;
    private FragmentHandler fragmentHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_intencity_fitness_log, container, false);

        connectionIssue = (LinearLayout) view.findViewById(R.id.layout_connection_issue);
        tryAgain = (TextView) view.findViewById(R.id.btn_try_again);
        tryAgain.setOnClickListener(tryConnectingAgainListener);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_loading);
        onStartLoading();

        manager = getFragmentManager();
        fragmentHandler = FragmentHandler.getInstance();

        onFinishedLoading(Constant.ID_FRAGMENT_ROUTINE);

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
            routineFragment.initRoutineCards();
        }
    };

    /**
     * Pushes the RoutineFragment on the stack.
     *
     * @param reload    A boolean value of whether or not to replace the existing fragment.
     */
    private void pushRoutineFragment(boolean reload)
    {
        routineFragment = new RoutineFragment();
        routineFragment.setListener(this);

        // Pushes the routine fragment onto the stack when everything has finished loading.
        fragmentHandler.pushFragment(manager, LAYOUT_FITNESS_LOG, routineFragment, Constant.FRAGMENT_ROUTINE, false,
                                     null, reload);

        stopLoading();
        removeConnectionIssue();
    }

    /**
     * Displays a connection issue message to the user.
     */
    private void showConnectionIssue()
    {
        connectionIssue.setVisibility(View.VISIBLE);
        tryAgain.setVisibility(View.VISIBLE);
    }

    /**
     * Removes the message to the user about the connection issue.
     */
    private void removeConnectionIssue()
    {
        connectionIssue.setVisibility(View.GONE);
        tryAgain.setVisibility(View.GONE);
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
                pushRoutineFragment(false);
                break;
            case Constant.ID_FRAGMENT_ROUTINE_RELOAD:
                pushRoutineFragment(true);
                break;
            case Constant.ID_FRAGMENT_EXERCISE_LIST:
                Bundle bundle = new Bundle();
                bundle.putString(Constant.BUNDLE_ROUTINE_NAME, routineFragment.getRoutineName());
                bundle.putParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST, routineFragment.getPreviousExercises());
                bundle.putInt(Constant.BUNDLE_EXERCISE_LIST_INDEX, routineFragment.getIndex());
                bundle.putInt(Constant.BUNDLE_ROUTINE_TYPE, routineFragment.getRoutineState());

                ExerciseListFragment fragment = new ExerciseListFragment();
                fragment.setLoadingListener(FitnessLogFragment.this);

                fragmentHandler.pushFragment(manager,
                                             LAYOUT_FITNESS_LOG,
                                             fragment,
                                             "",
                                             false,
                                             bundle,
                                             true);

                removeConnectionIssue();
                break;
            case (int) Constant.CODE_FAILED:
                showConnectionIssue();
                break;
            // We don't need anything to happen.
            // We just need the progress bar to stop.
            case Constant.ID_SAVE_EXERCISE_LIST:
            default:
                removeConnectionIssue();
                break;
        }

        stopLoading();
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
}