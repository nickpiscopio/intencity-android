package com.intencity.intencity.task;

import android.os.AsyncTask;
import android.util.Log;

import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The AsyncTask for service calls.
 *
 * Created by Nick Piscopio on 12/10/15.
 */
public class ServiceTask extends AsyncTask<String, Void, String>
{
    // This is the failure response from Intencity's services.
    public static final String RESPONSE_FAILURE = "RESPONSE_FAILURE";

    // This is the OK response from Google Services.
    public static final String RESPONSE_OK = "OK";

    // This is a node from Google services to check the status of an API call,
    public static final String NODE_STATUS = "status";

    private final String POST = "POST";
    private final String GET = "GET";

    private ServiceListener serviceListener;

    private boolean success = true;

    /**
     * This is a constructor for the ServiceTask.
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
        OutputStreamWriter request;

        URL url;
        StringBuffer response = new StringBuffer();
        String inputLine;
        String parameters = "";
        String requestMethod;

        try
        {
            parameters = params[1];
            requestMethod = POST;
        }
        catch (Exception e)
        {
            requestMethod = GET;
        }

        try
        {
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod(requestMethod);

            if (requestMethod.equals(POST))
            {
                request = new OutputStreamWriter(connection.getOutputStream());
                request.write(parameters);
                request.flush();
                request.close();
            }

            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);

            while ((inputLine = reader.readLine()) != null)
            {
                response.append(inputLine);
            }

            isr.close();
            reader.close();
        }
        catch(IOException e)
        {
            Log.e(Constant.TAG, "Error retrieving data: " + e.toString());

            success = false;
        }

        return response.toString();
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (serviceListener != null)
        {
            int statusCode = Constant.STATUS_CODE_FAILURE_GENERIC;

            if (success)
            {
                try
                {
                    JSONObject obj = new JSONObject(result);

                    statusCode = obj.getInt(Constant.STATUS_CODE);

                    String data = obj.getString(Constant.DATA);

                    serviceListener.onServiceResponse(statusCode, data);
                }
                catch (JSONException ex)
                {
                    // If we fail, it might be because we don't understand the response, so just send the result.
                    serviceListener.onServiceResponse(statusCode, result);
                }
            }
            else
            {
                // If we fail, it might be because we don't understand the response, so just send the result.
                serviceListener.onServiceResponse(statusCode, result);
            }
        }
    }
}