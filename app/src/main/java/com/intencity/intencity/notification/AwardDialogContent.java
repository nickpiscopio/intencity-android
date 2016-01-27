package com.intencity.intencity.notification;

/**
 * This is the award dialog entity.
 *
 * Created by Nick Piscopio on 12/20/15.
 */
public class AwardDialogContent
{
    private String title;
    private String description;

    private int imgRes;

    public AwardDialogContent(String title, String description)
    {
        this.title = title;
        this.description = description;
    }

    public AwardDialogContent(int imgRes, String description)
    {
        this.imgRes = imgRes;
        this.description = description;
    }

    /**
     * Getters and setters for the dialog class.
     */
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getImgRes()
    {
        return imgRes;
    }

    public void setImgRes(int imgRes)
    {
        this.imgRes = imgRes;
    }
}