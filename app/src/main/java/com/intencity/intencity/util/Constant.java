package com.intencity.intencity.util;

/**
 * A class to hold the Constants for Intencity.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class Constant
{
    public static final String TAG = "Intencity";

    // Currently not used
    public static final String SHARED_PREFERENCES = "com.intencity.intencity.shared.preferences";
    // Stored in SecurePreferences
    public static final String USER_ACCOUNT_EMAIL = "com.intencity.intencity.user.email";
    public static final String USER_ACCOUNT_TYPE = "com.intencity.intencity.user.account.type";

    // Bundles
    public static final String BUNDLE_SET_NUMBER = "com.intencity.intencity.bundle.set.number";
    public static final String BUNDLE_EXERCISE = "com.intencity.intencity.bundle.exercise";
    public static final String BUNDLE_EXERCISE_LIST = "com.intencity.intencity.bundle.exercise.list";

    public static final String TAG_SET = "TAG_SET";

    // Service Endpoint
    private static final String ENDPOINT = "http://www.intencityapp.com/";
    //TODO: Change this when creating release build.
    private static final String SERVICE_FOLDER = ENDPOINT + "dev/services/mobile/";

    // Services
    public static final String SERVICE_VALIDATE_USER_CREDENTIALS = SERVICE_FOLDER + "user_credentials.php";
    public static final String SERVICE_TRIAL_ACCOUNT = SERVICE_FOLDER + "account.php";
    public static final String SERVICE_STORED_PROCEDURE = SERVICE_FOLDER + "stored_procedure.php";

    // Parameters
    private static final String PARAMETER_AMPERSAND = "&";
    private static final String PARAMETER_DELIMETER = ",";
    private static final String PARAMETER_EMAIL = "email=";
    private static final String PARAMETER_PASSWORD = "password=";
    private static final String PARAMETER_DATA = "d=";
    private static final String PARAMETER_VARIABLE = "v=";
    private static final String PARAMETER_FIRST_NAME = "first_name=";
    private static final String PARAMETER_LAST_NAME = "last_name=";
    private static final String PARAMETER_ACCOUNT_TYPE = "account_type=";

    // Stored Procedure Names
    public static final String STORED_PROCEDURE_GET_ALL_DISPLAY_MUSCLE_GROUPS = "getAllDisplayMuscleGroups";
    public static final String STORED_PROCEDURE_GET_EXERCISES_FOR_TODAY = "getExercisesForToday";
    public static final String STORED_PROCEDURE_SET_CURRENT_MUSCLE_GROUP = "setCurrentMuscleGroup";

    // Service JSON Tags
    public static final String JSON_ACCOUNT_TYPE = "AccountType";
    public static final String JSON_EMAIL = "Email";

    // Column Values
    public static final String ACCOUNT_TYPE_ADMIN = "A";
    public static final String ACCOUNT_TYPE_BETA = "B";
    public static final String ACCOUNT_TYPE_NORMAL = "N";
    public static final String ACCOUNT_TYPE_TRIAL = "T";

    // Column Names
    public static final String COLUMN_CURRENT_MUSCLE_GROUP = "currentMuscleGroup";
    public static final String COLUMN_DISPLAY_NAME = "DisplayName";
    public static final String COLUMN_EXERCISE_NAME = "ExerciseName";

    public static String getValidateUserCredentialsServiceParameters(String email, String password)
    {
        return PARAMETER_EMAIL + email + PARAMETER_AMPERSAND + PARAMETER_PASSWORD + password;
    }

    public static String getTrialAccountParameters(long date)
    {
        return PARAMETER_FIRST_NAME + "Anonymous" + PARAMETER_AMPERSAND +
               PARAMETER_LAST_NAME + "User" + PARAMETER_AMPERSAND +
               PARAMETER_EMAIL + "user" + date + "@intencityapp.com" + PARAMETER_AMPERSAND +
               PARAMETER_ACCOUNT_TYPE + ACCOUNT_TYPE_TRIAL;
    }

    public static String getStoredProcedure(String data, String... variables)
    {
        String storedProcedureParameters = PARAMETER_DATA + data + PARAMETER_AMPERSAND + PARAMETER_VARIABLE;

        int length = variables.length;

        for (int i = 0; i < length; i++)
        {
            storedProcedureParameters += ((i > 0) ? PARAMETER_DELIMETER : "") + variables[i];
        }

        return storedProcedureParameters;
    }
}
