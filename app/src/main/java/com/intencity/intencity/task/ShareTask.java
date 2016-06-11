package com.intencity.intencity.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import com.intencity.intencity.listener.ShareListener;
import com.intencity.intencity.util.ScreenshotUtil;

/**
 * This async task to share a user's fitness overview.
 *
 * Created by Nick Piscopio on 6/5/16.
 */
public class ShareTask extends AsyncTask<LinearLayout, Void, Void>
{
    private Context context;

    private ShareListener shareListener;

    public ShareTask(Context context, ShareListener shareListener)
    {
        this.context = context;
        this.shareListener = shareListener;
    }

    @Override
    protected Void doInBackground(LinearLayout... params)
    {
        ScreenshotUtil ssUtil = new ScreenshotUtil();

        try
        {
            Bitmap screenshot = ssUtil.takeScreenShot(params[0]);
            ssUtil.saveImage(context, screenshot);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result)
    {
        shareListener.onImageProcessed();
    }
}