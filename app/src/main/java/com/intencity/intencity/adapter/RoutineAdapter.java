package com.intencity.intencity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.model.SelectableListItem;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the routine list.
 *
 * Created by Nick Piscopio on 5/6/16.
 */
public class RoutineAdapter extends ArrayAdapter<SelectableListItem>
{
    private final int HEADER = 0;
    private final int ROW = 1;

    private Context context;

    private int headerResId;
    private int listItemResId;

    private ArrayList<SelectableListItem> objects;

    private LayoutInflater inflater;

    static class Holder
    {
        TextView title;
        RadioButton radioButton;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     * @param headerResId       The resource id of the view we are inflating for the headers.
     * @param listItemResId     The resource id of the view we are inflating for the list items.
     * @param titles            The title of the row.
     */
    public RoutineAdapter(Context context, int headerResId, int listItemResId, ArrayList<SelectableListItem> titles)
    {
        super(context, 0, titles);

        this.context = context;

        this.headerResId = headerResId;
        this.listItemResId = listItemResId;

        this.objects = titles;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        int type = getItemViewType(position);

        Holder holder;

        SelectableListItem row = objects.get(position);

        if (convertView == null)
        {
            holder = new Holder();

            int resourceId;
            int titleId;

            switch (type)
            {
                case HEADER:
                    resourceId = headerResId;
                    titleId = R.id.text_view_header;
                    break;
                case ROW:
                default:
                    resourceId = listItemResId;
                    titleId = R.id.text_view;
                    break;
            }

            convertView = inflater.inflate(resourceId, parent, false);

            holder.title = (TextView) convertView.findViewById(titleId);
            holder.radioButton = (RadioButton) convertView.findViewById(R.id.radio_button);

            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        holder.title.setText(row.getTitle());

        switch (type)
        {
            case ROW:
            default:
                if (holder.radioButton != null)
                {
                    holder.radioButton.setChecked(row.isSelected());
                }
                break;
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position)
    {
        return objects.get(position).getRowNumber() < 0 ? HEADER : ROW;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }
}