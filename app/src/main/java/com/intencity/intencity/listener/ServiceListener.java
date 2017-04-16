package com.intencity.intencity.listener;

import org.json.JSONObject;

/**
 * A listener for an API call.
 *
 * Created by Nick Piscopio on 12/10/15.
 */
public interface ServiceListener
{
    void onRetrievalSuccessful(JSONObject response);
    void onRetrievalFailed();
}