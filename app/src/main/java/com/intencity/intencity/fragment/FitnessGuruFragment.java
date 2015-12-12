package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intencity.intencity.R;

public class FitnessGuruFragment extends android.support.v4.app.Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_intencity_fitness_guru, container, false);

        return view;
    }
}