package com.intencity.intencity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.dialog.CustomDialog;
import com.intencity.intencity.dialog.Dialog;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.task.SetExerciseTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.FragmentHandler;
import com.intencity.intencity.util.Util;

import java.util.ArrayList;

/**
 * The Fragment for the exercise.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class CardFragmentExercise extends Fragment implements DialogListener
{
    public static int currentIndex = 0;
    private int savedIndex = 0;
    private int autoFillFrom = 0;

    private FragmentHandler fragmentHandler;

    private FragmentManager manager;

    private View view;

    private RelativeLayout exerciseLayout;
    private LinearLayout exerciseStatsLayout;

    private ArrayList<Exercise> exercises;

    private String currentExercise;

    private Context context;

    private boolean lastFragmentInList = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_card_exercise, container, false);

        exerciseLayout = (RelativeLayout) view.findViewById(R.id.layout_exercise);
        ImageButton menu = (ImageButton) view.findViewById(R.id.button_hide);
        menu.setOnClickListener(menuButtonListener);

        context = getContext();

        manager = getFragmentManager();

        fragmentHandler = FragmentHandler.getInstance();

        exerciseStatsLayout = (LinearLayout) view.findViewById(R.id.layout_exercise_stats);
        exerciseStatsLayout.setId(Util.getRandomId());

        Bundle bundle = getArguments();

        if (bundle != null)
        {
            exercises = bundle.getParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST);
            savedIndex = bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX);
            autoFillFrom = bundle.getInt(Constant.BUNDLE_EXERCISE_AUTOFILL_FROM);

            if (autoFillFrom < savedIndex)
            {
                setCardInfo(autoFillFrom, true);

                addExerciseStats();
            }
            else if (exercises != null && exercises.size() > savedIndex)
            {
                setCardInfo(savedIndex, false);

                // Only add the click listener if the exercise hasn't been completed yet.
                exerciseLayout.setOnClickListener(clickListener);
            }
        }

        return view;
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            exerciseLayout.setOnClickListener(null);

            addExerciseStats();

            newExerciseCard(savedIndex, getNextExerciseTag(savedIndex));
        }
    };

    private View.OnClickListener menuButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String[] buttonText = new String[]{getString(R.string.hide_for_now),
                                               getString(R.string.hide_forever)};

            new CustomDialog(context, CardFragmentExercise.this, new Dialog(buttonText));
        }
    };

    /**
     * Sets the exercise's card info.
     *
     * @param index             The index of the exercise to retrieve.
     * @param isAutoFilling     A boolean of if the exercise list is iterating
     *                          to get back to where the user was before.
     */
    private void setCardInfo(int index, boolean isAutoFilling)
    {
        currentExercise = exercises.get(index).getName();

        if (isAutoFilling)
        {
            newExerciseCard(++autoFillFrom, exercises.get(index + 1).getName());
        }
        else
        {
            savedIndex++;
        }

        TextView exerciseTitle = (TextView) view.findViewById(R.id.exercise);
        exerciseTitle.setText(currentExercise);
    }

    /**
     * Creates a new exercise card.
     *
     * @param autoFillFrom  Index of where the exercise list should auto fill the exercise
     *                      cards to. This is only used when the app closes before the
     *                      user finishes exercising.
     */
    private void newExerciseCard(int autoFillFrom, String tag)
    {
        if (savedIndex < exercises.size())
        {
            currentIndex = savedIndex;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST, exercises);
            bundle.putInt(Constant.BUNDLE_EXERCISE_LIST_INDEX, savedIndex);
            bundle.putInt(Constant.BUNDLE_EXERCISE_AUTOFILL_FROM, autoFillFrom);

            fragmentHandler.pushFragment(manager, R.id.layout_fitness_log,
                                         new CardFragmentExercise(), tag, false, bundle, false);
        }
    }

    /**
     * Add the exercise stats fragment to the card.
     */
    private void addExerciseStats()
    {
        lastFragmentInList = false;

        fragmentHandler.pushFragment(manager, exerciseStatsLayout.getId(),
                                     new CardFragmentExerciseStat(), "", true, null, false);
    }

    private void hideExercise()
    {
        lastFragmentInList = false;

        boolean removingLastIndex = currentIndex != savedIndex;

        int nextExerciseIndex = currentIndex;

        if (currentIndex > 0)
        {
            currentIndex--;
        }

        int fragmentIndex = --savedIndex;

        exercises.remove(fragmentIndex);

        fragmentHandler.removeFragment(manager, currentExercise);

        if (removingLastIndex)
        {
            newExerciseCard(savedIndex, getNextExerciseTag(nextExerciseIndex));
        }
    }

    /**
     * Get the next exercise tag from the index provided.
     *
     * @param index     The index of the next tag.
     *
     * @return  The exercise next exercise name.
     */
    private String getNextExerciseTag(int index)
    {
        return index < exercises.size() ? exercises.get(index).getName() : "";
    }

    @Override
    public void onStop()
    {
        super.onPause();

        // Only call to save to the database if this fragment is the last fragment in the list.
        if (lastFragmentInList)
        {
            // Save the exercises to the database in case the user wants
            // to continue with this routine later.
            new SetExerciseTask(context, exercises, savedIndex - 1).execute();
        }
    }

    @Override
    public void onButtonPressed(int which)
    {
        hideExercise();
    }

    @Override
    public void onPositiveButtonPressed()
    {

    }

    @Override
    public void onNegativeButtonPressed()
    {

    }
}