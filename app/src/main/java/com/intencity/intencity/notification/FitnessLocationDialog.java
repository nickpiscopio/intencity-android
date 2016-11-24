package com.intencity.intencity.notification;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.intencity.intencity.R;
import com.intencity.intencity.adapter.PlaceArrayAdapter;
import com.intencity.intencity.listener.DialogFitnessLocationListener;
import com.intencity.intencity.util.Constant;

/**
 * This class creates a dialog to show to the user to select a fitness location.
 *
 * Created by Nick Piscopio on 11/24/16.
 */
public class FitnessLocationDialog implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private Context context;

    private GoogleApiClient mGoogleApiClient;

    /**
     * The constructor for the dialog.
     *
     * @param activity      The activity that called this class.
     * @param displayName   The fitness location's display name.
     *                      We use this to populate the display name EditText.
     * @param location      The fitness location that the user exercises.
     *                      We use this to populate the location EditText.
     * @param listener      The listener so we can send the new display name and location back to the original activity.
     */
    public FitnessLocationDialog(Activity activity, String displayName, String location, final DialogFitnessLocationListener listener)
    {
        context = activity.getApplicationContext();

        getGoogleApiClientInstance(context);
        connectGoogleService();

        int style = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) ?
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert :
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, style);

        String title = context.getString(R.string.dialog_fitness_location_title);

        LayoutInflater factory = LayoutInflater.from(context);

        View view = factory.inflate(R.layout.dialog_fitness_location, null);

        final EditText editTextDisplayName = (EditText) view.findViewById(R.id.edit_text_display_name);
        final AutoCompleteTextView editTextLocation = (AutoCompleteTextView) view.findViewById(R.id.edit_text_location);
        editTextLocation.setAdapter(new PlaceArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, getCurrentBounds(context), null /*editTextLocation.getText().toString()*/));

        editTextDisplayName.setText(displayName);
        editTextLocation.setText(location);

        alertDialog.setView(view);
        alertDialog.setTitle(title);
        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Return the name and location.
                listener.onSaveFitnessLocation(editTextDisplayName.getText().toString(), editTextLocation.getText().toString());

                disconnectGoogleService();
            }
        });

        alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                disconnectGoogleService();
            }
        });

        alertDialog.show();
    }

    private void getGoogleApiClientInstance()
    {
        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void connectGoogleService()
    {
        mGoogleApiClient.connect();
    }

    private void disconnectGoogleService()
    {
        mGoogleApiClient.disconnect();
    }

    private LatLngBounds getCurrentBounds(Activity activity)
    {
        double radiusDegrees = 1.0;

//        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//        {
//
//            ActivityCompat.requestPermissions(context, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
//                    LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION );
//        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        Constant.REQUEST_CODE_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng northEast = new LatLng(center.latitude + radiusDegrees, center.longitude + radiusDegrees);
        LatLng southWest = new LatLng(center.latitude - radiusDegrees, center.longitude - radiusDegrees);

        return LatLngBounds.builder()
                .include(northEast)
                .include(southWest)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}