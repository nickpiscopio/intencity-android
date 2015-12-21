package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intencity.intencity.R;

/**
 * The header for the exercise list.
 *
 * Created by Nick Piscopio on 12/21/15.
 */
public class FragmentExerciseListHeader extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.exercise_list_header, container, false);

        return view;
    }
}