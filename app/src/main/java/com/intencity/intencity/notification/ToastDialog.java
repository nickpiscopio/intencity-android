package com.intencity.intencity.notification;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.util.Constant;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class creates an Toast Dialog to show to the user.
 *
 * Created by Nick Piscopio on 6/5/16.
 */
public class ToastDialog
{
    /**
     * The ToastDialog constructor.
     *
     * @param context       This is the class (not context) that is needed to display the dialog.
     * @param content       The CustomDialogContent for the toast.
     *                      This only accepts a title and positive button for now because that is all we need currently.
     *                      We may never need anything else, but this sets it up for an easy change if we need more.
     * @param listener      The listener for which button was clicked.
     */
    public ToastDialog(Context context, CustomDialogContent content, final DialogListener listener)
    {
        // The timeout for when the dialog should be dismissed.
        int dialogTimeout = 4000;

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_toast_clickable);

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        // Sets the dialog to the bottom of the screen.
        layoutParams.gravity = Gravity.BOTTOM;

        // Do not dim the background.
        layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        // This flag makes it so we can use the UI with the dialog active.
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.setAttributes(layoutParams);

        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText(content.getTitle());

        Button button = (Button) dialog.findViewById(R.id.button);
        button.setText(context.getString(content.getPositiveButtonStringRes()));
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onButtonPressed(Constant.POSITIVE_BUTTON);
            }
        });

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