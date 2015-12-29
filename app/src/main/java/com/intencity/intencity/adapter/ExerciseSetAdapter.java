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
import com.intencity.intencity.model.Set;

import java.util.ArrayList;

/**
 * This is the adapter for the exercise cards.
 *
 * Created by Nick Piscopio on 12/21/15.
 */
public class ExerciseSetAdapter extends ArrayAdapter<Set>
{
    private final Integer[] intensityValues = new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    private Context context;

    private SetListener listener;

    private ArrayList<Set> sets;

    private View row;

    int layoutResourceId;

    static class SetHolder
    {
        TextView setNumber;
        EditText weightEditText;
        EditText durationEditText;
        Spinner intensitySpinner;

        int intensity;
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

//
//        if(row == null)
//        {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        SetHolder holder = new SetHolder();
        holder.setNumber = (TextView) row.findViewById(R.id.text_view_set);
        holder.weightEditText = (EditText) row.findViewById(R.id.edit_text_weight);
        holder.durationEditText = (EditText) row.findViewById(R.id.edit_text_duration);
        holder.intensitySpinner = (Spinner) row.findViewById(R.id.spinner_intensity);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner, intensityValues);
        holder.intensitySpinner.setAdapter(adapter);

        holder.setNumber.setText(String.valueOf(position + 1));

        holder.intensitySpinner.setSelection(holder.intensity);

//            holder.weightEditText.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    listener.onSetClicked();
//                }
//            });
//            holder.durationEditText.setOnClickListener(setClickListener);
//            holder.intensitySpinner.setOnItemSelectedListener(itemClickListener);

//        row.setTag(holder);

        // Notify the ExerciseViewHolder that a set was added.
        // This is where we will set the new height of the ListView.
//        listener.onSetAdded();
//        }
//        else
//        {
//            holder = (SetHolder)row.getTag();
//
////            holder.intensity = holder.
////            holder.intensitySpinner.setSelection(holder.intensitySpinner.getSelectedItemPosition());
//        }

        return row;
    }

//    public View.OnClickListener setClickListener = new View.OnClickListener()
//    {
//        @Override
//        public void onClick(View v)
//        {
//            listener.onSetClicked();
//        }
//    };
//
//    private AdapterView.OnItemSelectedListener itemClickListener = new AdapterView.OnItemSelectedListener()
//    {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//        {
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent)
//        {
////            listener.onSetClicked();
//        }
//    };

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