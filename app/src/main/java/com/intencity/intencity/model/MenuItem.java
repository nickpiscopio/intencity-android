package com.intencity.intencity.model;

/**
 * The model class for the MenuItems in the menu ListView
 *
 * Created by Nick Piscopio on 1/26/16.
 */
public class MenuItem
{
    private String title;
    private Class cls;

    public MenuItem(String title, Class cls)
    {
        this.title = title;
        this.cls = cls;
    }

    /**
     * Getters and setters for the exercise model.
     */
    public String getTitle()
    {
        return title;
    }

    public Class getCls()
    {
        return cls;
    }
}