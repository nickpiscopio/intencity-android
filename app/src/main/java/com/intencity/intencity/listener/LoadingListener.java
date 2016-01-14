package com.intencity.intencity.listener;

/**
 * A listener for loading.
 *
 * Created by Nick Piscopio on 1/14/16.
 */
public interface LoadingListener
{
    void onStartLoading();
    void onFinishedLoading(int which);
}