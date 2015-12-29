package com.intencity.intencity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.ExerciseAdapter;
import com.intencity.intencity.listener.ExerciseListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.task.SetExerciseTask;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * The Exercise List Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class ExerciseListFragment extends android.support.v4.app.Fragment implements ExerciseListener
{
    private int TOTAL_EXERCISE_NUM = 5;

    private RecyclerView recyclerView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);

        nextExercise = (Button) view.findViewById(R.id.button_next);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

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

        mAdapter = new ExerciseAdapter(context, this, currentExercises);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private View.OnClickListener nextExerciseClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            addExercise();

            checkNextButtonEnablement();
        }
    };

    @Override
    public void onHideClicked(int position)
    {
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
    }

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
}