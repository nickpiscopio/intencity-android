package com.intencity.intencity.model;

/**
 * The model class for the different sections of the profile screen.
 *
 * Created by Nick Piscopio on 2/7/16.
 */
public class ProfileRow
{
    private boolean isSectionHeader;
    private String title;
    private String amount;

    public ProfileRow(boolean isSectionHeader, String title, String amount)
    {
        this.isSectionHeader = isSectionHeader;
        this.title = title;
        this.amount = amount;
    }

    /**
     * Getters and setters for the exercise model.
     */
    public boolean isSectionHeader()
    {
        return isSectionHeader;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAmount()
    {
        return amount;
    }
}