package com.intencity.intencity.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * The model class for the exercises the user is doing.
 *
 * Created by Nick Piscopio on 12/19/15.
 */
public class Exercise implements Parcelable
{
    private String name;
    private String description;

    private ArrayList<Set> sets;

    public Exercise() { }

    public Exercise(ArrayList<Set> sets)
    {
        this.sets = sets;
    }

    private Exercise(Parcel in)
    {
        name = in.readString();
        description = in.readString();

        sets = new ArrayList<>();
        in.readTypedList(sets, Set.CREATOR);
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>()
    {
        @Override
        public Exercise createFromParcel(Parcel in)
        {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size)
        {
            return new Exercise[size];
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
        dest.writeString(name);
        dest.writeString(description);
        dest.writeTypedList(sets);
    }

    /**
     * Getters and setters for the exercise model.
     */
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public ArrayList<Set> getSets()
    {
        return sets;
    }

    public void setSets(ArrayList<Set> sets)
    {
        this.sets = sets;
    }
}