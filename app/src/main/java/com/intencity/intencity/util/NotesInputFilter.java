package com.intencity.intencity.util;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;

import com.intencity.intencity.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a class that limits EditTexts to only have a specified number of decimal places.
 *
 * Created by Nick Piscopio on 2/5/16.
 */
public class NotesInputFilter implements InputFilter
{
    private Pattern pattern;

    private Context context;

    public NotesInputFilter(Context context)
    {
        this.context = context;

        pattern = Pattern.compile("[0-9a-zA-z\\.\\-_~\\s\\n]*");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
    {
        Matcher matcher = pattern.matcher(source);
        if(!matcher.matches() || dend >= context.getResources().getInteger(R.integer.exercise_notes_length))
        {
            return "";
        }

        return null;
    }
}