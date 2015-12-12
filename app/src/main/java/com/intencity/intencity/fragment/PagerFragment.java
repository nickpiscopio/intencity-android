package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.DemoActivity;

/**
 * Fragment class for the PagerFragment.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class PagerFragment extends Fragment
{
    public static String FRAGMENT_PAGE = "fragment_page";
    public static String CLASS_ID = "class_id";

    private static final int DESCRIPTION_PAGE = 0;
    private static final int INSPIRATION_PAGE = 1;
    private static final int SHARE_PAGE = 2;

    private static final int RANKING_PAGE = 0;
    private static final int FITNESS_GURU_PAGE = 1;
    private static final int MENU_PAGE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int pageId;
        int page = getArguments().getInt(FRAGMENT_PAGE);
        int classId = getArguments().getInt(CLASS_ID);
        if (classId == DemoActivity.CLASS_ID)
        {
            switch (page)
            {
                case INSPIRATION_PAGE:
                    pageId = R.layout.fragment_demo_inspire;
                    break;

                case SHARE_PAGE:
                    pageId = R.layout.fragment_demo_share;
                    break;

                case DESCRIPTION_PAGE:
                default:
                    pageId = R.layout.fragment_demo_about;
                    break;
            }
        }
        else
        {
            switch (page)
            {
                case RANKING_PAGE:
                    pageId = R.layout.fragment_intencity_rankings;
                    break;

                case MENU_PAGE:
                    pageId = R.layout.fragment_intencity_menu;
                    break;

                case FITNESS_GURU_PAGE:
                default:
                    pageId = R.layout.fragment_intencity_fitness_guru;
                    break;
            }
        }

        return inflater.inflate(pageId, container, false);
    }
}