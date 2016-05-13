package com.intencity.intencity.helper.doa;

import com.intencity.intencity.model.SelectableListItem;
import com.intencity.intencity.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The data access object for the RoutineSection class.
 *
 * Created by Nick Piscopio on 5/5/16.
 */
public class UserRoutineDao
{
    /**
     * Parses the JSON response.
     *
     * @param response  The String response from the server in a JSON format.
     *
     * @return  The ArrayList of sections for a routine list.
     */
    public ArrayList<SelectableListItem> parseJson(String response) throws JSONException
    {
        ArrayList<SelectableListItem> rows = new ArrayList<>();

        JSONArray array = new JSONArray(response);

        int length = array.length();

        for (int i = 0; i < length; i++)
        {
            JSONObject object = array.getJSONObject(i);

            String muscleGroup = object.getString(Constant.COLUMN_ROUTINE_NAME);
            int exerciseDay = object.getInt(Constant.COLUMN_EXERCISE_DAY);

            rows.add(new SelectableListItem(muscleGroup, exerciseDay));
        }

        return rows;
    }
}