package com.intencity.intencity.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.util.Constant;

/**
 * This class creates a dialog to show to the user.
 *
 * Created by Nick Piscopio on 12/10/15.
 */
public class CustomDialog
{
    public CustomDialog(Context context, final DialogListener dialogListener, Dialog dialog)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

        String title = dialog.getTitle();
        String message = dialog.getMessage();
        String[] buttons = dialog.getButtons();
        boolean includeNegativeButton = dialog.includeNegativeButton();

        if (title != null && message != null)
        {
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    dialogListener.onButtonPressed(Constant.POSITIVE_BUTTON);
                }
            });

            if (includeNegativeButton)
            {
                alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialogListener.onButtonPressed(Constant.NEGATIVE_BUTTON);
                    }
                });
            }
        }
        else
        {
            if (title != null)
            {
                alertDialog.setTitle(title);
            }

            alertDialog.setItems(buttons, new DialogInterface.OnClickListener()
            {
                @Override public void onClick(DialogInterface dialog, int which)
                {
                    dialogListener.onButtonPressed(which);
                }
            });
        }

        alertDialog.show();
    }
}