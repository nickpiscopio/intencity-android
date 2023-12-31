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

    private int routineState = 0;

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
        listener.onRetrievalSuccessful(routineState, routineName, exercises, index);
    }

    /**
     * Gets exercises from the database if it has them.
     *
     * @return The array list of exercises from the database.
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

                routineState = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_ROUTINE_STATE));
                routineName = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_ROUTINE_NAME));

                String name = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_DESCRIPTION));
                int priority = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_PRIORITY));
                boolean includedInIntencity = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_FROM_INTENCITY)) == 1;

                // If the last exercise name is equal to the last exercise we just got,
                // or if the exercise is the first in the list,
                // then we want to create a new exercise object.
                if (!lastExerciseName.equals(name))
                {
                    lastExerciseName = name;

                    exercise = new Exercise(new ArrayList<Set>());
                    exercise.setName(name);
                    exercise.setDescription(description);
                    exercise.setPriority(priority);
                    exercise.setIncludedInIntencity(includedInIntencity);

                    exercises.add(exercise);
                }

                ArrayList<Set> sets = exercise.getSets();
                String duration = cursor.getString(
                        cursor.getColumnIndex(ExerciseTable.COLUMN_DURATION));
                int webId = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_WEB_ID));
                float weight = cursor.getFloat(cursor.getColumnIndex(ExerciseTable.COLUMN_WEIGHT));
                int reps = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_REP));
                int difficulty = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_DIFFICULTY));
                String notes = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_NOTES));

                Set set = new Set();
                set.setWebId(webId);
                set.setReps(reps);
                set.setWeight(weight);
                set.setDuration(duration);
                set.setDifficulty(difficulty);
                set.setNotes(notes);

                sets.add(set);

                exercise.setSets(sets);

            } while(cursor.moveToNext());

            cursor.close();
        }

        return exercises;
    }
}