package com.intencity.intencity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.model.User;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the ranking list.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class RankingListAdapter extends ArrayAdapter<User>
{
    private ArrayList<User> objects;

    /**
     * The constructor.
     *
     * @param context   The application context.
     * @param resId     The layout resource id for the list item.
     * @param users     The list of users to populate the list.
     */
    public RankingListAdapter(Context context, int resId, ArrayList<User> users)
    {
        super(context, resId, users);

        this.objects = users;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_ranking, null);
        }

        User user = objects.get(position);

        if (user != null)
        {
            TextView rank = (TextView) view.findViewById(R.id.text_view_rank);
            TextView name = (TextView) view.findViewById(R.id.text_view_name);
            TextView points = (TextView) view.findViewById(R.id.text_view_points);

            rank.setText(String.valueOf(position + 1));
            name.setText(user.getFirstName() + " " + user.getLastName());
            points.setText(String.valueOf(user.getEarnedPoints()));
        }

        return view;
    }
}