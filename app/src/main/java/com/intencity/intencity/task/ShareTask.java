package com.intencity.intencity.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import com.intencity.intencity.listener.ShareListener;
import com.intencity.intencity.util.ScreenshotUtil;

import java.io.File;

/**
 * This async task to share a user's fitness overview.
 *
 * Created by Nick Piscopio on 6/5/16.
 */
public class ShareTask extends AsyncTask<LinearLayout, Void, File>
{
    private ShareListener shareListener;

    public ShareTask(ShareListener shareListener)
    {
        this.shareListener = shareListener;
    }

    @Override
    protected File doInBackground(LinearLayout... params)
    {
        ScreenshotUtil ssUtil = new ScreenshotUtil();

        File file = null;

        try
        {
            Bitmap screenshot = ssUtil.takeScreenShot(params[0]);
            file = ssUtil.saveImage(screenshot, "intencity_overview");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return file;
    }

    @Override
    protected void onPostExecute(File file)
    {
        shareListener.onImageProcessed(file);
    }
}