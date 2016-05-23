package com.intencity.interval.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.intencity.interval.R;
import com.intencity.interval.util.Constant;

public class MainActivity extends WearableActivity
{
    private final int INCREMENT_INTERVAL_ID = R.id.button_increment_interval;
    private final int DECREMENT_INTERVAL_ID = R.id.button_decrement_interval;
    private final int INCREMENT_INTERVAL_TIME_ID = R.id.button_increment_interval_time;
    private final int DECREMENT_INTERVAL_TIME_ID = R.id.button_decrement_interval_time;
    private final int INCREMENT_INTERVAL_REST_ID = R.id.button_increment_interval_rest;
    private final int DECREMENT_INTERVAL_REST_ID = R.id.button_decrement_interval_rest;

    private int INTERVAL_MIN_THRESHOLD = 1;

    private Context context;

    private BoxInsetLayout containerView;
    private TextView intervalTextView;
    private TextView intervalTimeTextView;
    private TextView intervalRestTextView;
    private ImageButton incrementInterval;
    private ImageButton decrementInterval;
    private ImageButton incrementIntervalTime;
    private ImageButton decrementIntervalTime;
    private ImageButton incrementIntervalRest;
    private ImageButton decrementIntervalRest;

    private Button start;

    private int intervals = 1;
    private int intervalSeconds = 10;
    private int intervalRestSeconds = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        context = getApplicationContext();

        containerView = (BoxInsetLayout)findViewById(R.id.container);
        intervalTextView = (TextView)findViewById(R.id.text_view_interval);
        intervalTimeTextView = (TextView)findViewById(R.id.text_view_interval_time);
        intervalRestTextView = (TextView)findViewById(R.id.text_view_interval_rest);
        incrementInterval = (ImageButton)findViewById(INCREMENT_INTERVAL_ID);
        decrementInterval = (ImageButton)findViewById(DECREMENT_INTERVAL_ID);
        incrementIntervalTime = (ImageButton)findViewById(INCREMENT_INTERVAL_TIME_ID);
        decrementIntervalTime = (ImageButton)findViewById(DECREMENT_INTERVAL_TIME_ID);
        incrementIntervalRest = (ImageButton)findViewById(INCREMENT_INTERVAL_REST_ID);
        decrementIntervalRest = (ImageButton)findViewById(DECREMENT_INTERVAL_REST_ID);
        start = (Button) findViewById(R.id.start);

        incrementInterval.setOnClickListener(incrementIntervalClickListener);
        decrementInterval.setOnClickListener(decrementIntervalClickListener);
        incrementIntervalTime.setOnClickListener(incrementIntervalClickListener);
        decrementIntervalTime.setOnClickListener(decrementIntervalClickListener);
        incrementIntervalRest.setOnClickListener(incrementIntervalClickListener);
        decrementIntervalRest.setOnClickListener(decrementIntervalClickListener);
        start.setOnClickListener(startClickListener);

        setIntervals(intervalTextView, intervals);
        setIntervals(intervalTimeTextView, intervalSeconds);
        setIntervals(intervalRestTextView, intervalRestSeconds);
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
        if (isAmbient())
        {
            containerView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black));
            intervalTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        }
        else
        {
            containerView.setBackground(null);
            intervalTextView.setTextColor(ContextCompat.getColor(context, R.color.secondary_dark));
        }
    }

    private void setIntervals(TextView textView, int time)
    {
        textView.setText(String.valueOf(time));
    }

    View.OnClickListener incrementIntervalClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case INCREMENT_INTERVAL_ID:
                    setIntervals(intervalTextView, ++intervals);
                    break;
                case INCREMENT_INTERVAL_TIME_ID:
                    setIntervals(intervalTimeTextView, ++intervalSeconds);
                    break;
                case INCREMENT_INTERVAL_REST_ID:
                    setIntervals(intervalRestTextView, ++intervalRestSeconds);
                    break;
                default:
                    break;
            }
        }
    };

    View.OnClickListener decrementIntervalClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case DECREMENT_INTERVAL_ID:
                    if (intervals > INTERVAL_MIN_THRESHOLD)
                    {
                        setIntervals(intervalTextView, --intervals);
                    }
                    break;
                case DECREMENT_INTERVAL_TIME_ID:
                    if (intervalSeconds > INTERVAL_MIN_THRESHOLD)
                    {
                        setIntervals(intervalTimeTextView, --intervalSeconds);
                    }
                    break;
                case DECREMENT_INTERVAL_REST_ID:
                    if (intervalRestSeconds > INTERVAL_MIN_THRESHOLD)
                    {
                        setIntervals(intervalRestTextView, --intervalRestSeconds);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    View.OnClickListener startClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(MainActivity.this, IntervalActivity.class);
            intent.putExtra(Constant.BUNDLE_INTERVALS, intervals);
            intent.putExtra(Constant.BUNDLE_INTERVAL_MILLIS, intervalSeconds * Constant.ONE_SECOND_MILLIS);
            intent.putExtra(Constant.BUNDLE_INTERVAL_REST_MILLIS, intervalRestSeconds * Constant.ONE_SECOND_MILLIS);
            startActivity(intent);
        }
    };
}
