package com.intencity.intencity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.model.MenuItem;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the menu list.
 *
 * Created by Nick Piscopio on 1/26/16.
 */
public class MenuAdapter extends ArrayAdapter<MenuItem>
{
    private final int HEADER = 0;
    private final int ROW = 1;

    private Context context;

    private int headerResId;
    private int listItemResId;

    private ArrayList<MenuItem> objects;

    private LayoutInflater inflater;

    static class ViewHolder
    {
        TextView title;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     * @param headerResId       The resource id of the view we are inflating for the headers.
     * @param listItemResId     The resource id of the view we are inflating for the list items.
     * @param items             The list of menu items.
     */
    public MenuAdapter(Context context, int headerResId, int listItemResId, ArrayList<MenuItem> items)
    {
        super(context, 0, items);

        this.context = context;

        this.headerResId = headerResId;
        this.listItemResId = listItemResId;

        this.objects = items;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        int type = getItemViewType(position);

        MenuItem item = objects.get(position);

        ViewHolder holder;

        if (convertView == null)
        {
            holder = new ViewHolder();

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

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(item.getTitle());

        return convertView;
    }

    @Override
    public int getItemViewType(int position)
    {
        MenuItem item = objects.get(position);

        return (item.getCls() == null &&
                !item.getTitle().equals(context.getString(R.string.title_log_out))) &&
                !item.getTitle().equals(context.getString(R.string.title_rate_intencity)) &&
                !item.getTitle().equals(context.getString(R.string.title_contribute_intencity)) ? HEADER : ROW;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }
}