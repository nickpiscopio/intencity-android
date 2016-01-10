package com.intencity.intencity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.Direction;
import com.intencity.intencity.activity.StatActivity;
import com.intencity.intencity.adapter.ExerciseAdapter;
import com.intencity.intencity.dialog.CustomDialog;
import com.intencity.intencity.dialog.Dialog;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ExerciseListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.task.SetExerciseTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SecurePreferences;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Exercise List Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class ExerciseListFragment extends android.support.v4.app.Fragment implements DialogListener,
                                                                                     ExerciseListener
{
    private int TOTAL_EXERCISE_NUM = 5;

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

        autoFillTo = bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX) > 0 ?
                        bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX) : 1;
        currentExercises = new ArrayList<>();

        for (int i = 0; i < autoFillTo; i++)
        {
            currentExercises.add(allExercises.get(i));

            completedExerciseNum++;
        }

        updateRoutineName(completedExerciseNum);

        checkNextButtonEnablement();

        context = getContext();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(
                new HeaderDecoration(context, recyclerView, R.layout.recycler_view_header));

        adapter = new ExerciseAdapter(context, currentExercises, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        SecurePreferences securePreferences = new SecurePreferences(context);
        email = securePreferences.getString(Constant.USER_ACCOUNT_EMAIL, "");

        return view;
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

            new CustomDialog(context, ExerciseListFragment.this, new Dialog(buttonText));
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
            addExercise();

            checkNextButtonEnablement();
        }
    };

    /**
     * Gets the next exercise from the list.
     */
    private void addExercise()
    {
        completedExerciseNum++;
        updateRoutineName(completedExerciseNum);

        currentExercises.add(allExercises.get(autoFillTo++));

        adapter.notifyDataSetChanged();
        // TODO: set the recycler view to scroll to the bottom when a new view is added.
        //        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    /**
     * Sets the next button to be gone if we don't have anymore exercises left.
     */
    private void checkNextButtonEnablement()
    {
        if (currentExercises.size() == allExercises.size())
        {
            nextExercise.setVisibility(View.INVISIBLE);
        }
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

    @Override
    public void onStop()
    {
        super.onStop();

        // Save the exercises to the database in case the user wants
        // to continue with this routine later.
        new SetExerciseTask(context, routineName, allExercises, currentExercises.size()).execute();
    }

    @Override
    public void onButtonPressed(int which)
    {
        switch (which)
        {
            case 0: // Hide was clicked.
                Exercise exerciseToRemove = currentExercises.get(position);

                currentExercises.remove(exerciseToRemove);
                allExercises.remove(exerciseToRemove);

                autoFillTo--;
                completedExerciseNum--;

                // Remove 1 from the last position so we can se the animation
                // for the next exercise that will be added.
                adapter.setLastPosition(adapter.getLastPosition() - 1);

                if (position == currentExercises.size())
                {
                    addExercise();
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
        Intent intent = new Intent(context, Direction.class);
        intent.putExtra(Constant.BUNDLE_EXERCISE_NAME, currentExercises.get(position).getName());
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
                    public void onRetrievalSuccessful(String response)
                    {
                        Log.i(Constant.TAG, "COMPLEX UPDATE RESPONSE: " + response);
                    }

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
                        Log.i(Constant.TAG, "COMPLEX INSERT RESPONSE: " + response);

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
        boolean hasWeight = weight > 0;
        boolean isDuration = duration != null && duration.contains(":");

        String weightParam = hasWeight ? Constant.COLUMN_EXERCISE_WEIGHT + equals + weight + Constant.PARAMETER_DELIMITER : "";
        // Choose whether we are inserting reps or duration.
        String durationParam = isDuration ?
                Constant.COLUMN_EXERCISE_DURATION + equals + "'" + duration + "'" + Constant.PARAMETER_DELIMITER
                + Constant.COLUMN_EXERCISE_REPS + equals + nullString + Constant.PARAMETER_DELIMITER :
                Constant.COLUMN_EXERCISE_DURATION + equals + nullString + Constant.PARAMETER_DELIMITER
                + Constant.COLUMN_EXERCISE_REPS + equals + set.getReps() + Constant.PARAMETER_DELIMITER;

        return getParameterTitle(Constant.PARAMETER_TABLE, index) + Constant.TABLE_COMPLETED_EXERCISE
               + getParameterTitle(setParam, index)
                    + weightParam
                    + durationParam
                    + Constant.COLUMN_EXERCISE_DIFFICULTY + equals + set.getDifficulty()
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
                                                    + Constant.COLUMN_EXERCISE_DIFFICULTY
                + getParameterTitle(inserts, index) + curDate + Constant.PARAMETER_DELIMITER
                                                    + now + Constant.PARAMETER_DELIMITER
                                                    + name + Constant.PARAMETER_DELIMITER
                                                    + weightValue
                                                    + durationValue + Constant.PARAMETER_DELIMITER
                                                    + set.getDifficulty();
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
}