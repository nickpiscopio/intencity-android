package com.intencity.intencity.handler;

import com.intencity.intencity.notification.AwardDialogContent;
import com.intencity.intencity.listener.NotificationListener;

import java.util.ArrayList;

/**
 * Handles the notifications for Intencity.
 *
 * Created by Nick Piscopio on 12/13/15.
 */
public class NotificationHandler
{
    public static NotificationHandler notificationHandler = null;

    private NotificationListener listener;

    private ArrayList<AwardDialogContent> awards;

    /**
     * Gets the instance of the singleton for the FragmentHandler.
     *
     * @return  The singleton instance of the FragmentHandler.
     */
    public static NotificationHandler getInstance(NotificationListener listener)
    {
        if (notificationHandler == null)
        {
            notificationHandler = new NotificationHandler(listener);
        }

        return notificationHandler;
    }

    /**
     * Constructor for the NotificationHandler.
     *
     * @param listener  The notification listener to call later to notify of new awards.
     */
    public NotificationHandler(NotificationListener listener)
    {
        instantiateAwards();

        this.listener = listener;
    }

    public void resetInstance()
    {
        notificationHandler = null;
    }

    public void instantiateAwards()
    {
        awards = new ArrayList<>();
    }

    /**
     * Adds an award to the list if it is not in the list already.
     * If it is in the list, then we increment the count.
     *
     * @param award     The award to add.
     */
    public void addAward(AwardDialogContent award)
    {
        AwardDialogContent adc = getAward(award);
        if (adc != null)
        {
            adc.incrementAmount();
        }
        else
        {
            // Adds the award to the first index so we can display them in reverse order.
            awards.add(0, award);
        }

        listener.onNotificationAdded();
    }

    /**
     * Gets the award if it is in the award list.
     *
     * @param award     The award to get.
     *
     * @return An award from the award list.
     */
    public AwardDialogContent getAward(AwardDialogContent award)
    {
        String awardDescription = award.getDescription();

        for (AwardDialogContent adc : awards)
        {
            String description = adc.getDescription();

            if (awardDescription.equals(description))
            {
                return adc;
            }
        }

        return null;
    }

    /**
     * Clears the awards and reinstantiates the list.
     */
    public void clearAwards()
    {
        awards.clear();

        instantiateAwards();

        listener.onNotificationsCleared();
    }

    public int getAwardCount()
    {
        return awards.size();
    }

    public ArrayList<AwardDialogContent> getAwards()
    {
        return awards;
    }
}