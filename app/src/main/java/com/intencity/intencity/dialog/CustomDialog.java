package com.intencity.intencity.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.intencity.intencity.listener.DialogListener;

/**
 * Created by nickpiscopio on 12/10/15.
 */
public class CustomDialog
{
    public CustomDialog(Context context, final DialogListener dialogListerner, String title, String message, boolean includeNegativeButton)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialogListerner.onPositiveButtonPressed();
            }
        });

        if (includeNegativeButton)
        {
            alertDialog.setNegativeButton(android.R.string.cancel,
                                          new DialogInterface.OnClickListener()
                                          {
                                              public void onClick(DialogInterface dialog, int which)
                                              {
                                                  dialogListerner.onNegativeButtonPressed();
                                              }
                                          });
        }

        alertDialog.show();
    }
}
