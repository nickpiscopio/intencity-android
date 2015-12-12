package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intencity.intencity.R;

/**
 * The Ranking Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class RankingFragment extends android.support.v4.app.Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_intencity_rankings, container, false);

        return view;
    }
}