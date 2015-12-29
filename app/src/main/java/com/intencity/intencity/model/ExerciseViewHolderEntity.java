package com.intencity.intencity.model;

import com.intencity.intencity.adapter.ExerciseSetAdapter;

import java.util.ArrayList;

/**
 * Created by Nick Piscopio on 12/29/15.
 */
public class ExerciseViewHolderEntity
{
    public static ExerciseViewHolderEntity fragmentHandler = null;

    private ArrayList<ExerciseSetAdapter> adapters;
    private ArrayList<Set> sets;

    public ExerciseViewHolderEntity()
    {
        adapters = new ArrayList<>();
        sets = new ArrayList<>();
    }

    /**
     * Gets the instance of the singleton for the FragmentHandler.
     *
     * @return  The singleton instance of the FragmentHandler.
     */
    public static ExerciseViewHolderEntity getInstance()
    {
        if (fragmentHandler == null)
        {
            fragmentHandler = new ExerciseViewHolderEntity();
        }

        return fragmentHandler;
    }

    /**
     * Gets the adapters so we can use them in the view holder.
     *
     * @return  The adapters.
     */
    public ArrayList<ExerciseSetAdapter> getAdapters()
    {
        return adapters;
    }

    /**
     * Gets the sets so we can use them in the view holder.
     *
     * @return  The sets of the exercise.
     */
    public ArrayList<Set> getSets()
    {
        return sets;
    }
}