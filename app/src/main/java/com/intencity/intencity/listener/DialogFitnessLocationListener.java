package com.intencity.intencity.listener;

/**
 * A listener for the button clicks of the fitness location dialog.
 *
 * Created by Nick Piscopio on 11/24/16.
 */
public interface DialogFitnessLocationListener extends DialogListener
{
    void onSaveFitnessLocation(String displayName, String location);
}