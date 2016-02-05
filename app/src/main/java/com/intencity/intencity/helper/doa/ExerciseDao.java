package com.intencity.intencity.helper.doa;

import android.content.ContentValues;
import android.util.Log;

import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The data access object for the Exercise class.
 *
 * Created by Nick Piscopio on 12/19/15.
 */
public class ExerciseDao
{
    private String tableName;
    private ContentValues contentValues;

    public ExerciseDao() { }

    public ExerciseDao(String tableName, ContentValues contentValues)
    {
        this.tableName = tableName;
        this.contentValues = contentValues;
    }

    /**
     * Parses the JSON response.
     *
     * @param response  The String response from the server in a JSON format.
     *
     * @return  The ArrayList of exercises.
     */
    public ArrayList<Exercise> parseJson(String response)
    {
        ArrayList<Exercise> exercises = new ArrayList<>();

        try
        {
            JSONArray array = new JSONArray(response);

            int length = array.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject object = array.getJSONObject(i);

                String name = object.getString(Constant.COLUMN_EXERCISE_NAME);
                String weight = object.getString(Constant.COLUMN_EXERCISE_WEIGHT);
                String reps = object.getString(Constant.COLUMN_EXERCISE_REPS);
                String duration = object.getString(Constant.COLUMN_EXERCISE_DURATION);
                String difficulty = object.getString(Constant.COLUMN_EXERCISE_DIFFICULTY);
                String notes = object.getString(Constant.COLUMN_NOTES);

                Set set = new Set();
                set.setWeight(weight.equalsIgnoreCase(Constant.RETURN_NULL) ? Constant.CODE_FAILED :
                                      Float.valueOf(weight));
                set.setReps(reps.equalsIgnoreCase(Constant.RETURN_NULL) ? 0 :
                                    Integer.valueOf(reps));
                set.setDuration(duration);
                set.setDifficulty(difficulty.equalsIgnoreCase(Constant.RETURN_NULL) ?
                                          Constant.CODE_FAILED : Integer.valueOf(difficulty));
                set.setNotes(notes);

                ArrayList<Set> sets = new ArrayList<>();
                sets.add(set);

                Exercise exercise = new Exercise();
                exercise.setName(name);
                exercise.setSets(sets);

                // Add all the exercises from the database to the array list.
                exercises.add(exercise);
            }
        }
        catch (JSONException exception)
        {
            Log.e(Constant.TAG, "Couldn't parse exercise search " + exception.toString());
        }

        return exercises;
    }

    public String getTableName()
    {
        return tableName;
    }

    public ContentValues getContentValues()
    {
        return contentValues;
    }
}