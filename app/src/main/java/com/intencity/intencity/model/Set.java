package com.intencity.intencity.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The model class for sets of an exercise.
 *
 * Created by Nick Piscopio on 12/29/15.
 */
public class Set implements Parcelable
{
    // This is the auto generated webId set when inserting a record in the web database.
    private int webId;
    private int weight;
    private int reps;

    // This will need to be converted to a long later to accept times.
    private String duration;
    private int difficulty;

    private String notes;

    public Set() { }

    private Set(Parcel in)
    {
        webId = in.readInt();
        weight = in.readInt();
        reps = in.readInt();
        duration = in.readString();
        difficulty = in.readInt();
        notes = in.readString();
    }

    public static final Creator<Set> CREATOR = new Creator<Set>()
    {
        @Override
        public Set createFromParcel(Parcel in)
        {
            return new Set(in);
        }

        @Override
        public Set[] newArray(int size)
        {
            return new Set[size];
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
        dest.writeInt(webId);
        dest.writeInt(weight);
        dest.writeInt(reps);
        dest.writeString(duration);
        dest.writeInt(difficulty);
        dest.writeString(notes);
    }

    /**
     * Getters and setters for the set model.
     */
    public int getWebId()
    {
        return webId;
    }

    public void setWebId(int webId)
    {
        this.webId = webId;
    }

    public int getWeight()
    {
        return weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public int getReps()
    {
        return reps;
    }

    public void setReps(int reps)
    {
        this.reps = reps;
    }

    public String getDuration()
    {
        return duration;
    }

    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    public int getDifficulty()
    {
        return difficulty;
    }

    public void setDifficulty(int difficulty)
    {
        this.difficulty = difficulty;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }
}