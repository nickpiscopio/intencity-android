package com.intencity.intencity.listener;

/**
 * A listener to for the exercise priority list.
 *
 * Created by Nick Piscopio on 3/24/16.
 */
public interface ExercisePriorityListener
{
    void onSetExercisePriority(int position, int priority, boolean increment);
}