package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.FragmentHandler;
import com.intencity.intencity.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * The Fragment for the exercise.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class CardFragmentExercise extends Fragment
{
    private final int FIRST_INDEX = 0;

    private FragmentHandler fragmentHandler;

    private FragmentManager manager;

    private View view;

    private RelativeLayout exerciseLayout;
    private LinearLayout exerciseStatsLayout;

    private List<String> exercises;

    private String currentExercise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_card_exercise, container, false);

        exerciseLayout = (RelativeLayout) view.findViewById(R.id.layout_exercise);
        exerciseLayout.setOnClickListener(clickListener);

        Bundle bundle = getArguments();

        if (bundle != null)
        {
            exercises = bundle.getStringArrayList(Constant.BUNDLE_EXERCISE_LIST);

            if (exercises != null && exercises.size() > FIRST_INDEX)
            {
                currentExercise = exercises.get(FIRST_INDEX);
                exercises.remove(FIRST_INDEX);

                TextView exerciseTitle = (TextView) view.findViewById(R.id.exercise);
                exerciseTitle.setText(currentExercise);
            }
        }

        manager = getFragmentManager();

        fragmentHandler = FragmentHandler.getInstance();

        exerciseStatsLayout = (LinearLayout) view.findViewById(R.id.layout_exercise_stats);
        exerciseStatsLayout.setId(Util.getRandomId());

        return view;
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            exerciseLayout.setOnClickListener(null);

            addExerciseStats();

            Bundle bundle = new Bundle();
            bundle.putStringArrayList(Constant.BUNDLE_EXERCISE_LIST, (ArrayList<String>)exercises);

            if (exercises.size() > 0)
            {
                fragmentHandler.pushFragment(manager, R.id.layout_fitness_log,
                                             new CardFragmentExercise(), false, bundle, false);
            }
        }
    };

    /**
     * Add the exercice stats fragment to the card.
     */
    private void addExerciseStats()
    {
        fragmentHandler.pushFragment(manager, exerciseStatsLayout.getId(),
                                     new CardFragmentExerciseStat(), true, null, false);
    }
}