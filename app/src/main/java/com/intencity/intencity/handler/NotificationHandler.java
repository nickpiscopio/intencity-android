package com.intencity.intencity.handler;

import com.intencity.intencity.notification.AwardDialogContent;
import com.intencity.intencity.listener.NotificationListener;

import java.util.ArrayList;

/**
 * Handles the fragments for Intencity.
 *
 * Created by Nick Piscopio on 12/13/15.
 */
public class NotificationHandler
{
    public static NotificationHandler fragmentHandler = null;

    private NotificationListener listener;

    private ArrayList<AwardDialogContent> awards;

    /**
     * Gets the instance of the singleton for the FragmentHandler.
     *
     * @return  The singleton instance of the FragmentHandler.
     */
    public static NotificationHandler getInstance(NotificationListener listener)
    {
        if (fragmentHandler == null)
        {
            fragmentHandler = new NotificationHandler(listener);
        }

        return fragmentHandler;
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
        fragmentHandler = null;
    }

    public void instantiateAwards()
    {
        awards = new ArrayList<>();
    }

    public void addAward(AwardDialogContent award)
    {
        // Adds the award to the first index so we can display them in reverse order.
        awards.add(0, award);

        listener.onNotificationAdded();
    }

    /**
     * Checks to see if the award is already granted to the user.
     *
     * @param award     The award to check.
     *
     * @return  Boolean value if the user has already received a certain award.
     */
    public boolean hasAward(AwardDialogContent award)
    {
        String awardDescription = award.getDescription();

        for (AwardDialogContent adc : awards)
        {
            String description = adc.getDescription();

            if (awardDescription.equals(description))
            {
                return true;
            }
        }

        return false;
    }

    public void clearAwards()
    {
        awards.clear();

        instantiateAwards();
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