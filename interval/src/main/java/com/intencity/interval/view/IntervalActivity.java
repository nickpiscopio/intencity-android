package com.intencity.interval.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intencity.interval.R;
import com.intencity.interval.util.Constant;

public class IntervalActivity extends WearableActivity implements DelayedConfirmationView.DelayedConfirmationListener
{
    private enum ActivityState
    {
        WARM_UP,
        INTERVAL,
        REST,
        COOL_DOWN
    }

    // 1 Minute for the WARM-UP / COOL DOWN.
    private final int INJURY_PREVENTION_MILLIS = 60000;

    private Context context;

    private int intervals = 1;
    private int intervalSeconds = 10;
    private int intervalRestSeconds = 10;

    private int currentInterval = 0;

    private DelayedConfirmationView delayedView;
    private TextView title;
    private TextView timeLeftTextView;
    private ImageButton pause;
    private LinearLayout intervalLayout;

    private CountDownTimer countDownTimer;

    private ActivityState activityState;

    private String warmUpTitle;
    private String intervalTitle;
    private String restTitle;
    private String coolDownTitle;

    private LayoutInflater inflater;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval);
        setAmbientEnabled();

        context = getApplicationContext();

        Bundle extras = getIntent().getExtras();

        intervals = extras.getInt(Constant.BUNDLE_INTERVALS);
        intervalSeconds = extras.getInt(Constant.BUNDLE_INTERVAL_MILLIS);
        intervalRestSeconds = extras.getInt(Constant.BUNDLE_INTERVAL_REST_MILLIS);

        intervalLayout = (LinearLayout) findViewById(R.id.layout_intervals);
        title = (TextView) findViewById(R.id.title);
        timeLeftTextView = (TextView) findViewById(R.id.time_left);
        pause = (ImageButton) findViewById(R.id.pause);
        pause.setOnClickListener(pauseClickLister);

        delayedView = (DelayedConfirmationView) findViewById(R.id.delayed_confirm);
        delayedView.setListener(this);

        activityState = ActivityState.WARM_UP;

        warmUpTitle = context.getString(R.string.title_warm_up);
        intervalTitle = context.getString(R.string.title_interval);
        restTitle = context.getString(R.string.title_rest);
        coolDownTitle = context.getString(R.string.title_cool_down);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        res = context.getResources();

        addIntervalChart();

        startTimer();
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
            startTimer();
        }
    };

    @Override
    public void onTimerFinished(View view)
    {
        // User didn't cancel, perform the action
    }

    @Override
    public void onTimerSelected(View view) {
        // User canceled, abort the action
    }

    private void startTimer()
    {
        // The time we are displaying on the interval count down.
        int interval = 0;

        if (countDownTimer != null)
        {
            countDownTimer.cancel();
        }

        switch (activityState)
        {
            case WARM_UP:
                title.setText(warmUpTitle);
                interval = INJURY_PREVENTION_MILLIS;
                break;
            case INTERVAL:
                title.setText(intervalTitle);
                interval = intervalSeconds;
                break;
            case REST:
                title.setText(restTitle);
                interval = intervalRestSeconds;
                break;
            case COOL_DOWN:
                title.setText(coolDownTitle);
                interval = INJURY_PREVENTION_MILLIS;
                break;
            default:
                break;
        }

        countDownTimer = new CountDownTimer(interval, Constant.ONE_SECOND_MILLIS)
        {
            public void onTick(long millisUntilFinished)
            {

                timeLeftTextView.setText(String.valueOf(convertToSeconds(millisUntilFinished)));
            }

            public void onFinish()
            {
                boolean stillExercising = activityState != ActivityState.COOL_DOWN;

                switch (activityState)
                {
                    case WARM_UP:
                        activityState = ActivityState.INTERVAL;
                        break;
                    case INTERVAL:
                        currentInterval++;
                        activityState = currentInterval >= intervals ? ActivityState.COOL_DOWN : ActivityState.REST;
                        break;
                    case REST:
                        activityState = ActivityState.INTERVAL;
                        break;
                    case COOL_DOWN:
                    default:
                        // Do nothing
                        break;
                }

                if (stillExercising)
                {
                    // We start the timer over until we finish all the intervals.
                    startTimer();
                }

                timeLeftTextView.setText("0");
            }
        };

        // Two seconds to cancel the action
        delayedView.setTotalTimeMs(interval);
        // Start the timer
        delayedView.start();

        countDownTimer.start();
    }

    /**
     * Adds the interval chart to the UI.
     */
    private void addIntervalChart()
    {
        insertIntervalItem(ActivityState.WARM_UP);

        for (int i = 0; i < intervals; i++)
        {
            insertIntervalItem(ActivityState.INTERVAL);

            if (i != intervals - 1)
            {
                insertIntervalItem(ActivityState.REST);
            }
        }

        insertIntervalItem(ActivityState.COOL_DOWN);
    }

    /**
     * Inserts an interval item to the interval chart.
     *
     * @param state     The interval item to add.
     */
    private void insertIntervalItem(ActivityState state)
    {
        int margin = res.getDimensionPixelSize(R.dimen.chart_interval_margin);
        int height = res.getDimensionPixelSize(R.dimen.chart_interval_max_height);

        switch (state)
        {
            case INTERVAL:
                height *= getPercentage(convertToSeconds(intervalSeconds));
                break;
            case REST:
                height *= getPercentage(convertToSeconds(intervalRestSeconds));
                break;
            case WARM_UP:
            case COOL_DOWN:
            default:
                // We want this to be the max height since they are warm-up and cool down.
                break;
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, height, 1.0f);
//        params.setMarginStart(margin);
//        params.setMarginEnd(margin);
//        params.weight = 1.0f;

        View v = inflater.inflate(R.layout.view_chart_interval, null);
        v.setLayoutParams(params);

        intervalLayout.addView(v);
    }

    /**
     * Converts milliseconds to seconds.
     *
     * @param millisToConvert   The milliseconds to convert.
     *
     * @return  The converted seconds.
     */
    private int convertToSeconds(long millisToConvert)
    {
        float seconds = (float) Math.round((float) millisToConvert / (float) Constant.ONE_SECOND_MILLIS);
        return (int) seconds;
    }

    /**
     * Converts the seconds inputted to the percentage of INJURY_PREVENTION_MILLIS.
     * We use this to create the height of each inteval item.
     *
     * @param seconds   The seconds of the interval item.
     *
     * @return  The percentage to muliply the height of the interval item by.
     */
    private float getPercentage(int seconds)
    {
        return (float) seconds / (float) (INJURY_PREVENTION_MILLIS / Constant.ONE_SECOND_MILLIS);
    }
}