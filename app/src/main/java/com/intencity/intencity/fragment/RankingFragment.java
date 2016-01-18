package com.intencity.intencity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.intencity.intencity.R;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_intencity_rankings, container, false);

        ranking = (ListView) view.findViewById(R.id.list_view_ranking);

        context = getContext();

        SecurePreferences securePreferences = new SecurePreferences(context);

        String email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");


        new ServiceTask(this).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                        Constant.generateStoredProcedureParameters(
                                                                Constant.STORED_PROCEDURE_GET_FOLLOWING,
                                                                email));

        return view;
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        populateRankingList(new UserDao().parseJson(response));
    }

    @Override
    public void onRetrievalFailed() { }

    /**
     * Populates the ranking list.
     *
     * @param users  The list of users.
     */
    private void populateRankingList(ArrayList<User> users)
    {
        RankingListAdapter arrayAdapter = new RankingListAdapter(context, users);

        ranking.setAdapter(arrayAdapter);
    }
}