package com.intencity.intencity.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intencity.intencity.R;
import com.intencity.intencity.view.activity.DemoActivity;

/**
 * Fragment class for the PagerFragment.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class PagerFragment extends Fragment
{
    public static String FRAGMENT_PAGE = "fragment_page";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int pageId;
        int page = getArguments().getInt(FRAGMENT_PAGE);
        switch (page)
        {
            case DemoActivity.FITNESS_GURU:
                pageId = R.layout.fragment_demo_fitness_guru;
                break;

            case DemoActivity.FITNESS_DIRECTION:
                pageId = R.layout.fragment_demo_fitness_direction;
                break;

            case DemoActivity.FITNESS_LOG:
                pageId = R.layout.fragment_demo_fitness_log;
                break;

            case DemoActivity.RANKING:
                pageId = R.layout.fragment_demo_ranking;
                break;

            case DemoActivity.DESCRIPTION:
            default:
                pageId = R.layout.fragment_demo_description;
                break;
        }

        return inflater.inflate(pageId, container, false);
    }
}