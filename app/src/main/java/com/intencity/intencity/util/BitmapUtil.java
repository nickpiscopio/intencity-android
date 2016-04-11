package com.intencity.intencity.util;

import android.graphics.Bitmap;
import android.support.v4.graphics.BitmapCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * The bitmap util class.
 *
 * Created by Nick Piscopio on 4/9/16.
 */
public class BitmapUtil
{
    private final int MAX_BITMAP_THRESHOLD = 8000000;
    private final int IMAGE_COMPRESSION_QUALITY_MAX = 100;

    public byte[] compressBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, getBitmapCompressionQuality(bitmap), stream);

        Log.i (Constant.TAG, "New image size: " + stream.toByteArray().length);

        return stream.toByteArray();
    }

    public int getBitmapCompressionQuality(Bitmap bitmap)
    {
        int quality = IMAGE_COMPRESSION_QUALITY_MAX;
        int size = getBitmapSize(bitmap);
        if (size > MAX_BITMAP_THRESHOLD)
        {
            float newQuality =  ((float)MAX_BITMAP_THRESHOLD / (float)size) * 100;
            quality = (int)newQuality;
        }

        Log.i (Constant.TAG, "Image size: " + size);
        Log.i (Constant.TAG, "quality: " + quality);

        return quality;
    }

    public int getBitmapSize(Bitmap bitmap)
    {
        return BitmapCompat.getAllocationByteCount(bitmap);
    }
}