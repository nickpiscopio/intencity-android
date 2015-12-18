package com.intencity.intencity.model;

/**
 * The model class for User.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class User
{
    private String firstName;
    private String lastName;

    private int earnedPoints;

    /**
     * Getters and setters for the User model.
     */
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

    public int getEarnedPoints()
    {
        return earnedPoints;
    }

    public void setEarnedPoints(int earnedPoints)
    {
        this.earnedPoints = earnedPoints;
    }
}