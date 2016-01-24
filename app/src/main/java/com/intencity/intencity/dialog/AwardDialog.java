package com.intencity.intencity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
     * @param context   This is the class (not context) that is needed to display the dialog.
     * @param content   The content of the dialog.
     */
    public AwardDialog(Context context, AwardDialogContent content)
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

        ImageView imageView = (ImageView) dialog.findViewById(R.id.image_view_award);
        TextView titleTextView = (TextView) dialog.findViewById(R.id.text_view_title);
        TextView descriptionTextView = (TextView) dialog.findViewById(R.id.text_view_description);

        int imgRes = content.getImgRes();
        String title = content.getTitle();
        String description = content.getDescription();

        if (imgRes > 0)
        {
            imageView.setImageResource(imgRes);
            imageView.setVisibility(View.VISIBLE);
            titleTextView.setVisibility(View.GONE);
        }
        else
        {
            titleTextView.setText(title);
            titleTextView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }

        descriptionTextView.setText(description);

        dialog.show();

        // Dismiss the dialog after so many seconds.
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                // This try catch is needed in case the dialog
                // is dismissed before the code dismisses it.
                try
                {
                    dialog.dismiss();
                }
                catch (Exception e) { }
            }
        }, dialogTimeout);
    }
}