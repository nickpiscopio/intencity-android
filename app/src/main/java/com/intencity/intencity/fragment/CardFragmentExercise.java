package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.util.Constant;

/**
 * The Fragment for the exercise.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class CardFragmentExercise extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_card_exercise, container, false);

        Bundle bundle = getArguments();

        if (bundle != null)
        {
            String exercise = bundle.getString(Constant.BUNDLE_EXERCISE);

            if (exercise != "")
            {
                TextView exerciseTitle = (TextView) view.findViewById(R.id.exercise);
                exerciseTitle.setText(exercise);
            }
        }

        return view;
    }
}