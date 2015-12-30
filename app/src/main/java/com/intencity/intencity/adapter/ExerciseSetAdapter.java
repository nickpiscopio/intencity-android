package com.intencity.intencity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.SetListener;
import com.intencity.intencity.listener.ViewChangeListener;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.GenericItemSelectionListener;
import com.intencity.intencity.util.GenericTextWatcher;

import java.util.ArrayList;

/**
 * This is the adapter for the exercise cards.
 *
 * Created by Nick Piscopio on 12/21/15.
 */
public class ExerciseSetAdapter extends ArrayAdapter<Set> implements ViewChangeListener
{
    private final Integer[] intensityValues = new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    public static final int WEIGHT = R.id.edit_text_weight;
    public static final int DURATION = R.id.edit_text_duration;
    public static final int INTENSITY = R.id.spinner_intensity;

    private Context context;

    private SetListener listener;

    private ArrayList<Set> sets;

    private View row;

    private int layoutResourceId;

    static class SetHolder
    {
        TextView setNumber;
        EditText weightEditText;
        EditText durationEditText;
        Spinner intensitySpinner;
    }

    public ExerciseSetAdapter(Context context, int layoutResourceId, SetListener listener,
                              ArrayList<Set> sets)
    {
        super(context, layoutResourceId, sets);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.listener = listener;
        this.sets = sets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        row = convertView;

        SetHolder holder;

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        // Would recreate this only if (row == null),
        // but when that happens the second list item always overwrites the first.
        // This is how it will have to be for now.
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new SetHolder();
        holder.setNumber = (TextView) row.findViewById(R.id.text_view_set);
        holder.weightEditText = (EditText) row.findViewById(WEIGHT);
        holder.durationEditText = (EditText) row.findViewById(DURATION);
        holder.intensitySpinner = (Spinner) row.findViewById(INTENSITY);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner, intensityValues);
        holder.intensitySpinner.setAdapter(adapter);

        // Add the listeners to the views for each list item.
        holder.weightEditText.addTextChangedListener(new GenericTextWatcher(this, position, WEIGHT));
//        holder.durationEditText.addTextChangedListener(new GenericTextWatcher(this, position, DURATION));
        holder.intensitySpinner.setOnItemSelectedListener(new GenericItemSelectionListener(this, position));

        // This is auto incremented with each added view.
        holder.setNumber.setText(String.valueOf(position + 1));

        Set set = sets.get(position);

        // Get the values that were in the list items before or the default values.
        String weight = String.valueOf(set.getWeight());
        String duration = String.valueOf(set.getDuration());
        int difficulty = set.getDifficulty();
        int intensity = difficulty < intensityValues[0] ? intensityValues[intensityValues.length - 1] : difficulty;

        String codeFailed = String.valueOf(Constant.CODE_FAILED);

        // Add the values to each list item.
        holder.weightEditText.setText(weight.equals(codeFailed) ? "" : weight);
        holder.durationEditText.setText(duration.equals(codeFailed) ||
                                        duration == Constant.RETURN_NULL ? "" : duration);

        holder.intensitySpinner.setSelection(intensity - 1);

        return row;
    }

    @Override
    public void onTextChanged(String value, int position, int viewId) {
        // Not implemented for now. Will implement later when duration is implemented.
    }

    @Override
    public void onTextChanged(int value, int position, int viewId)
    {
        Set set = sets.get(position);

        switch (viewId)
        {
            case WEIGHT:
                set.setWeight(value);
                break;
            case DURATION:
                set.setReps(value);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSpinnerItemSelected(int spinnerPosition, int position)
    {
        Set set = sets.get(position);
        set.setDifficulty(intensityValues[spinnerPosition]);
    }

    /**
     * Gets the view of the set adapter.
     *
     * @return  The view.
     */
    public View getView()
    {
        return row;
    }

    /**
     * Gets the number of items in the adapter.
     *
     * @return  The amount of sets.
     */
    public int getItemCount()
    {
        return sets.size();
    }
}