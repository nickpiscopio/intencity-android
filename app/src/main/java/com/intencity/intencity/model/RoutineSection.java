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

    private boolean shouldAnimate;

    private ArrayList<SelectableListItem> rows;

    /**
     * The constructor for the RoutineSection.
     *
     * @param type      The type of routine.
     * @param title     The title of the section.
     * @param rows      The routine rows for each section.
     */
    public RoutineSection(RoutineType type, String title, ArrayList<SelectableListItem> rows)
    {
        this.type = type;
        this.title = title;
        this.rows = rows;
        this.shouldAnimate = true;
    }

    private RoutineSection(Parcel in)
    {
        type = RoutineType.valueOf(in.readString());
        title = in.readString();

        rows = new ArrayList<>();
        in.readTypedList(rows, SelectableListItem.CREATOR);

        shouldAnimate = in.readByte() == 1;
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
        dest.writeTypedList(rows);
        dest.writeByte(shouldAnimate ? 1 : (byte)0);
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

    public boolean shouldAnimate()
    {
        return shouldAnimate;
    }

    public void shouldAnimate(boolean shouldAnimate)
    {
        this.shouldAnimate = shouldAnimate;
    }

    public ArrayList<SelectableListItem> getRoutineRows()
    {
        return rows;
    }
}