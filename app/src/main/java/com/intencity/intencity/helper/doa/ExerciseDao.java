package com.intencity.intencity.helper.doa;

import android.content.ContentValues;

/**
 * The data access object for the Exercise class.
 *
 * Created by Nick Piscopio on 12/19/15.
 */
public class ExerciseDao
{
    private String tableName;
    private ContentValues contentValues;

    public ExerciseDao(String tableName, ContentValues contentValues)
    {
        this.tableName = tableName;
        this.contentValues = contentValues;
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