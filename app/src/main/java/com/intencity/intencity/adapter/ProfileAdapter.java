package com.intencity.intencity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.model.ProfileRow;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the profile screen.
 *
 * Created by Nick Piscopio on 2/7/16.
 */
public class ProfileAdapter extends ArrayAdapter<ProfileRow>
{
    private Context context;

    private int headerResId;
    private int listItemResId;

    private ArrayList<ProfileRow> sections;

    private LayoutInflater inflater;

    private int position;

    static class ProfileHolder
    {
        TextView title;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     * @param headerResId       The resource id of the view we are inflating for the headers.
     * @param listItemResId     The resource id of the view we are inflating for the list items.
     * @param sections          The sections for the listview for the profile screen.
     */
    public ProfileAdapter(Context context, int headerResId, int listItemResId,
                          ArrayList<ProfileRow> sections)
    {
        super(context, 0, sections);

        this.context = context;

        this.headerResId = headerResId;
        this.listItemResId = listItemResId;

        this.sections = sections;

        position = (int) Constant.CODE_FAILED;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ProfileHolder holder = (convertView == null) ?
                                            new ProfileHolder() :
                                            (ProfileHolder)convertView.getTag();

        if (this.position != position || convertView == null)
        {
            this.position = position;

            ProfileRow row = sections.get(position);

            convertView = inflater.inflate(row.isSectionHeader() ? headerResId : listItemResId, parent, false);

            holder.title = (TextView) convertView.findViewById(R.id.text_view);
            holder.title.setText(row.getTitle());

            convertView.setTag(holder);
        }

        return convertView;
    }
}