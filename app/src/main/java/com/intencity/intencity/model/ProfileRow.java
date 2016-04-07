package com.intencity.intencity.model;

import java.util.ArrayList;

/**
 * The model class for the different sections of the profile screen.
 *
 * Created by Nick Piscopio on 2/7/16.
 */
public class ProfileRow
{
    private boolean isSectionHeader;
    private String title;
    private ArrayList<AwardRow> awards;

    public ProfileRow(boolean isSectionHeader, String title, ArrayList<AwardRow> awards)
    {
        this.isSectionHeader = isSectionHeader;
        this.title = title;
        this.awards = awards;
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

    public ArrayList<AwardRow> getRows()
    {
        return awards;
    }
}