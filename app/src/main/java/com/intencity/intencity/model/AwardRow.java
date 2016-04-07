package com.intencity.intencity.model;

/**
 * The model class for the different rows of the profile screen.
 *
 * Created by Nick Piscopio on 2/7/16.
 */
public class AwardRow
{
    private String title;
    private String amount;

    public AwardRow(String title, String amount)
    {
        this.title = title;
        this.amount = amount;
    }

    /**
     * Getters and setters for the exercise model.
     */
    public String getTitle()
    {
        return title;
    }

    public String getAmount()
    {
        return amount;
    }
}