package com.intencity.intencity.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * The model class for the groups for the routines.
 *
 * Created by Nick Piscopio on 5/5/16.
 */
public class RoutineGroup implements Parcelable
{
    private String title;

    private ArrayList<RoutineRow> rows;

    private RoutineGroup(Parcel in)
    {
        title = in.readString();

        rows = new ArrayList<>();
        in.readTypedList(rows, RoutineRow.CREATOR);
    }

    public static final Creator<RoutineGroup> CREATOR = new Creator<RoutineGroup>()
    {
        @Override
        public RoutineGroup createFromParcel(Parcel in)
        {
            return new RoutineGroup(in);
        }

        @Override
        public RoutineGroup[] newArray(int size)
        {
            return new RoutineGroup[size];
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
        dest.writeTypedList(rows);
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

    public ArrayList<RoutineRow> getRows()
    {
        return rows;
    }

    public void setRows(ArrayList<RoutineRow> rows)
    {
        this.rows = rows;
    }
}