package com.intencity.intencity.listener;

/**
 * A listener for an API call.
 *
 * Created by Nick Piscopio on 12/10/15.
 */
public interface ServiceListener
{
    // TODO: REMOVE LATER
    void onRetrievalSuccessful(String response);

    void onServiceResponse(int statusCode, String response);
    void onRetrievalFailed(int statusCode);
}