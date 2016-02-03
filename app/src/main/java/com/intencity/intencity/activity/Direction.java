package com.intencity.intencity.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.intencity.intencity.R;
import com.intencity.intencity.adapter.DirectionListAdapter;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The class to tell the user the directions for a specific exercise.
 *
 * Created by Nick Piscopio on 12/28/15.
 */
public class Direction extends AppCompatActivity implements ServiceListener, YouTubePlayer.OnInitializedListener,
                                                            DialogListener
{
    private String API_KEY = "AIzaSyC8GgBP0oBCNUS3JQrDsnOlDu5fTJG37XE";

    private String videoUrl = "";

    private RelativeLayout youTubeLayout;
    private LinearLayout noVideoLayout;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        ActionBar actionBar = getSupportActionBar();

        youTubeLayout = (RelativeLayout) findViewById(R.id.youtube_layout);
        noVideoLayout = (LinearLayout) findViewById(R.id.no_video_layout);

        context = getApplicationContext();

        Bundle bundle = getIntent().getExtras();

        String exerciseName = bundle.getString(Constant.BUNDLE_EXERCISE_NAME, "");

        if(!exerciseName.equals(""))
        {
            searchForDirections(exerciseName);

            if (actionBar != null)
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(exerciseName);
            }
        }
        else
        {
            // Display to user that we can't find the exercise.
            showMessage(context.getString(R.string.intencity_communication_error));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * Searches the database for the directions to an exercise.
     */
    private void searchForDirections(String exerciseName)
    {
        new ServiceTask(this).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(
                                              Constant.STORED_PROCEDURE_GET_EXERCISE_DIRECTION,
                                              exerciseName));
    }

    /**
     * Populates the direction list.
     *
     * @param directions    The ArrayList of directions.
     * @param submittedBy   The user that submitted the exercise.
     * @param videoUrl      The URL of the video to load from YouTube.
     */
    private void populate(ArrayList<String> directions, String submittedBy, String videoUrl)
    {
        this.videoUrl = videoUrl;

        try
        {
            // Initializing YouTube player view to search for the video.
            YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment)getFragmentManager()
                    .findFragmentById(R.id.youtube_view);

            youTubePlayerFragment.initialize(API_KEY, this);
        }
        catch (Exception e)
        {
            // Remove the youtube layout because it cannot be loaded.
            youTubeLayout.setVisibility(View.GONE);
            noVideoLayout.setVisibility(View.VISIBLE);
        }


        ListView directionsListView = (ListView) findViewById(R.id.list_view_directions);

        // Populates the directions list.
        DirectionListAdapter menuAdapter = new DirectionListAdapter(getApplicationContext(), directions);
        directionsListView.setAdapter(menuAdapter);

        TextView submittedByTextView = (TextView) findViewById(R.id.text_view_submitted_by);
        submittedByTextView.setText(getResources().getString(R.string.submitted_by, submittedBy));
    }

    /**
     * Show a message to the user.
     *
     * @param message   The message to display.
     */
    private void showMessage(String message)
    {
        CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), message, false);

        new CustomDialog(Direction.this, this, dialog, false);
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            ArrayList<String> directions = new ArrayList<>();
            String submittedBy = "";
            String videoUrl = "";

            JSONArray array = new JSONArray(response);

            int length = array.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject object = array.getJSONObject(i);

                if (i == 0)
                {
                    submittedBy = object.getString(Constant.COLUMN_SUBMITTED_BY);
                    videoUrl = object.getString(Constant.COLUMN_VIDEO_URL);
                }

                // Start at the third character so we can remove the number from the string.
                // This also means we can never have more than 9 steps in the directions.
                // We add the number later so it can be formatted properly.
                String step = object.getString(Constant.COLUMN_DIRECTION).substring(3);
                directions.add(step);
            }

            populate(directions, submittedBy, videoUrl);
        }
        catch (JSONException e)
        {
            showMessage(context.getString(R.string.intencity_communication_error));

            Log.e(Constant.TAG, "Error parsing muscle group data " + e.toString());
        }
    }

    @Override
    public void onRetrievalFailed()
    {
        showMessage(context.getString(R.string.intencity_communication_error));
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored)
    {
        // Add listeners to YouTubePlayer instance
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        // Start buffering
        if (!wasRestored) {
            player.cueVideo(videoUrl);
        }
    }

    @Override public void
    onInitializationFailure(YouTubePlayer.Provider provider,
                                                  YouTubeInitializationResult youTubeInitializationResult)
    {
        // Remove the youtube layout because it cannot be loaded.
        youTubeLayout.setVisibility(View.GONE);
        noVideoLayout.setVisibility(View.VISIBLE);
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener()
    {
        @Override
        public void onBuffering(boolean arg0) { }

        @Override
        public void onPaused() { }

        @Override
        public void onPlaying() { }

        @Override
        public void onSeekTo(int arg0) { }

        @Override
        public void onStopped() { }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener()
    {
        @Override
        public void onAdStarted() { }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) { }

        @Override
        public void onLoaded(String arg0) { }

        @Override
        public void onLoading()  { }

        @Override
        public void onVideoEnded()  { }

        @Override
        public void onVideoStarted()  { }
    };

    @Override
    public void onButtonPressed(int which)
    {
        // There is only 1 buttons so we are going to ignore the switch.
        finish();
    }
}