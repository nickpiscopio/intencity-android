package com.intencity.intencity.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

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

    private Bitmap bmp;

    public User() { }

    private User(Parcel in)
    {
        id = in.readInt();
        followingId = in.readInt();
        earnedPoints = in.readInt();
        totalBadges = in.readInt();
        firstName = in.readString();
        lastName = in.readString();

        byte[] bytes = new byte[in.readInt()];
        in.readByteArray(bytes);

        bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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

        if (bmp != null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] bytes = stream.toByteArray();

            dest.writeInt(bytes.length);
            dest.writeByteArray(bytes);
        }
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

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getFullName()
    {
        return this.firstName + " " + this.lastName;
    }

    public Bitmap getBmp()
    {
        return bmp;
    }

    public void setBmp(Bitmap bmp)
    {
        this.bmp = bmp;
    }
}