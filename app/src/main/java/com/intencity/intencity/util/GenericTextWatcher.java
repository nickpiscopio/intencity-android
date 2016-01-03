package com.intencity.intencity.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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

    private EditText editText;

    private String beforeText;

    /**
     * Constructor for the GenericTextWatcher.
     *
     * @param listener  The ViewChangeListener to call when we have a value to send.
     * @param position  The index of the ListView where the TextView is located.
     * @param editText  The EditText we are editing.
     */
    public GenericTextWatcher(ViewChangeListener listener, int position, EditText editText)
    {
        this.listener = listener;

        this.position = position;

        this.editText = editText;

        beforeText = "";
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
            int viewId = editText.getId();
            switch (viewId)
            {
                case ExerciseSetAdapter.WEIGHT:
                    listener.onTextChanged(value.equals("") ? 0 : Integer.parseInt(value), position, viewId);
                    break;
                case ExerciseSetAdapter.DURATION:
                    if (value.contains(":"))
                    {
                        listener.onTextChanged(value.equals("") ? "0" : value, position, editText);
                    }
                    else
                    {
                        listener.onTextChanged(value.equals("") ? 0 : Integer.parseInt(value), position, viewId);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}