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

    // The last exercise we left off at.
    public static final String COLUMN_ROUTINE_NAME = "RoutineName";
    public static final String COLUMN_INDEX = "LastExerciseIndex";
    public static final String COLUMN_NAME = "ExerciseName";
    public static final String COLUMN_WEIGHT = "ExerciseWeight";
    public static final String COLUMN_REP = "ExerciseRep";
    public static final String COLUMN_DURATION = "ExerciseDuration";
    public static final String COLUMN_DIFFICULTY = "ExerciseDifficulty";
}