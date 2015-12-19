package com.intencity.intencity.task;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.intencity.intencity.helper.DbHelper;
import com.intencity.intencity.helper.ExerciseTable;
import com.intencity.intencity.helper.doa.ExerciseDao;
import com.intencity.intencity.model.Exercise;

import java.util.ArrayList;

/**
 * This async task inserts exercises into the database.
 *
 * Created by Nick Piscopio on 12/19/15.
 */
public class SetExerciseTask extends AsyncTask<Void, Void, Void>
{
    private Context context;

    private ArrayList<Exercise> exercises;

    private int index;

    public SetExerciseTask(Context context, ArrayList<Exercise> exercises, int index)
    {
        this.context = context;

        this.exercises = exercises;

        this.index = index;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        writeExercises();

        return null;
    }

    @Override
    protected void onPostExecute(Void result) { }

    /**
     * Creates content values and inserts it into the database.
     */
    private void writeExercises()
    {
        ArrayList<ExerciseDao> exerciseDaos = new ArrayList<>();

        for (Exercise exercise : exercises)
        {
            int weight = exercise.getWeight();
            int reps = exercise.getReps();
            int difficulty = exercise.getDifficulty();
            String duration = exercise.getDuration();

            ContentValues values = new ContentValues();
            values.put(ExerciseTable.COLUMN_INDEX, index);
            values.put(ExerciseTable.COLUMN_NAME, exercise.getName());

            if (weight > 0)
            {
                values.put(ExerciseTable.COLUMN_WEIGHT, weight);
            }

            if (reps > 0)
            {
                values.put(ExerciseTable.COLUMN_REP, reps);
            }

            if (duration != null)
            {
                values.put(ExerciseTable.COLUMN_DURATION, duration);
            }

            if (difficulty > 0)
            {
                values.put(ExerciseTable.COLUMN_DIFFICULTY, difficulty);
            }

            exerciseDaos.add(new ExerciseDao(ExerciseTable.TABLE_NAME, values));
        }

        DbHelper dbHelper = new DbHelper(context);

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        dbHelper.resetDb(database);
        dbHelper.insertIntoDb(database, exerciseDaos);
    }
}