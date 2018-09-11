package com.intencity.intencity.handler;

/**
 * Handles the user location permissions for Intencity.
 *
 * Created by Nick Piscopio on 9/10/18.
 */
public class UserLocationPermissionHandler
{
    public static UserLocationPermissionHandler locationPermissionHandler = null;

    private boolean userSelectedToNotSetLocation;

    /**
     * Gets the instance of the singleton for the FragmentHandler.
     *
     * @return  The singleton instance of the FragmentHandler.
     */
    public static UserLocationPermissionHandler getInstance()
    {
        if (locationPermissionHandler == null)
        {
            locationPermissionHandler = new UserLocationPermissionHandler();
        }

        return locationPermissionHandler;
    }

    public UserLocationPermissionHandler()
    {
        resetUserLocationSelection();
    }

    private void resetUserLocationSelection()
    {
        userSelectedToNotSetLocation = false;
    }

    public boolean didUserSelectToNotSetLocation()
    {
        return userSelectedToNotSetLocation;
    }

    public void setUserSelectionToNotSetLocation(boolean userSelectedToNotSetLocation)
    {
        this.userSelectedToNotSetLocation = userSelectedToNotSetLocation;
    }
}