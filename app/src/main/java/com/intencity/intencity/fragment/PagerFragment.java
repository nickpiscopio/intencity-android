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
            case DemoActivity.INSPIRATION_PAGE:
                pageId = R.layout.fragment_demo_inspire;
                break;

            case DemoActivity.SHARE_PAGE:
                pageId = R.layout.fragment_demo_share;
                break;

            case DemoActivity.DESCRIPTION_PAGE:
            default:
                pageId = R.layout.fragment_demo_about;
                break;
        }

        return inflater.inflate(pageId, container, false);
    }
}