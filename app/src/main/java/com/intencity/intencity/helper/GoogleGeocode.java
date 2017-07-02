package com.intencity.intencity.helper;

import android.Manifest;
import android.content.Context;
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
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.intencity.intencity.R;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.GeocodeListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.notification.ToastDialog;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;

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
    private final String TAG = GoogleGeocode.class.getSimpleName();

    public static final int REQUEST_CODE_CANCELED = 100;
    public static final int REQUEST_CODE_PERMISSION_NEEDED = 110;
    public static final int LOCATION_NOT_AVAILABLE = 120;
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

    private ToastDialog toastDialog;

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
        // We remove the spaces because on older version of android it doesn't see the URL as valid unless we manually replace spaces with the encoded equivalent.
        address = address.replaceAll(Constant.REGEX_SPACE, Constant.URL_ENCODE_SPACE);

        return PARAMETER_ADDRESS + address + PARAMETER_KEY + API_KEY;
    }

    /**
     * Gets the formatted address parameters for the google api service.
     *
     * @param location  The long/lat from the location we are trying to find.
     *
     * @return  The parameters for the google api service to get a formatted address.
     */
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
            googleApiClient.stopAutoManage(activity);
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
     * @param activity          The activity that called this method.
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults, FragmentActivity activity)
    {
        switch (requestCode)
        {
            case Constant.REQUEST_CODE_PERMISSION:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    listener.onLocationServiceEnabled();

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

        if (isLocationAccuracySufficient())
        {
            // The geocode API request to check if the fitness location is a valid address.
            // We need it to be valid because we use that locations proximity to check what equipment the user currently has.
            new ServiceTask(googleGeoCodeAddressListener).execute(ROUTE + getGoogleGeocodeAddressParameters(location));
        }
    }

    /**
     * Gets the address from a longitude and latitude.
     *
     * @param requestCode   The request code that called this method.
     * @param location      The location to get the address from.
     */
    public void getLastLocationAddress(int requestCode, Location location)
    {
        this.requestCode = requestCode;

        if (isLocationAccuracySufficient())
        {
            // The geocode API request to get an address from a long and lat.
            new ServiceTask(googleGeoCodeAddressListener).execute(ROUTE + getGoogleGeocodeFormattedAddressParameters(location));
        }
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

        if (isLocationAccuracySufficient())
        {
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
    }

    /**
     * Checks if the location service is enabled and high enough for Intencity to determine the user's location.
     *
     * @return Boolean value of whether Intencity will be able to determine the user's location.
     */
    private boolean isLocationAccuracySufficient()
    {
        int locationMode = Settings.Secure.LOCATION_MODE_OFF;

        try
        {
            // DOCUMENTATION: https://developer.android.com/reference/android/provider/Settings.Secure.html
            // 0 = LOCATION_MODE_OFF
            // 1 = LOCATION_MODE_SENSORS_ONLY
            // 2 = LOCATION_MODE_BATTERY_SAVING
            // 3 = LOCATION_MODE_HIGH_ACCURACY
            locationMode = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.LOCATION_MODE);
        }
        catch (Settings.SettingNotFoundException ex)
        {
            Log.d(TAG, "High accuracy isn't set: " + ex.getMessage());
        }

        if (locationMode < Settings.Secure.LOCATION_MODE_HIGH_ACCURACY)
        {
            int titleRes = R.string.gps_network_not_enabled_title;
            int positiveButtonRes = R.string.gps_network_positive_button;

            CustomDialogContent dialog = new CustomDialogContent(context.getString(titleRes));
            dialog.setPositiveButtonStringRes(positiveButtonRes);

            if (toastDialog == null)
            {
                toastDialog = new ToastDialog(activity, dialog, new DialogListener()
                {
                    @Override
                    public void onButtonPressed(int which)
                    {
                        switch (which)
                        {
                            case Constant.POSITIVE_BUTTON:
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                break;
                            case Constant.NEGATIVE_BUTTON:
                            default:
                                listener.onLocationServiceNotEnabled(REQUEST_CODE_CANCELED);
                                break;
                        }
                    }
                });
            }

            listener.onLocationServiceNotEnabled(LOCATION_NOT_AVAILABLE);
        }

//        return locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
        return true;
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
            CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.edit_equipment_location_permission_needed_title),
                                                                 context.getString(R.string.edit_equipment_location_permission_needed_description),
                                                                 true);
            new CustomDialog(activity, new DialogListener()
            {
                @Override
                public void onButtonPressed(int which)
                {
                    switch (which)
                    {
                        case Constant.POSITIVE_BUTTON:
                            ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, Constant.REQUEST_CODE_PERMISSION);
                            break;
                        default:
                            listener.onLocationServiceNotEnabled(REQUEST_CODE_CANCELED);
                            break;
                    }
                }
            }, dialog, true);

            listener.onLocationServiceNotEnabled(REQUEST_CODE_PERMISSION_NEEDED);
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
        public void onServiceResponse(int statusCode, String response)
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

                    listener.onGeocodeRetrievalSuccessful(requestCode, formattedAddress);
                }
                else
                {
                    listener.onGeocodeRetrievalFailed(requestCode);
                }
            }
            catch (JSONException e)
            {
                listener.onGeocodeRetrievalFailed(requestCode);
            }
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
        Location location = getLastLocation();
        if (location != null)
        {
            // We've connected to the Google Maps API, so notify the listener.
            listener.onGoogleApiClientConnected(requestCode, googleApiClient, location);
        }
        else
        {
            googleGeoCodeAddressListener.onServiceResponse(Constant.STATUS_CODE_FAILURE_GENERIC, null);
        }
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(context, context.getString(R.string.location_issue), Toast.LENGTH_SHORT).show();
    }
}