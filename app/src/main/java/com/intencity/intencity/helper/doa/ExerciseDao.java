package com.intencity.intencity.helper.doa;

import android.content.ContentValues;

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
     * @param response      The String response from the server in a JSON format.
     * @param searchString  The exercise the user typed.
     *
     * @return  The ArrayList of exercises.
     */
    public ArrayList<Exercise> parseJson(String response, String searchString) throws JSONException
    {
        boolean foundSearchResult = false;

        ArrayList<Exercise> exercises = new ArrayList<>();

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
            set.setWeight(
                    weight.equalsIgnoreCase(Constant.RETURN_NULL) ? (int)Constant.CODE_FAILED :
                            Float.valueOf(weight));
            set.setReps(reps.equalsIgnoreCase(Constant.RETURN_NULL) ? 0 : Integer.valueOf(reps));
            set.setDuration(duration);
            set.setDifficulty(difficulty.equalsIgnoreCase(Constant.RETURN_NULL) ?
                                      (int)Constant.CODE_FAILED : Integer.valueOf(difficulty));

            if (!notes.equalsIgnoreCase(Constant.RETURN_NULL))
            {
                set.setNotes(notes);
            }

            ArrayList<Set> sets = new ArrayList<>();
            sets.add(set);

            Exercise exercise = new Exercise();
            exercise.setName(name);
            exercise.setSets(sets);

            // This determines if what we searched for has been returned from the database.
            // This is not case sensitive.
            if (!searchString.equals("") && !foundSearchResult && name.equalsIgnoreCase(searchString))
            {
                foundSearchResult = true;
            }

            // Add all the exercises from the database to the array list.
            exercises.add(exercise);
        }

        if (!searchString.equals("") && !foundSearchResult)
        {
            exercises.add(getExercise(searchString, false));
        }

        return exercises;
    }

    /**
     * Gets an exercise.
     *
     * @param exerciseName          The name of the exercise to get.
     * @param includedInIntencity   Boolean value of whether the exercise is from Intencity.
     *
     * @return The exercise.
     */
    private Exercise getExercise(String exerciseName, boolean includedInIntencity)
    {
        return getNewExercise(exerciseName, Constant.RETURN_NULL, Constant.RETURN_NULL, Constant.RETURN_NULL, Constant.RETURN_NULL, includedInIntencity);
    }

    /**
     * Get a new exercise.
     *
     * @param name                  The name of the exercise.
     * @param weight                The weight the user did last time.
     * @param reps                  The amount of reps the user did.
     * @param duration              The duration the user did.
     *                              Usually in 00:00:00 format.
     * @param difficulty            The difficulty from 1-10.
     * @param includedInIntencity   Boolean value of whether the exercise is from Intencity.
     *
     * @return  The new exercise.
     */
    public Exercise getNewExercise(String name, String weight, String reps, String duration, String difficulty, boolean includedInIntencity)
    {
        Set set = new Set();
        set.setWeight(weight.equalsIgnoreCase(Constant.RETURN_NULL) ? (int) Constant.CODE_FAILED :
                              Float.valueOf(weight));
        set.setReps(reps.equalsIgnoreCase(Constant.RETURN_NULL) ? 0 : Integer.valueOf(reps));
        set.setDuration(duration);
        set.setDifficulty(difficulty.equalsIgnoreCase(Constant.RETURN_NULL) ? (int) Constant.CODE_FAILED :
                                  Integer.valueOf(difficulty));

        ArrayList<Set> sets = new ArrayList<>();
        sets.add(set);

        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setSets(sets);
        exercise.setIncludedInIntencity(includedInIntencity);

        return exercise;
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