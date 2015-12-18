package com.intencity.intencity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.User;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the ranking list.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class RankingListAdapter extends ArrayAdapter<User>
{
    private ArrayList<User> objects;

    private Context context;

    private ServiceListener serviceListener;

    private int id;

    /**
     * The constructor.
     *
     * @param context   The application context.
     * @param resId     The layout resource id for the list item.
     * @param users     The list of users to populate the list.
     */
    public RankingListAdapter(Context context, int resId, ArrayList<User> users, ServiceListener serviceListener)
    {
        super(context, resId, users);

        this.context = context;

        this.objects = users;

        this.serviceListener = serviceListener;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_ranking, null);
        }

        ImageButton add = (ImageButton) view.findViewById(R.id.button_add);
        add.setOnClickListener(addClickListener);

        User user = objects.get(position);

        if (user != null)
        {
            TextView rank = (TextView) view.findViewById(R.id.text_view_rank);
            TextView name = (TextView) view.findViewById(R.id.text_view_name);
            TextView points = (TextView) view.findViewById(R.id.text_view_points);

            id = user.getId();
            rank.setText(String.valueOf(position + 1));
            name.setText(user.getFirstName() + " " + user.getLastName());
            points.setText(String.valueOf(user.getEarnedPoints()));
        }

        return view;
    }

    private View.OnClickListener addClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            SecurePreferences securePreferences = new SecurePreferences(context);

            String email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

            new ServiceTask(serviceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                            Constant.getStoredProcedure(Constant.STORED_PROCEDURE_FOLLOW_USER,
                                                                                        email, String.valueOf(id)));
        }
    };
}