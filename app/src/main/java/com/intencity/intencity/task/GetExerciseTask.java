package com.intencity.intencity.task;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.intencity.intencity.helper.DbHelper;
import com.intencity.intencity.helper.ExerciseTable;
import com.intencity.intencity.listener.DatabaseListener;
import com.intencity.intencity.model.Exercise;

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
        listener.onRetrievalSuccessful(exercises, index);
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

        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();

            do
            {
                if (index <= 0)
                {
                    index = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_INDEX));
                }

                String name = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_NAME));
                String duration = cursor.getString(cursor.getColumnIndex(ExerciseTable.COLUMN_DURATION));
                int weight  = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_WEIGHT));
                int reps = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_REP));
                int difficulty = cursor.getInt(cursor.getColumnIndex(ExerciseTable.COLUMN_DIFFICULTY));

                Exercise exercise = new Exercise();
                exercise.setName(name);
                exercise.setReps(reps);
                exercise.setWeight(weight);
                exercise.setDuration(duration);
                exercise.setDifficulty(difficulty);

                exercises.add(exercise);

            } while(cursor.moveToNext());

            cursor.close();
        }

        return exercises;
    }
}