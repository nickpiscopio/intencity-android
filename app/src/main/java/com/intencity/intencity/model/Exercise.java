package com.intencity.intencity.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The model class for the exercises the user is doing for the day.
 *
 * Created by Nick Piscopio on 12/19/15.
 */
public class Exercise implements Parcelable
{
    private String name;

    private int weight;
    private int reps;

    // This will need to be converted to a long later to accept times.
    private String duration;
    private int difficulty;

    public Exercise() { }

    private Exercise(Parcel in)
    {
        name = in.readString();
        weight = in.readInt();
        reps = in.readInt();
        duration = in.readString();
        difficulty = in.readInt();
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
        dest.writeInt(weight);
        dest.writeInt(reps);
        dest.writeString(duration);
        dest.writeInt(difficulty);
    }

    /**
     * Getters and setters for the User model.
     */
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
}