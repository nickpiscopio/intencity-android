package com.intencity.intencity.notification;

/**
 * This is the custom dialog entity.
 *
 * Created by Nick Piscopio on 12/20/15.
 */
public class CustomDialogContent
{
    private String title;
    private String message;
    private String[] buttons;
    private boolean includeNegativeButton;

    private int positiveButtonStringRes;
    private int negativeButtonStringRes;

    public CustomDialogContent(String title)
    {
        this.title = title;
    }

    public CustomDialogContent(String title, String message, boolean includeNegativeButton)
    {
        this.title = title;
        this.message = message;

        this.includeNegativeButton = includeNegativeButton;
    }

    public CustomDialogContent(String title, String[] buttons)
    {
        this.title = title;
        this.buttons = buttons;
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

    public String getMessage()
    {
        return message;
    }

    public String[] getButtons()
    {
        return buttons;
    }

    public boolean includeNegativeButton()
    {
        return includeNegativeButton;
    }

    public int getPositiveButtonStringRes()
    {
        return positiveButtonStringRes;
    }

    public void setPositiveButtonStringRes(int positiveButtonStringRes)
    {
        this.positiveButtonStringRes = positiveButtonStringRes;
    }

    public int getNegativeButtonStringRes()
    {
        return negativeButtonStringRes;
    }

    public void setNegativeButtonStringRes(int negativeButtonStringRes)
    {
        this.negativeButtonStringRes = negativeButtonStringRes;
    }
}