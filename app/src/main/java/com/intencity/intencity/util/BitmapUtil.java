package com.intencity.intencity.util;

import android.graphics.Bitmap;
import android.support.v4.graphics.BitmapCompat;

import java.io.ByteArrayOutputStream;

/**
 * The bitmap util class.
 *
 * Created by Nick Piscopio on 4/9/16.
 */
public class BitmapUtil
{
    private final int MAX_BITMAP_THRESHOLD = 1000000;

    private final int IMAGE_COMPRESSION_QUALITY_MAX = 100;

    // This value needs to compress the image to be less than 1MB.
    private final int IMAGE_COMPRESSION_QUALITY_COMPRESSED = 70;

    public byte[] compressBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, bitmapExceedsThreshold(bitmap) ? IMAGE_COMPRESSION_QUALITY_COMPRESSED : IMAGE_COMPRESSION_QUALITY_MAX, stream);

        return stream.toByteArray();
    }

    public boolean bitmapExceedsThreshold(Bitmap bitmap)
    {
        return BitmapCompat.getAllocationByteCount(bitmap) > MAX_BITMAP_THRESHOLD;
    }
}
