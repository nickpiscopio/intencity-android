package com.intencity.intencity.listener;

/**
 * A listener for when notifications are added or removed.
 *
 * Created by Nick Piscopio on 1/26/16.
 */
public interface NotificationListener
{
    void onNotificationAdded();
    void onNotificationsCleared();
}