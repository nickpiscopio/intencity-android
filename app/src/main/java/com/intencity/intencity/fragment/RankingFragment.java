package com.intencity.intencity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.SearchActivity;
import com.intencity.intencity.adapter.RankingListAdapter;
import com.intencity.intencity.helper.doa.UserDao;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.User;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

import java.util.ArrayList;

/**
 * The Ranking Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class RankingFragment extends android.support.v4.app.Fragment implements ServiceListener
{
    private Context context;

    private SwipeRefreshLayout swipeContainer;

    private ListView ranking;

    private FloatingActionButton findFriend;

    private TextView followingMessage;

    private String email;

    private ArrayList<User> users;

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

        followingMessage = (TextView) footer.findViewById(R.id.following_message);

//        LinearLayout searchUsers = (LinearLayout) footer.findViewById(R.id.searchUsers);
//        searchUsers.setOnClickListener(searchUsersListener);

        SecurePreferences securePreferences = new SecurePreferences(context);
        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        users = new ArrayList<>();

        getFollowing();

        return view;
    }

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
        RankingListAdapter arrayAdapter = new RankingListAdapter(context, R.layout.list_item_ranking, users, true);
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
}