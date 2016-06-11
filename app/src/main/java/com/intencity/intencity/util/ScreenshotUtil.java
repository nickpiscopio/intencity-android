package com.intencity.intencity.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  Utility class for screenshots.
 *
 * Created by Nick Piscopio on 6/5/16.
 */
public class ScreenshotUtil
{
    // The FileProvider authority.
    public static final String AUTHORITY = "com.intencity.intencity.view.activity.OverviewActivity";
    public static final String DIRECTORY = "images";
    public static final String IMAGE_NAME = "image.png";

    /**
     * Takes a screenshot of a specified view.
     *
     * @param view  The view to take a screenshot.
     *
     * @return  A bitmap of the view.
     */
    public Bitmap takeScreenShot(View view) throws Exception
    {
        Bitmap b = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.draw(c);

        return b;
    }

    /**
     * Saves an image to the cache directory on the device.
     * The image will be saved over the old image every time.
     * This get cleared if the user removes the app.
     *
     * @param context   The application context.
     * @param bitmap    The image to save.
     *
     * @return  File that was saved.
     */
    public File saveImage(Context context, Bitmap bitmap) throws IOException
    {
        File cachePath = new File(context.getCacheDir(), DIRECTORY);
        cachePath.mkdirs();

        // Overwrites the image every time
        FileOutputStream stream = new FileOutputStream(cachePath + "/" + IMAGE_NAME);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        stream.close();

        return cachePath;
    }
}