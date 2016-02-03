package com.intencity.intencity.notification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.NotificationAdapter;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * This class creates a dialog to show to the user.
 *
 * Created by Nick Piscopio on 12/10/15.
 */
public class CustomDialog
{
    public CustomDialog(Context context, final DialogListener dialogListener, CustomDialogContent dialog, boolean dismissOnBack)
    {
        int style = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) ?
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert :
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, style);

        String title = dialog.getTitle();
        String message = dialog.getMessage();
        String[] buttons = dialog.getButtons();
        boolean includeNegativeButton = dialog.includeNegativeButton();

        if (title != null && message != null)
        {
            ArrayList<AwardDialogContent> awards = dialog.getAwards();
            if (awards != null && awards.size() > 0)
            {
                LayoutInflater factory = LayoutInflater.from(context);

                View view = factory.inflate(R.layout.alert_badge_view, null);

                NotificationAdapter settingsAdapter =
                        new NotificationAdapter(context, R.layout.list_item_award, awards);

                ListView listView = (ListView) view.findViewById(R.id.list_view_awards);
                listView.setAdapter(settingsAdapter);

                alertDialog.setView(view);
            }
            else
            {
                alertDialog.setMessage(message);
            }

            alertDialog.setTitle(title);

            int positiveButtonRes = dialog.getPositiveButtonStringRes();
            int negativeButtonRes = dialog.getNegativeButtonStringRes();

            alertDialog.setPositiveButton(positiveButtonRes > 0 ? positiveButtonRes : android.R.string.ok, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    if (dialogListener != null)
                    {
                        dialogListener.onButtonPressed(Constant.POSITIVE_BUTTON);
                    }
                }
            });

            if (includeNegativeButton)
            {
                alertDialog.setNegativeButton(positiveButtonRes > 0 ? negativeButtonRes : android.R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (dialogListener != null)
                        {
                            dialogListener.onButtonPressed(Constant.NEGATIVE_BUTTON);
                        }
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
                    if (dialogListener != null)
                    {
                        dialogListener.onButtonPressed(which);
                    }
                }
            });
        }

        alertDialog.setCancelable(dismissOnBack);
        alertDialog.show();
    }
}