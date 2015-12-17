package com.intencity.intencity.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.FragmentListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.util.FragmentHandler;


/**
 * The Fitness Guru Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class FitnessLogFragment extends android.support.v4.app.Fragment implements ServiceListener,
                                                                                   FragmentListener
{
    private final String PROPERTY = "scrollY";

    private ScrollView scrollView;

    private LinearLayout fitnessLogLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_intencity_fitness_log, container, false);

        scrollView = (ScrollView) view.findViewById(R.id.scroll_view_fitness_log);
        fitnessLogLayout = (LinearLayout) view.findViewById(R.id.layout_fitness_log);

        FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.setFragmentListener(this);
        fragmentHandler.pushFragment(getFragmentManager(), R.id.layout_fitness_log,
                                     new CardRoutineFragment(), false, null, false);

        return view;
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {

    }

    @Override
    public void onRetrievalFailed()
    {

    }

    @Override
    public void onFragmentAdded()
    {
        Activity activity = getActivity();

        View view = activity.getCurrentFocus();
        if (view != null)
        {
            view.clearFocus();
        }

        int scrollDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

        // Get the keyboard so we can check when it is visible.
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);

        if (!imm.isAcceptingText())
        {
            // Scroll to the bottom of the scrollview.
            ObjectAnimator.ofInt(scrollView, PROPERTY, fitnessLogLayout.getHeight()).setDuration(scrollDuration).start();
        }
    }
}