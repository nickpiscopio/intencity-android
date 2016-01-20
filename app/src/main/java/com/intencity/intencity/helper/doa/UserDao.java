package com.intencity.intencity.helper.doa;

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
    /**
     * Parses the JSON response.
     *
     * @param response  The String response from the server in a JSON format.
     *
     * @return  The ArrayList of users.
     */
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

                // Only try to get the FollowingId if the service is searching for it.
                if (object.has(Constant.COLUMN_FOLLOWING_ID))
                {
                    String followingId = object.getString(Constant.COLUMN_FOLLOWING_ID);
                    user.setFollowingId(followingId.equalsIgnoreCase(Constant.RETURN_NULL) ? Constant.CODE_FAILED :
                                                Integer.valueOf(followingId));
                }

                user.setId(Integer.valueOf(object.getString(Constant.COLUMN_ID)));
                user.setEarnedPoints(
                        Integer.valueOf(object.getString(Constant.COLUMN_EARNED_POINTS)));
                user.setTotalBadges(Integer.valueOf(object.getString(Constant.COLUMN_TOTAL_BADGES)));
                user.setFirstName(object.getString(Constant.COLUMN_FIRST_NAME));
                user.setLastName(object.getString(Constant.COLUMN_LAST_NAME));

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