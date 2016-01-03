package com.intencity.intencity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.intencity.intencity.R;
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
    private final Integer[] INTENSITY_VALUES = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    public static final int WEIGHT = R.id.edit_text_weight;
    public static final int DURATION = R.id.edit_text_duration;
    public static final int INTENSITY = R.id.spinner_intensity;

    private Context context;

    private int layoutResourceId;

    private ArrayList<Set> sets;

    private LayoutInflater inflater;

    private int position;

    static class SetHolder
    {
        TextView setNumber;
        EditText weightEditText;
        EditText durationEditText;
        Spinner intensitySpinner;
    }

    public ExerciseSetAdapter(Context context, int layoutResourceId, ArrayList<Set> sets)
    {
        super(context, layoutResourceId, sets);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.sets = sets;

        position = -1;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final SetHolder holder = (convertView == null) ? new SetHolder() : (SetHolder)convertView.getTag();

        if (this.position != position || convertView == null)
        {
            this.position = position;

            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder.setNumber = (TextView)convertView.findViewById(R.id.text_view_set);
            holder.weightEditText = (EditText)convertView.findViewById(WEIGHT);
            holder.durationEditText = (EditText)convertView.findViewById(DURATION);
            holder.intensitySpinner = (Spinner)convertView.findViewById(INTENSITY);

            ArrayAdapter<Integer> adapter =
                    new ArrayAdapter<>(context, R.layout.spinner, INTENSITY_VALUES);
            holder.intensitySpinner.setAdapter(adapter);
            adapter.setDropDownViewResource(R.layout.spinner_item);

            // Add the listeners to the views for each list item.
            holder.weightEditText.addTextChangedListener(
                    new GenericTextWatcher(this, position, holder.weightEditText));
            holder.durationEditText.addTextChangedListener(new GenericTextWatcher(this, position, holder.durationEditText));
            holder.intensitySpinner
                    .setOnItemSelectedListener(new GenericItemSelectionListener(this, position));

            // This is auto incremented with each added view.
            holder.setNumber.setText(String.valueOf(position + 1));

            Set set = sets.get(position);

            // Get the values that were in the list items before or the default values.
            String weight = String.valueOf(set.getWeight());
            String reps = String.valueOf(set.getReps());
            String time = String.valueOf(set.getDuration());
            int difficulty = set.getDifficulty();
            int intensity = difficulty < INTENSITY_VALUES[0] ? INTENSITY_VALUES[INTENSITY_VALUES.length - 1] : difficulty;

            String codeFailed = String.valueOf(Constant.CODE_FAILED);

            // Add the values to each list item.
            holder.weightEditText.setText(weight.equals(codeFailed) ? "" : weight);

            if (time.equals(Constant.RETURN_NULL))
            {
                holder.durationEditText.setText(
                        reps.equals(codeFailed) || reps.equals(Constant.RETURN_NULL) ? "" : reps);
            }
            else
            {
                holder.durationEditText.setText(
                        time.equals(codeFailed) || time.equals(Constant.RETURN_NULL) ? "" : time);
            }

            holder.intensitySpinner.setSelection(intensity - 1);

            convertView.setTag(holder);
        }

        return convertView;
    }

    /**
     * Gets a rep format from an input.
     *
     * @param input     The value to be converted.
     *
     * @return  An integer of the formatted value.
     */
    private String getRepFormat(String input)
    {
        // Make the input 0 if it doesn't have a value.
        input = (input.length() < 1) ? "0" : input;

        String formatted = input.replaceAll(":", "");
        return formatted.replaceFirst("^0+(?!$)", "");
    }

    /**
     * Gets the time format of an input.
     *
     * @param input     The value to convert to a time format.
     *
     * @return  The vlue in a time format.
     */
    private String getTimeFormat(String input)
    {
        // Convert to rep format so we can remove all the colons.
        // We do this so we don't add unwanted colons later.
        input = getRepFormat(input);

        int timeLength = 6;

        String padded = String.format("%0" + timeLength + "d",
                                      Integer.parseInt(getRepFormat(input)));
        if (padded.length() > timeLength)
        {
            padded = padded.substring(0, timeLength);
        }
        return padded.replaceAll("..(?!$)", "$0:");
    }

    @Override
    public void onTextChanged(String value, int position, EditText editText)
    {
        editText.setText(getTimeFormat(value));

        String time = editText.getText().toString();

        editText.setSelection(time.length());

        Set set = sets.get(position);
        set.setDuration(time);
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
        set.setDifficulty(INTENSITY_VALUES[spinnerPosition]);
    }
}