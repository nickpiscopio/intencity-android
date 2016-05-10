package com.intencity.intencity.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.intencity.intencity.util.RoutineType;

import java.util.ArrayList;

/**
 * The model class for the section for each routine.
 *
 * Created by Nick Piscopio on 5/5/16.
 */
public class RoutineSection implements Parcelable
{
    private RoutineType type;

    private String title;

    private int[] keys;

    private ArrayList<RoutineRow> rows;

    /**
     * The constructor for the RoutineSection.
     *
     * @param type      The type of rotine.
     * @param title     The title of the section.
     * @param keys      The int equivalents to what dot is being shown for each routine, which will correlate to the routine key.
     * @param rows      The routine rows for each section.
     */
    public RoutineSection(RoutineType type, String title, int[] keys, ArrayList<RoutineRow> rows)
    {
        this.type = type;
        this.title = title;
        this.keys = keys;
        this.rows = rows;
    }

    private RoutineSection(Parcel in)
    {
        type = RoutineType.valueOf(in.readString());
        title = in.readString();

        in.readIntArray(keys);

        rows = new ArrayList<>();
        in.readTypedList(rows, RoutineRow.CREATOR);
    }

    public static final Creator<RoutineSection> CREATOR = new Creator<RoutineSection>()
    {
        @Override
        public RoutineSection createFromParcel(Parcel in)
        {
            return new RoutineSection(in);
        }

        @Override
        public RoutineSection[] newArray(int size)
        {
            return new RoutineSection[size];
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
        dest.writeString(type.name());
        dest.writeString(title);
        dest.writeIntArray(keys);
        dest.writeTypedList(rows);
    }

    /**
     * Getters and setters for the exercise model.
     */
    public RoutineType getType()
    {
        return type;
    }

    public void setType(RoutineType type)
    {
        this.type = type;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int[] getKeys()
    {
        return keys;
    }

    public ArrayList<RoutineRow> getRoutineRows()
    {
        return rows;
    }
}