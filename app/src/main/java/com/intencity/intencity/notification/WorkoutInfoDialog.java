package com.intencity.intencity.notification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.intencity.intencity.R;

/**
 * This class creates a dialog to show to the user.
 *
 * Created by Nick Piscopio on 12/10/15.
 */
public class WorkoutInfoDialog
{
    private static final int SUSTAIN_ID = R.id.radio_button_sustain;

    public WorkoutInfoDialog(Context context)
    {
        int style = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) ?
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert :
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, style);

        String title = context.getString(R.string.how_to_workout_title);

        LayoutInflater factory = LayoutInflater.from(context);

        View view = factory.inflate(R.layout.dialog_workout_info, null);

        final TextView recommendedWeight = (TextView) view.findViewById(R.id.text_view_recommended_weight);
        final TextView recommendedDuration = (TextView) view.findViewById(R.id.text_view_recommended_duration);
        final TextView recommendedRest = (TextView) view.findViewById(R.id.text_view_recommended_rest);
        final TextView recommendedCardio = (TextView) view.findViewById(R.id.text_view_recommended_cardio);

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group_fitness_type);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.radio_button_gain:
                        recommendedWeight.setText(R.string.weight_gain);
                        recommendedDuration.setText(R.string.duration_gain);
                        recommendedRest.setText(R.string.rest_gain);
                        recommendedCardio.setText(R.string.cardio_day_gain);
                        break;
                    case SUSTAIN_ID:
                        recommendedWeight.setText(R.string.weight_sustain);
                        recommendedDuration.setText(R.string.duration_sustain);
                        recommendedRest.setText(R.string.rest_sustain);
                        recommendedCardio.setText(R.string.cardio_day_sustain);
                        break;
                    case R.id.radio_button_lose:
                        recommendedWeight.setText(R.string.weight_lose);
                        recommendedDuration.setText(R.string.duration_lose);
                        recommendedRest.setText(R.string.rest_lose);
                        recommendedCardio.setText(R.string.cardio_day_lose);
                        break;
                    case R.id.radio_button_tone:
                        recommendedWeight.setText(R.string.weight_tone);
                        recommendedDuration.setText(R.string.duration_tone);
                        recommendedRest.setText(R.string.rest_tone);
                        recommendedCardio.setText(R.string.cardio_day_tone);
                        break;
                    default:
                        break;
                }
            }
        });
        // We set the default to sustain.
        radioGroup.check(SUSTAIN_ID);

        alertDialog.setView(view);
        alertDialog.setTitle(title);
        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which) {}
        });
        alertDialog.show();
    }
}