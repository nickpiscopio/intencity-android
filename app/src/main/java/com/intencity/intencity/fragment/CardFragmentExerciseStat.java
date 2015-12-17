package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.intencity.intencity.R;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.FragmentHandler;
import com.intencity.intencity.util.Util;

/**
 * The Fragment for the exercise.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class CardFragmentExerciseStat extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_card_exercise_stats, container, false);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.parent_layout_sets);

        int tag = Util.getRandomId();

        linearLayout.setId(tag);

        Bundle bundle = new Bundle();
        bundle.putInt(Constant.BUNDLE_ID, linearLayout.getId());

        new FragmentHandler().pushFragment(getFragmentManager(), tag,
                                     new ExerciseSetFragment(), null, false);


        return view;
    }
}