package com.intencity.interval.view;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;

import com.intencity.interval.R;
import com.intencity.interval.util.Constant;

public class IntervalActivity extends WearableActivity
{
    private Context context;

    private int intervals = 1;
    private long intervalSeconds = 10;
    private long intervalRestSeconds = 10;

    private TextView timeLeftTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval);
        setAmbientEnabled();

        context = getApplicationContext();

        Bundle extras = getIntent().getExtras();

        intervals = extras.getInt(Constant.BUNDLE_INTERVALS);
        intervalSeconds = extras.getLong(Constant.BUNDLE_INTERVAL_MILLIS);
        intervalRestSeconds = extras.getLong(Constant.BUNDLE_INTERVAL_REST_MILLIS);

        timeLeftTextView = (TextView) findViewById(R.id.time_left);

        new CountDownTimer(intervalSeconds, Constant.ONE_SECOND_MILLIS) {

            public void onTick(long millisUntilFinished) {
                timeLeftTextView.setText(String.valueOf(intervalSeconds / Constant.ONE_SECOND_MILLIS));
            }

            public void onFinish() {
//                mTextField.setText("done!");
            }
        }.start();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails)
    {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient()
    {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient()
    {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay()
    {
//        if (isAmbient())
//        {
//            containerView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black));
//            intervalTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
//        }
//        else
//        {
//            containerView.setBackground(null);
//            intervalTextView.setTextColor(ContextCompat.getColor(context, R.color.secondary_dark));
//        }
    }

    View.OnClickListener pauseClickLister = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

        }
    };
}
