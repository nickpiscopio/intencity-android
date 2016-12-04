package com.intencity.intencity.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.intencity.intencity.R;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.GeocodeListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.notification.ToastDialog;
import com.intencity.intencity.task.ServiceTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class handles the Google Geocode API.
 *
 * Created by Nick Piscopio on 12/4/16.
 */
public class GoogleGeocode implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final int GOOGLE_API_CLIENT_ID = 0;

    // Service Endpoint
    private final String ENDPOINT = "https://maps.googleapis.com/";
    private final String ROUTE = ENDPOINT + "maps/api/geocode/json";

    // Parameters
    private final String PARAMETER_LAT_LONG = "?latlng=";
    private final String PARAMETER_ADDRESS = "?address=";
    private final String PARAMETER_KEY = "&key=";
    private final String PARAMETER_SENSOR = "&sensor=true";
    private final String DELIMITER = ",";
    private final String API_KEY = "AIzaSyB8gL500bzBsvzh8QEOzkRl_GhwziH8vtU";

    // Node names from Google's Geocode API.
    private final String NODE_RESULTS = "results";
    private final String NODE_FORMATTED_ADDRESS = "formatted_address";

    private int requestCode;

    private GoogleApiClient googleApiClient;

    private FragmentActivity activity;

    private Context context;

    private GeocodeListener listener;

    /**
     * The GoogleGeocode constructor.
     *
     * @param activity  The activity that created this class.
     * @param listener  The GeocodeListener to call when operations in this class complete.
     */
    public GoogleGeocode(FragmentActivity activity, GeocodeListener listener)
    {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
        this.listener = listener;
    }

    /**
     * Creates the geocode parameters.
     *
     * @param address   The address that we are validation.
     *
     * @return  The geocode parameters.
     */
    public String getGoogleGeocodeAddressParameters(String address)
    {
        return PARAMETER_ADDRESS + address + PARAMETER_KEY + API_KEY;
    }

    public String getGoogleGeocodeFormattedAddressParameters(Location location)
    {
        return PARAMETER_LAT_LONG + location.getLatitude() + DELIMITER + location.getLongitude() + PARAMETER_SENSOR;
    }

    /**
     * Called when onDestroy is called in the activity.
     */
    public void onDestroy()
    {
        if (googleApiClient != null)
        {
            googleApiClient.disconnect();
        }
    }

    /**
     * Called when onRequestPermissionResult is called in the activity.
     *
     * @param requestCode       The request code passed in requestPermissions(String[], int).
     * @param permissions       The requested permissions. Never null.
     * @param grantResults      The grant results for the corresponding permissions which is either
     *                          android.content.pm.PackageManager.PERMISSION_GRANTED or android.content.pm.PackageManager.PERMISSION_DENIED.
     *                          Never null.
     * @param activity          Teh activity that called this method.
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults, FragmentActivity activity)
    {
        switch (requestCode)
        {
            case Constant.REQUEST_CODE_PERMISSION:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    requestGoogleApiClient();
                }
                else
                {
                    CustomDialogContent content = new CustomDialogContent(context.getString(R.string.edit_equipment_location_permission_needed));
                    content.setPositiveButtonStringRes(R.string.button_preferences);

                    new ToastDialog(activity, content, dialogListener);

                    listener.onRequestPermissionsResult(requestCode, permissions, grantResults, activity);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Check if a location is valid according to Google's Geocode API.
     *
     * @param requestCode   The request code that called this method.
     * @param location      The location to validate.
     */
    public void checkLocationValidity(int requestCode, String location)
    {
        this.requestCode = requestCode;

        // The geocode API request to check if the fitness location is a valid address.
        // We need it to be valid because we use that locations proximity to check what equipment the user currently has.
        new ServiceTask(googleGeoCodeAddressListener).execute(ROUTE + getGoogleGeocodeAddressParameters(location));
    }

    /**
     * Checks to see if we have the location permission.
     *
     * @param requestCode  The request code that called this method.
     *                     We use this when we want to know what to do next in the activity that called this method.
     */
    public void checkLocationPermission(int requestCode)
    {
        // We use this to switch in GeocodeListener.onGoogleApiClientConnected();
        this.requestCode = requestCode;

        // This is for Android M+
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            // Permission was already granted, so show the fitness location dialog.
            requestGoogleApiClient();
        }
        else
        {
            requestLocationPermission();
        }
    }

    /**
     * Gets the address from a longitude and latitude.
     *
     * @param requestCode   The request code that called this method.
     * @param location  The location to get the address from.
     */
    public void getLastLocationAddress(int requestCode, Location location)
    {
        this.requestCode = requestCode;

        // The geocode API request to get an address from a long and lat.
        new ServiceTask(googleGeoCodeFormattedAddressListener).execute(ROUTE + getGoogleGeocodeFormattedAddressParameters(location));
    }

    /**
     * Displays the fitness location dialog to the user.
     */
    private void requestGoogleApiClient()
    {
        if (googleApiClient == null)
        {
            googleApiClient = new GoogleApiClient.Builder(context)
                                    .addApi(Places.GEO_DATA_API)
                                    .addApi(LocationServices.API)
                                    .enableAutoManage(activity, GOOGLE_API_CLIENT_ID, this)
                                    .addConnectionCallbacks(this).build();
        }

        if (googleApiClient.isConnected())
        {
            // We're already connected to google's service, so notify the listener.
            listener.onGoogleApiClientConnected(requestCode, googleApiClient, getLastLocation());
        }
        else
        {
            googleApiClient.connect();
        }
    }

    /**
     * Requests the location permission.
     */
    private void requestLocationPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(activity)
                    .setMessage(context.getResources().getString(R.string.edit_equipment_location_permission_needed))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, Constant.REQUEST_CODE_PERMISSION);
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            }).show();
        }
        else
        {
            // Permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, Constant.REQUEST_CODE_PERMISSION);
        }
    }

    /**
     * Gets the last known location for the user.
     */
    // We suppress this warning because at this point in the code, we already have the permission to use the user's location.
    @SuppressWarnings({ "MissingPermission" })
    private Location getLastLocation()
    {
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    /**
     * Starts the app settings screen.
     */
    private void startInstalledAppDetailsActivity()
    {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(intent);

        Toast.makeText(context, context.getString(R.string.directions_set_permissions), Toast.LENGTH_LONG).show();
    }

    /**
     * The service listener to check if the location the user typed in was a valid address according to google.
     */
    private ServiceListener googleGeoCodeAddressListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            try
            {
                JSONObject obj = new JSONObject(response);
                String status = obj.getString(ServiceTask.NODE_STATUS);
                if (status.equalsIgnoreCase(ServiceTask.RESPONSE_OK))
                {
                    listener.onRetrievalSuccessful(requestCode, null);
                }
                else
                {
                    onRetrievalFailed();
                }
            }
            catch (JSONException e)
            {
                onRetrievalFailed();
            }
        }

        @Override
        public void onRetrievalFailed()
        {
            listener.onRetrievalFailed(requestCode);
        }
    };

    /**
     * The service listener to check for the user's formatted address.
     *
     * i.e. 1234 Abc Street, State, 00000, Country
     */
    private ServiceListener googleGeoCodeFormattedAddressListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            try
            {
                JSONObject obj = new JSONObject(response);
                String status = obj.getString(ServiceTask.NODE_STATUS);
                if (status.equalsIgnoreCase(ServiceTask.RESPONSE_OK))
                {
                    JSONArray addresses = obj.getJSONArray(NODE_RESULTS);
                    // The formatted address we are looking for will always be in the first index,
                    // so there is no reason to search through the array.
                    String formattedAddress = addresses.getJSONObject(0).getString(NODE_FORMATTED_ADDRESS);

                    listener.onRetrievalSuccessful(requestCode, formattedAddress);
                }
                else
                {
                    onRetrievalFailed();
                }
            }
            catch (JSONException e)
            {
                onRetrievalFailed();
            }
        }

        @Override
        public void onRetrievalFailed()
        {
            listener.onRetrievalFailed(requestCode);
        }
    };

    /**
     * The dialog listener for the equipment activity.
     */
    private DialogListener dialogListener = new DialogListener()
    {
        @Override
        public void onButtonPressed(int which)
        {
            switch (which)
            {
                case Constant.POSITIVE_BUTTON:
                    startInstalledAppDetailsActivity();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        // We've connected to the Google Maps API, so notify the listener.
        listener.onGoogleApiClientConnected(requestCode, googleApiClient, getLastLocation());
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(context, context.getString(R.string.location_issue), Toast.LENGTH_SHORT).show();
    }
}