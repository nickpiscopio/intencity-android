package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intencity.intencity.R;

/**
 * Fragment class for the PagerFragment.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class PagerFragment extends Fragment
{
    public static String FRAGMENT_PAGE = "fragment_page";

    private static final int DESCRIPTION_PAGE = 0;
    private static final int INSPIRATION_PAGE = 1;
    private static final int SHARE_PAGE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int pageId;
        int page = getArguments().getInt(FRAGMENT_PAGE);
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

        return inflater.inflate(pageId, container, false);
    }
}