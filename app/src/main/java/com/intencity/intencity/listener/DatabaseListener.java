package com.intencity.intencity.listener;

import java.util.ArrayList;

/**
 * A listener for a database call.
 *
 * Created by Nick Piscopio on 12/19/15.
 */
public interface DatabaseListener
{
    void onRetrievalSuccessful(String routineName, ArrayList<?> results, int index);
}