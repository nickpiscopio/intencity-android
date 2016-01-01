package com.intencity.intencity.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ExerciseListener;

/**
 * The ExerciseViewHolder for the exercise RecyclerView.
 *
 * Created by Nick Piscopio on 12/21/15.
 */
public class ExerciseViewHolder extends RecyclerView.ViewHolder
{
    private View view;

    private TextView exercise;

    private ExerciseListener listener;

    private int position;

    public ExerciseViewHolder(View view, ExerciseListener listener)
    {
        super(view);

        this.view = view;
        this.view.setOnClickListener(exerciseClickListener);

        this.listener = listener;

        exercise = (TextView) view.findViewById(R.id.exercise);
    }

    /**
     * The exercise click listener to see the stats of the exercise.
     */
    private View.OnClickListener exerciseClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            listener.onExerciseClicked(position);
        }
    };

    /**
     * Getters and setters for the view holder class.
     */
    public View getView()
    {
        return view;
    }

    public TextView getExerciseTextView()
    {
        return exercise;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }
}