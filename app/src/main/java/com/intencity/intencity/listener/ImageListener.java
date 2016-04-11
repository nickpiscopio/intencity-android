package com.intencity.intencity.listener;

import android.graphics.Bitmap;

/**
 * A listener for when we are trying to retrieve an image from the server.
 *
 * Created by Nick Piscopio on 4/7/16.
 */
public interface ImageListener
{
    void onImageRetrieved(Bitmap bmp);
}