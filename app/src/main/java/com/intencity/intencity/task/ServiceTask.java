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
 * The AsynTask for service calls.
 *
 * Created by Nick Piscopio on 12/10/15.
 */
public class ServiceTask extends AsyncTask<String, Void, String>
{
    private final String FAILURE = "FAILURE";
    private ServiceListener serviceListener;

    private boolean success = true;

    /**
     * This is the generic constructor for the ServiceTask.
     *
     * @param serviceListener   The listener to call when the service is done.
     */
    public ServiceTask(ServiceListener serviceListener)
    {
        this.serviceListener = serviceListener;
    }

    @Override
    protected String doInBackground(String... params)
    {
        HttpURLConnection connection;
        OutputStreamWriter request = null;

        URL url;
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

            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);

            // Response from server after login process will be stored in response variable.
            response = reader.readLine();

            isr.close();
            reader.close();

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
        if (success && result.length() > 0 && !result.replaceAll("\"", "").equalsIgnoreCase(FAILURE))
        {
            serviceListener.onRetrievalSuccessful(result);
        }
        else
        {
            serviceListener.onRetrievalFailed();
        }
    }
}
