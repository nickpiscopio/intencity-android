package com.intencity.intencity.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.StatActivity;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * The ExerciseViewHolder for the exercise RecyclerView.
 *
 * Created by Nick Piscopio on 12/21/15.
 */
public class ExerciseViewHolder extends RecyclerView.ViewHolder
{
    private Context context;

    private View view;

    private TextView exercise;

    private ArrayList<Set> sets;

    public ExerciseViewHolder(Context context, View view)
    {
        super(view);

        this.context = context;

        this.view = view;
        this.view.setOnClickListener(exerciseClickListener);

        exercise = (TextView) view.findViewById(R.id.exercise);
    }

    private View.OnClickListener exerciseClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(context, StatActivity.class);
            intent.putExtra(Constant.BUNDLE_EXERCISE_NAME, exercise.getText().toString());
            intent.putExtra(Constant.BUNDLE_EXERCISE_SETS, sets);
            context.startActivity(intent);
        }
    };

    /**
     * Getters and setters for the view holder class.
     */
    public View getView()
    {
        return view;
    }

    public TextView getExercise()
    {
        return exercise;
    }

    public void setSets(ArrayList<Set> sets)
    {
        this.sets = sets;
    }
}