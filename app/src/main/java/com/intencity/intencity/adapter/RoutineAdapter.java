package com.intencity.intencity.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.model.RoutineGroup;
import com.intencity.intencity.model.RoutineSection;
import com.intencity.intencity.util.RoutineType;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the routine list.
 *
 * Created by Nick Piscopio on 5/5/16.
 */
public class RoutineAdapter extends ArrayAdapter<RoutineSection>
{
    private final int PLURAL_LIMIT = 2;

    private Context context;

    private int layoutResourceId;

    private ArrayList<RoutineSection> sections;

    private LayoutInflater inflater;

    private int position;

    static class RoutineHolder
    {
        CardView cardView;
        TextView title;
        TextView description;
        ImageView imageView;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     * @param layoutResourceId  The resource id of the view we are inflating.
     * @param sections          The routine sections for the list view.
     */
    public RoutineAdapter(Context context, int layoutResourceId, ArrayList<RoutineSection> sections)
    {
        super(context, layoutResourceId, sections);

        this.context = context;

        this.layoutResourceId = layoutResourceId;

        this.sections = sections;

        position = -1;



        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final RoutineHolder holder = (convertView == null) ?
                                            new RoutineHolder() :
                                            (RoutineHolder)convertView.getTag();

        if (this.position != position || convertView == null)
        {
            this.position = position;

            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder.cardView = (CardView) convertView.findViewById(R.id.card_view);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.imageView = (ImageView) convertView.findViewById(R.id.routine_image);

            RoutineSection section = sections.get(position);
            ArrayList<RoutineGroup> groups = section.getRoutineGroups();

            RoutineType type = section.getType();
            String description;

            if (groups != null)
            {
                int routineTotal = 0;

                for (RoutineGroup group : groups)
                {
                    routineTotal += group.getRows().size();
                }

                int descriptionRes = routineTotal < PLURAL_LIMIT ? R.string.description_default_routine : R.string.description_default_routines;

                description = context.getString(descriptionRes, String.valueOf(routineTotal));
            }
            else
            {
                description = context.getString(R.string.description_custom_routines);
            }

            holder.title.setText(section.getTitle());
            holder.description.setText(description);

            switch (type)
            {
                case CONTINUE:
                    break;
                case CUSTOM_ROUTINE:
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.secondary_dark));
                    holder.imageView.setImageResource(R.mipmap.custom_routine_background);
                    break;
                case INTENCITY_ROUTINE:
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.secondary_light));
                    holder.imageView.setImageResource(R.mipmap.intencity_routine_background);
                    break;
                case SAVED_ROUTINE:
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                    holder.imageView.setImageResource(R.mipmap.saved_routine_background);
                    break;
                default:
                    break;
            }

            convertView.setTag(holder);
        }

        return convertView;
    }
}