package com.intencity.intencity.helper.doa;

import android.content.Context;

import com.intencity.intencity.R;
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
public class IntencityRoutineDao
{
    // We add the 'default' title to the rows first, so row 6 (Starting from 0) will be the first custom routine.
    private final int INTENCITY_DEFAULT_ROUTINE_TOTAL = 6;

    /**
     * Parses the JSON response.
     *
     * @param context   The context of the activity.
     * @param response  The String response from the server in a JSON format.
     *
     * @return  The ArrayList of sections for a routine list.
     */
    public ArrayList<SelectableListItem> parseJson(Context context, String response) throws JSONException
    {
        ArrayList<SelectableListItem> rows = new ArrayList<>();

        // There isn't an exercise day for a title.
        // This is how we will differentiate between titles and rows when displaying the information.
        int titleNumber = (int)Constant.CODE_FAILED;

        // Add the title of the default routines.
        rows.add(new SelectableListItem(context.getString(R.string.title_default_routines), titleNumber));

        JSONArray array = new JSONArray(response);

        int length = array.length();

        for (int i = 0; i < length; i++)
        {
            JSONObject object = array.getJSONObject(i);

            String muscleGroup = object.getString(Constant.COLUMN_DISPLAY_NAME);
            int exerciseDay = object.getInt(Constant.COLUMN_EXERCISE_DAY);

            // This would be the first row of a custom routine.
            if (i == INTENCITY_DEFAULT_ROUTINE_TOTAL)
            {
                // Add the title of the custom routines.
                rows.add(new SelectableListItem(context.getString(R.string.title_custom_routine), titleNumber));
            }

            rows.add(new SelectableListItem(muscleGroup, exerciseDay));
        }

        return rows;
    }
}