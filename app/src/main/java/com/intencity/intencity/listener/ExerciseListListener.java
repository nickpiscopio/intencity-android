package com.intencity.intencity.listener;

import com.intencity.intencity.model.Exercise;

import java.util.ArrayList;

/**
 * A listener to find out when a user starts exercising.
 *
 * Created by Nick Piscopio on 1/15/16.
 */
public interface ExerciseListListener
{
    void onNextExercise(ArrayList<Exercise> exercises);
}