package com.intencity.intencity.notification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.SaveRoutineListener;

/**
 * This class creates a dialog to show to the user.
 *
 * Created by Nick Piscopio on 5/9/16.
 */
public class SaveDialog
{
    public enum SaveDialogState
    {
        INIT,
        SAME_NAME_ERROR,
        NAME_REQUIRED
    }

    public SaveDialog(Context context, final SaveRoutineListener listener, SaveDialogState state)
    {
        int style = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) ?
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert :
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, style);

        String title;
        String message;

        switch (state)
        {
            case SAME_NAME_ERROR:
                title = context.getString(R.string.routine_saved_title_error);
                message = context.getString(R.string.routine_saved_description_error);
                break;
            case NAME_REQUIRED:
                title = context.getString(R.string.routine_saved_title_error);
                message = context.getString(R.string.routine_saved_routine_name_error);
                break;
            case INIT:
            default:
                title = context.getString(R.string.routine_saved_title);
                message = context.getString(R.string.routine_saved_description);
                break;
        }

        LayoutInflater factory = LayoutInflater.from(context);

        View view = factory.inflate(R.layout.dialog_save_routine, null);

        final EditText routineNameEditText = (EditText) view.findViewById(R.id.edit_text_routine_name);

        alertDialog.setView(view);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                listener.onSavePressed(routineNameEditText.getText().toString().trim());
            }
        });
        alertDialog.show();
    }
}