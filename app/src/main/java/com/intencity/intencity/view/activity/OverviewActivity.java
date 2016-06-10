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
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.notification.ToastDialog;
import com.intencity.intencity.task.ShareTask;
import com.intencity.intencity.util.Badge;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.NotificationUtil;
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
        // Alternate date format EEE, MMM d, yyyy
        date.setText(new SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(new Date()));

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
                CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.title_finish_routine), context.getString(R.string.description_finish_routine), true);
                dialog.setPositiveButtonStringRes(R.string.title_finish);
                dialog.setNegativeButtonStringRes(android.R.string.cancel);

                new CustomDialog(OverviewActivity.this, dialogListener, dialog, true);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onImageProcessed(File file)
    {
        if (file != null)
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType(IMAGE_TYPE);

            startActivityForResult(intent, Constant.REQUEST_CODE_SHARE);
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
        new ShareTask(this).execute(layout);
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
        setResult(Constant.REQUEST_OVERVIEW);
        finish();
    }

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
            default:
                break;
        }
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