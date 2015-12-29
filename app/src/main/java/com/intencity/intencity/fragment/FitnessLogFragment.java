package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.DatabaseListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.task.GetExerciseTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.FragmentHandler;

import java.util.ArrayList;

/**
 * The Fitness Log Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class FitnessLogFragment extends android.support.v4.app.Fragment implements DatabaseListener
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_intencity_fitness_log, container, false);

        new GetExerciseTask(getContext(), this).execute();

        return view;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRetrievalSuccessful(String routineName, ArrayList<?> results, int index)
    {
        Bundle bundle = null;

        if (results.size() > 0)
        {
            bundle = new Bundle();
            bundle.putString(Constant.BUNDLE_ROUTINE_NAME, routineName);
            bundle.putParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST, (ArrayList<Exercise>)results);
            bundle.putInt(Constant.BUNDLE_EXERCISE_LIST_INDEX, index);
        }

        // TODO: There is an issue with this.
        // TODO: If Android recreates this view, the fragment manager will push 2 CardRoutineFragments

        FragmentManager manager = getFragmentManager();
        FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.pushFragment(manager, R.id.layout_fitness_log,
                                     new RoutineFragment(), "", false, bundle, false);
    }
}