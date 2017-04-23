package com.intencity.intencity.helper.doa;

import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.SelectableListItem;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The data access object for the FitnessLocation class.
 *
 * Created by Nick Piscopio on 1/21/17.
 */
public class FitnessLocationDao
{
    private ServiceListener listener;

    private String email;

    private boolean hasValidFitnessLocation;

    /**
     * The FitnessLocationDao constructor.
     *
     * @param listener      The service listener to notify when fitness locations are returned.
     * @param email         The user's email to send to the server to get his or her fitness locations.
     */
    public FitnessLocationDao(ServiceListener listener, String email)
    {
        this.listener = listener;
        this.email = email;

        hasValidFitnessLocation = false;
    }

    /**
     * Parses the JSON response.
     *
     * @param response              The String response from the server in a JSON format.
     * @param currentUserLocation   The current location of the user.
     *                              We search through the locations anyway just in case the user is in a location that isn't saved.
     *                              If this is the case, then we go to the fitness location screen for the user to either select or create a new fitness location.
    * @param listItemType           The type of list we want to see after parsing.
     *
     * @return  The ArrayList of the user's fitness locations.
     */
    public ArrayList<SelectableListItem> parseJson(String response, String currentUserLocation, SelectableListItem.ListItemType listItemType) throws JSONException
    {
        ArrayList<SelectableListItem> locations = new ArrayList<>();

        JSONArray array = new JSONArray(response);

        int length = array.length();

        for (int i = 0; i < length; i++)
        {
            JSONObject object = array.getJSONObject(i);

            String name = object.getString(Constant.COLUMN_DISPLAY_NAME);
            String location = object.getString(Constant.COLUMN_LOCATION);

            // Only check if the user has a valid fitness location if the currentUserLocation isn't null
            if (currentUserLocation != null && location.equals(currentUserLocation))
            {
                hasValidFitnessLocation = true;

                // We don't need to continue here because we the user has a valid fitness location, so stop searching.
                break;
            }

            SelectableListItem listItem = new SelectableListItem(name, location);
            listItem.setListItemType(listItemType);

            // Add all the locations to the array.
            locations.add(listItem);
        }

        return locations;
    }

    /**
     * Starts the service to get the user's fitness locations.
     */
    public void getFitnessLocations()
    {
        new ServiceTask(listener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_USER_FITNESS_LOCATIONS, email));
    }

    /**
     * Getter for if the user has a valid fitness location.
     *
     * @return  Boolean value of whether the user has a valid fitness location.
     */
    public boolean hasValidFitnessLocation()
    {
        return hasValidFitnessLocation;
    }
}