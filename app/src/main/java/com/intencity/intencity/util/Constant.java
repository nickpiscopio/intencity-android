package com.intencity.intencity.util;

import com.intencity.intencity.BuildType;

import java.util.ArrayList;

/**
 * A class to hold the Constants for Intencity.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class Constant
{
    public static final String TAG = "Intencity";

    public static final int CODE_FAILED = -1;
    public static final int CODE_FAILED_REPOPULATE = -2;

    public static final int REQUIRED_PASSWORD_LENGTH = 8;

    public static final int LOGIN_POINTS_THRESHOLD = 43200000;
    public static final int EXERCISE_POINTS_THRESHOLD = 60000;

    public static final int POINTS_LOGIN = 5;
    public static final int POINTS_EXERCISE = 5;
    public static final int POINTS_COMPLETING_WORKOUT = 10;
    public static final int POINTS_SHARING = 5;
    public static final int POINTS_FOLLOWING = 5;
    public static final int POINTS_AWARD = 10;

    public static final String TRUE = "true";

    public static final String DURATION_0 = "00:00:00";
    public static final String RETURN_NULL = "null";

    public static final int NEGATIVE_BUTTON = 0;
    public static final int POSITIVE_BUTTON = 1;

    public static final String SUCCESS = "SUCCESS";
    public static final String EMAIL_EXISTS = "Email already exists";
    public static final String INVALID_PASSWORD = "Invalid password";
    public static final String ACCOUNT_CREATED = "Account created";

    public static final String SHARED_PREFERENCES = "com.intencity.intencity.shared.preferences";
    // Stored in SecurePreferences
    public static final String USER_ACCOUNT_EMAIL = "com.intencity.intencity.user.email";
    public static final String USER_ACCOUNT_TYPE = "com.intencity.intencity.user.account.type";
    public static final String USER_LAST_LOGIN = "com.intencity.intencity.user.last.login";
    public static final String USER_LAST_EXERCISE_TIME = "com.intencity.intencity.user.last.exercise.time";
    //Tutorial SharedPreferences
    public static final String TUTORIAL_SWIPE_TO_DISMISS = "com.intencity.intencity.tutorial.swipe.to.dismiss";

    // Request codes
    public static final int REQUEST_CODE_STAT = 10;
    public static final int REQUEST_CODE_SEARCH = 20;
    public static final int REQUEST_CODE_TWEET = 30;
    public static final int REQUEST_CODE_LOG_OUT = 40;

    // Fragment IDs
    public static final int ID_FRAGMENT_EXERCISE_LIST = 1;

    // Extras
    public static final String EXTRA_DEMO_PAGE = "com.intencity.intencity.extra.demo.page";

    // Bundles
    public static final String BUNDLE_SEARCH_EXERCISES = "com.intencity.intencity.bundle.search.exercises";
    public static final String BUNDLE_SET_NUMBER = "com.intencity.intencity.bundle.set.number";
    public static final String BUNDLE_DISPLAY_MUSCLE_GROUPS = "com.intencity.intencity.bundle.display.muscle.groups";
    public static final String BUNDLE_ROUTINE_NAME = "com.intencity.intencity.bundle.routine.name";
    public static final String BUNDLE_EXERCISE_LIST = "com.intencity.intencity.bundle.exercise.list";
    public static final String BUNDLE_EXERCISE = "com.intencity.intencity.bundle.exercise";
    public static final String BUNDLE_EXERCISE_POSITION = "com.intencity.intencity.bundle.exercise.position";
    public static final String BUNDLE_EXERCISE_SETS = "com.intencity.intencity.bundle.exercise.sets";
    public static final String BUNDLE_EXERCISE_LIST_INDEX = "com.intencity.intencity.bundle.exercise.list.index";
    public static final String BUNDLE_RECOMMENDED_ROUTINE = "com.intencity.intencity.bundle.recommended.routine";
    public static final String BUNDLE_EXERCISE_NAME = "com.intencity.intencity.bundle.exercise.name";
    public static final String BUNDLE_ID = "com.intencity.intencity.bundle.id";
    public static final String BUNDLE_EXERCISE_TYPE = "com.intencity.intencity.exercise.type";
    public static final String BUNDLE_EXERCISE_SKIPPED = "com.intencity.intencity.exercise.skipped";

    // Service Endpoint
    private static final String ENDPOINT = "http://www.intencityapp.com/";
    private static final String SERVICE_FOLDER = ENDPOINT + ((BuildType.type == Build.Type.DEBUG) ? "dev/" : "") + "services/";
    private static final String SERVICE_FOLDER_MOBILE = SERVICE_FOLDER + "mobile/";

    // Services
    public static final String SERVICE_VALIDATE_USER_CREDENTIALS = SERVICE_FOLDER_MOBILE + "user_credentials.php";
    public static final String SERVICE_CREATE_ACCOUNT = SERVICE_FOLDER_MOBILE + "account.php";
    public static final String SERVICE_STORED_PROCEDURE = SERVICE_FOLDER_MOBILE + "stored_procedure.php";
    public static final String SERVICE_COMPLEX_INSERT = SERVICE_FOLDER_MOBILE + "complex_insert.php";
    public static final String SERVICE_COMPLEX_UPDATE = SERVICE_FOLDER_MOBILE + "complex_update.php";
    public static final String SERVICE_UPDATE_EQUIPMENT = SERVICE_FOLDER_MOBILE + "update_equipment.php";
    public static final String SERVICE_UPDATE_EXCLUSION = SERVICE_FOLDER_MOBILE + "update_exclusion.php";
    public static final String SERVICE_CHANGE_PASSWORD = SERVICE_FOLDER_MOBILE + "change_password.php";
    public static final String SERVICE_FORGOT_PASSWORD = SERVICE_FOLDER + "forgot_password.php";

    // Parameters
    public static final String LIKE_OPERATOR = "%";
    public static final String PARAMETER_AMPERSAND = "&";
    public static final String PARAMETER_DELIMITER = ",";
    // This does not have "=" because it usually has a number followed by it.
    // i.e. &table0
    public static final String PARAMETER_TABLE = "table";
    public static final String PARAMETER_EMAIL = "email=";
    private static final String PARAMETER_PASSWORD = "password=";
    private static final String PARAMETER_CURRENT_PASSWORD = "oldPassword=";
    private static final String PARAMETER_DATA = "d=";
    private static final String PARAMETER_VARIABLE = "v=";
    private static final String PARAMETER_FIRST_NAME = "first_name=";
    private static final String PARAMETER_LAST_NAME = "last_name=";
    private static final String PARAMETER_ACCOUNT_TYPE = "account_type=";
    private static final String PARAMETER_INSERTS = "inserts=";

    // Stored Procedure Names
    public static final String STORED_PROCEDURE_GET_ALL_DISPLAY_MUSCLE_GROUPS = "getAllDisplayMuscleGroups";
    public static final String STORED_PROCEDURE_GET_EXERCISES_FOR_TODAY = "getExercisesForToday";
    public static final String STORED_PROCEDURE_SET_CURRENT_MUSCLE_GROUP = "setCurrentMuscleGroup";
    public static final String STORED_PROCEDURE_GET_FOLLOWING = "getFollowing";
    public static final String STORED_PROCEDURE_REMOVE_FROM_FOLLOWING = "removeFromFollowing";
    public static final String STORED_PROCEDURE_SEARCH_EXERCISES = "searchExercises";
    public static final String STORED_PROCEDURE_SEARCH_USERS = "searchUsers";
    public static final String STORED_PROCEDURE_FOLLOW_USER = "followUser";
    public static final String STORED_PROCEDURE_GET_EXERCISE_DIRECTION = "getDirection";
    public static final String STORED_PROCEDURE_GET_EQUIPMENT = "getEquipment";
    public static final String STORED_PROCEDURE_GET_EXCLUSION = "getExclusionList";
    public static final String STORED_PROCEDURE_GRANT_POINTS = "grantPointsToUser";
    public static final String STORED_PROCEDURE_GRANT_BADGE = "grantBadgeToUser";
    public static final String STORED_PROCEDURE_GET_INJURY_PREVENTION_WORKOUTS = "getInjuryPreventionWorkouts";
    public static final String STORED_PROCEDURE_EXCLUDE_EXERCISE = "excludeExercise";

    // Column Values
    public static final String ACCOUNT_TYPE_ADMIN = "A";
    public static final String ACCOUNT_TYPE_BETA = "B";
    public static final String ACCOUNT_TYPE_NORMAL = "N";
    public static final String ACCOUNT_TYPE_TRIAL = "T";

    // Exercise Type Values
    public static final String EXERCISE_TYPE_WARM_UP = "W";
    public static final String EXERCISE_TYPE_STRETCH = "S";
    public static final String EXERCISE_TYPE_EXERCISE = "E";

    // Routine Names
    // We need these because the cardio routine doesn't have stretches or warm-ups,
    // so we replace them with legs and lower back.
    public static final String ROUTINE_CARDIO = "Cardio";
    public static final String ROUTINE_LEGS_AND_LOWER_BACK = "Legs & Lower Back";

    // Table Names
    public static final String TABLE_COMPLETED_EXERCISE = "CompletedExercise";

    // Column Names
    // The current muscle group a user is recommended to do.
    public static final String COLUMN_CURRENT_MUSCLE_GROUP = "currentMuscleGroup";
    public static final String COLUMN_DISPLAY_NAME = "DisplayName";
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_TIME = "Time";
    public static final String COLUMN_EXERCISE_NAME = "ExerciseName";
    public static final String COLUMN_EXERCISE_WEIGHT = "ExerciseWeight";
    public static final String COLUMN_EXERCISE_REPS = "ExerciseReps";
    public static final String COLUMN_EXERCISE_DURATION = "ExerciseDuration";
    public static final String COLUMN_EXERCISE_DIFFICULTY = "ExerciseDifficulty";
    public static final String COLUMN_NOTES = "Notes";
    public static final String COLUMN_ACCOUNT_TYPE = "AccountType";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_FIRST_NAME = "FirstName";
    public static final String COLUMN_LAST_NAME = "LastName";
    public static final String COLUMN_EARNED_POINTS = "EarnedPoints";
    public static final String COLUMN_TOTAL_BADGES = "TotalBadges";
    public static final String COLUMN_PROFILE_PICTURE_URL = "Url";
    // The id from the following table if a user is following that user from a search.
    public static final String COLUMN_FOLLOWING_ID = "FollowingId";
    public static final String COLUMN_SUBMITTED_BY = "SubmittedBy";
    public static final String COLUMN_VIDEO_URL = "VideoURL";
    public static final String COLUMN_DIRECTION = "Direction";
    public static final String COLUMN_EQUIPMENT_NAME = "EquipmentName";
    public static final String COLUMN_EXCLUSION_NAME = "ExclusionName";
    public static final String COLUMN_HAS_EQUIPMENT = "HasEquipment";

    public static final String SPACE_REGEX = "\\s";
    public static final String REGEX_EMAIL = "[a-zA-Z0-9]+([\\-\\.\\{\\}\\^\\+*_~]*[a-zA-Z0-9]+)*@[a-zA-Z0-9]+([\\.\\-]*[a-zA-Z0-9]+)*[\\.][a-zA-Z]{2}[A-Za-z]*";
    public static final String REGEX_FIELD = "[a-zA-Z0-9]+[\\-\\.\\{\\}\\^\\*\\(\\)\\[\\]\\$/;:,*_~!@#%]*";

    public static String getValidateUserCredentialsServiceParameters(String email, String password)
    {
        return PARAMETER_EMAIL + email + PARAMETER_AMPERSAND + PARAMETER_PASSWORD + password;
    }

    /**
     * Generates the forgot password parameter to send to the server.
     *
     * @param email     The user's email to add to the url.
     *
     * @return  The generated url parameter.
     */
    public static String getForgotPasswordParameter(String email)
    {
        return PARAMETER_EMAIL + email;
    }

    /**
     * Generates the create account parameters.
     *
     * @param firstName     The first name of the user.
     * @param lastName      The last name of the user.
     * @param email         The user's email.
     * @param password      The password of the user.
     * @param accountType   The account type of the user.
     *
     * @return  The parameters for creating an account.
     */
    public static String getAccountParameters(String firstName, String lastName, String email, String password, String accountType)
    {
        return PARAMETER_FIRST_NAME + firstName + PARAMETER_AMPERSAND +
               PARAMETER_LAST_NAME + lastName + PARAMETER_AMPERSAND +
               PARAMETER_EMAIL + email + PARAMETER_AMPERSAND +
               PARAMETER_PASSWORD + password + PARAMETER_AMPERSAND +
               PARAMETER_ACCOUNT_TYPE + accountType;
    }

    /**
     * Generates the stored procedure parameters.
     *
     * @param name          The name of the stored procedure to call.
     * @param variables     The variable to send into the stored procedure.
     *
     * @return  The stored procedure method call with the parameters included.
     */
    public static String generateStoredProcedureParameters(String name, String... variables)
    {
        String storedProcedureParameters = PARAMETER_DATA + name + PARAMETER_AMPERSAND + PARAMETER_VARIABLE;

        int length = variables.length;

        for (int i = 0; i < length; i++)
        {
            storedProcedureParameters += ((i > 0) ? PARAMETER_DELIMITER : "") + variables[i];
        }

        return storedProcedureParameters;
    }

    /**
     * Generates the URL string to update the user's equipment list or the user's exclusion list.
     *
     * @param email         The user's email.
     * @param variables     The list items to update.
     *
     * @return  The generated URL string.
     */
    public static String generateListVariables(String email, ArrayList<String> variables)
    {
        String parameters = PARAMETER_EMAIL + email;

        int length = variables.size();
        for (int i = 0; i < length; i++)
        {
            if (i == 0)
            {
                parameters += PARAMETER_AMPERSAND + PARAMETER_INSERTS;
            }

            parameters += ((i > 0) ? PARAMETER_DELIMITER : "") + variables.get(i);
        }

        return parameters;
    }

    /**
     * Generates the change password URL string.
     *
     * @param email             The user's email to change the password.
     * @param currentPassword   The user's current password.
     * @param newPassword       The user's new password.
     *
     * @return  The change password URL string.
     */
    public static String generateChangePasswordVariables(String email, String currentPassword, String newPassword)
    {
        return PARAMETER_EMAIL + email +
               PARAMETER_AMPERSAND + PARAMETER_CURRENT_PASSWORD + currentPassword +
               PARAMETER_AMPERSAND + PARAMETER_PASSWORD + newPassword;
    }
}
