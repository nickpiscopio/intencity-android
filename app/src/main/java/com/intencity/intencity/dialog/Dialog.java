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

    private int imgRes;
    private int positiveButtonStringRes;
    private int negativeButtonStringRes;

    public Dialog(String title, String message, boolean includeNegativeButton)
    {
        this.title = title;
        this.message = message;

        this.includeNegativeButton = includeNegativeButton;
    }

    public Dialog(String title, String[] buttons)
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

    public int getImgRes()
    {
        return imgRes;
    }

    public void setImgRes(int imgRes)
    {
        this.imgRes = imgRes;
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