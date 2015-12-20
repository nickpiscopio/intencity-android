package com.intencity.intencity.dialog;

/**
 * This is the dialog entity.
 *
 * Created by Nick Piscopio on 12/20/15.
 */
public class Dialog
{
    private String title;
    private String message;
    private String[] buttons;
    private boolean includeNegativeButton;

    public Dialog(String title, String message, boolean includeNegativeButton)
    {
        this.title = title;
        this.message = message;
        this.includeNegativeButton = includeNegativeButton;
    }

    public Dialog(String[] buttons)
    {
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

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String[] getButtons()
    {
        return buttons;
    }

    public void setButtons(String[] buttons)
    {
        this.buttons = buttons;
    }

    public boolean includeNegativeButton()
    {
        return includeNegativeButton;
    }

    public void setIncludeNegativeButton(boolean includeNegativeButton)
    {
        this.includeNegativeButton = includeNegativeButton;
    }
}