package com.intencity.intencity.notification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.intencity.intencity.R;
import com.intencity.intencity.adapter.PlaceArrayAdapter;
import com.intencity.intencity.listener.DialogFitnessLocationListener;

/**
 * This class creates a dialog to show to the user to select a fitness location.
 *
 * Created by Nick Piscopio on 11/24/16.
 */
public class FitnessLocationDialog
{
    /**
     * The constructor for the dialog.
     *
     * @param context       The application's context.
     * @param displayName   The fitness location's display name.
     *                      We use this to populate the display name EditText.
     * @param location      The fitness location that the user exercises.
     *                      We use this to populate the location EditText.
     * @param apiClient     The google api client so we can get the addresses when the user types it in the location dialog.
     * @param listener      The listener so we can send the new display name and location back to the original activity.
     */
    public FitnessLocationDialog(Context context, String displayName, String location, GoogleApiClient apiClient, Location deviceLocation, final DialogFitnessLocationListener listener)
    {
        int style = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) ?
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert :
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, style);

        String title = context.getString(R.string.dialog_fitness_location_title);

        LayoutInflater factory = LayoutInflater.from(context);

        View view = factory.inflate(R.layout.dialog_fitness_location, null);

        final EditText editTextDisplayName = (EditText) view.findViewById(R.id.edit_text_display_name);
        final AutoCompleteTextView editTextLocation = (AutoCompleteTextView) view.findViewById(R.id.edit_text_location);

        PlaceArrayAdapter adapter = new PlaceArrayAdapter(context, android.R.layout.simple_list_item_1, getCurrentBounds(deviceLocation), null);
        adapter.setGoogleApiClient(apiClient);
        editTextLocation.setAdapter(adapter);

        editTextDisplayName.setText(displayName);
        editTextLocation.setText(location);

        alertDialog.setView(view);
        alertDialog.setTitle(title);
        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // Return the name and location.
                listener.onSaveFitnessLocation(editTextDisplayName.getText().toString(), editTextLocation.getText().toString());
            }
        });

        alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which) { }
        });

        alertDialog.show();
    }

    /**
     * Gets the current bounds of the user's device.
     *
     * @param location      The last known location of the user's device.
     *
     * @return A LatLngBounds so we can find an address the user types in easier.
     */
    private LatLngBounds getCurrentBounds(Location location)
    {
        double radiusDegrees = 1.0;

        LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng northEast = new LatLng(center.latitude + radiusDegrees, center.longitude + radiusDegrees);
        LatLng southWest = new LatLng(center.latitude - radiusDegrees, center.longitude - radiusDegrees);

        return LatLngBounds.builder()
                .include(northEast)
                .include(southWest)
                .build();
    }
}