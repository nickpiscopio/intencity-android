package com.intencity.intencity.util;

/**
 * A class to hold the Google Geocode endpoint.
 *
 * Created by Nick Piscopio on 11/27/16.
 */
public class GoogleGeocode
{
    // Service Endpoint
    private static final String ENDPOINT = "https://maps.googleapis.com/";
    public static final String ROUTE = ENDPOINT + "maps/api/geocode/json";

    // Parameters
    private static final String PARAMETER_ADDRESS = "?address=";
    private static final String PARAMETER_KEY = "&key=";
    private static final String API_KEY = "AIzaSyB8gL500bzBsvzh8QEOzkRl_GhwziH8vtU";

    /**
     * Creates the geocode parameters.
     *
     * @param address   The address that we are validation.
     *
     * @return  The geocode parameters.
     */
    public static String getGoogleGeocodeParameters(String address)
    {
        return PARAMETER_ADDRESS + address + PARAMETER_KEY + API_KEY;
    }
}