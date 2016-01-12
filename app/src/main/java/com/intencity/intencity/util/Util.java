package com.intencity.intencity.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.intencity.intencity.activity.MainActivity;

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
     * @param email         The email of the user.
     * @param accountType   The account type of the user.
     */
    public static void loadIntencity(Activity activity, String email, String accountType)
    {
        Context context = activity.getApplicationContext();
        SecurePreferences securePreferences = new SecurePreferences(context);
        SecurePreferences.Editor editor = securePreferences.edit();
        editor.putString(Constant.USER_ACCOUNT_EMAIL, email);
        editor.putString(Constant.USER_ACCOUNT_TYPE, accountType);
        editor.apply();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
        activity.finish();
    }
}
