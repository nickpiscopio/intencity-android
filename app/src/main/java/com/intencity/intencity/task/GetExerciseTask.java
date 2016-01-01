package com.intencity.intencity.task;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.intencity.intencity.helper.DbHelper;
import com.intencity.intencity.helper.ExerciseTable;
import com.intencity.intencity.listener.DatabaseListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * This async task checks the database to see if there are any exercises available
 *
 * Created by Nick Piscopio on 12/19/15.
 */
public class GetExerciseTask extends AsyncTask<Void, Void, ArrayList<Exercise>>
{
    private Context context;

    private DatabaseListener listener;

    private int index = 0;

    private String routineName;

    public GetExerciseTask(Context context, DatabaseListener listener)
    {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected ArrayList<Exercise> doInBackground(Void... params)
    {
        return getExercises();
    }

    @Override
    protected void onPostExecute(ArrayList<Exercise> exercises)
    {
        listener.onRetrievalSuccessful(routineName, exercises, index);
    }

    /**
     * Gets exercises from the database if it has them.
     *
     * @return The array list of exercises from the database.
     *
     * @throws JSONException
     */
    private ArrayList<Exercise> getExercises()
    {
        ArrayList<Exercise> exercises = new ArrayList<>();

        DbHelper dbHelper = new DbHelper(context);

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery(DbHelper.SQL_GET_EXERCISES, null);

        String lastExerciseName = "";

        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();

            Exercise exercise = null;

            do
            {
                if (index <= 0)
                {
                    index = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_INDEX));
                }

                routineName = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_ROUTINE_NAME));

                String name = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_NAME));

                // If the last exercise name is equal to the last exercise we just got,
                // or if the exercise is the first in the list,
                // then we want to create a new exercise object.
                if (!lastExerciseName.equals(name))
                {
                    lastExerciseName = name;

                    exercise = new Exercise(new ArrayList<Set>());
                    exercise.setName(name);

                    exercises.add(exercise);
                }

                ArrayList<Set> sets = exercise.getSets();
                String duration = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_DURATION));
                int weight  = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_WEIGHT));
                int reps = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_REP));
                int difficulty = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_DIFFICULTY));

                Set set = new Set();
                set.setReps(reps);
                set.setWeight(weight);
                set.setDuration(duration);
                set.setDifficulty(difficulty);

                sets.add(set);

                exercise.setSets(sets);

            } while(cursor.moveToNext());

            cursor.close();
        }

        return exercises;
    }
}