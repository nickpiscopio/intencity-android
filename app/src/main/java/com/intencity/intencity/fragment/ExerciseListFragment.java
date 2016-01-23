package com.intencity.intencity.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.Direction;
import com.intencity.intencity.activity.ExerciseSearchActivity;
import com.intencity.intencity.activity.StatActivity;
import com.intencity.intencity.adapter.ExerciseAdapter;
import com.intencity.intencity.dialog.CustomDialog;
import com.intencity.intencity.dialog.DialogContent;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ExerciseListListener;
import com.intencity.intencity.listener.ExerciseListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.task.SetExerciseTask;
import com.intencity.intencity.util.Badge;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;
import com.intencity.intencity.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * The Exercise List Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class ExerciseListFragment extends android.support.v4.app.Fragment implements DialogListener,
                                                                                     ExerciseListener
{
    private int TOTAL_EXERCISE_NUM = 7;

    private int autoFillTo;

    private ArrayList<Exercise> allExercises;
    private ArrayList<Exercise> currentExercises;

    private TextView routineProgress;
    private TextView routine;
    private Button nextExercise;

    private ExerciseAdapter adapter;

    private Context context;

    private String routineName;

    private int completedExerciseNum = 0;

    private int position;

    private String email;

    private boolean workoutFinished;

    private ExerciseListListener fitnessLogListener;

    private  SecurePreferences securePreferences;

    private String warmUphExerciseName;
    private String stretchExerciseName;
    private String finishWorkoutString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);

        nextExercise = (Button) view.findViewById(R.id.button_next);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        routineProgress = (TextView) view.findViewById(R.id.text_view_routine_progress);
        routine = (TextView) view.findViewById(R.id.text_view_routine);

        nextExercise.setOnClickListener(nextExerciseClickListener);

        Bundle bundle = getArguments();

        routineName = bundle.getString(Constant.BUNDLE_ROUTINE_NAME);

        allExercises = bundle.getParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST);

        warmUphExerciseName = getString(R.string.warm_up);
        stretchExerciseName = getString(R.string.stretch);
        finishWorkoutString = getString(R.string.finish_workout);

                updateTotalExercises();

        autoFillTo = bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX) > 0 ?
                        bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX) : 1;
        currentExercises = new ArrayList<>();

        for (int i = 0; i < autoFillTo; i++)
        {
            currentExercises.add(allExercises.get(i));

            completedExerciseNum++;
        }

        notifyFitnessLogOfNewExercise();

        updateRoutineName(completedExerciseNum);

        checkNextButtonEnablement(true);

        context = getContext();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(
                new HeaderDecoration(context, recyclerView, R.layout.recycler_view_header));

        adapter = new ExerciseAdapter(context, currentExercises, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        securePreferences = new SecurePreferences(context);
        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        workoutFinished = false;

        return view;
    }

    /**
     * Updates the total number of exercises the user can do.
     */
    private void updateTotalExercises()
    {
        // Change the total number of exercises if the exercise list doesn't have enough exercises.
        if (allExercises != null && allExercises.size() < TOTAL_EXERCISE_NUM)
        {
            TOTAL_EXERCISE_NUM = allExercises.size();
        }
    }
    /**
     * The swipe listener for the recycler view.
     */
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT)
    {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target)
        {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir)
        {
            position = viewHolder.getAdapterPosition();

            String[] buttonText = new String[]{context.getString(R.string.hide_for_now),
                                               context.getString(android.R.string.cancel)/*,
                                               context.getString(R.string.hide_forever)*/};

            new CustomDialog(context, ExerciseListFragment.this, new DialogContent("", buttonText));
        }
    };

    /**
     * The next exercise click listener.
     */
    private View.OnClickListener nextExerciseClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (nextExercise.getText().toString().equals(finishWorkoutString))
            {
                workoutFinished = true;

                DialogContent dialog = new DialogContent(context.getResources().getString(R.string.badge_earned_title), context.getResources().getString(R.string.badged_earned_finisher), true);
                dialog.setImgRes(R.mipmap.finisher);
                dialog.setPositiveButtonStringRes(R.string.tweet_button);
                dialog.setNegativeButtonStringRes(R.string.finish_button);

                new CustomDialog(context, dialogListener, dialog);
            }
            else
            {
                addExercise(false);

                checkNextButtonEnablement(false);
            }
        }
    };

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
                    // The user selected to tweet, so we open the twitter URI.
                    Uri uri = Uri.parse(generateTweet());
                    startActivityForResult(new Intent(Intent.ACTION_VIEW, uri), Constant.REQUEST_CODE_TWEET);
                    break;
                default:
                    break;
            }

            Util.grantPointsToUser(context, email, Constant.POINTS_COMPLETING_WORKOUT, context.getString(R.string.award_completed_workout_description));
            Util.grantBadgeToUser(email, Badge.FINISHER);

            // Start the fitness log over again.
            fitnessLogListener.onCompletedWorkout();
        }
    };

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
        // Remove all the whitespace so the hashtags are proper.
        // Replace the ampersand with its equivalent url code and add a hashtag.
        String routine = routineName.replaceAll(" ", "").replace(Constant.PARAMETER_AMPERSAND, " %26 %23");

        String twitterUrl = "https://twitter.com/intent/tweet?text=";
        String[] tweetText = { "I %23dominated my %23workout with %23Intencity! %23WOD %23Fitness",
                               "I %23finished my %23workout of the day with %23Intencity! %23WOD %23Fitness",
                               "I made it through %23Intencity%27s %23routine! %23Fitness",
                               "I %23completed %23" + routine + " with %23Intencity! %23WOD %23Fitness",
                               "%23Finished my %23Intencity %23workout! %23Retweet if you've %23exercised today. %23WOD %23Fitness"};
        String tweetUrl = "&url=www.Intencity.fit";
        String via = "&via=IntencityApp";

        int tweet = Util.getRandom(0, tweetText.length - 1);

        return twitterUrl + tweetText[tweet] + tweetUrl +via;
    }

    /**
     * Gets the next exercise from the list.
     */
    private void addExercise(boolean addingExerciseFromSearch)
    {
        // If there is 1 exercise left, we want to display the stretch.
        // We remove all the unnecessary exercises.
        if (!addingExerciseFromSearch && TOTAL_EXERCISE_NUM - completedExerciseNum <= 1)
        {
            Exercise stretch = allExercises.get(allExercises.size() - 1);

            allExercises.clear();
            allExercises.addAll(currentExercises);
            allExercises.add(stretch);
        }

        long lastExerciseTime = securePreferences.getLong(Constant.USER_LAST_EXERCISE_TIME, 0);
        long now = new Date().getTime();

        if ((now - lastExerciseTime) >= Constant.EXERCISE_POINTS_THRESHOLD)
        {
            SecurePreferences.Editor editor = securePreferences.edit();
            editor.putLong(Constant.USER_LAST_EXERCISE_TIME, now);
            editor.apply();

            Util.grantPointsToUser(context, email, Constant.POINTS_EXERCISE, context.getString(R.string.award_exercise_description));
        }

        completedExerciseNum++;
        updateRoutineName(completedExerciseNum);

        currentExercises.add(allExercises.get(autoFillTo++));

        notifyFitnessLogOfNewExercise();

        adapter.notifyDataSetChanged();
        // TODO: set the recycler view to scroll to the bottom when a new view is added.
        //        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    /**
     * Sets the next button to be gone if we don't have anymore exercises left.
     */
    private void checkNextButtonEnablement(boolean loadedFromStart)
    {
        if (nextExercise.getText().toString().equals(stretchExerciseName) ||
            (loadedFromStart && completedExerciseNum >= TOTAL_EXERCISE_NUM))
        {
            nextExercise.setText(finishWorkoutString);
        }
        else if (currentExercises.size() >= allExercises.size() - 1 || completedExerciseNum >= TOTAL_EXERCISE_NUM - 1)
        {
            nextExercise.setText(stretchExerciseName);
        }
    }

    /**
     * Notifies the fitness log of the new exercise so we can update the search to not include the add button.
     */
    private void notifyFitnessLogOfNewExercise()
    {
        fitnessLogListener.onNextExercise(currentExercises);
    }

    /**
     * Updates the routine name.
     *
     * @param completedExerciseNum  The number of completed exercises.
     */
    private void updateRoutineName(int completedExerciseNum)
    {
        routineProgress.setText(completedExerciseNum + "/" + TOTAL_EXERCISE_NUM);
        routine.setText(routineName.toUpperCase());
    }

    /**
     * Adds an exercise to the list of exercises the user has completed
     * as well as the total exercises from the web server.
     *
     * @param exercise  The exercise to add.
     */
    public void addExerciseToList(Exercise exercise)
    {
        allExercises.add(autoFillTo, exercise);
        addExercise(true);

        String nextExerciseText = nextExercise.getText().toString();

        if (!nextExerciseText.equals(stretchExerciseName) &&
            !nextExerciseText.equals(finishWorkoutString))
        {
            checkNextButtonEnablement(false);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if (workoutFinished)
        {
            // Remove exercises from database since we are finished with the workout.
            new SetExerciseTask(context).execute();
        }
        else
        {
            // Save the exercises to the database in case the user wants
            // to continue with this routine later.
            new SetExerciseTask(context, routineName, allExercises, currentExercises.size()).execute();
        }
    }

    @Override
    public void onButtonPressed(int which)
    {
        switch (which)
        {
            case 0: // Hide was clicked.
                Exercise exerciseToRemove = currentExercises.get(position);
                String exerciseName = exerciseToRemove.getName();

                currentExercises.remove(exerciseToRemove);

                // Only remove the exercise from allExercises if it is not a stretch.
                if(!exerciseName.equalsIgnoreCase(stretchExerciseName))
                {
                    allExercises.remove(exerciseToRemove);
                }

                autoFillTo--;
                completedExerciseNum--;

                // Remove 1 from the last position so we can se the animation
                // for the next exercise that will be added.
                adapter.setLastPosition(adapter.getLastPosition() - 1);

                int currentExerciseSize = currentExercises.size();
                if (exerciseName.equalsIgnoreCase(stretchExerciseName))
                {
                    updateTotalExercises();

                    updateRoutineName(completedExerciseNum);

                    checkNextButtonEnablement(false);

                    adapter.notifyDataSetChanged();

                }
                else if (position == currentExerciseSize)
                {
                    updateTotalExercises();

                    if (currentExerciseSize != allExercises.size())
                    {
                        addExercise(false);
                    }
                    else
                    {
                        updateRoutineName(completedExerciseNum);
                    }

                    checkNextButtonEnablement(false);
                }
                else
                {
                    adapter.notifyDataSetChanged();
                }
                break;
            case 1: // Cancel was clicked.
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onExerciseClicked(int position)
    {
        String exerciseName = allExercises.get(position).getName();
        if (exerciseName.equalsIgnoreCase(warmUphExerciseName))
        {
            startActivity(context, Constant.EXERCISE_TYPE_WARM_UP, routineName);
        }
        else if (exerciseName.equalsIgnoreCase(stretchExerciseName))
        {
            startActivity(context, Constant.EXERCISE_TYPE_STRETCH, routineName);
        }
        else
        {
            Intent intent = new Intent(context, Direction.class);
            intent.putExtra(Constant.BUNDLE_EXERCISE_NAME, currentExercises.get(position).getName());
            startActivity(intent);
        }
    }

    /**
     * Starts the ExerciseSearchActivity.
     *
     * @param context       The application context.
     * @param type          The exercise type.
     * @param routineName   The routine name.
     */
    private void startActivity(Context context, String type, String routineName)
    {
        Intent intent = new Intent(context, ExerciseSearchActivity.class);
        intent.putExtra(Constant.BUNDLE_EXERCISE_TYPE, type);
        intent.putExtra(Constant.BUNDLE_ROUTINE_NAME, routineName);
        startActivity(intent);
    }

    @Override
    public void onStatClicked(int position)
    {
        Intent intent = new Intent(context, StatActivity.class);
        intent.putExtra(Constant.BUNDLE_EXERCISE_POSITION, position);
        intent.putExtra(Constant.BUNDLE_EXERCISE, currentExercises.get(position));
        startActivityForResult(intent, Constant.REQUEST_CODE_STAT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result codes are set from an activity with setResult().
        if(resultCode == Constant.REQUEST_CODE_STAT)
        {
            Bundle extras = data.getExtras();
            int position = extras.getInt(Constant.BUNDLE_EXERCISE_POSITION);
            final ArrayList<Set> sets = extras.getParcelableArrayList(Constant.BUNDLE_EXERCISE_SETS);

            Exercise currentExercise = currentExercises.get(position);
            currentExercise.setSets(sets);
            String exerciseName = currentExercise.getName();
            allExercises.get(position).setSets(sets);

            adapter.notifyDataSetChanged();

            int setSize = sets.size();
            String statements = "statements=";
            String updateString = statements + setSize + Constant.PARAMETER_AMPERSAND + Constant.PARAMETER_EMAIL + email;
            String insertString = statements + setSize + Constant.PARAMETER_AMPERSAND + Constant.PARAMETER_EMAIL + email;

            // The update and insert Strings already starts out with a value in it,
            // so we need this boolean to decide if we are going to conduct these actions.
            boolean conductUpdate = false;
            boolean conductInsert = false;

            // we need our own indexes here or the services won't work properly.
            int updateIndex = 0;
            int insertIndex = 0;

            // Sort through the sets to determine if we are updating or inserting.
            for (int i = 0; i < setSize; i++)
            {
                Set set = sets.get(i);
                if (set.getWebId() > 0)
                {
                    // Call update
                    updateString += generateComplexUpdateParameters(updateIndex, set);
                    conductUpdate = true;
                    updateIndex++;
                }
                else
                {
                    // Concatenate the insert parameter String.
                    insertString += generateComplexInsertParameters(insertIndex, exerciseName, set);
                    conductInsert = true;
                    insertIndex++;
                }
            }

            if (conductUpdate)
            {
                // Update the exercise on the web.
                new ServiceTask(new ServiceListener()
                {
                    @Override
                    public void onRetrievalSuccessful(String response){ }

                    @Override
                    public void onRetrievalFailed() { }
                }).execute(Constant.SERVICE_COMPLEX_UPDATE, updateString);
            }

            if (conductInsert)
            {
                // Save the exercise to the web.
                // The service listener was placed in here so we can update the sets of the exercise
                // with the auto incremented ids from the web.
                new ServiceTask(new ServiceListener()
                {
                    @Override
                    public void onRetrievalSuccessful(String response)
                    {
                        // Remove the first and last character because they are "[" and "]";
                        String responseArr = response.substring(1, response.length() - 1);
                        String[] ids =  responseArr.split(Constant.PARAMETER_DELIMITER);

                        for (Set set : sets)
                        {
                            if (set.getWebId() > 0)
                            {
                                continue;
                            }

                            // Set the web id to the first id in the list.
                            set.setWebId(Integer.valueOf(ids[0]));

                            // Create a new array withe the use id removed.
                            ids = Arrays.copyOfRange(ids, 1, ids.length);
                        }
                    }

                    @Override
                    public void onRetrievalFailed() { }
                }).execute(Constant.SERVICE_COMPLEX_INSERT, insertString);
            }
        }
        // Request codes are from the original startActivityForResult().
        else if (requestCode == Constant.REQUEST_CODE_TWEET)
        {
            // There will be no way we can know if they actually tweeted or not, so we will
            // Grant points to the user for at least opening up twitter and thinking about tweeting.
            Util.grantPointsToUser(context, email, Constant.POINTS_SHARING, context.getString(R.string.award_sharing_description));
        }
    }

    /**
     * Constructs a snippet of the parameter query sent to the service for updating.
     *
     * @param index     The index of the sets we are updating.
     * @param set       The set we are updating.
     *
     * @return  The constructed snippet of the complex update parameter String.
     */
    private String generateComplexUpdateParameters(int index, Set set)
    {
        String equals = "=";
        String setParam = "set";
        String whereParam = "where";
        String nullString = "NULL";

        int weight = set.getWeight();
        String duration = set.getDuration();
        String notes = set.getNotes();
        boolean hasWeight = weight > 0;
        boolean isDuration = duration != null && duration.contains(":");

        String weightParam = hasWeight ? Constant.COLUMN_EXERCISE_WEIGHT + equals + weight + Constant.PARAMETER_DELIMITER : "";
        // Choose whether we are inserting reps or duration.
        String durationParam = isDuration ?
                Constant.COLUMN_EXERCISE_DURATION + equals + "'" + duration + "'" + Constant.PARAMETER_DELIMITER
                + Constant.COLUMN_EXERCISE_REPS + equals + nullString + Constant.PARAMETER_DELIMITER :
                Constant.COLUMN_EXERCISE_DURATION + equals + nullString + Constant.PARAMETER_DELIMITER
                + Constant.COLUMN_EXERCISE_REPS + equals + set.getReps() + Constant.PARAMETER_DELIMITER;

        String notesParam = notes != null && notes.length() > 0 ? "'" + notes + "'" : nullString;

        return getParameterTitle(Constant.PARAMETER_TABLE, index) + Constant.TABLE_COMPLETED_EXERCISE
               + getParameterTitle(setParam, index)
                    + weightParam
                    + durationParam
                    + Constant.COLUMN_EXERCISE_DIFFICULTY + equals + set.getDifficulty() + Constant.PARAMETER_DELIMITER
                    + Constant.COLUMN_NOTES + equals + notesParam
               + getParameterTitle(whereParam, index)
                    + Constant.COLUMN_ID + equals + set.getWebId();
    }
    /**
     * Constructs a snippet of the parameter query sent to the service to insert.
     *
     * @param index     The index of the sets we are inserting.
     * @param name      The name of the exercise we are inserting.
     * @param set       The set we are inserting.
     *
     * @return  The constructed snippet of the complex insert parameter String.
     */
    private String generateComplexInsertParameters(int index, String name, Set set)
    {
        String columns = "columns";
        String inserts = "inserts";

        String curDate = "CURDATE()";
        String now = "NOW()";

        int weight = set.getWeight();
        String duration = set.getDuration();
        boolean hasWeight = weight > 0;
        boolean isDuration = duration != null && duration.contains(":");

        String weightParam = hasWeight ? Constant.COLUMN_EXERCISE_WEIGHT + Constant.PARAMETER_DELIMITER : "";
        String weightValue = hasWeight ? weight + Constant.PARAMETER_DELIMITER : "";
        // Choose whether we are inserting reps or duration.
        String durationParam = isDuration ? Constant.COLUMN_EXERCISE_DURATION + Constant.PARAMETER_DELIMITER :
                                            Constant.COLUMN_EXERCISE_REPS + Constant.PARAMETER_DELIMITER;
        String durationValue = isDuration ? duration : String.valueOf(set.getReps());

        return getParameterTitle(Constant.PARAMETER_TABLE, index)
                + Constant.TABLE_COMPLETED_EXERCISE
                + getParameterTitle(columns, index) + Constant.COLUMN_DATE + Constant.PARAMETER_DELIMITER
                                                    + Constant.COLUMN_TIME + Constant.PARAMETER_DELIMITER
                                                    + Constant.COLUMN_EXERCISE_NAME + Constant.PARAMETER_DELIMITER
                                                    + weightParam
                                                    + durationParam
                                                    + Constant.COLUMN_EXERCISE_DIFFICULTY + Constant.PARAMETER_DELIMITER
                                                    + Constant.COLUMN_NOTES
                + getParameterTitle(inserts, index) + curDate + Constant.PARAMETER_DELIMITER
                                                    + now + Constant.PARAMETER_DELIMITER
                                                    + name + Constant.PARAMETER_DELIMITER
                                                    + weightValue
                                                    + durationValue + Constant.PARAMETER_DELIMITER
                                                    + set.getDifficulty() + Constant.PARAMETER_DELIMITER
                                                    + set.getNotes();
    }

    /**
     * Constructs the parameter title.
     *
     * @param name      The name of the parameter to use.
     * @param index     The index of the parameter to use.
     *
     * @return  The formatted parameter to send to the service.
     *          i.e. &columns0=
     */
    private String getParameterTitle(String name, int index)
    {
        return Constant.PARAMETER_AMPERSAND + name + index + "=";
    }

    /**
     * Sets the fitness listener.
     *
     * @param listener  The ExerciseListListener to set the fitness listener to.
     */
    public void setFitnessLogListener(ExerciseListListener listener)
    {
        fitnessLogListener = listener;
    }
}