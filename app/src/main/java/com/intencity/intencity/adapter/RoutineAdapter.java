package com.intencity.intencity.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.model.RoutineRow;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the routine list.
 *
 * Created by Nick Piscopio on 5/6/16.
 */
public class RoutineAdapter extends ArrayAdapter<RoutineRow>
{
    private final int HEADER_RES_ID = R.layout.list_item_header;

    private Context context;

    private int headerResId;
    private int listItemResId;

    private ArrayList<RoutineRow> objects;

    private LayoutInflater inflater;

    private int position;

    static class Holder
    {
        TextView title;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     * @param headerResId       The resource id of the view we are inflating for the headers.
     * @param listItemResId     The resource id of the view we are inflating for the list items.
     * @param titles            The title of the row.
     */
    public RoutineAdapter(Context context, int headerResId, int listItemResId, ArrayList<RoutineRow> titles)
    {
        super(context, 0, titles);

        this.context = context;

        this.headerResId = headerResId;
        this.listItemResId = listItemResId;

        this.objects = titles;

        position = (int) Constant.CODE_FAILED;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final Holder holder = (convertView == null) ? new Holder() : (Holder)convertView.getTag();

        if (this.position != position || convertView == null)
        {
            this.position = position;

            RoutineRow row = objects.get(position);
            int rowNumber = row.getRowNumber();

            int resourceId = (rowNumber > Constant.CODE_FAILED ? listItemResId : headerResId);

            convertView = inflater.inflate(resourceId, parent, false);

            boolean isHeader = resourceId == HEADER_RES_ID;

            holder.title = (TextView) convertView.findViewById(isHeader ? R.id.text_view_header : android.R.id.text1);
            holder.title.setText(row.getTitle());

            if (!isHeader)
            {
                holder.title.setTextColor(ContextCompat.getColor(context, R.color.secondary_dark));
            }

            convertView.setTag(holder);
        }

        return convertView;
    }
}