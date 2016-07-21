package com.intencity.intencity.model;

/**
 * The model class for the exercise that was removed from the exercise list..
 *
 * Created by Nick Piscopio on 7/21/16.
 */
public class IndexedExercise
{
    private int index;
    private Exercise exercise;

    public IndexedExercise(int index, Exercise exercise)
    {
        this.index = index;
        this.exercise = exercise;
    }

    /**
     * Getters.
     */
    public int getIndex()
    {
        return index;
    }

    public Exercise getExercise()
    {
        return exercise;
    }
}