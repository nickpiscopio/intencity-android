package com.intencity.intencity.listener;

import org.json.JSONObject;

/**
 * A listener for an API call.
 *
 * Created by Nick Piscopio on 12/10/15.
 */
public interface ServiceListener
{
    // TODO: REMOVE LATER
    void onRetrievalSuccessful(String response);

    void onRetrievalSuccessful(int statusCode, JSONObject response);
    void onRetrievalFailed();
}