package com.intencity.intencity.task;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.intencity.intencity.helper.DbHelper;
import com.intencity.intencity.helper.ExerciseTable;
import com.intencity.intencity.helper.doa.ExerciseDao;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;

import java.util.ArrayList;

/**
 * This async task inserts exercises into the database.
 *
 * Created by Nick Piscopio on 12/19/15.
 */
public class SetExerciseTask extends AsyncTask<Void, Void, Void>
{
    private Context context;

    private int index;
    private int routineState;

    private String routineName;

    private ArrayList<Exercise> exercises;

    public SetExerciseTask(Context context)
    {
        this.context = context;
    }

    public SetExerciseTask(Context context, int routineState, String routineName, ArrayList<Exercise> exercises, int index)
    {
        this.context = context;

        this.index = index;
        this.routineState = routineState;

        this.routineName = routineName;

        this.exercises = exercises;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        editSavedExercises();

        return null;
    }

    @Override
    protected void onPostExecute(Void result) { }

    /**
     * Edits the exercises in the database.
     */
    private void editSavedExercises()
    {
        ArrayList<ExerciseDao> exerciseDaos = new ArrayList<>();

        // Add the exercises to ContentValues if there are exercises to save.
        if (exercises != null)
        {
            for (Exercise exercise : exercises)
            {
                ArrayList<Set> sets = exercise.getSets();
                for (Set set : sets)
                {
                    ContentValues values = new ContentValues();
                    values.put(ExerciseTable.COLUMN_ROUTINE_STATE, routineState);
                    values.put(ExerciseTable.COLUMN_INDEX, index);
                    values.put(ExerciseTable.COLUMN_ROUTINE_NAME, routineName);
                    values.put(ExerciseTable.COLUMN_NAME, exercise.getName());
                    values.put(ExerciseTable.COLUMN_DESCRIPTION, exercise.getDescription());
                    values.put(ExerciseTable.COLUMN_FROM_INTENCITY, exercise.isIncludedInIntencity());

                    int webId = set.getWebId();
                    float weight = set.getWeight();
                    int reps = set.getReps();
                    int difficulty = set.getDifficulty();
                    String duration = set.getDuration();
                    String notes = set.getNotes();

                    if (webId > 0)
                    {
                        values.put(ExerciseTable.COLUMN_WEB_ID, set.getWebId());
                    }

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

                    if (notes != null)
                    {
                        values.put(ExerciseTable.COLUMN_NOTES, notes);
                    }

                    exerciseDaos.add(new ExerciseDao(ExerciseTable.TABLE_NAME, values));
                }
            }

        }

        DbHelper dbHelper = new DbHelper(context);

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // Reset the database.
        dbHelper.resetDb(database);

        // Insert exercises into the database if there are exercises to save.
        if (exercises != null)
        {
            dbHelper.insertIntoDb(database, exerciseDaos);
        }
    }
}