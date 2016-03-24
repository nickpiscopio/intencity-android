package com.intencity.intencity.listener;

import java.io.Serializable;

/**
 * A listener for an exercise.
 *
 * Created by Nick Piscopio on 12/10/15.
 */
public interface ExerciseListener extends Serializable
{
    void onExerciseClicked(int position);
    void onStatClicked(int position);
    void onHideClicked(int position);
}