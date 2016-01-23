package com.intencity.intencity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.intencity.intencity.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class creates an Award Dialog to show to the user.
 *
 * Created by Nick Piscopio on 1/23/16.
 */
public class AwardDialog
{
    /**
     * The AwardDialog constructor.
     *
     * @param context       This is the class (not context) that is needed to display the dialog.
     * @param title         The title of the dialog.
     * @param description   The description of the dialog.
     */
    public AwardDialog(Context context, String title, String description)
    {
        // The timeout for when the dialog should be dismissed.
        int dialogTimeout = 3500;

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_award);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        // Sets the dialog to the bottom of the screen.
        layoutParams.gravity = Gravity.BOTTOM;

        // Do not dim the background.
        layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        // This flag makes it so we can use the UI with the dialog active.
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.setAttributes(layoutParams);

        TextView titleTextView = (TextView) dialog.findViewById(R.id.text_view_title);
        TextView descriptionTextView = (TextView) dialog.findViewById(R.id.text_view_description);

        titleTextView.setText(title);
        descriptionTextView.setText(description);

        dialog.show();

        // Dismiss the dialog after so many seconds.
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                dialog.dismiss();
            }
        }, dialogTimeout);
    }
}