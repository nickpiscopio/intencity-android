package com.intencity.intencity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intencity.intencity.R;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the direction list.
 *
 * Created by Nick Piscopio on 12/28/15.
 */
public class DirectionListAdapter extends ArrayAdapter<String>
{
    private ArrayList<String> steps;

    private Context context;

    /**
     * The constructor.
     *
     * @param context   The application context.
     * @param steps     The list of directions for the exercise.
     */
    public DirectionListAdapter(Context context, ArrayList<String> steps)
    {
        super(context, 0, steps);

        this.context = context;

        this.steps = steps;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_standard, null);
        }

        String step = steps.get(position);

        TextView number = (TextView) view.findViewById(R.id.text_view_direction_number);
        TextView directionTextView = (TextView) view.findViewById(R.id.text_view_direction);

        number.setText(String.valueOf(position + 1) + ".");
        directionTextView.setText(step);

        // Commenting this out to decide later if I really want to animate the steps.
//        Animation animation = AnimationUtils
//                .loadAnimation(context, R.anim.anim_slide_in_up);
//        view.startAnimation(animation);

        return view;
    }
}