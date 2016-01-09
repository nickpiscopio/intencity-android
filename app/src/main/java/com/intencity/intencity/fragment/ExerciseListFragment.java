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

/**
 * The Exercise List Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class ExerciseListFragment extends android.support.v4.app.Fragment implements DialogListener,
                                                                                     ExerciseListener,
                                                                                     ServiceListener
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
            ArrayList<Set> sets = extras.getParcelableArrayList(Constant.BUNDLE_EXERCISE_SETS);

            Exercise currentExercise = currentExercises.get(position);
            currentExercise.setSets(sets);
            allExercises.get(position).setSets(sets);

            adapter.notifyDataSetChanged();

            // Save exercise to the web.
            new ServiceTask(this).execute(Constant.SERVICE_COMPLEX_INSERT,
                                          generateComplexInsertParameters(currentExercise));
        }
    }

    /**
     * Constructs the parameter query sent to the service.
     *
     * @param exercise  The exercise that is getting parsed to create the query.
     *
     * @return  The constructed String.
     */
    private String generateComplexInsertParameters(Exercise exercise)
    {
        String statements = "statements=";
        String table = "table";
        String columns = "columns";
        String inserts = "inserts";
        String tableCompletedExercise = "CompletedExercise";
        String columnDate = "Date";
        String columnTime = "Time";
        String columnExerciseName = "ExerciseName";
        String columnExerciseWeight = "ExerciseWeight";
        String columnExerciseReps = "ExerciseReps";
        String columnExerciseDuration = "ExerciseDuration";
        String columnExerciseDifficulty = "ExerciseDifficulty";

        String curDate = "CURDATE()";
        String now = "NOW()";

        String exerciseName = exercise.getName();
        ArrayList<Set> sets = exercise.getSets();

        int setSize = sets.size();

        String parameter = statements + setSize
                           + Constant.PARAMETER_AMPERSAND + Constant.PARAMETER_EMAIL + email;

        for (int i = 0; i < setSize; i++)
        {
            Set set = sets.get(i);
            int weight = set.getWeight();

            String duration = set.getDuration();
            boolean hasWeight = weight > 0;
            boolean isDuration = duration.contains(":");

            String weightParam = hasWeight ? columnExerciseWeight + Constant.PARAMETER_DELIMITER : "";
            String weightValue = hasWeight ? String.valueOf(weight) + Constant.PARAMETER_DELIMITER : "";
            // Choose whether we are inserting reps or duration.
            String durationParam = isDuration ? columnExerciseDuration + Constant.PARAMETER_DELIMITER :
                                                                columnExerciseReps + Constant.PARAMETER_DELIMITER;
            String durationValue = isDuration ? duration : String.valueOf(set.getReps());

            parameter += getParameterTitle(table, i)
                            + tableCompletedExercise
                            + getParameterTitle(columns, i) + columnDate + Constant.PARAMETER_DELIMITER
                                                            + columnTime + Constant.PARAMETER_DELIMITER
                                                            + columnExerciseName + Constant.PARAMETER_DELIMITER
                                                            + weightParam
                                                            + durationParam
                                                            + columnExerciseDifficulty
                            + getParameterTitle(inserts, i) + curDate + Constant.PARAMETER_DELIMITER
                                                            + now + Constant.PARAMETER_DELIMITER
                                                            + exerciseName + Constant.PARAMETER_DELIMITER
                                                            + weightValue
                                                            + durationValue + Constant.PARAMETER_DELIMITER
                                                            + set.getDifficulty();
        }

        return parameter;
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

    @Override
    public void onRetrievalSuccessful(String response)
    {
        Log.i(Constant.TAG, "COMPLEX INSERT RESPONSE: " + response);
    }

    @Override
    public void onRetrievalFailed() { }
}