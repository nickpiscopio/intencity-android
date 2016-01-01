package com.intencity.intencity.fragment;

import android.content.Context;
import android.content.Intent;
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
import com.intencity.intencity.activity.StatActivity;
import com.intencity.intencity.adapter.ExerciseAdapter;
import com.intencity.intencity.dialog.CustomDialog;
import com.intencity.intencity.dialog.Dialog;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ExerciseListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.task.SetExerciseTask;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

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

    private ExerciseAdapter mAdapter;

    private Context context;

    private String routineName;

    private int completedExerciseNum = 0;

    private int position;

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

        mAdapter = new ExerciseAdapter(context, currentExercises, this);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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

        mAdapter.notifyDataSetChanged();
        // TODO: set the recycler view to scroll to the bottom when a new view is added.
        //        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
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
                mAdapter.setLastPosition(mAdapter.getLastPosition() - 1);

                if (position == currentExercises.size())
                {
                    addExercise();
                }
                else
                {
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case 1: // Cancel was clicked.
                mAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onExerciseClicked(int position)
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

            currentExercises.get(position).setSets(sets);
            allExercises.get(position).setSets(sets);
        }
    }
}