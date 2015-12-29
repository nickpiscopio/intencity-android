package com.intencity.intencity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

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
public class CardExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private final int POSITION_0 = 0;

    private Context context;

    private ExerciseListener listener;

    private ArrayList<Exercise> exercises;

    // Allows to remember the last item shown on screen
    private int lastPosition = 0;

    public CardExerciseAdapter(Context context, ExerciseListener listener, ArrayList<Exercise> exercises)
    {
        this.context = context;
        this.listener = listener;
        this.exercises = exercises;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_card_exercise, parent, false);

        return new ExerciseViewHolder(context, listener, v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ExerciseViewHolder exerciseHolder = (ExerciseViewHolder) holder;
        exerciseHolder.setPosition(position);
        exerciseHolder.getExercise().setText(exercises.get(position).getName());
        View view = exerciseHolder.getView();

//        if (position == POSITION_0 || position == exercises.size() - 1)
//        {
//            view.setLayoutParams(getNewLayout(position));
//        }

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
        // If the bound view wasn't previously displayed on screen, it's animated
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

    private RelativeLayout.LayoutParams getNewLayout(int position)
    {
        RelativeLayout.LayoutParams lp =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);

        if (position == POSITION_0)
        {
            lp.setMargins(0,
                          (int)context.getResources().getDimension(R.dimen.layout_margin),
                          0,
                          (int)context.getResources().getDimension(R.dimen.layout_margin_half));
        }
        else
        {
            lp.setMargins(0,
                          0,
                          0,
                          (int)context.getResources().getDimension(R.dimen.layout_margin));
        }

        return lp;
    }
}