package com.intencity.intencity.task;

import android.os.AsyncTask;
import android.util.Log;

import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.util.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nickpiscopio on 12/10/15.
 */
public class ServiceTask extends AsyncTask<String, Void, String>
{
    private ServiceListener serviceListener;

    private boolean success;

    public ServiceTask(ServiceListener serviceListener)
    {
        this.serviceListener = serviceListener;
    }

    @Override
    protected String doInBackground(String... params)
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url = null;
        String response = null;
        String parameters = params[1];

        try
        {
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");

            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(parameters);
            request.flush();
            request.close();
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            // Response from server after login process will be stored in response variable.
            response = sb.toString();

            isr.close();
            reader.close();

            success = true;

        }
        catch(IOException e)
        {
            Log.e(Constant.TAG, "Error retrieving data: " + e.toString());

            success = false;
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (success)
        {
            serviceListener.onRetrievalSuccessful(result);
        }
        else
        {
            serviceListener.onRetrievalFailed();
        }
    }
}
