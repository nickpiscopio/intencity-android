package com.intencity.intencity.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ExerciseListener;

/**
 * The ExerciseViewHolder for the exercise RecyclerView.
 *
 * Created by Nick Piscopio on 12/21/15.
 */
public class ExerciseViewHolder extends RecyclerView.ViewHolder
{
    private View view;

    private LinearLayout exerciseLayout;
    private LinearLayout lastSetLayout;
    private LinearLayout lastSet;
    private TextView exercise;
    private TextView weight;
    private TextView weightSuffix;
    private TextView slash;
    private TextView duration;
    private TextView repsTextView;
    private TextView edit;

    private ExerciseListener listener;

    private int position;

    public ExerciseViewHolder(View view, ExerciseListener listener)
    {
        super(view);

        this.view = view;

        this.listener = listener;

        exerciseLayout = (LinearLayout) view.findViewById(R.id.layout_exercise);
        lastSetLayout = (LinearLayout) view.findViewById(R.id.layout_last_set);
        lastSet = (LinearLayout) view.findViewById(R.id.last_set);
        exercise = (TextView) view.findViewById(R.id.exercise);
        weight = (TextView) view.findViewById(R.id.weight);
        weightSuffix = (TextView) view.findViewById(R.id.weight_suffix);
        slash = (TextView) view.findViewById(R.id.slash);
        duration = (TextView) view.findViewById(R.id.duration);
        repsTextView = (TextView) view.findViewById(R.id.suffix);
        edit = (TextView) view.findViewById(R.id.edit);

        exerciseLayout.setOnClickListener(exerciseClickListener);
        lastSetLayout.setOnClickListener(setClickListener);
    }

    /**
     * The click listener to see how to do an exercise.
     */
    private View.OnClickListener exerciseClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            listener.onExerciseClicked(position);
        }
    };

    /**
     * The click listener to see the stats of the exercise.
     */
    private View.OnClickListener setClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            listener.onStatClicked(position);
        }
    };

    /**
     * Gets the entire view of the exercise.
     */
    public View getView()
    {
        return view;
    }

    /**
     * Sets the  exercise TextView.
     *
     * @param exerciseName  The name of the exercise to set the TextView.
     */
    public void setExerciseText(String exerciseName)
    {
        exercise.setText(exerciseName);
    }

    /**
     * Sets the duration TextView based on whether there are reps or time saved
     *
     * @param duration  The string value to set the duration TextView.
     */
    public void setDuration(String duration)
    {
        if (duration.contains(":"))
        {
            repsTextView.setVisibility(View.GONE);
        }

        this.duration.setText(duration);
    }

    /**
     * Sets the weight text.
     *
     * @param weight    The string to set in the TextView.
     */
    public void setWeight(int weight)
    {
        if (weight <= 0)
        {
            this.weight.setVisibility(View.GONE);
            this.weightSuffix.setVisibility(View.GONE);
            this.slash.setVisibility(View.GONE);
        }
        else
        {
            this.weight.setVisibility(View.VISIBLE);
            this.weightSuffix.setVisibility(View.VISIBLE);
            this.slash.setVisibility(View.VISIBLE);

            this.weight.setText(String.valueOf(weight));
        }
    }

    /**
     * Shows either the edit layout or the last set based on whether there is a last set or not.
     */
    public void showEditLayout()
    {
        // If a user doesn't have a duration, set the last set to gone and show the edit button.
        if (Integer.parseInt(duration.getText().toString().replaceAll(":", "")) <= 0)
        {
            lastSet.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
        }
        else
        {
            edit.setVisibility(View.GONE);
            lastSet.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets the position on which list item we currently have.
     *
     * @param position  The position in the list.
     */
    public void setPosition(int position)
    {
        this.position = position;
    }
}