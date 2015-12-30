package com.intencity.intencity.util;

import android.view.View;
import android.widget.AdapterView;

import com.intencity.intencity.listener.ViewChangeListener;

/**
 * This is a ItemSelectionListener to implement in the ExerciseSetAdapter
 * so we can save values of the spinner to an array.
 *
 * Created by Nick Piscopio on 12/30/15.
 */
public class GenericItemSelectionListener implements AdapterView.OnItemSelectedListener
{
    private ViewChangeListener listener;

    private int position;

    /**
     * Constructor for the GenericItemSelectionListener.
     *
     * @param listener  The ViewChangeListener to call when we have a value to send.
     * @param position  The index of the ListView where the spinner is located.
     */
    public GenericItemSelectionListener(ViewChangeListener listener, int position)
    {
        this.listener = listener;

        this.position = position;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        listener.onSpinnerItemSelected(position, this.position);
    }

    @Override public void onNothingSelected(AdapterView<?> parent) { }
}