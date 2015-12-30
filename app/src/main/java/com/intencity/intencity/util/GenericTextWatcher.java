package com.intencity.intencity.util;

import android.text.Editable;
import android.text.TextWatcher;

import com.intencity.intencity.adapter.ExerciseSetAdapter;
import com.intencity.intencity.listener.ViewChangeListener;

/**
 * This is a TextWatcher to implement in the ExerciseSetAdapter
 * so we can save values of the text to an array.
 *
 * Created by Nick Piscopio on 12/30/15.
 */
public class GenericTextWatcher implements TextWatcher
{
    private ViewChangeListener listener;

    private int position;

    private int viewId;

    private String beforeText;

    /**
     * Constructor for the GenericTextWatcher.
     *
     * @param listener  The ViewChangeListener to call when we have a value to send.
     * @param position  The index of the ListView where the TextView is located.
     * @param viewId    The resource id of the view for this TextWatcher.
     */
    public GenericTextWatcher(ViewChangeListener listener, int position, int viewId)
    {
        this.listener = listener;

        this.position = position;

        this.viewId = viewId;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        beforeText = String.valueOf(s);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s)
    {
        String value = String.valueOf(s);

        if (!beforeText.equals(value))
        {
            switch (viewId)
            {
                case ExerciseSetAdapter.WEIGHT:
                    listener.onTextChanged(value.equals("") ? 0 : Integer.parseInt(value), position, viewId);
                    break;
                case ExerciseSetAdapter.DURATION:
                    break;
                case ExerciseSetAdapter.INTENSITY:
                    break;
                default:
                    break;
            }
        }
    }
}