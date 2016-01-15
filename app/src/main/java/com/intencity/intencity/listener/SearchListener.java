package com.intencity.intencity.listener;

import com.intencity.intencity.model.Exercise;

/**
 * A listener for when a user searches.
 *
 * Created by Nick Piscopio on 1/15/16.
 */
public interface SearchListener
{
    void onExerciseAdded(Exercise name);
}