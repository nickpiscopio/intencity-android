package com.intencity.intencity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.RankingListAdapter;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.User;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                                                        Constant.getStoredProcedure(
                                                                Constant.STORED_PROCEDURE_GET_FOLLOWING,
                                                                email));

        return view;
    }

    private void populateRankingList(ArrayList<User> rankings)
    {
        RankingListAdapter arrayAdapter = new RankingListAdapter(
                context,
                R.layout.list_item_ranking,
                rankings);

        ranking.setAdapter(arrayAdapter);
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        ArrayList<User> rankings = new ArrayList<>();

        try
        {

            JSONArray array = new JSONArray(response);

            int length = array.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject object = array.getJSONObject(i);

                User user = new User();
                user.setFirstName(object.getString(Constant.JSON_FIRST_NAME));
                user.setLastName(object.getString(Constant.JSON_LAST_NAME));
                user.setEarnedPoints(Integer.valueOf(object.getString(Constant.JSON_EARNED_POINTS)));

                rankings.add(user);
            }

            populateRankingList(rankings);
        }
        catch (JSONException e)
        {
            Log.e(Constant.TAG, "Error parsing muscle group data " + e.toString());
        }
    }

    @Override
    public void onRetrievalFailed() { }


}