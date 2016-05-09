package com.intencity.intencity.adapter.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
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
    private Context context;

    private View view;

    private LinearLayout exerciseLayout;
    private LinearLayout exerciseEditLayout;
    private LinearLayout lastSetLayout;
    private LinearLayout lastSet;
    private TextView exercise;
    private TextView weight;
    private TextView weightSuffix;
    private TextView slash;
    private TextView duration;
    private TextView repsTextView;
    private TextView edit;
    private TextView description;
    private ImageButton hide;
    private ImageButton morePriority;
    private ImageButton lessPriority;

    private ExerciseListener listener;

    private int position;

    public ExerciseViewHolder(Context context, View view, ExerciseListener listener)
    {
        super(view);

        this.context = context;

        this.view = view;

        this.listener = listener;

        exerciseLayout = (LinearLayout) view.findViewById(R.id.layout_exercise);
        exerciseEditLayout = (LinearLayout) view.findViewById(R.id.layout_exercise_edit);
        lastSetLayout = (LinearLayout) view.findViewById(R.id.layout_last_set);
        lastSet = (LinearLayout) view.findViewById(R.id.last_set);
        exercise = (TextView) view.findViewById(R.id.exercise);
        weight = (TextView) view.findViewById(R.id.weight);
        weightSuffix = (TextView) view.findViewById(R.id.weight_suffix);
        slash = (TextView) view.findViewById(R.id.slash);
        duration = (TextView) view.findViewById(R.id.duration);
        repsTextView = (TextView) view.findViewById(R.id.suffix);
        edit = (TextView) view.findViewById(R.id.edit);
        description = (TextView) view.findViewById(R.id.description);
        hide = (ImageButton) view.findViewById(R.id.button_hide);
        morePriority = (ImageButton) view.findViewById(R.id.button_more_priority);
        lessPriority = (ImageButton) view.findViewById(R.id.button_less_priority);

        exerciseLayout.setOnClickListener(exerciseClickListener);
        lastSetLayout.setOnClickListener(setClickListener);
        hide.setOnClickListener(hideClicked);
        morePriority.setOnClickListener(morePriorityClicked);
        lessPriority.setOnClickListener(LessPriorityClicked);
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
     * The click listener to hide an exercise.
     */
    private View.OnClickListener hideClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            listener.onHideClicked(position);
        }
    };

    /**
     * The click listener to increase an exercise priority.
     */
    private View.OnClickListener morePriorityClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            listener.onSetExercisePriority(position, true);
        }
    };

    /**
     * The click listener to decrease an exercise priority.
     */
    private View.OnClickListener LessPriorityClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            listener.onSetExercisePriority(position, false);
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
     * Sets the exercise card information. 
     *
     * @param exerciseName          The name of the exercise to set the TextView. 
     * @param description           The description to show. 
     * @param includedInIntencity   Boolean value of whether the exercise is included in Intencity. 
     */
    public void setExercise(String exerciseName, String description, boolean includedInIntencity)
    {
        if (description != null)
        {
            this.description.setText(description);
            this.description.setVisibility(View.VISIBLE);
            this.exerciseEditLayout.setVisibility(View.GONE);
            this.hide.setVisibility(View.GONE);
            this.exercise.setTextColor(ContextCompat.getColor(context, R.color.secondary_light));
        }
        else
        {
            this.description.setVisibility(View.GONE);
            this.exerciseEditLayout.setVisibility(View.VISIBLE);
            this.hide.setVisibility(View.VISIBLE);

            exercise.setClickable(!includedInIntencity);
            exercise.setBackgroundResource(includedInIntencity ? R.drawable.button_card : 0);
            exercise.setTextColor(ContextCompat.getColor(context, includedInIntencity ? R.color.primary : R.color.secondary_dark));
        }

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
        else
        {
            repsTextView.setVisibility(View.VISIBLE);
        }

        this.duration.setText(duration);
    }

    /**
     * Sets the weight text.
     *
     * @param weight    The string to set in the TextView.
     */
    public void setWeight(float weight)
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