package com.intencity.intencity.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.DatabaseListener;
import com.intencity.intencity.listener.FragmentListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.task.GetExerciseTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.FragmentHandler;

import java.util.ArrayList;

/**
 * The Fitness Guru Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class FitnessLogFragment extends android.support.v4.app.Fragment implements DatabaseListener, FragmentListener
{
    private final String PROPERTY = "scrollY";

    private ScrollView scrollView;

    private LinearLayout fitnessLogLayout;

    private FragmentManager manager;
    private FragmentHandler fragmentHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_intencity_fitness_log, container, false);

        scrollView = (ScrollView) view.findViewById(R.id.scroll_view_fitness_log);
        fitnessLogLayout = (LinearLayout) view.findViewById(R.id.layout_fitness_log);

        new GetExerciseTask(getContext(), this).execute();

        return view;
    }

    @Override
    public void onFragmentAdded()
    {
        int scrollDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

        // Scroll to the bottom of the scrollview.
        ObjectAnimator.ofInt(scrollView, PROPERTY, fitnessLogLayout.getHeight()).setDuration(scrollDuration).start();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRetrievalSuccessful(ArrayList<?> results, int index)
    {
        Bundle bundle = null;

        if (results.size() > 0)
        {
            bundle = new Bundle();
            bundle.putParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST, (ArrayList<Exercise>)results);
            bundle.putInt(Constant.BUNDLE_EXERCISE_LIST_INDEX, index);
        }

        manager = getFragmentManager();
        fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.setFragmentListener(this);
        fragmentHandler.pushFragment(manager, R.id.layout_fitness_log,
                                     new CardRoutineFragment(), "", false, bundle, false);
    }
}