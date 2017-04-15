package com.intencity.intencity.listener;

import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * A listener for the GoogleGeocode class.
 *
 * Created by Nick Piscopio on 12/4/16.
 */
public interface GeocodeListener
{
    void onGoogleApiClientConnected(int requestCode, GoogleApiClient googleApiClient, Location location);
    void onRetrievalSuccessful(int requestCode, Object obj);
    void onRetrievalFailed(int requestCode);
    void onLocationServiceEnabled();
    void onLocationServiceNotEnabled(int requestCode);
    void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults, FragmentActivity activity);
}