package com.intencity.intencity.util;

/**
 * A class to hold the Constants for Intencity.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class Constant
{
    public static final String TAG = "Intencity";

    public static final String SHARED_PREFERENCES = "com.intencity.intencity.shared.preferences";
    public static final String USER_ID = "com.intencity.intencity.user.id";
    public static final String USER_ACCOUNT_TYPE = "com.intencity.intencity.user.account.type";

    // Service Endpoint
    private static final String ENDPOINT = "http://www.intencityapp.com/";
    //TODO: Change this when creating release build.
    private static final String SERVICE_FOLDER = ENDPOINT + "dev/services/mobile/";

    // Services
    public static final String SERVICE_VALIDATE_USER_CREDENTIALS = SERVICE_FOLDER + "user_credentials.php";
    public static final String SERVICE_TRIAL_ACCOUNT = SERVICE_FOLDER + "account.php";

    // Parameters
    private static final String PARAMETER_AMPERSAND = "&";
    private static final String PARAMETER_EMAIL = "email=";
    private static final String PARAMETER_PASSWORD = "password=";

    private static final String PARAMETER_FIRST_NAME = "first_name=";
    private static final String PARAMETER_LAST_NAME = "last_name=";
    private static final String PARAMETER_ACCOUNT_TYPE = "account_type=";

    // Service JSON Tags
    public static final String JSON_ID = "ID";
    public static final String JSON_ACCOUNT_TYPE = "AccountType";

    public static final String ACCOUNT_TYPE_ADMIN = "A";
    public static final String ACCOUNT_TYPE_BETA = "B";
    public static final String ACCOUNT_TYPE_NORMAL = "N";
    public static final String ACCOUNT_TYPE_TRIAL = "T";

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
}
