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
    // The number of times this award shows up in the list.
    private int amount = 1;

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

    public int getAmount()
    {
        return amount;
    }

    /**
     * Increments the amount of times this award is shown in the notifications.
     */
    public void incrementAmount()
    {
        amount++;
    }
}