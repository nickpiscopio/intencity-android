package com.intencity.intencity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.model.SelectableListItem;
import com.intencity.intencity.util.Util;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the routine list.
 *
 * Created by Nick Piscopio on 5/6/16.
 */
public class CheckboxAdapter extends ArrayAdapter<SelectableListItem>
{
    private Context context;

    private int listItemResId;

    private ArrayList<SelectableListItem> objects;

    private LayoutInflater inflater;

    static class Holder
    {
        TextView title;
        TextView description;
        ImageView edit;
        CheckBox checkbox;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     * @param listItemResId     The resource id of the view we are inflating for the list items.
     * @param titles            The title of the row.
     */
    public CheckboxAdapter(Context context, int listItemResId, ArrayList<SelectableListItem> titles)
    {
        super(context, 0, titles);

        this.context = context;

        this.listItemResId = listItemResId;

        this.objects = titles;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Holder holder;

        if (convertView == null)
        {
            holder = new Holder();

            convertView = inflater.inflate(listItemResId, parent, false);

            holder.title = (TextView) convertView.findViewById(R.id.text_view_title);
            holder.description = (TextView) convertView.findViewById(R.id.text_view_description);
            holder.edit = (ImageView) convertView.findViewById(R.id.edit);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);

            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        SelectableListItem row = objects.get(position);

        String title = row.getTitle();
        String description = row.getDescription();

        boolean hasTitle = title.length() > 0;
        if (hasTitle)
        {
            holder.title.setText(title);
            holder.title.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.title.setVisibility(View.GONE);
        }

        holder.checkbox.setChecked(row.isChecked());

        if(description != null && description.length() > 0)
        {
            Resources res = context.getResources();

            if (hasTitle)
            {
                int paddingTop = (int) Util.convertDpToPixel(res.getDimension(R.dimen.layout_margin_sixteenth));

                holder.description.setTextColor(ContextCompat.getColor(context, R.color.secondary_light));
                holder.description.setPadding(0, paddingTop, 0, 0);
                holder.description.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.card_title3_size));
            }
            else
            {
                holder.description.setTextColor(ContextCompat.getColor(context, R.color.secondary_dark));
                holder.description.setPadding(0, 0, 0, 0);
                holder.description.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.card_title_size));
            }

            holder.description.setText(description);
            holder.description.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.description.setVisibility(View.GONE);
        }

        if(row.isDeletionEnabled())
        {
            holder.edit.setVisibility(View.GONE);
            holder.checkbox.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.edit.setVisibility(View.VISIBLE);
            holder.checkbox.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * Changes all the deletion flag for all the objects.
     *
     * @param deletionEnabled   Boolean value of whether the deletion flag is enabled or not.
     */
    public void setDeletionEnabled(boolean deletionEnabled)
    {
        for (SelectableListItem listItem : objects)
        {
            listItem.setDeletionEnabled(deletionEnabled);
        }

        notifyDataSetChanged();
    }
}