package com.intencity.intencity.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intencity.intencity.R;
import com.intencity.intencity.handler.NotificationHandler;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ShareListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.notification.AwardDialogContent;
import com.intencity.intencity.notification.ToastDialog;
import com.intencity.intencity.task.ShareTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;
import com.intencity.intencity.util.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * This is the overview activity for Intencity.
 *
 * Created by Nick Piscopio on 6/5/16.
 */
public class OverviewActivity extends AppCompatActivity implements ShareListener, DialogListener
{
    private enum Card
    {
        EXERCISE,
        AWARD
    }

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private final String IMAGE_TYPE = "image/png";

    private Context context;

    private ProgressDialog progressDialog;

    private SecurePreferences securePreferences;
    private String email;
    private String warmUphExerciseName;
    private String stretchExerciseName;

    private LinearLayout layout;
    private LinearLayout exerciseLayout;
    private LinearLayout awardLayout;

    private ArrayList<Exercise> exercises;

    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        context = getApplicationContext();

        securePreferences = new SecurePreferences(context);
        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");
        warmUphExerciseName = getString(R.string.warm_up);
        stretchExerciseName = getString(R.string.stretch);

        layout = (LinearLayout) findViewById(R.id.layout);
        TextView title = (TextView) findViewById(R.id.text_view_header);
        TextView date = (TextView) findViewById(R.id.text_view_date);
        exerciseLayout = (LinearLayout) findViewById(R.id.layout_exercise);
        awardLayout = (LinearLayout) findViewById(R.id.layout_award);

        inflater = getLayoutInflater();

        Bundle extras = getIntent().getExtras();
        String routineTitle = extras.getString(Constant.BUNDLE_ROUTINE_NAME);
        exercises = extras.getParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST);

        title.setText(context.getString(R.string.header_overview, routineTitle));
        // Alternate date format EEE, MMM d, yyyy
        date.setText(new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(new Date()));

        addExercises();
        addAwards();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.share:
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
                {
                    // RUNTIME PERMISSION Android M
                    if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    {
                        share();
                    }
                    else
                    {
                        requestPermission();
                    }
                }

                return true;
            case R.id.finish:
                finishOverview();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * Adds the exercises to the exercise card.
     */
    private void addExercises()
    {
        addHeader(Card.EXERCISE, exerciseLayout);

        for (Exercise exercise : exercises)
        {
            String exerciseName = exercise.getName();
            if(exerciseName.equals(warmUphExerciseName) || exerciseName.equals(stretchExerciseName))
            {
                continue;
            }

            View titleRow = inflater.inflate(R.layout.list_item_overview_exercise_title, exerciseLayout, false);
            TextView title = (TextView) titleRow.findViewById(R.id.text_view_title);
            LinearLayout setLayout = (LinearLayout) titleRow.findViewById(R.id.layout_set);
            setExerciseTitle(exerciseName, exercise.isIncludedInIntencity(), title);

            ArrayList<Set> sets = exercise.getSets();
            int setSize = sets.size();
            for (int i = 0; i < setSize; i++)
            {
                Set set = sets.get(i);

                View setRow = inflater.inflate(R.layout.list_item_overview_exercise_set, setLayout, false);
                TextView setNumber = (TextView) setRow.findViewById(R.id.text_view_list_number);
                TextView weight = (TextView) setRow.findViewById(R.id.weight);
                TextView weightSuffix = (TextView) setRow.findViewById(R.id.weight_suffix);
                TextView slash = (TextView) setRow.findViewById(R.id.slash);
                TextView durationTextView = (TextView) setRow.findViewById(R.id.duration);
                TextView repsTextView = (TextView) setRow.findViewById(R.id.suffix);

                setWeight(set.getWeight(), weight, weightSuffix, slash);

                String reps = String.valueOf(set.getReps());
                String duration = set.getDuration();
                setDuration(duration == null || duration.equals(Constant.RETURN_NULL) ? reps : duration, repsTextView, durationTextView);

                if (showSet(durationTextView))
                {
                    String setNum = (i + 1) + ".";
                    setNumber.setText(setNum);

                    setLayout.addView(setRow);
                }
            }

            exerciseLayout.addView(titleRow);
        }
    }

    /**
     * Adds the awards to the award card.
     */
    private void addAwards()
    {
        addHeader(Card.AWARD, awardLayout);

        ArrayList<AwardDialogContent> awards = NotificationHandler.getInstance(null).getAwards();

        for (AwardDialogContent award : awards)
        {
            View row = inflater.inflate(R.layout.list_item_award, awardLayout, false);

            ImageView awardImage = (ImageView) row.findViewById(R.id.image_view_award);
            TextView title = (TextView) row.findViewById(R.id.text_view_title);
            TextView description = (TextView) row.findViewById(R.id.text_view_description);

            int awardImgRes = award.getImgRes();
            if (awardImgRes > 0)
            {
                awardImage.setImageResource(awardImgRes);
                awardImage.setVisibility(View.VISIBLE);

                title.setVisibility(View.GONE);
            }
            else
            {
                title.setText(award.getTitle());
                title.setVisibility(View.VISIBLE);

                awardImage.setVisibility(View.GONE);
            }

            description.setText(award.getDescription());

            awardLayout.addView(row);
        }
    }

    /**
     * Add a header to the card layout.
     *
     * @param type      The type of card we are adding the header to.
     * @param layout    The layout to add the header.
     */
    private void addHeader(Card type, LinearLayout layout)
    {
        View header = inflater.inflate(R.layout.list_item_overview_card_header, exerciseLayout, false);
        TextView headerText = (TextView) header.findViewById(R.id.text_view_title);

        String title = "";

        switch (type)
        {
            case EXERCISE:
                title = context.getString(R.string.title_exercises);
                break;
            case AWARD:
                title = context.getString(R.string.awards_title).toUpperCase();
                break;
            default:
                break;
        }

        headerText.setText(title);
        layout.addView(header);
    }

    /** 
     * Sets the exercise card information. 
     *
     * @param exerciseName          The name of the exercise to set the TextView. 
     * @param includedInIntencity   Boolean value of whether the exercise is included in Intencity. 
     * @param title                 The title TextView
     */
    private void setExerciseTitle(String exerciseName, boolean includedInIntencity, TextView title)
    {
        title.setTextColor(ContextCompat.getColor(context, includedInIntencity ? R.color.primary : R.color.secondary_dark));
        title.setText(exerciseName);
    }

    /**
     * Sets the duration TextView based on whether there are reps or time saved
     *
     * @param duration  The string value to set the duration TextView.
     */
    private void setDuration(String duration, TextView repsTextView, TextView durationTextView)
    {
        if (duration.contains(":"))
        {
            repsTextView.setVisibility(View.GONE);
        }
        else
        {
            repsTextView.setVisibility(View.VISIBLE);
        }

        durationTextView.setText(duration);
    }

    /**
     * Sets the weight text.
     *
     * @param weight            The string to set in the TextView.
     * @param weightTextView    The weight TextView.
     * @param weightSuffix      The weight suffix TextView.
     * @param slash             The slash TextView.
     */
    private void setWeight(float weight, TextView weightTextView, TextView weightSuffix, TextView slash)
    {
        if (weight <= 0)
        {
            weightTextView.setVisibility(View.GONE);
            weightSuffix.setVisibility(View.GONE);
            slash.setVisibility(View.GONE);
        }
        else
        {
            weightTextView.setVisibility(View.VISIBLE);
            weightSuffix.setVisibility(View.VISIBLE);
            slash.setVisibility(View.VISIBLE);

            weightTextView.setText(String.valueOf(weight));
        }
    }

    /**
     * Returns a boolean value of weather a set exists.
     *
     * @return Whether to show the set or not.
     */
    private boolean showSet(TextView duration)
    {
        return Integer.parseInt(duration.getText().toString().replaceAll(":", "")) > 0;
    }

    /**
     * Starts the task to share the overview screenshot.
     */
    private void share()
    {
        showProgressDialog(context.getString(R.string.share_processing));
        new ShareTask(this).execute(layout);
    }

    private void finishOverview()
    {
//        // We remove the exercises from the database here, so when we go back to
//        // the fitness log, it doesn't ask if we want to continue where we left off.
//        removeExercisesFromDatabase();
//
//        workoutFinished = true;
//
//        // Grant the user the "Kept Swimming" badge if he or she didn't skip an exercise.
//        if (!securePreferences.getBoolean(Constant.BUNDLE_EXERCISE_SKIPPED, false))
//        {
//            Util.grantBadgeToUser(email, Badge.KEPT_SWIMMING,
//                                  new AwardDialogContent(R.mipmap.kept_swimming,
//                                                         context.getString(R.string.award_kept_swimming_description)), true);
//        }
//        else
//        {
//            // Set the user has skipped an exercise to false for next time.
//            setExerciseSkipped(false);
//        }
//
//        String finisherDescription = context.getString(R.string.award_finisher_description);
//
//        NotificationHandler notificationHandler = NotificationHandler.getInstance(null);
//        ArrayList<AwardDialogContent> awards = notificationHandler.getAwards();
//
//        AwardDialogContent finisherAward = new AwardDialogContent(R.mipmap.finisher, finisherDescription);
//        if (!notificationHandler.hasAward(finisherAward))
//        {
//            Util.grantPointsToUser(email, Constant.POINTS_COMPLETING_WORKOUT, context.getString(
//                    R.string.award_completed_workout_description));
//            Util.grantBadgeToUser(email, Badge.FINISHER, finisherAward, true);
//        }
//
//        CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.completed_workout_title), context.getString(R.string.award_workout_completed_award_description), true);
//        dialog.setAwards(awards);
//        dialog.setPositiveButtonStringRes(R.string.tweet_button);
//        dialog.setNegativeButtonStringRes(R.string.finish_button);
//
//        new CustomDialog(context, dialogListener, dialog, true);
    }
//
//    /**
//     * The dialog listener for when the user finishes exercising.
//     */
//    private DialogListener dialogListener = new DialogListener()
//    {
//        @Override
//        public void onButtonPressed(int which)
//        {
//            switch (which)
//            {
//                case Constant.POSITIVE_BUTTON:
//                    // The user selected to tweet, so we open the twitter URI.
//                    Uri uri = Uri.parse(generateTweet());
//                    startActivityForResult(new Intent(Intent.ACTION_VIEW, uri), Constant.REQUEST_CODE_TWEET);
//                    break;
//                default:
//                    break;
//            }
//
//            // Start the routine view over again.
//            loadingListener.onFinishedLoading(Constant.ID_FRAGMENT_ROUTINE_RELOAD);
//        }
//    };

    /**
     * Requests permission to write external storage.
     */
    private void requestPermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(OverviewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(OverviewActivity.this)
                    .setMessage(context.getResources().getString(R.string.storage_needed))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            ActivityCompat.requestPermissions(OverviewActivity.this,
                                                              new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                              REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }
                    }).show();
        }
        else
        {
            // permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(OverviewActivity.this,
                                              new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                              REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                   share();
                }
                else
                {
                    new ToastDialog(OverviewActivity.this, this);

                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
               break;
            default:
                break;
        }
    }

    /**
     * Starts the app settings screen.
     */
    private void startInstalledAppDetailsActivity()
    {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(intent);

        Toast.makeText(context, context.getString(R.string.directions_set_permissions), Toast.LENGTH_LONG).show();
    }

    /**
     * Randomly generates a tweet from an array.
     *
     * Documentation:
     * https://dev.twitter.com/web/tweet-button/parameters
     *
     * @return  The generated tweet.
     */
    private String generateTweet()
    {
        String twitterUrl = "https://twitter.com/intent/tweet?text=";
        String[] tweetText = { "I %23dominated my %23workout with %23Intencity! %23WOD %23Fitness",
                               "I %23finished my %23workout of the day with %23Intencity! %23WOD %23Fitness",
                               "I made it through %23Intencity%27s %23routine! %23Fitness",
                               "Making %23gains with %23Intencity! %23WOD %23Fitness %23Exercise %23Gainz",
                               "%23Finished my %23Intencity %23workout! %23Retweet if you've %23exercised today. %23WOD %23Fitness",
                               "I %23lifted with %23Intencity today! %23lift %23lifting",
                               "%23Intencity %23trained me today!",
                               "Getting %23strong with %23Intencity! %23GetStrong %23DoWork %23Fitness",
                               "Getting that %23BeachBody with %23Intencity! %23Lift %23Exercise %23Fitness",
                               "Nothing feels better than finishing a great %23workout!"};
        String tweetUrl = "&url=www.Intencity.fit";
        String via = "&via=IntencityApp";

        int tweet = Util.getRandom(0, tweetText.length - 1);

        return twitterUrl + tweetText[tweet] + tweetUrl + via;
    }

    /**
     * Displays a progress dialog to the user.
     *
     * @param message   The message to display.
     */
    private void showProgressDialog(String message)
    {
        progressDialog = ProgressDialog.show(this, null, message, false);
    }

    @Override
    public void onImageProcessed(File file)
    {
        if (file != null)
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType(IMAGE_TYPE);

            startActivity(intent);
        }
        else
        {
            Toast.makeText(context, context.getString(R.string.error_processing_overview), Toast.LENGTH_SHORT).show();
        }

        progressDialog.dismiss();
    }

    @Override
    public void onButtonPressed(int which)
    {
        // There is only 1 button, so we aren't switching here.
        startInstalledAppDetailsActivity();
    }
}