package com.intencity.intencity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.viewholder.ExerciseViewHolder;
import com.intencity.intencity.listener.ExerciseListener;
import com.intencity.intencity.model.Exercise;

import java.util.ArrayList;

/**
 * This is the adapter for the exercise cards.
 *
 * Created by Nick Piscopio on 12/21/15.
 */
public class ExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;

    private ArrayList<Exercise> exercises;

    private ExerciseListener listener;

    // Allows to remember the last item shown on screen
    private int lastPosition = 0;

    public ExerciseAdapter(Context context, ArrayList<Exercise> exercises, ExerciseListener listener)
    {
        this.context = context;
        this.exercises = exercises;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_exercise,
                                                                  parent, false);

        return new ExerciseViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        Exercise exercise = exercises.get(position);

        ExerciseViewHolder exerciseHolder = (ExerciseViewHolder) holder;
        exerciseHolder.getExerciseTextView().setText(exercise.getName());
        exerciseHolder.setPosition(position);
        View view = exerciseHolder.getView();

        // Here you apply the animation when the view is bound
        setAnimation(view, position);
    }

    @Override
    public int getItemCount()
    {
        return exercises.size();
    }

    /**
     * Sets the animation for a view.
     *
     * @param viewToAnimate     The view to set an animation.
     * @param position          The current position of the recycler view.
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen.
        // Add animation.
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils
                    .loadAnimation(context, R.anim.anim_slide_in_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    /**
     * Gets the last position of the recycler view.
     *
     * @return  The last position of the recycler view.
     */
    public int getLastPosition()
    {
        return lastPosition;
    }

    /**
     * Sets the last position of the recycler view.
     *
     * @param lastPosition  The position to set.
     */
    public void setLastPosition(int lastPosition)
    {
        this.lastPosition = lastPosition;
    }
}