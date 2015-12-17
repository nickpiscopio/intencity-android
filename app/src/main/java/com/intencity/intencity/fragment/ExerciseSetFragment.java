package com.intencity.intencity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.FragmentHandler;

/**
 * The Fragment for the exercise.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class ExerciseSetFragment extends Fragment
{
    private final int ID_WEIGHT = R.id.edit_text_weight;
    private final int ID_DURATION = R.id.edit_text_duration;
    private final int ID_INTENSITY = R.id.spinner_intensity;
    private final float ALPHA_TRANSPARENT = 0.25f;
    private final float ALPHA_OPAQUE = 1f;

    private FragmentHandler fragmentHandler;

    private FragmentManager manager;

    private LinearLayout layout;

    private TextView set;

    private int setNumber = 1;

    private int layoutId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_exercise_set, container, false);

        layout = (LinearLayout) view.findViewById(R.id.layout_set);
        layout.setAlpha(ALPHA_TRANSPARENT);

        set = (TextView) view.findViewById(R.id.text_view_set);
        EditText weight = (EditText) view.findViewById(ID_WEIGHT);
        EditText duration = (EditText) view.findViewById(ID_DURATION);
        Spinner intensity = (Spinner) view.findViewById(ID_INTENSITY);

        weight.setOnFocusChangeListener(clickListener);
        duration.setOnFocusChangeListener(clickListener);
        intensity.setOnItemSelectedListener(itemClickListener);

        Bundle bundle = getArguments();

        if (bundle != null)
        {
            setNumber = bundle.getInt(Constant.BUNDLE_SET_NUMBER);
            layoutId = bundle.getInt(Constant.BUNDLE_ID);
        }

        fragmentHandler = new FragmentHandler();

        manager = getFragmentManager();

        return view;
    }

    private void addSet()
    {
        layout.setAlpha(ALPHA_OPAQUE);

        setNumber++;

        set.setText(String.valueOf(setNumber));

        Bundle bundle = new Bundle();
        bundle.putInt(Constant.BUNDLE_SET_NUMBER, setNumber);
        bundle.putInt(Constant.BUNDLE_ID, layoutId);

        new FragmentHandler().pushFragment(getFragmentManager(), layoutId,
                                           new ExerciseSetFragment(), bundle, false);
    }

    private View.OnFocusChangeListener clickListener = new View.OnFocusChangeListener()
    {
        @Override
        public void onFocusChange(View v, boolean hasFocus)
        {
            if (hasFocus)
            {
                switch (v.getId())
                {
                    case ID_WEIGHT:
                    case ID_DURATION:
                    default:
                        break;
                }

                addSet();
            }
        }
    };

    private AdapterView.OnItemSelectedListener itemClickListener = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            addSet();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            addSet();
        }
    };
}