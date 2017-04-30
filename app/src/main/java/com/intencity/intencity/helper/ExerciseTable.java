package com.intencity.intencity.helper;

import android.provider.BaseColumns;

/**
 * The Exercise table in the database.
 *
 * In SQLite, table rows normally have a 64-bit signed integer ROWID which is unique among all
 * rows in the same table.
 *
 * You can access the ROWID of an SQLite table using one of the special column names
 * ROWID, _ROWID_, or OID.
 *
 * https://www.sqlite.org/autoinc.html
 *
 * Created by Nick Piscopio on 12/19/15.
 */
public class ExerciseTable implements BaseColumns
{
    public static final String TABLE_NAME = "Exercise";

    public static final String COLUMN_ROUTINE_STATE = "RoutineState";
    public static final String COLUMN_ROUTINE_NAME = "RoutineName";
    // The last exercise we left off at.
    public static final String COLUMN_INDEX = "LastExerciseIndex";
    // This is the exercise ID.
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "ExerciseName";
    public static final String COLUMN_DESCRIPTION = "ExerciseDescription";
    public static final String COLUMN_WEB_ID = "WebId";
    public static final String COLUMN_WEIGHT = "ExerciseWeight";
    public static final String COLUMN_REP = "ExerciseRep";
    public static final String COLUMN_DURATION = "ExerciseDuration";
    public static final String COLUMN_DIFFICULTY = "ExerciseDifficulty";
    public static final String COLUMN_PRIORITY = "ExercisePriority";
    public static final String COLUMN_NOTES = "Notes";
    // Whether a search result is from Intencity or the user typed it in.
    public static final String COLUMN_FROM_INTENCITY = "FromIntencity";
}