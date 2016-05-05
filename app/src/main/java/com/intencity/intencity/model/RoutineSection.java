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

    private ArrayList<RoutineGroup> routineGroups;

    /**
     * The constructor for the RoutineSection.
     *
     * @param type      The type of rotine.
     * @param title     The title of the section.
     * @param keys      The int equivalents to what dot is being shown for each routine, which will correlate to the routine key.
     * @param groups    The routine groups for each section.
     */
    public RoutineSection(RoutineType type, String title, int[] keys, ArrayList<RoutineGroup> groups)
    {
        this.type = type;
        this.title = title;
        this.keys = keys;
        this.routineGroups = groups;
    }

    private RoutineSection(Parcel in)
    {
        type = RoutineType.valueOf(in.readString());
        title = in.readString();

        in.readIntArray(keys);

        routineGroups = new ArrayList<>();
        in.readTypedList(routineGroups, RoutineGroup.CREATOR);
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
        dest.writeTypedList(routineGroups);
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

    public void setKeys(int[] keys)
    {
        this.keys = keys;
    }

    public ArrayList<RoutineGroup> getRoutineGroups()
    {
        return routineGroups;
    }

    public void setRoutineGroups(ArrayList<RoutineGroup> routineGroups)
    {
        this.routineGroups = routineGroups;
    }
}