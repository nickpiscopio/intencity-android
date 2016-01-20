package com.intencity.intencity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

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

    private ListView ranking;

    private LinearLayout noFriends;
    private LinearLayout searchUsers;

    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_intencity_rankings, container, false);

        ranking = (ListView) view.findViewById(R.id.list_view_ranking);

        noFriends = (LinearLayout) view.findViewById(R.id.layout_no_friends);
        searchUsers = (LinearLayout) view.findViewById(R.id.searchUsers);
        searchUsers.setOnClickListener(searchUsersListener);

        context = getContext();

        SecurePreferences securePreferences = new SecurePreferences(context);

        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        getFollowing();

        return view;
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        populateRankingList(new UserDao().parseJson(response));
    }

    @Override
    public void onRetrievalFailed() { }

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
     *
     * @param users  The list of users.
     */
    private void populateRankingList(ArrayList<User> users)
    {
        RankingListAdapter arrayAdapter = new RankingListAdapter(context, R.layout.list_item_ranking, users, true);
        ranking.setAdapter(arrayAdapter);

        // The size will be 1 if there are no followers because
        // we get back the current user logged in as well.
        if (users.size() > 1)
        {
            noFriends.setVisibility(View.GONE);
        }
        else
        {
            noFriends.setVisibility(View.VISIBLE);
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