package com.intencity.intencity.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.RankingListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.User;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The custom ArrayAdapter for the ranking list.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class RankingListAdapter extends ArrayAdapter<User>
{
    private Context context;

    private RankingListener listener;

    private int layoutResourceId;

    private ArrayList<User> objects;

    private boolean isSearch;

    private LayoutInflater inflater;

    private int position;

    static class RankingListHolder
    {
        LinearLayout badgesLayout;
        TextView rank;
        TextView name;
        TextView points;
        TextView totalBadgesTextView;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     * @param listener          The listener to call back when we want to remove a user.
     * @param layoutResourceId  The resource id of the view we are inflating.
     * @param users             The list of users to populate the list.
     * @param isSearch          A boolean value of whether or not we are searching for a user.
     */
    public RankingListAdapter(Context context, RankingListener listener, int layoutResourceId, ArrayList<User> users, boolean isSearch)
    {
        super(context, layoutResourceId, users);

        this.context = context;

        this.listener = listener;

        this.layoutResourceId = layoutResourceId;

        this.objects = users;

        this.isSearch = isSearch;

        position = -1;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final RankingListHolder holder = (convertView == null) ?
                                            new RankingListHolder() :
                                            (RankingListHolder)convertView.getTag();

        if (this.position != position || convertView == null)
        {
            this.position = position;
            final int arrayIndex = position;

            convertView = inflater.inflate(layoutResourceId, parent, false);

            final User user = objects.get(position);

            holder.badgesLayout = (LinearLayout) convertView.findViewById(R.id.layout_badges);
            holder.name = (TextView) convertView.findViewById(R.id.text_view_name);
            holder.points = (TextView) convertView.findViewById(R.id.text_view_points);
            holder.totalBadgesTextView = (TextView) convertView.findViewById(R.id.total_badges);

            holder.name.setText(user.getFirstName() + " " + user.getLastName());
            holder.points.setText(String.valueOf(user.getEarnedPoints()));

            if (!isSearch)
            {
                holder.rank = (TextView) convertView.findViewById(R.id.text_view_rank);
                holder.rank.setText(String.valueOf(position + 1));
                holder.rank.setVisibility(View.VISIBLE);

                final int followingId = user.getFollowingId();

                if (followingId > 0)
                {
                    // The remove user button.
                    final TextView deleteButton = (TextView) convertView.findViewById(R.id.button_remove);

                    final View view = convertView;

                    // The long click listener for the ListView to remove followers.
                    convertView.setOnLongClickListener(new View.OnLongClickListener()
                    {
                        @Override
                        public boolean onLongClick(View v)
                        {
                            int timeOut = 4500;

                            deleteButton.setVisibility(View.VISIBLE);
                            deleteButton.setOnClickListener(new View.OnClickListener()

                            {
                                @Override
                                public void onClick(View v)
                                {
                                    // Stop following a user.
                                    listener.onRemoveUser(view, arrayIndex, followingId);
                                }
                            });

                            // Dismiss the dialog after so many seconds.
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask()
                            {
                                @Override
                                public void run()
                                {
                                    new Handler(Looper.getMainLooper()).post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            deleteButton.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }, timeOut);
                            return false;
                        }
                    });
                }
            }

            int totalBadges = user.getTotalBadges();
            if (totalBadges > 0)
            {
                holder.totalBadgesTextView.setText(String.valueOf(totalBadges));

                holder.badgesLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.badgesLayout.setVisibility(View.GONE);
            }

            final int id = user.getId();

            // This isn't in the holder because when we set it to invisible, it can't see it.
            final ImageButton addButton = (ImageButton) convertView.findViewById(R.id.button_add);

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
                            // Set the following ID to 0, so when this view is recreated,
                            // we don't get the button back.
                            user.setFollowingId(0);
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
            if (user.getFollowingId() == Constant.CODE_FAILED && isSearch)
            {
                addButton.setVisibility(View.VISIBLE);
            }
            else
            {
                // Remove the ability to add a user if he or she is already connected to the user.
                addButton.setVisibility(View.GONE);
            }

            convertView.setTag(holder);
        }

        return convertView;
    }
}