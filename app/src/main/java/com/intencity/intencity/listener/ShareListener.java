package com.intencity.intencity.listener;

import java.io.File;

/**
 * Public interface to listen for when the share finishes processing the image.
 *
 * Created by Nick Piscopio on 6/5/16.
 */
public interface ShareListener
{
    void onImageProcessed(File file);
}