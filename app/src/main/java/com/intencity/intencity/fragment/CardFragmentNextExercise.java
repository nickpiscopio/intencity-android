package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.FragmentHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * The Fragment for the next exercise.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class CardFragmentNextExercise extends Fragment
{
    private final int FIRST_INDEX = 0;

    private List<String> exercises;

    private String nextExercise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_card_next_exercise, container, false);

        Button nextExerciseButton = (Button) view.findViewById(R.id.button_next);
        nextExerciseButton.setOnClickListener(nextExerciseClickListener);

        Bundle bundle = getArguments();

        if (bundle != null)
        {
            exercises = bundle.getStringArrayList(Constant.BUNDLE_EXERCISE_LIST);

            if (exercises != null && exercises.size() > FIRST_INDEX)
            {
                nextExercise = exercises.get(FIRST_INDEX);
            }

        }

        TextView exerciseTitle = (TextView) view.findViewById(R.id.exercise);
        exerciseTitle.setText(nextExercise);

        return view;
    }

    private View.OnClickListener nextExerciseClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            exercises.remove(FIRST_INDEX);

            new FragmentHandler().pushFragment(CardFragmentNextExercise.this, R.id.layout_fitness_guru, new CardFragmentExercise(), getBundle(nextExercise), true);

            if (exercises.size() > 0)
            {
                new FragmentHandler().pushFragment(CardFragmentNextExercise.this, R.id.layout_fitness_guru, new CardFragmentNextExercise(), getBundle(exercises), false);
            }

        }
    };

    @SuppressWarnings("unchecked")
    private Bundle getBundle(Object argument)
    {
        Bundle bundle = new Bundle();

        if (argument instanceof String)
        {
            bundle.putString(Constant.BUNDLE_EXERCISE, (String) argument);
        }
        else
        {
            bundle.putStringArrayList(Constant.BUNDLE_EXERCISE_LIST, (ArrayList<String>) argument);
        }

        return bundle;
    }
}