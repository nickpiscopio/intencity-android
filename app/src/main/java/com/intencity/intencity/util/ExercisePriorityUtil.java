package com.intencity.intencity.util;

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
    public int getExercisePriority(int priority, boolean increment)
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
}
