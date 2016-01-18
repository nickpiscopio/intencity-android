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

    /**
     * The constructor.
     *
     * @param context   The application context.
     * @param users     The list of users to populate the list.
     */
    public RankingListAdapter(Context context, ArrayList<User> users)
    {
        super(context, 0, users);

        this.context = context;

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

        TextView rank = (TextView) view.findViewById(R.id.text_view_rank);
        TextView name = (TextView) view.findViewById(R.id.text_view_name);
        TextView points = (TextView) view.findViewById(R.id.text_view_points);

        rank.setText(String.valueOf(position + 1));
        name.setText(user.getFirstName() + " " + user.getLastName());
        points.setText(String.valueOf(user.getEarnedPoints()));
        final int id = user.getId();

        final ImageButton addButton = (ImageButton) view.findViewById(R.id.button_add);

        // Needed to add the click listener in getView because the id was
        // getting overwritten by the last id in the list.
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SecurePreferences securePreferences = new SecurePreferences(context);

                String email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

                // Needed to add the service listener in the parameter because the ImageButton was
                // getting overwritten by the last ImageButton in the list.
                new ServiceTask(new ServiceListener()
                {
                    @Override
                    public void onRetrievalSuccessful(String response)
                    {
                        // Remove the ability to add a user since said user was just added to follow.
                        addButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onRetrievalFailed() { }
                }).execute(Constant.SERVICE_STORED_PROCEDURE,
                           Constant.generateStoredProcedureParameters(
                                   Constant.STORED_PROCEDURE_FOLLOW_USER, email, String.valueOf(id)));
            }
        });

        // CODE_FAILED should get returned if the user isn't connected to the
        // person that was returned from the search term.
        if (user.getFollowingId() == Constant.CODE_FAILED)
        {
            addButton.setVisibility(View.VISIBLE);
        }
        else
        {
            // Remove the ability to add a user if he or she is already connected to the user.
            addButton.setVisibility(View.GONE);
        }

        return view;
    }
}