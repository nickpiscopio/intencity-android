package com.intencity.intencity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.model.MenuItem;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the menu list.
 *
 * Created by Nick Piscopio on 1/26/16.
 */
public class MenuAdapter extends ArrayAdapter<MenuItem>
{
    private final int HEADER_RES_ID = R.layout.list_item_header;

    private Context context;

    private int headerResId;
    private int listItemResId;

    private ArrayList<MenuItem> objects;

    private LayoutInflater inflater;

    private int position;

    static class MenuHolder
    {
        TextView title;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     * @param headerResId       The resource id of the view we are inflating for the headers.
     * @param listItemResId     The resource id of the view we are inflating for the list items.
     * @param awards            The list of awards the user received.
     */
    public MenuAdapter(Context context, int headerResId, int listItemResId, ArrayList<MenuItem> awards)
    {
        super(context, 0, awards);

        this.context = context;

        this.headerResId = headerResId;
        this.listItemResId = listItemResId;

        this.objects = awards;

        position = (int) Constant.CODE_FAILED;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final MenuHolder holder = (convertView == null) ? new MenuHolder() : (MenuHolder)convertView.getTag();

        if (this.position != position || convertView == null)
        {
            this.position = position;

            MenuItem item = objects.get(position);

            int resourceId = (item.getCls() == null &&
                              !item.getTitle().equals(context.getString(R.string.title_log_out))) &&
                              !item.getTitle().equals(context.getString(R.string.title_rate_intencity)) ? headerResId : listItemResId;

            convertView = inflater.inflate(resourceId, parent, false);

            holder.title = (TextView) convertView.findViewById(resourceId == HEADER_RES_ID ? R.id.text_view_header : R.id.text_view);
            holder.title.setText(item.getTitle());

            convertView.setTag(holder);
        }

        return convertView;
    }
}