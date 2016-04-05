package com.intencity.intencity.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The model class for User.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class User implements Parcelable
{
    private int id;
    private int followingId;
    private int earnedPoints;
    private int totalBadges;

    private String firstName;
    private String lastName;

    public User() { }

    private User(Parcel in)
    {
        id = in.readInt();
        followingId = in.readInt();
        earnedPoints = in.readInt();
        totalBadges = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>()
    {
        @Override public User createFromParcel(Parcel in)
        {
            return new User(in);
        }

        @Override public User[] newArray(int size)
        {
            return new User[size];
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
        dest.writeInt(id);
        dest.writeInt(followingId);
        dest.writeInt(earnedPoints);
        dest.writeInt(totalBadges);
        dest.writeString(firstName);
        dest.writeString(lastName);
    }

    /**
     * Getters and setters for the User model.
     */
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getFollowingId()
    {
        return followingId;
    }

    public void setFollowingId(int followingId)
    {
        this.followingId = followingId;
    }

    public int getEarnedPoints()
    {
        return earnedPoints;
    }

    public void setEarnedPoints(int earnedPoints)
    {
        this.earnedPoints = earnedPoints;
    }

    public int getTotalBadges()
    {
        return totalBadges;
    }

    public void setTotalBadges(int totalBadges)
    {
        this.totalBadges = totalBadges;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getFullName()
    {
        return this.firstName + " " + this.lastName;
    }
}