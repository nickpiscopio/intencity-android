package com.intencity.intencity.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.intencity.intencity.listener.ImageListener;
import com.intencity.intencity.util.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * The AsynTask for service calls.
 *
 * Created by Nick Piscopio on 12/10/15.
 */
public class RetrieveImageTask extends AsyncTask<String, Void, Bitmap>
{
    private ImageListener listener;

    private Bitmap bmp;

    /**
     * This is the generic constructor for the task.
     *
     * @param listener   The listener to call when the service is done.
     */
    public RetrieveImageTask(ImageListener listener)
    {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {
        String parameters = params[0];

        try
        {
            InputStream in = new URL(parameters).openStream();

            bmp = BitmapFactory.decodeStream(in);
        }
        catch(IOException e)
        {
            Log.e(Constant.TAG, "Error retrieving image: " + e.toString());
        }

        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap result)
    {
        if (listener != null)
        {
            if (bmp != null)
            {
                listener.onImageRetrieved(bmp);
            }
            else
            {
                listener.onImageRetrievalFailed();
            }
        }
    }
}
