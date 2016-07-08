package com.intencity.intencity.util;

import android.view.View;
import android.widget.ImageButton;

import com.intencity.intencity.R;

/**
 * A utility class for the exercise priorities.
 *
 * Created by Nick Piscopio on 4/14/16.
 */
public class ExercisePriorityUtil
{
    public static final int PRIORITY_LIMIT_UPPER = 40;
    public static final int PRIORITY_LIMIT_LOWER = 0;
    public static final int INCREMENTAL_VALUE = 10;

    /**
     * Sets the priority for the exercise.
     *
     * @param priority  The current priority we are changing.
     * @param increment         Boolean value of whether or not the exercise should increment or decrement the current priority.
     *
     * @return The new exercise priority.
     */
    public static int getExercisePriority(int priority, boolean increment)
    {
        if (priority < PRIORITY_LIMIT_UPPER && increment)
        {
            priority += INCREMENTAL_VALUE;
        }
        else if (priority > PRIORITY_LIMIT_LOWER && !increment)
        {
            priority -= INCREMENTAL_VALUE;
        }

        return priority;
    }

    /**
     * Sets the exercise priority buttons.
     *
     * @param priority          The current priority for the exercise.
     * @param morePriority      The more priority ImageButton.
     * @param lessPriority      The less priority ImageButton.
     */
    public static void setPriorityButtons(int priority, ImageButton morePriority, ImageButton lessPriority)
    {
        switch (priority)
        {
            case PRIORITY_LIMIT_UPPER:
                morePriority.setVisibility(View.INVISIBLE);
                lessPriority.setVisibility(View.VISIBLE);
                lessPriority.setImageResource(R.mipmap.thumb_down_outline);
                break;
            case PRIORITY_LIMIT_UPPER -  INCREMENTAL_VALUE:
                morePriority.setVisibility(View.VISIBLE);
                morePriority.setImageResource(R.mipmap.thumb_up);
                lessPriority.setVisibility(View.VISIBLE);
                lessPriority.setImageResource(R.mipmap.thumb_down_outline);
                break;
            case PRIORITY_LIMIT_LOWER + INCREMENTAL_VALUE:
                morePriority.setVisibility(View.VISIBLE);
                morePriority.setImageResource(R.mipmap.thumb_up_outline);
                lessPriority.setVisibility(View.VISIBLE);
                lessPriority.setImageResource(R.mipmap.thumb_down);
                break;
            case PRIORITY_LIMIT_LOWER:
                morePriority.setVisibility(View.VISIBLE);
                morePriority.setImageResource(R.mipmap.thumb_up_outline);
                lessPriority.setVisibility(View.INVISIBLE);
                break;
            default:
                morePriority.setVisibility(View.VISIBLE);
                morePriority.setImageResource(R.mipmap.thumb_up_outline);
                lessPriority.setVisibility(View.VISIBLE);
                lessPriority.setImageResource(R.mipmap.thumb_down_outline);
                break;
        }
    }
}