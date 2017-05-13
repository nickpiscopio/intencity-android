package com.intencity.intencity.listener;

/**
 * A listener for an API call.
 *
 * Created by Nick Piscopio on 12/10/15.
 */
public interface ServiceListener
{
    void onServiceResponse(int statusCode, String response);
}