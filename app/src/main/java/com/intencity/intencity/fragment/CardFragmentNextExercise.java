package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.intencity.intencity.R;
import com.intencity.intencity.util.FragmentHandler;

/**
 * The Fragment for the next exercise.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class CardFragmentNextExercise extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_card_next_exercise, container, false);

        Button nextExercise = (Button) view.findViewById(R.id.button_next);
        nextExercise.setOnClickListener(nextExerciseClickListener);

        return view;
    }

    private View.OnClickListener nextExerciseClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            new FragmentHandler().pushFragment(CardFragmentNextExercise.this, R.id.layout_fitness_guru, new CardFragmentExercise(), true);
            new FragmentHandler().pushFragment(CardFragmentNextExercise.this, R.id.layout_fitness_guru, new CardFragmentNextExercise(), false);
        }
    };
}