package com.intencity.intencity.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;

import com.intencity.intencity.R;
import com.intencity.intencity.view.activity.MainActivity;
import com.intencity.intencity.handler.NotificationHandler;
import com.intencity.intencity.helper.DbHelper;
import com.intencity.intencity.notification.AwardDialogContent;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ServiceTask;

import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A static class of Utils.
 *
 * Created by Nick Piscopio on 12/17/15.
 */
public class Util
{
    /**
     * Gets a rep format from an input.
     *
     * @param input     The value to be converted.
     *
     * @return  An integer of the formatted value.
     */
    public static String getRepFormat(String input)
    {
        // Make the input 0 if it doesn't have a value.
        input = (input == null ||
                 input.equals(Constant.RETURN_NULL)
                 || input.length() < 1) ? "0" : input;

        String formatted = input.replaceAll(":", "");
        return formatted.replaceFirst("^0+(?!$)", "");
    }
    /**
     * Generate a random number between two values.
     *
     * @param min   The minimum number.
     * @param max   The maximum number.
     *
     * @return  The random number.
     */
    public static int getRandom(int min, int max)
    {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return new Random().nextInt((max - min) + 1) + min;
    }

    /**
     * Check if text is in a valid format with the regular expression given.
     *
     * @param text  The text to validate.
     *
     * @return Boolean value if the emailEditText is valid.
     */
    public static boolean isFieldValid(String text, String regEx)
    {
        CharSequence input = text;

        Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }

    /**
     * Saves the login information for Intencity.
     *
     * @param email                 The email of the user.
     * @param accountType           The account type of the user.
     * @param trialCreatedDate      The long value of the date the trial account was created.
     */
    public static void loadIntencity(Activity activity, String email, String accountType, long trialCreatedDate)
    {
        Context context = activity.getApplicationContext();

        // Clear the database because when uninstalling it doesn't do that.
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        dbHelper.resetDb(database);

        // Set the email and account type in the SecurePreferences.
        SecurePreferences securePreferences = new SecurePreferences(context);
        SecurePreferences.Editor editor = securePreferences.edit();

        editor.putString(Constant.USER_ACCOUNT_EMAIL, Util.replacePlus(email));
        editor.putString(Constant.USER_ACCOUNT_TYPE, accountType);
        if (trialCreatedDate > 0)
        {
            editor.putLong(Constant.USER_TRIAL_CREATED_DATE, trialCreatedDate);
        }
        editor.apply();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
        activity.finish();
    }

    /**
     * Sets the progress bar color.
     *
     * @param context       The application context.
     * @param progressBar   The progress bar to change the color.
     */
    public static void setProgressBarColor(Context context, ProgressBar progressBar)
    {
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(context, R.color.primary),
                android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    /**
     * Shows a communication error message to the user.
     *
     * @param context   The application context.
     */
    public static void showCommunicationErrorMessage(Context context)
    {
        showMessage(context, context.getString(R.string.generic_error),
                    context.getString(R.string.intencity_communication_error_email));
    }

    /**
     * Displays the login error to the user.
     *
     * @param context   The application context.
     * @param title     The title the dialog should display.
     * @param message   The message the dialog should display.
     */
    public static void showMessage(Context context, String title, String message)
    {
        CustomDialogContent dialog = new CustomDialogContent(title, message, false);

        new CustomDialog(context, null, dialog, true);
    }

    /**
     * Calls the service to grant points to the user.
     *
     * @param email         The email of the user to grant points.
     * @param points        The amount of points that will be granted.
     * @param description   The description of why points are being granted.
     */
    public static void grantPointsToUser(String email, int points, String description)
    {
        new ServiceTask(null).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(
                                              Constant.STORED_PROCEDURE_GRANT_POINTS, email,
                                              String.valueOf(points)));

        // Add an award to the notification handler.
        NotificationHandler.getInstance(null).addAward(new AwardDialogContent("+" + points, description));
    }

    /**
     * Calls the service to grant a badge to the user.
     *
     * @param email         The email of the user to grant points.
     * @param badgeName     The name of the badge that is being awarded.
     * @param content       The content that will be displayed to the user.
     */
    private static void grantBadgeToUser(String email, String badgeName, AwardDialogContent content)
    {
        // We won't display the date anywhere, so we probably don't need this in local time.
        long now = new Date().getTime();

        new ServiceTask(null).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GRANT_BADGE,
                                                                                 email, String.valueOf(now), badgeName));
        // Add an award to the notification handler.
        NotificationHandler.getInstance(null).addAward(content);
    }

    /**
     * Calls the service to grant a badge to the user.
     *
     * @param email         The email of the user to grant points.
     * @param badgeName     The name of the badge that is being awarded.
     * @param content       The content that will be displayed to the user.
     * @param onlyAllowOne  Boolean value to only allow one instance of a specifed badge.
     */
    public static void grantBadgeToUser(String email, String badgeName, AwardDialogContent content, boolean onlyAllowOne)
    {
        NotificationHandler notificationHandler = NotificationHandler.getInstance(null);

        // Only grant the badge to the user if he or she doesn't have it
        if (onlyAllowOne)
        {
            if (!notificationHandler.hasAward(content))
            {
                grantBadgeToUser(email, badgeName, content);
            }
        }
        else
        {
            grantBadgeToUser(email, badgeName, content);
        }
    }

    /**
     * Checks if the user has entered text in the edit text.
     *
     * @param text      The edit text to check.
     * @param length    The value the edit text string should be greater than.
     *
     * @return Boolean value of if the user has entered text in the edit text.
     */
    public static boolean checkStringLength(String text, int length)
    {
        return text.length() >= length;
    }

    /**
     * Replaces the '+' character in a String of text.
     * This is so we can create an account on the server with an email that has a '+' in it.
     *
     * @param text  The text to search.
     *
     * @return  The new String with its replaced character.
     */
    public static String replacePlus(String text)
    {
        return text.replaceAll("\\+", "%2B");
    }
}