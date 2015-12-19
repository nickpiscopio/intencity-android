package com.intencity.intencity.model;

/**
 * The model class for User.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class User
{
    private int id;
    private int followingId;
    private int earnedPoints;

    private String firstName;
    private String lastName;

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
}