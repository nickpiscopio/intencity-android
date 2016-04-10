package com.intencity.intencity.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import com.intencity.intencity.listener.ImageListener;
import com.intencity.intencity.util.BitmapUtil;
import com.intencity.intencity.util.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * The AsyncTask for uploading an image to the server.
 *
 * Created by Nick Piscopio on 4/9/16.
 */
public class UploadImageTask extends AsyncTask<Void, Void, String>
{
    private boolean success = true;

    private ImageListener listener;

    private Bitmap image;

    private int userId;

    /**
     * This is the constructor for the task.
     *
     * @param listener   The listener to call when the service is done.
     */
    public UploadImageTask(ImageListener listener, Bitmap image, int userId)
    {
        this.listener = listener;

        this.image = image;

        this.userId = userId;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        String response = null;

        String encodedImage = Base64.encodeToString(new BitmapUtil().compressBitmap(image), Base64.DEFAULT);

        Map<String,String> dataToSend = new HashMap<>();
        dataToSend.put("image", encodedImage);
        dataToSend.put("id", String.valueOf(userId));

        // Encoded String - we will have to encode string by our custom method (Very easy)
        String encodedStr = getEncodedData(dataToSend);

        // Will be used if we want to read some data from server
        BufferedReader reader = null;

        try
        {
            URL url = new URL(Constant.SERVICE_UPLOAD_PROFILE_PIC);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            // To enable inputting values using POST method
            // (Basically, after this we can write the dataToSend to the body of POST method)
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(encodedStr);
            writer.flush();

            InputStreamReader isr = new InputStreamReader(conn.getInputStream());
            reader = new BufferedReader(isr);

            // Response from server after login process will be stored in response variable.
            response = reader.readLine(); // Saving complete data received in string, you can do it differently
        }
        catch (Exception e)
        {
            e.printStackTrace();

            success = false;
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (listener != null)
        {
            if (success && result.length() > 0 && !result.replaceAll("\"", "").equalsIgnoreCase(ServiceTask.FAILURE))
            {
                listener.onImageRetrieved(image);
            }
        }
    }

    /**
     * Encodes the data that is sent to the server.
     *
     * @param data  The data to encode.
     *
     * @return  The String of encoded data.
     */
    private String getEncodedData(Map<String,String> data)
    {
        StringBuilder sb = new StringBuilder();

        for(String key : data.keySet())
        {
            String value = null;

            int stringLength = sb.length();

            try
            {
                value = URLEncoder.encode(data.get(key), "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            if(stringLength > 0)
            {
                sb.append("&");
            }

            sb.append(key + "=" + value);
        }

        return sb.toString();
    }
}