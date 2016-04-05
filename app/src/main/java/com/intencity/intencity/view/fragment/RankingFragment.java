package com.intencity.intencity.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.RankingListAdapter;
import com.intencity.intencity.helper.doa.UserDao;
import com.intencity.intencity.listener.RankingListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.User;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;
import com.intencity.intencity.util.Util;
import com.intencity.intencity.view.activity.ProfileActivity;
import com.intencity.intencity.view.activity.SearchActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The Ranking Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class RankingFragment extends android.support.v4.app.Fragment implements ServiceListener,
                                                                                RankingListener
{
    private Context context;

    private SwipeRefreshLayout swipeContainer;

    private ListView ranking;

    private FloatingActionButton findFriend;

    private TextView followingMessage;

    private String email;

    private ArrayList<User> users;

    private RankingListAdapter arrayAdapter;

    private Animation animation;

    private int row;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        context = getContext();

        View view = inflater.inflate(R.layout.fragment_intencity_rankings, container, false);
        View footer = inflater.inflate(R.layout.fragment_intencity_rankings_footer, null);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(refreshListener);

        findFriend = (FloatingActionButton) view.findViewById(R.id.button_next);
        findFriend.setOnClickListener(searchUsersListener);

        ranking = (ListView) view.findViewById(R.id.list_view_ranking);
        ranking.addFooterView(footer, null, false);
        ranking.setOnItemClickListener(userClickListener);

        animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_right);
        animation.setAnimationListener(animationListener);

        followingMessage = (TextView) footer.findViewById(R.id.following_message);

        SecurePreferences securePreferences = new SecurePreferences(context);
        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        users = new ArrayList<>();

        getFollowing();

        return view;
    }

    /**
     * The animation listener for removing a user from the following list.
     */
    private Animation.AnimationListener animationListener = new Animation.AnimationListener()
    {
        @Override public void onAnimationStart(Animation animation) { }

        @Override public void onAnimationRepeat(Animation animation) { }

        @Override
        public void onAnimationEnd(Animation animation)
        {
            arrayAdapter.remove(arrayAdapter.getItem(row));
            arrayAdapter.notifyDataSetChanged();
        }
    };

    /**
     * The refresh listener for the listview.
     */
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener()
    {
        @Override
        public void onRefresh()
        {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            getFollowing();

            Calendar cal = Calendar.getInstance();
            // We set the timezone to New York because this is where the server is located.
            cal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            int daysInWeek = 7;
            int today = cal.get(Calendar.DAY_OF_WEEK);

            // If Sunday,
            // Subtract the current numerical day of the week to get the number of days needed to get to Monday.
            // Else
            // Subtract today's date from the number of days in the week (7), then add Monday (2).
            // This gets us how many days we need to add to get to monday.
            cal.add(Calendar.DAY_OF_MONTH, today < Calendar.MONDAY ? Calendar.MONDAY - today : daysInWeek - today + Calendar.MONDAY);

            boolean is24HourFormat = DateFormat.is24HourFormat(context);
            SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d " + (is24HourFormat ? "HH:mm" : "h:mm a"), Locale.getDefault());

            String date = format.format(cal.getTime());
            Toast.makeText(context, context.getString(R.string.rankings_updated, date),
                               Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onRetrievalSuccessful(String response)
    {
        swipeContainer.setRefreshing(false);

        users = new UserDao().parseJson(response);

        populateRankingList();
    }

    @Override
    public void onRetrievalFailed()
    {
        swipeContainer.setRefreshing(false);

        populateRankingList();
    }

    /**
     * The click listener for searching for a user.
     */
    private View.OnClickListener searchUsersListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constant.BUNDLE_SEARCH_EXERCISES, false);

            Intent intent = new Intent(RankingFragment.this.getActivity(), SearchActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, Constant.REQUEST_CODE_SEARCH);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Constant.REQUEST_CODE_SEARCH)
        {
            getFollowing();
        }
    }

    /**
     * Populates the ranking list.
     */
    private void populateRankingList()
    {
        arrayAdapter = new RankingListAdapter(context, this, R.layout.list_item_ranking, users, false);
        ranking.setAdapter(arrayAdapter);

        // The size will be 1 if there are no followers because
        // we get back the current user logged in as well.
        if (users.size() > 1)
        {
            followingMessage.setVisibility(View.GONE);
        }
        else
        {
            followingMessage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Gets the users that the logged in user is following.
     */
    public void getFollowing()
    {
        new ServiceTask(this).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(
                                              Constant.STORED_PROCEDURE_GET_FOLLOWING, email));
    }

    /**
     * The click listener for each user clicked in the listview.
     */
    private AdapterView.OnItemClickListener userClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            User user  = users.get(position);

            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(Constant.BUNDLE_USER, user);

            startActivity(intent);
        }
    };

    @Override
    public void onRemoveUser(View view, int position, int webServerId)
    {
        final int pos = position;
        final View v = view;

        new ServiceTask(new ServiceListener()
        {
            @Override
            public void onRetrievalSuccessful(String response)
            {
                row = pos;
                v.startAnimation(animation);
            }

            @Override
            public void onRetrievalFailed()
            {
                Util.showMessage(context, context.getString(R.string.generic_error), context.getString(R.string.intencity_communication_error));
            }
        }).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(
                                              Constant.STORED_PROCEDURE_REMOVE_FROM_FOLLOWING, String.valueOf(webServerId)));
    }
}