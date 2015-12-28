package com.intencity.intencity.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.Direction;
import com.intencity.intencity.dialog.CustomDialog;
import com.intencity.intencity.dialog.Dialog;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ExerciseListener;
import com.intencity.intencity.util.Constant;

/**
 * Created by nickpiscopio on 12/21/15.
 */
public class ExerciseViewHolder extends RecyclerView.ViewHolder implements DialogListener
{
    private Context context;

    private ExerciseListener listener;

    private int position;

    private View view;
    private TextView exercise;
    private ImageButton hide;

    public ExerciseViewHolder(Context context, ExerciseListener listener, View view)
    {
        super(view);

        this.context = context;
        this.listener = listener;

        this.view = view;

        exercise = (TextView) view.findViewById(R.id.exercise);
        hide = (ImageButton) view.findViewById(R.id.button_hide);

        view.setOnClickListener(viewClickListener);

        hide.setOnClickListener(hideClickListener);
    }

    private View.OnClickListener viewClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            listener.onExerciseClicked();
            v.setOnClickListener(null);
            exercise.setOnClickListener(exerciseClickListener);
        }
    };

    private View.OnClickListener exerciseClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(context, Direction.class);
            intent.putExtra(Constant.BUNDLE_EXERCISE_NAME, exercise.getText().toString());
            context.startActivity(intent);
        }
    };

    private View.OnClickListener hideClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String[] buttonText = new String[]{context.getString(R.string.hide_for_now),
                                               context.getString(R.string.hide_forever)};

            new CustomDialog(context, ExerciseViewHolder.this, new Dialog(buttonText));
        }
    };


    @Override
    public void onButtonPressed(int which)
    {
        listener.onHideClicked(position);
    }

    @Override
    public void onPositiveButtonPressed()
    {

    }

    @Override
    public void onNegativeButtonPressed()
    {

    }

    /**
     * Getters and setters for the view holder class.
     */
    public void setPosition(int position)
    {
        this.position = position;
    }

    public View getView()
    {
        return view;
    }

    public void setView(View view)
    {
        this.view = view;
    }

    public TextView getExercise()
    {
        return exercise;
    }

    public void setExercise(TextView exercise)
    {
        this.exercise = exercise;
    }

    public ImageButton getHide()
    {
        return hide;
    }

    public void setHide(ImageButton hide)
    {
        this.hide = hide;
    }
}