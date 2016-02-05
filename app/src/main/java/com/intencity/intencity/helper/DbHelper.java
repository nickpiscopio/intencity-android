package com.intencity.intencity.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.intencity.intencity.helper.doa.ExerciseDao;

import java.util.ArrayList;

/**
 * Database helper file.
 *
 * Created by Nick Piscopio on 12/19/15.
 */
public class DbHelper extends SQLiteOpenHelper
{
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Intencity.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + ExerciseTable.TABLE_NAME + " (" +
            ExerciseTable.COLUMN_INDEX + INTEGER_TYPE + COMMA_SEP +
            ExerciseTable.COLUMN_ROUTINE_NAME + TEXT_TYPE + COMMA_SEP +
            ExerciseTable.COLUMN_WEB_ID + INTEGER_TYPE + COMMA_SEP +
            ExerciseTable.COLUMN_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
            ExerciseTable.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
            ExerciseTable.COLUMN_WEIGHT + REAL_TYPE + COMMA_SEP +
            ExerciseTable.COLUMN_REP + INTEGER_TYPE + COMMA_SEP +
            ExerciseTable.COLUMN_DURATION + TEXT_TYPE + COMMA_SEP +
            ExerciseTable.COLUMN_DIFFICULTY + INTEGER_TYPE + COMMA_SEP +
            ExerciseTable.COLUMN_NOTES + TEXT_TYPE +
            " );";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ExerciseTable.TABLE_NAME + ";";

    // Get the list of exercises from the database that were saved.
    // These are exercises the user might want to continue with later.
    public static final String SQL_GET_EXERCISES = "SELECT * FROM " + ExerciseTable.TABLE_NAME + ";";

    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void resetDb(SQLiteDatabase db)
    {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * Inserts the content into the database.
     *
     * @param db    The database to edit.
     * @param list  The list of parameters to insert.
     */
    public void insertIntoDb(SQLiteDatabase db, ArrayList<ExerciseDao> list)
    {
        try
        {
            db.beginTransaction();

            for (ExerciseDao table : list)
            {
                db.insert(table.getTableName(), null, table.getContentValues());
            }

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }
}