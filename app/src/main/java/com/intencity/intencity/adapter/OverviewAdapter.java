package com.intencity.intencity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.intencity.intencity.model.SelectableListItem;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the Overview list.
 *
 * Created by Nick Piscopio on 5/6/16.
 */
public class OverviewAdapter extends ArrayAdapter<SelectableListItem>
{
    private Context context;

    private int listItemResId;

    private ArrayList<SelectableListItem> objects;

    private LayoutInflater inflater;

    static class Holder
    {
        TextView title;
        CheckBox checkbox;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     */
    public OverviewAdapter(Context context)
    {
        super(context, 0);

        this.context = context;

        this.listItemResId = listItemResId;

        this.objects = null;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
//        Holder holder;
//
//        if (convertView == null)
//        {
//            holder = new Holder();
//
//            convertView = inflater.inflate(listItemResId, parent, false);
//
//            holder.title = (TextView) convertView.findViewById(R.id.text_view);
//            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
//
//            convertView.setTag(holder);
//        }
//        else
//        {
//            holder = (Holder) convertView.getTag();
//        }
//
//        SelectableListItem row = objects.get(position);
//
//        holder.title.setText(row.getTitle());
//        holder.checkbox.setChecked(row.isChecked());
//
//        return convertView;

        return null;
    }
}