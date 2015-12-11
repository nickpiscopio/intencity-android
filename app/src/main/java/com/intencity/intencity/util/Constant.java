package com.intencity.intencity.util;

/**
 * Created by nickpiscopio on 12/9/15.
 */
public class Constant
{
    public static final String TAG = "Intencity";

    public static final String SHARED_PREFERENCES = "com.intencity.intencity.shared.preferences";
    public static final String DEMO_FINISHED = "com.intencity.intencity.demo.finished";

    // Service Endpoint
    private static final String ENDPOINT = "http://www.intencityapp.com/";
    private static final String SERVICE_FOLDER = ENDPOINT + "services/";

    // Services
    public static final String SERVICE_VALIDATE_USER_CREDENTIALS = SERVICE_FOLDER + "user_credentials.php";

    // Parameters
    private static final String PARAMETER_AMPERSAND = "&";
    private static final String PARAMETER_EMAIL = "email=";
    private static final String PARAMETER_PASSWORD = "password=";

    // Service Responses
    public static final String RESPONSE_DELIMITER = ",";
    public static final String LOG_IN_VALID = "Valid credentials";

    public static String getValidateUserCredentialsServiceParameters(String email, String password)
    {
        return PARAMETER_EMAIL + email + PARAMETER_AMPERSAND + PARAMETER_PASSWORD + password;
    }
}
