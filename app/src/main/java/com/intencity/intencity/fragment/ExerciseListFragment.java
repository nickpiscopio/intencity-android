package com.intencity.intencity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.CardExerciseAdapter;
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
    private RecyclerView recyclerView;

    private int autoFillTo;

    private ArrayList<Exercise> allExercises;
    private ArrayList<Exercise> currentExercises;

    private RecyclerView.Adapter mAdapter;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        Bundle bundle = getArguments();

        allExercises = bundle.getParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST);
        // This will add one more exercise every time it starts.
        // If we don't want this, then add a ternary operator to set this to
        // autoFillTo = bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX) > 0 ?
        //              bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX) : 1;
        autoFillTo = bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX) + 1;
        currentExercises = new ArrayList<>();

        for (int i = 0; i < autoFillTo; i++)
        {
            currentExercises.add(allExercises.get(i));
        }

        context = getContext();

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CardExerciseAdapter(context, this, currentExercises);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onExerciseClicked()
    {
        addExercise();
    }

    @Override
    public void onHideClicked(int position)
    {
        Exercise exerciseToRemove = currentExercises.get(position);

        currentExercises.remove(exerciseToRemove);
        allExercises.remove(exerciseToRemove);

        autoFillTo--;

        if (position == currentExercises.size())
        {
            addExercise();
        }
        else
        {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void addExercise()
    {
        currentExercises.add(allExercises.get(++autoFillTo));

        mAdapter.notifyDataSetChanged();
        // TODO: set the recycler view to scroll to the bottom when a new view is added.
        //        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // Save the exercises to the database in case the user wants
        // to continue with this routine later.
        new SetExerciseTask(context, allExercises, currentExercises.size()).execute();
    }
}