package com.intencity.intencity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.model.RoutineRow;
import com.intencity.intencity.model.RoutineSection;
import com.intencity.intencity.util.RoutineKey;
import com.intencity.intencity.util.RoutineType;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the routine section list.
 *
 * Created by Nick Piscopio on 5/5/16.
 */
public class RoutineSectionAdapter extends ArrayAdapter<RoutineSection>
{
    private final int DEFAULT_ROUTINE_TOTAL = 6;

    private final int PLURAL_LIMIT = 2;

    private Context context;
    private Resources res;

    private int layoutResourceContinueId;
    private int layoutResourceRoutineId;

    private ArrayList<RoutineSection> sections;

    private LayoutInflater inflater;

    private int position;

    static class RoutineHolder
    {
        CardView cardView;
        LinearLayout titleLayout;
        TextView title;
        TextView description;
        ImageView imageView;
    }

    /**
     * The constructor.
     *
     * @param context                   The application context.
     * @param layoutResourceContinueId  The resource id of the continue card we are inflating.
     * @param layoutResourceRoutineId   The resource id of the routine card  we are inflating.
     * @param sections                  The routine sections for the list view.
     */
    public RoutineSectionAdapter(Context context, int layoutResourceContinueId, int layoutResourceRoutineId, ArrayList<RoutineSection> sections)
    {
        super(context, layoutResourceContinueId, layoutResourceRoutineId, sections);

        this.context = context;
        this.res = context.getResources();

        this.layoutResourceContinueId = layoutResourceContinueId;
        this.layoutResourceRoutineId = layoutResourceRoutineId;

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

            RoutineSection section = sections.get(position);
            ArrayList<RoutineRow> rows = (ArrayList<RoutineRow>)section.getRoutineRows();

            int[] keys = section.getKeys();

            RoutineType type = section.getType();

            convertView = inflater.inflate(type == RoutineType.CONTINUE ? layoutResourceContinueId : layoutResourceRoutineId, parent, false);

            holder.cardView = (CardView) convertView.findViewById(R.id.card_view);
            holder.titleLayout = (LinearLayout) convertView.findViewById(R.id.layout_title);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.imageView = (ImageView) convertView.findViewById(R.id.routine_image);

            String description;

            if (rows != null)
            {
                int routineTotal = 0;

                routineTotal += rows.size();

                if (type == RoutineType.INTENCITY_ROUTINE)
                {
                    for (int i = 0; i < 2; i++)
                    {
                        if (routineTotal > DEFAULT_ROUTINE_TOTAL)
                        {
                            routineTotal--;
                        }
                    }
                }

                int descriptionRes = routineTotal < PLURAL_LIMIT ? R.string.description_default_routine : R.string.description_default_routines;

                description = context.getString(descriptionRes, String.valueOf(routineTotal));
            }
            else
            {
                description = context.getString(R.string.description_custom_routines);
            }

            holder.title.setText(section.getTitle());

            // This means we aren't using the continue routine card.
            if (keys != null)
            {
                holder.description.setText(description);

                for (int key : keys)
                {
                    ImageView imageView = new ImageView(context);

                    switch (key)
                    {
                        case RoutineKey.USER_SELECTED:
                            imageView.setImageResource(R.drawable.circle_red);
                            break;
                        case RoutineKey.RANDOM:
                            imageView.setImageResource(R.drawable.circle_accent);
                            break;
                        case RoutineKey.CONSECUTIVE:
                            imageView.setImageResource(R.drawable.circle_primary_dark);
                            break;
                        default:
                            break;
                    }

                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setPadding(res.getDimensionPixelSize(R.dimen.layout_margin_quarter), 0, 0, 0);

                    holder.titleLayout.addView(imageView);
                }
            }

            switch (type)
            {
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