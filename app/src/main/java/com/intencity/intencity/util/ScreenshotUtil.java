package com.intencity.intencity.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

/**
 *  Utility class for screenshots.
 *
 * Created by Nick Piscopio on 6/5/16.
 */
public class ScreenshotUtil
{
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
     * Saves an image to the public picture directory in android.
     *
     * @param bitmap    The image to save.
     * @param name      The name to save it. This will be saved as a PNG.
     *
     * @return  File that was saved.
     */
    public File saveImage(Bitmap bitmap, String name) throws Exception
    {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + Constant.TAG);

        if(!dir.exists())
        {
            dir.mkdirs();
        }

        String fileName = name + ".png";

        File file = new File(dir, fileName);
        if (file.exists())
        {
            file.delete();
        }

        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();

        return file;
    }
}