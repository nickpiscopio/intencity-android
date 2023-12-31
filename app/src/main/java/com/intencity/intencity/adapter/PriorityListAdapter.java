package com.intencity.intencity.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ExercisePriorityListener;
import com.intencity.intencity.util.ExercisePriorityUtil;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the exercise priority list.
 *
 * Created by Nick Piscopio on 3/24/16.
 */
public class PriorityListAdapter extends ArrayAdapter<String>
{
    private String HIGH_PRIORITY;
    private String MEDIUM_PRIORITY;
    private String NORMAL_PRIORITY;
    private String LOW_PRIORITY;
    private String HIDDEN;

    private Context context;

    private ExercisePriorityListener listener;

    private int layoutResourceId;

    private ArrayList<String> exerciseNames;
    private ArrayList<String> priorities;

    private LayoutInflater inflater;

    private int position;

    static class PriorityListHolder
    {
        TextView name;
        TextView priority;
        ImageButton morePriority;
        ImageButton lessPriority;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     * @param listener          The listener to call back when we want to remove a user.
     * @param layoutResourceId  The resource id of the view we are inflating.
     * @param exerciseNames     The list of exercises to populate the list.
     * @param priorities        The list of priorities that coincide with the exercises.
     */
    public PriorityListAdapter(Context context, ExercisePriorityListener listener, int layoutResourceId,
                               ArrayList<String> exerciseNames, ArrayList<String> priorities)
    {
        super(context, layoutResourceId, exerciseNames);

        this.context = context;

        this.listener = listener;

        this.layoutResourceId = layoutResourceId;

        this.exerciseNames = exerciseNames;
        this.priorities = priorities;

        position = -1;

        HIGH_PRIORITY = context.getString(R.string.high_priority);
        MEDIUM_PRIORITY = context.getString(R.string.medium_priority);
        NORMAL_PRIORITY = context.getString(R.string.normal_priority);
        LOW_PRIORITY = context.getString(R.string.low_priority);
        HIDDEN = context.getString(R.string.hidden_exercise);

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final PriorityListHolder holder = (convertView == null) ?
                                            new PriorityListHolder() :
                                            (PriorityListHolder)convertView.getTag();

        if (this.position != position || convertView == null || position == 0)
        {
            this.position = position;

            convertView = inflater.inflate(layoutResourceId, parent, false);

            String exerciseName = exerciseNames.get(position);
            final int priority = Integer.valueOf(priorities.get(position));

            holder.name = (TextView) convertView.findViewById(R.id.text_view_exercise_name);
            holder.priority = (TextView) convertView.findViewById(R.id.text_view_priority);
            holder.morePriority = (ImageButton) convertView.findViewById(R.id.button_more_priority);
            holder.lessPriority = (ImageButton) convertView.findViewById(R.id.button_less_priority);

            holder.name.setText(exerciseName);

            switch(priority)
            {
                //40
                case ExercisePriorityUtil.PRIORITY_LIMIT_UPPER:
                    holder.priority.setTextColor(ContextCompat.getColor(context, R.color.primary));
                    holder.priority.setText(HIGH_PRIORITY);
                    break;
                // 30
                case ExercisePriorityUtil.INCREMENTAL_VALUE * 3:
                    holder.priority.setTextColor(ContextCompat.getColor(context, R.color.primary_dark));
                    holder.priority.setText(MEDIUM_PRIORITY);
                    break;
                // 20
                case ExercisePriorityUtil.INCREMENTAL_VALUE * 2:
                    holder.priority.setTextColor(ContextCompat.getColor(context, R.color.secondary_dark));
                    holder.priority.setText(NORMAL_PRIORITY);
                    break;
                // 10
                case ExercisePriorityUtil.INCREMENTAL_VALUE:
                    holder.priority.setTextColor(ContextCompat.getColor(context, R.color.secondary_light));
                    holder.priority.setText(LOW_PRIORITY);
                    break;
                default:
                    holder.priority.setTextColor(ContextCompat.getColor(context, R.color.card_button_delete_select));
                    holder.priority.setText(HIDDEN);
                    break;
            }

            ExercisePriorityUtil.setPriorityButtons(priority, holder.morePriority, holder.lessPriority);

            holder.morePriority.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onSetExercisePriority(position, priority, true);
                }
            });

            holder.lessPriority.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onSetExercisePriority(position, priority, false);
                }
            });

            convertView.setTag(holder);
        }

        return convertView;
    }
}