package com.intencity.intencity.listener;

/**
 * Created by nickpiscopio on 12/10/15.
 */
public interface ServiceListener
{
    void onRetrievalSuccessful(String response);
    void onRetrievalFailed();
}
