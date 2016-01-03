package com.intencity.intencity.listener;

import android.widget.EditText;

/**
 * A listener for when text changes on an EditText.
 *
 * Created by Nick Piscopio on 12/30/15.
 */
public interface ViewChangeListener
{
    void onTextChanged(String value, int position, EditText editText);
    void onTextChanged(int value, int position, int viewId);
    void onSpinnerItemSelected(int spinnerPosition, int position);
}
