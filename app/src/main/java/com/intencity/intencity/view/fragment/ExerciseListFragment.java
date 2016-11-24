package com.intencity.intencity.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.ExerciseAdapter;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ExerciseListener;
import com.intencity.intencity.listener.LoadingListener;
import com.intencity.intencity.listener.SaveRoutineListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.IndexedExercise;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.notification.AwardDialogContent;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.notification.SaveDialog;
import com.intencity.intencity.notification.WorkoutInfoDialog;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.task.SetExerciseTask;
import com.intencity.intencity.util.Badge;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.ExercisePriorityUtil;
import com.intencity.intencity.util.RoutineState;
import com.intencity.intencity.util.SecurePreferences;
import com.intencity.intencity.util.Util;
import com.intencity.intencity.view.activity.Direction;
import com.intencity.intencity.view.activity.ExerciseSearchActivity;
import com.intencity.intencity.view.activity.OverviewActivity;
import com.intencity.intencity.view.activity.SearchActivity;
import com.intencity.intencity.view.activity.StatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * The Exercise List Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class ExerciseListFragment extends android.support.v4.app.Fragment implements ExerciseListener, SaveRoutineListener, LoadingListener
{
    private static final int EXERCISE_MIN_SAVE_THRESHOLD = 2;
    private int TOTAL_EXERCISE_NUM = 7;

    private ArrayList<Exercise> allExercises;
    private ArrayList<Exercise> currentExercises;

    private ArrayList<String> awards = new ArrayList<>();

    private Context context;

    private TextView routineProgress;
    private TextView routine;

    private ImageButton save;

    private FloatingActionButton addExerciseButton;

    private LinearLayout searchDirectionsLayout;

    private RecyclerView recyclerView;

    private ExerciseAdapter adapter;

    private Snackbar snackbar;

    private int completedExerciseNum = 0;
    private int position;
    private int autoFillTo;
    private int routineState;

    private LoadingListener loadingListener;

    private SecurePreferences securePreferences;

    private boolean workoutFinished;

    private String email;
    private String routineName;
    private String warmUphExerciseName;
    private String stretchExerciseName;
    private String savedRoutineName;

    private IndexedExercise indexedExercise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);

        LinearLayout routineNameLayout = (LinearLayout) view.findViewById(R.id.layout_routine_name);
        ImageButton finish = (ImageButton) view.findViewById(R.id.finish);
        ImageButton info = (ImageButton) view.findViewById(R.id.info);
        save = (ImageButton) view.findViewById(R.id.save);
        addExerciseButton = (FloatingActionButton) view.findViewById(R.id.button_add_exercise);
        searchDirectionsLayout = (LinearLayout) view.findViewById(R.id.layout_search_exercise_directions);

        context = getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        routineProgress = (TextView) view.findViewById(R.id.text_view_routine_progress);
        routine = (TextView) view.findViewById(R.id.text_view_routine);

        routineNameLayout.setOnClickListener(routineNameClickListener);
        finish.setOnClickListener(finishClickListener);
        info.setOnClickListener(infoClickListener);
        save.setOnClickListener(saveClickListener);
        addExerciseButton.setOnClickListener(addExerciseButtonClickListener);

        Bundle bundle = getArguments();

        routineName = bundle.getString(Constant.BUNDLE_ROUTINE_NAME);
        routineState = bundle.getInt(Constant.BUNDLE_ROUTINE_TYPE);

        allExercises = bundle.getParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST);

        warmUphExerciseName = getString(R.string.warm_up);
        stretchExerciseName = getString(R.string.stretch);

        updateTotalExercises();

        autoFillTo = bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX) > 0 ?
                        bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX) : 1;
        currentExercises = new ArrayList<>();

        for (int i = 0; i < autoFillTo; i++)
        {
            currentExercises.add(allExercises.get(i));

            completedExerciseNum++;
        }

        setRoutineState();

        updateRoutineName(completedExerciseNum);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HeaderDecoration(context, recyclerView, R.layout.recycler_view_header));

        adapter = new ExerciseAdapter(context, routineState, currentExercises, this);
        recyclerView.setAdapter(adapter);

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
     * Sets the routine state for the routine.
     */
    private void setRoutineState()
    {
        switch (routineState)
        {
            case RoutineState.CUSTOM:
                searchDirectionsLayout.setVisibility(View.GONE);
                break;
            case RoutineState.SAVED:
                TOTAL_EXERCISE_NUM = allExercises.size();
            default:
                addExerciseButton.setOnLongClickListener(addExerciseButtonLongClickListener);
                break;
        }
    }

    /**
     * Displays the overview to the user.
     */
    private void displayOverview()
    {
        // Only display the overview screen if the user has completed 1 exercise.
        if (currentExercises.size() > 1)
        {
            Intent intent = new Intent(context, OverviewActivity.class);
            intent.putExtra(Constant.BUNDLE_ROUTINE_NAME, routineName);
            intent.putExtra(Constant.BUNDLE_EXERCISE_LIST, currentExercises);
            startActivityForResult(intent, Constant.REQUEST_CODE_OVERVIEW);
        }
        else
        {
            CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.title_finish_routine), context.getString(R.string.description_finish_routine), true);
            dialog.setPositiveButtonStringRes(R.string.title_finish);
            dialog.setNegativeButtonStringRes(android.R.string.cancel);

            new CustomDialog(context, dialogListener, dialog, true);
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
                    finishedExercising();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * The click listener for the routine name.
     */
    private View.OnClickListener routineNameClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            CustomDialogContent dialog = new CustomDialogContent(routineName, context.getString(R.string.routine_total_description, completedExerciseNum, TOTAL_EXERCISE_NUM), false);

            new CustomDialog(context, null, dialog, true);
        }
    };

    /**
     * The finish exercise click listener.
     */
    private View.OnClickListener finishClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            displayOverview();
        }
    };

    /**
     * The save workout click listener.
     */
    private View.OnClickListener saveClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            new SaveDialog(context, ExerciseListFragment.this, SaveDialog.SaveDialogState.INIT);
        }
    };

    /**
     * The info click listener.
     *
     * This is where we display how to workout with Intencity
     */
    private View.OnClickListener infoClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            new WorkoutInfoDialog(context);
        }
    };

    /**
     * The add exercise button click listener.
     */
    private View.OnClickListener addExerciseButtonClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (routineState)
            {
                case RoutineState.CUSTOM:

                    openSearchScreen();

                    break;

                default:
                    if (currentExercises.size() == allExercises.size())
                    {
                        displayOverview();
                    }
                    else
                    {
                        addExercise(false);
                    }

                    break;
            }
        }
    };

    /**
     * The long click listener for the add exercise button click.
     */
    private View.OnLongClickListener addExerciseButtonLongClickListener = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View v)
        {
            openSearchScreen();
            return false;
        }
    };

    /**
     * Opens the search screen to search for an exercise.
     */
    private void openSearchScreen()
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.BUNDLE_SEARCH_EXERCISES, true);
        bundle.putParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST, currentExercises);
        Intent intent = new Intent(ExerciseListFragment.this.getActivity(), SearchActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constant.REQUEST_CODE_SEARCH);
    }

    /**
     * The service listener for saving a routine.
     */
    private ServiceListener saveRoutineServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            Toast.makeText(context, getString(R.string.routine_saved, savedRoutineName), Toast.LENGTH_SHORT).show();

            loadingListener.onFinishedLoading(Constant.ID_SAVE_EXERCISE_LIST);
        }

        @Override
        public void onRetrievalFailed()
        {
            new SaveDialog(context, ExerciseListFragment.this, SaveDialog.SaveDialogState.SAME_NAME_ERROR);

            loadingListener.onFinishedLoading(Constant.ID_SAVE_EXERCISE_LIST);
        }
    };

    /**
     * Gets the next exercise from the list.
     */
    private void addExercise(boolean addingExerciseFromSearch)
    {
        // Dismiss the snackbar if it is being shown.
        if (snackbar != null && snackbar.isShown())
        {
            snackbar.dismiss();
        }

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

            Util.grantPointsToUser(email, Constant.POINTS_EXERCISE, context.getString(R.string.award_exercise_description));
        }

        int position = currentExercises.size() - 1;

        Exercise lastCurrentExercise = currentExercises.get(position);

        if (addingExerciseFromSearch && lastCurrentExercise.getName().equalsIgnoreCase(stretchExerciseName))
        {
            animateRemoveItem(position, true);
            allExercises.add(lastCurrentExercise);
        }
        else
        {
            updateRoutineName(++completedExerciseNum);
            currentExercises.add(allExercises.get(autoFillTo++));

            adapter.notifyItemInserted(position + 1);
        }

        // Scroll to the bottom of the list.
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
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

        save.setVisibility(completedExerciseNum < EXERCISE_MIN_SAVE_THRESHOLD ? View.GONE : View.VISIBLE);
    }

    /**
     * Adds an exercise to the list of exercises the user has completed
     * as well as the total exercises from the web server as well as removing
     * an exercise with the same name if it exists.
     *
     * @param exercise  The exercise to add.
     */
    public void addExerciseToList(Exercise exercise)
    {
        String exerciseName = exercise.getName();

        int totalExercises = allExercises.size();

        // TODO: This search will need to be updated better in case we have a lot of exercises.
        // TODO: (continued) Either that, or we should only get so many exercises per workout.
        for (int i = autoFillTo; i < totalExercises; i++)
        {
            if (allExercises.get(i).getName().equals(exerciseName))
            {
                allExercises.remove(i);

                break;
            }
        }

        allExercises.add(autoFillTo, exercise);

        addExercise(true);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if (!workoutFinished)
        {
            // Save the exercises to the database in case the user wants
            // to continue with this routine later.
            new SetExerciseTask(context, routineState, routineName, allExercises, currentExercises.size()).execute();
        }
    }

    /**
     * Starts the AsyncTask to remove the exercises from the database.
     */
    private void removeExercisesFromDatabase()
    {
        new SetExerciseTask(context, this).execute();
    }

    /**
     * Insert an exercise back into the array.
     *
     * @param indexedExercise   The exercise with an index that is being inserted into the array.
     */
    private void insertExercise(IndexedExercise indexedExercise)
    {
        Exercise exercise = indexedExercise.getExercise();
        int index = indexedExercise.getIndex();

        allExercises.add(index, exercise);
        currentExercises.add(index, exercise);

        adapter.insertItem(indexedExercise);

        updateRoutineName(++completedExerciseNum);
        autoFillTo++;
    }

    /**
     * Animates the removal of an item from the exercise list.
     *
     * @param position      The position in the list to remove.
     * @param fromSearch    A boolean value of whether or not we are coming from the search.
     */
    private void animateRemoveItem(int position, boolean fromSearch)
    {
        final Exercise exerciseToRemove = allExercises.get(position);

        allExercises.remove(exerciseToRemove);
        adapter.animateRemoveItem(position);

        if (fromSearch)
        {
            currentExercises.add(allExercises.get(autoFillTo - 1));
        }
        else
        {
            indexedExercise = new IndexedExercise(position, exerciseToRemove);

            String title = context.getString(R.string.undo_hide_exercise_title, exerciseToRemove.getName());

            // Show the message that the exercise was removed.
            // This allows users to undo removing the exercise.
            snackbar = Snackbar
                    .make(recyclerView, title, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.undo_hide_button, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            insertExercise(indexedExercise);

                            // Reset the model so we can use it again later.
                            indexedExercise = null;
                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(context, R.color.accent));
            snackbar.show();

            autoFillTo--;

            updateTotalExercises();

            updateRoutineName(--completedExerciseNum);

            // Add that the user has skipped an exercise.
            // Can't get the Kept Swimming badge.
            Util.setExerciseSkipped(securePreferences, true);
        }
    }

    @Override
    public void onExerciseClicked(int position)
    {
        String exerciseName = allExercises.get(position).getName();
        if (exerciseName.equalsIgnoreCase(warmUphExerciseName))
        {
            startSearchActivity(context, Constant.EXERCISE_TYPE_WARM_UP, routineName);
        }
        else if (exerciseName.equalsIgnoreCase(stretchExerciseName))
        {
            startSearchActivity(context, Constant.EXERCISE_TYPE_STRETCH, routineName);
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
    private void startSearchActivity(Context context, String type, String routineName)
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
        intent.putExtra(Constant.BUNDLE_POSITION, position);
        intent.putExtra(Constant.BUNDLE_EXERCISE, currentExercises.get(position));
        startActivityForResult(intent, Constant.REQUEST_CODE_STAT);
    }

    @Override
    public void onHideClicked(int position)
    {
        this.position = position;

        animateRemoveItem(this.position, false);
    }

    @Override
    public void onSetExercisePriority(int position, ImageButton morePriority, ImageButton lessPriority, boolean increment)
    {
        this.position = position;

        Exercise exercise = currentExercises.get(position);

        String exerciseName = exercise.getName();

        int priority = ExercisePriorityUtil.getExercisePriority(exercise.getPriority(), increment);
        exercise.setPriority(priority);
        ExercisePriorityUtil.setPriorityButtons(priority, morePriority, lessPriority);

        // Set the exercise priority on the web server.
        // The ServiceListener is null because we don't care if it reached the server.
        // The worst that will happen is a user will have to click the exercise priority again.
        new ServiceTask(null).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_SET_EXERCISE_PRIORITY,
                                                                                 email, exerciseName, increment ? "1" : "0"));

        // Remove the exercise if the user decremented it and it is less than 0.
        if (priority == ExercisePriorityUtil.PRIORITY_LIMIT_LOWER && !increment)
        {
            animateRemoveItem(this.position, false);
        }

        // Displays a toast to the user telling them they will see an exercise more or less.
        Toast.makeText(context, context.getString(increment ? R.string.more_priority_string : R.string.less_priority_string, exerciseName), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSavePressed(String routineName)
    {
        if (routineName.length() > 0)
        {
            loadingListener.onStartLoading();

            savedRoutineName = routineName;

            String params = Constant.generateRoutineListVariables(email, routineName, currentExercises);
            new ServiceTask(saveRoutineServiceListener).execute(Constant.SERVICE_SET_ROUTINE, params);
        }
        else
        {
            new SaveDialog(context, ExerciseListFragment.this, SaveDialog.SaveDialogState.NAME_REQUIRED);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result codes are set from an activity with setResult().
        if (resultCode == Constant.REQUEST_CODE_STAT)
        {
            Bundle extras = data.getExtras();
            int position = extras.getInt(Constant.BUNDLE_POSITION);
            final ArrayList<Set> sets = extras.getParcelableArrayList(Constant.BUNDLE_EXERCISE_SETS);

            Exercise currentExercise = currentExercises.get(position);
            currentExercise.setSets(sets);
            String exerciseName = currentExercise.getName();
            allExercises.get(position).setSets(sets);

            // We notify the data set changed just in case something changed in the stat activity.
            // This will update the last set the user did.
            adapter.notifyDataSetChanged();

            int setSize = sets.size();
            String statements = "statements=";
            String updateString = statements + setSize + Constant.PARAMETER_AMPERSAND + Constant.PARAMETER_EMAIL + email;
            String insertString = statements + setSize + Constant.PARAMETER_AMPERSAND + Constant.PARAMETER_EMAIL + email;

            // The update and insert Strings already starts out with a value in it,
            // so we need this boolean to decide if we are going to conduct these actions.
            boolean conductUpdate = false;
            boolean conductInsert = false;

            String badge = Badge.LEFT_IT_ON_THE_FIELD;
            int setsWithRepsGreaterThan10 = 0;

            // we need our own indexes here or the services won't work properly.
            int updateIndex = 0;
            int insertIndex = 0;

            // Sort through the sets to determine if we are updating or inserting.
            for (int i = 0; i < setSize; i++)
            {
                Set set = sets.get(i);

                // Checks to see if the user deserves the "Left it on the Field" badge.
                if (!awards.contains(badge + exerciseName) && (set.getReps() >= 10 || Integer.valueOf(Util.getRepFormat(set.getDuration())) >= 100))
                {
                    setsWithRepsGreaterThan10++;

                    if (setsWithRepsGreaterThan10 >= 2)
                    {
                        SecurePreferences securePreferences = new SecurePreferences(context);
                        String email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

                        Util.grantBadgeToUser(email, badge,
                                              new AwardDialogContent(R.mipmap.left_it_on_the_field,
                                                                     context.getString(R.string.award_left_it_on_the_field_description)), false);

                        awards.add(badge + exerciseName);
                    }
                }

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
        else if(resultCode == Constant.REQUEST_CODE_SEARCH)
        {
            Bundle extras = data.getExtras();
            boolean searchExercise = extras.getBoolean(Constant.BUNDLE_SEARCH_EXERCISES);

            if (searchExercise)
            {
                Exercise exercise = extras.getParcelable(Constant.BUNDLE_EXERCISE);

                addExerciseToList(exercise);
            }
        }
        else if (resultCode  == Constant.REQUEST_CODE_OVERVIEW)
        {
            // The user finished the exercise from the overview screen.
            // Start the routine view over again.
            finishedExercising();
        }
    }

    /**
     * The user is finished exercising, so we load the routine fragment again.
     */
    private void finishedExercising()
    {
        workoutFinished = true;

        removeExercisesFromDatabase();
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

        float weight = set.getWeight();
        String duration = set.getDuration();
        String notes = set.getNotes();
        boolean isDuration = duration != null && duration.contains(":");

        String weightParam = Constant.COLUMN_EXERCISE_WEIGHT + equals + weight + Constant.PARAMETER_DELIMITER;
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

        float weight = set.getWeight();
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

    @Override
    public void onStartLoading()
    {

    }

    @Override
    public void onFinishedLoading(int which)
    {
        // Only the remove exercises gets loaded, so we don't need to switch.
        // Starts the routine fragment over.
        loadingListener.onFinishedLoading(Constant.ID_FRAGMENT_ROUTINE_RELOAD);
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
     * Sets the loading listener.
     *
     * @param listener  The LoadingListener to set.
     */
    public void setLoadingListener(LoadingListener listener)
    {
        loadingListener = listener;
    }
}