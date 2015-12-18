package com.intencity.intencity.doa;

import android.util.Log;

import com.intencity.intencity.model.User;
import com.intencity.intencity.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The data access object for the User class.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class UserDao
{
    public ArrayList<User> parseJson(String response)
    {
        ArrayList<User> users = new ArrayList<>();

        try
        {
            JSONArray array = new JSONArray(response);

            int length = array.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject object = array.getJSONObject(i);

                User user = new User();
                user.setId(Integer.valueOf(object.getString(Constant.JSON_ID)));
                user.setEarnedPoints(Integer.valueOf(object.getString(Constant.JSON_EARNED_POINTS)));
                user.setFirstName(object.getString(Constant.JSON_FIRST_NAME));
                user.setLastName(object.getString(Constant.JSON_LAST_NAME));

                users.add(user);
            }
        }
        catch (JSONException exception)
        {
            Log.e(Constant.TAG, "Couldn't parse search " + exception.toString());
        }

        return users;
    }
}