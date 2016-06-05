package com.intencity.intencity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.model.SelectableListItem;
import com.intencity.intencity.model.RoutineSection;
import com.intencity.intencity.util.Constant;
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

    private int layoutResId;

    private ArrayList<RoutineSection> sections;

    private LayoutInflater inflater;

    private int lastPosition = (int)Constant.CODE_FAILED;

    static class RoutineHolder
    {
        CardView cardView;
        LinearLayout indicatorLayout;
        TextView title;
        TextView description;
        ImageView nextImage;
        ImageView imageView;
    }

    /**
     * The constructor.
     *
     * @param context       The application context.
     * @param layoutResId   The resource id of the continue card we are inflating.
     * @param sections  The routine sections for the list view.
     */
    public RoutineSectionAdapter(Context context, int layoutResId, ArrayList<RoutineSection> sections)
    {
        super(context, layoutResId, sections);

        this.context = context;
        this.res = context.getResources();

        this.layoutResId = layoutResId;

        this.sections = sections;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        RoutineSection section = sections.get(position);
        RoutineType type = section.getType();

        // Avoid unnecessary calls to findViewById() on each row, which is expensive!
        RoutineHolder holder;

        // If convertView is not null, we can reuse it directly, no inflation required!
        // We only inflate a new View when the convertView is null.
        if (convertView == null || type == RoutineType.CONTINUE)
        {
            convertView = inflater.inflate(layoutResId, parent, false);

            // Create a ViewHolder and store references to the two children views
            holder = new RoutineHolder();
            holder.cardView = (CardView) convertView.findViewById(R.id.card_view);
            holder.indicatorLayout = (LinearLayout) convertView.findViewById(R.id.layout_indicator);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.nextImage = (ImageView) convertView.findViewById(R.id.image_next);
            holder.imageView = (ImageView) convertView.findViewById(R.id.routine_image);

            // The tag can be any Object, this just happens to be the ViewHolder
            convertView.setTag(holder);
        }
        else
        {
            // Get the ViewHolder back to get fast access to the views in the layout.
            holder = (RoutineHolder) convertView.getTag();
        }

        ArrayList<SelectableListItem> rows = section.getRoutineRows();

        int[] keys = section.getKeys();
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
            holder.title.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.card_title_size));
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(description);
            holder.indicatorLayout.removeAllViews();
            holder.imageView.setVisibility(View.VISIBLE);
            holder.nextImage.setVisibility(View.GONE);

            for (int key : keys)
            {
                ImageView imageView = new ImageView(context);

                switch (key)
                {
                    case RoutineKey.USER_SELECTED:
                        imageView.setImageResource(R.drawable.circle_red);
                        break;
                    case RoutineKey.RANDOM:
                        imageView.setImageResource(R.drawable.circle_active);
                        break;
                    case RoutineKey.CONSECUTIVE:
                        imageView.setImageResource(R.drawable.circle_primary_dark);
                        break;
                    default:
                        break;
                }

                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                imageView.setPadding(res.getDimensionPixelSize(R.dimen.layout_margin_quarter), 0, 0, 0);

                holder.indicatorLayout.addView(imageView);
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
        }
        else
        {
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.secondary_light));
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.card_continue_title_size));
            holder.description.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.nextImage.setVisibility(View.VISIBLE);
        }

        setAnimation(holder.cardView, position);

        return convertView;
    }

    /**
     * Sets the animation for a view.
     *
     * @param viewToAnimate     The view to set an animation.
     * @param position          The current position of the recycler view.
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen.
        // Add animation.
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}