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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.LeaderboardAdapter;
import com.intencity.intencity.helper.doa.UserDao;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.User;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.Util;
import com.intencity.intencity.view.activity.ProfileActivity;
import com.intencity.intencity.view.activity.SearchActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The Leaderboard Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class LeaderboardFragment extends android.support.v4.app.Fragment implements ServiceListener
{
    private Context context;

    private SwipeRefreshLayout swipeContainer;

    private ListView ranking;

    private FloatingActionButton findFriend;

    private TextView followingMessage;

    private int userId;

    private ArrayList<User> users;

    private LeaderboardAdapter arrayAdapter;

    private ImageLoader imageLoaderInstance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        context = getContext();

        View view = inflater.inflate(R.layout.fragment_intencity_rankings, container, false);
        View footer = inflater.inflate(R.layout.fragment_intencity_rankings_footer, null);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(refreshListener);

        findFriend = (FloatingActionButton) view.findViewById(R.id.button_add);
        findFriend.setOnClickListener(searchUsersListener);

        ranking = (ListView) view.findViewById(R.id.list_view_ranking);
        ranking.addFooterView(footer, null, false);
        ranking.setOnItemClickListener(userClickListener);

        followingMessage = (TextView) footer.findViewById(R.id.following_message);

        userId = Util.getSecurePreferencesUserId(context);

        users = new ArrayList<>();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        imageLoaderInstance = ImageLoader.getInstance();
        imageLoaderInstance.init(config);
        imageLoaderInstance.clearDiskCache();

        getFollowing();

        return view;
    }

    /**
     * The refresh listener for the ListView.
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
    public void onServiceResponse(int statusCode, String response)
    {
        switch (statusCode)
        {
            case Constant.STATUS_CODE_SUCCESS_STORED_PROCEDURE:

                swipeContainer.setRefreshing(false);

                users = new UserDao().parseJson(response);

                populateRankingList();

                break;

            case Constant.STATUS_CODE_FAILURE_STORED_PROCEDURE:
            default:

                swipeContainer.setRefreshing(false);

                populateRankingList();

                break;
        }
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

            Intent intent = new Intent(LeaderboardFragment.this.getActivity(), SearchActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, Constant.REQUEST_CODE_SEARCH);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Constant.REQUEST_CODE_SEARCH || resultCode == Constant.REQUEST_CODE_PROFILE)
        {
            if (resultCode == Constant.REQUEST_CODE_PROFILE && data != null)
            {
                Bundle bundle = data.getExtras();

                int userIndex = bundle.getInt(Constant.BUNDLE_POSITION);

                User user = users.get(userIndex);

                String path = Constant.UPLOAD_FOLDER + user.getId() + Constant.USER_PROFILE_PIC_NAME;

                MemoryCacheUtils.removeFromCache(path, imageLoaderInstance.getMemoryCache());
                DiskCacheUtils.removeFromCache(path, imageLoaderInstance.getDiskCache());

                populateRankingList();
            }
            else
            {
                getFollowing();
            }
        }
    }

    /**
     * Populates the ranking list.
     */
    private void populateRankingList()
    {
        arrayAdapter = new LeaderboardAdapter(context, R.layout.list_item_ranking, users, false);
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
                                              Constant.STORED_PROCEDURE_GET_FOLLOWING, userId));
    }

    /**
     * The click listener for each user clicked in the ListView.
     */
    private AdapterView.OnItemClickListener userClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            User user  = users.get(position);

            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(Constant.BUNDLE_POSITION, position);
            intent.putExtra(Constant.BUNDLE_USER, user);
            intent.putExtra(Constant.BUNDLE_PROFILE_IS_USER, user.getFollowingId() < 0);

            startActivityForResult(intent, Constant.REQUEST_CODE_PROFILE);
        }
    };
}