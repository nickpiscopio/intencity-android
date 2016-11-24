package com.intencity.intencity.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.handler.NotificationHandler;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ShareListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.notification.AwardDialogContent;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ShareTask;
import com.intencity.intencity.util.Badge;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.NotificationUtil;
import com.intencity.intencity.util.ScreenshotUtil;
import com.intencity.intencity.util.SecurePreferences;
import com.intencity.intencity.util.Util;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * This is the overview activity for Intencity.
 *
 * Created by Nick Piscopio on 6/5/16.
 */
public class OverviewActivity extends AppCompatActivity implements ShareListener
{
    private enum Card
    {
        EXERCISE,
        AWARD
    }

    private final String TEXT_TYPE = "text/plain";

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

    private NotificationHandler notificationHandler;

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

        routineTitle = context.getString(R.string.header_overview, routineTitle);

        title.setText(routineTitle.toUpperCase());

        Date now = new Date();
        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        date.setText(format.format(now));

        grantAwards();
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
                share();
                return true;
            case R.id.finish:
                CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.title_finish_routine), context.getString(R.string.description_finish_routine), true);
                dialog.setPositiveButtonStringRes(R.string.title_finish);
                dialog.setNegativeButtonStringRes(R.string.title_share);

                new CustomDialog(OverviewActivity.this, dialogListener, dialog, true);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onImageProcessed()
    {
        File imagePath = new File(context.getCacheDir(), ScreenshotUtil.DIRECTORY);
        File newFile = new File(imagePath, ScreenshotUtil.IMAGE_NAME);
        Uri contentUri = FileProvider.getUriForFile(context, ScreenshotUtil.AUTHORITY, newFile);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        if (contentUri != null)
        {
            // DOCUMENTATION:
            // http://stackoverflow.com/questions/9049143/android-share-intent-for-a-bitmap-is-it-possible-not-to-save-it-prior-sharing
            // Temp permission for receiving app to read this file.
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_TEXT, generateShareText());
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        }
        else
        {
            // Shares text instead because the image didn't work.
            intent.setType(TEXT_TYPE);
            intent.putExtra(Intent.EXTRA_TEXT, generateShareText());
        }

        startActivityForResult(intent, Constant.REQUEST_CODE_SHARE);

        progressDialog.dismiss();
    }

    /**
     * Grants awards to the user for completing the workout.
     */
    private void grantAwards()
    {
        // Grant the user the "Kept Swimming" badge if he or she didn't skip an exercise.
        if (!securePreferences.getBoolean(Constant.BUNDLE_EXERCISE_SKIPPED, false))
        {
            Util.grantBadgeToUser(email, Badge.KEPT_SWIMMING,
                                  new AwardDialogContent(R.mipmap.kept_swimming,
                                                         context.getString(R.string.award_kept_swimming_description)), true);
        }

        String finisherDescription = context.getString(R.string.award_finisher_description);
        AwardDialogContent finisherAward = new AwardDialogContent(R.mipmap.finisher, finisherDescription);
        notificationHandler = NotificationHandler.getInstance(null);
        if (notificationHandler.getAward(finisherAward) == null)
        {
            Util.grantPointsToUser(email, Constant.POINTS_COMPLETING_WORKOUT, context.getString(R.string.award_completed_workout_description));
            Util.grantBadgeToUser(email, Badge.FINISHER, finisherAward, true);
        }
    }

    /**
     * Adds the exercises to the exercise card.
     */
    private void addExercises()
    {
        addHeader(Card.EXERCISE, exerciseLayout);

        int totalExercises = exercises.size();
        int lastExercise = totalExercises - 1;

        // Remove the last exercise from the list if it is a stretch.
        if (exercises.get(lastExercise).getName().equals(stretchExerciseName))
        {
            exercises.remove(lastExercise);
            totalExercises--;
        }

        for (int i = 0; i < totalExercises; i++)
        {
            Exercise exercise = exercises.get(i);

            String exerciseName = exercise.getName();
            if(!exerciseName.equals(warmUphExerciseName))
            {
                View titleRow = inflater.inflate(R.layout.list_item_overview_exercise_title, exerciseLayout, false);
                TextView title = (TextView) titleRow.findViewById(R.id.text_view_title);
                LinearLayout setLayout = (LinearLayout) titleRow.findViewById(R.id.layout_set);
                setExerciseTitle(exerciseName, exercise.isIncludedInIntencity(), title);

                ArrayList<Set> sets = exercise.getSets();
                int setSize = sets.size();
                for (int z = 0; z < setSize; z++)
                {
                    Set set = sets.get(z);

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
                        String setNum = (z + 1) + ".";
                        setNumber.setText(setNum);

                        setLayout.addView(setRow);
                    }
                }

                if (i == totalExercises - 1)
                {
                    titleRow.findViewById(R.id.divider).setVisibility(View.GONE);
                }

                exerciseLayout.addView(titleRow);
            }
        }
    }

    /**
     * Adds the awards to the award card.
     */
    private void addAwards()
    {
        ArrayList<AwardDialogContent> awards = NotificationHandler.getInstance(null).getAwards();
        int totalAwards = awards.size();

        if (totalAwards > 0)
        {
            addHeader(Card.AWARD, awardLayout);

            for (int i = 0; i < totalAwards; i++)
            {
                View row = inflater.inflate(R.layout.list_item_award, awardLayout, false);

                LinearLayout layout = (LinearLayout) row.findViewById(R.id.layout);
                layout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

                new NotificationUtil(awards.get(i),
                                     (ImageView) row.findViewById(R.id.image_view_award),
                                     (LinearLayout) row.findViewById(R.id.layout_badge_amount),
                                     (TextView) row.findViewById(R.id.text_view_title),
                                     (TextView) row.findViewById(R.id.text_view_description),
                                     (TextView) row.findViewById(R.id.amount));

                if (i == totalAwards - 1)
                {
                    row.findViewById(R.id.divider).setVisibility(View.GONE);
                }

                awardLayout.addView(row);
            }

            awardLayout.setVisibility(View.VISIBLE);
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
        ImageView icon = (ImageView) header.findViewById(R.id.icon);
        TextView headerText = (TextView) header.findViewById(R.id.text_view_title);

        switch (type)
        {
            case EXERCISE:
                icon.setImageResource(R.mipmap.tab_icon_fitness_guru);
                headerText.setText(context.getString(R.string.title_exercises));
                break;
            case AWARD:
                icon.setImageResource(R.mipmap.badge);
                headerText.setText(context.getString(R.string.awards_title).toUpperCase());
                break;
            default:
                break;
        }

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
        new ShareTask(context, this).execute(layout);
    }

    /**
     * Cleans up the awards, and sets the result and goes back to the previous activity.
     */
    private void goBack()
    {
        // Remove all the awards just in case the user comes back to the app later.
        // This is so the user doesn't see awards he or she already earned before.
        notificationHandler.clearAwards();

        // Set the user has skipped an exercise to false for next time.
        Util.setExerciseSkipped(securePreferences, false);

        // Call finish() to go back to the previous screen.
        setResult(Constant.REQUEST_CODE_OVERVIEW);
        finish();
    }

    /**
     * Randomly generates a text to share.
     *
     * Text length needs to total 110 to work with images on twitter.
     *
     * @return  The generated text.
     */
    private String generateShareText()
    {
        // Each can only be 78 characters in length.
        String[] text = { "#Dominated my #workout! #fitness",
                          "Finished my #workout of the day!",
                          "Made it through #Intencity!",
                          "Making #gains with #Intencity!",
                          "#Exercising completed! #WOD",
                          "Share if you've #exercised today",
                          "#Lifted with #Intencity! #lift",
                          "#Intencity #trained me today!",
                          "Getting #strong with #Intencity!",
                          "Getting that #BeachBody! #fit",
                          "#Hurt so good! #FitnessPain"};

        // 18 characters.
        String url = " www.Intencity.fit";

        // 14 characters.
        String via = " @IntencityApp";

        int index = Util.getRandom(0, text.length - 1);

        return text[index] + url + via;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_CODE_SHARE)
        {
            // There will be no way we can know if they actually shared or not, so we will
            // Grant points to the user for at least opening up twitter and thinking about tweeting.
            Util.grantPointsToUser(email, Constant.POINTS_SHARING, context.getString(R.string.award_sharing_description));
        }
    }

    /**
     * The dialog listener for when the user finishes exercising.
     */
    private DialogListener dialogListener = new DialogListener()
    {
        @Override
        public void onButtonPressed(int which)
        {
            switch (which)
            {
                case Constant.POSITIVE_BUTTON:
                    goBack();
                    break;
                case Constant.NEGATIVE_BUTTON:
                    share();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Displays a progress dialog to the user.
     *
     * @param message   The message to display.
     */
    private void showProgressDialog(String message)
    {
        progressDialog = ProgressDialog.show(this, null, message, false);
    }
}