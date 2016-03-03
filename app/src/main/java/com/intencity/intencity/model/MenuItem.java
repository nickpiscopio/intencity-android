package com.intencity.intencity.model;

import android.os.Bundle;

/**
 * The model class for the MenuItems in the menu ListView
 *
 * Created by Nick Piscopio on 1/26/16.
 */
public class MenuItem
{
    private String title;
    private Class cls;
    private Bundle bundle;

    public MenuItem(String title, Class cls)
    {
        this.title = title;
        this.cls = cls;
    }

    public MenuItem(String title, Class cls, Bundle bundle)
    {
        this.title = title;
        this.cls = cls;
        this.bundle = bundle;
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

    public Bundle getBundle()
    {
        return bundle;
    }
}