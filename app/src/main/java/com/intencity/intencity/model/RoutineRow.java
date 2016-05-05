package com.intencity.intencity.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The model class for the rows in each routine group.
 *
 * Created by Nick Piscopio on 5/5/16.
 */
public class RoutineRow implements Parcelable
{
    private String title;

    private int rowNumber;

    public RoutineRow(String title, int rowNumber)
    {
        this.title = title;
        this.rowNumber = rowNumber;
    }

    private RoutineRow(Parcel in)
    {
        title = in.readString();
        rowNumber = in.readInt();
    }

    public static final Creator<RoutineRow> CREATOR = new Creator<RoutineRow>()
    {
        @Override
        public RoutineRow createFromParcel(Parcel in)
        {
            return new RoutineRow(in);
        }

        @Override
        public RoutineRow[] newArray(int size)
        {
            return new RoutineRow[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeInt(rowNumber);
    }

    /**
     * Getters and setters for the exercise model.
     */
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getRowNumber()
    {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber)
    {
        this.rowNumber = rowNumber;
    }
}