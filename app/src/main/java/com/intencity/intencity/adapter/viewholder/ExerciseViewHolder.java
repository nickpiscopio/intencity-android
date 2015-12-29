package com.intencity.intencity.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.Direction;
import com.intencity.intencity.adapter.ExerciseSetAdapter;
import com.intencity.intencity.dialog.CustomDialog;
import com.intencity.intencity.dialog.Dialog;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ExerciseListener;
import com.intencity.intencity.listener.SetListener;
import com.intencity.intencity.model.ExerciseViewHolderEntity;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * Created by nickpiscopio on 12/21/15.
 */
public class ExerciseViewHolder extends RecyclerView.ViewHolder implements DialogListener,
                                                                           SetListener
{
    private Context context;

    private ExerciseListener listener;

    private int position;

    private View view;

    private TextView exercise;

    private ImageButton hide;

    private ListView setsListView;

    private ArrayList<Set> sets;

    private ExerciseViewHolderEntity entity;

    public ExerciseViewHolder(Context context, ExerciseListener listener, View view)
    {
        super(view);

        this.context = context;
        this.listener = listener;

        this.view = view;

        exercise = (TextView) view.findViewById(R.id.exercise);

        hide = (ImageButton) view.findViewById(R.id.button_hide);

        setsListView = (ListView) view.findViewById(R.id.list_view);

        exercise.setOnClickListener(exerciseClickListener);
        hide.setOnClickListener(hideClickListener);

        entity = ExerciseViewHolderEntity.getInstance();
    }

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
            String[] buttonText = new String[]{context.getString(R.string.add_set),
                                               context.getString(R.string.hide_for_now)/*,
                                               context.getString(R.string.hide_forever)*/};

            new CustomDialog(context, ExerciseViewHolder.this, new Dialog(buttonText));
        }
    };


    @Override
    public void onButtonPressed(int which)
    {
        switch (which)
        {
            case 0: // Add set was clicked.
                addSet();
                break;
            case 1: // Hide was clicked.
                listener.onHideClicked(position);
                break;
            default:
                break;
        }

    }

    public void initializeSets(ArrayList<Set> setList)
    {
        ArrayList<ExerciseSetAdapter> adapters = entity.getAdapters();

        try
        {
            adapters.get(position);
        }
        catch (Exception e)
        {
            sets = new ArrayList<>();
            sets.addAll(setList);

            ExerciseSetAdapter adapter = new ExerciseSetAdapter(context, R.layout.fragment_exercise_set, this, sets);

            adapters.add(adapter);

            setsListView.setAdapter(adapters.get(position));
        }
    }


//    public ListView.OnItemClickListener setClickLisener = new ListView.OnItemClickListener()
//    {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//        {
//            EditText weightEditText = (EditText) view.findViewById(R.id.edit_text_weight);
//            EditText durationEditText = (EditText) view.findViewById(R.id.edit_text_duration);
//            Spinner intensitySpinner = (Spinner) view.findViewById(R.id.spinner_intensity);
//
//            weightEditText.setOnClickListener(setClickListener);
//            durationEditText.setOnClickListener(setClickListener);
//            intensitySpinner.setOnItemSelectedListener(itemClickListener);
//        }
//    };
//
//    public View.OnClickListener setClickListener = new View.OnClickListener()
//    {
//        @Override
//        public void onClick(View v)
//        {
//            addSet();
//        }
//    };
//
//    private AdapterView.OnItemSelectedListener itemClickListener = new AdapterView.OnItemSelectedListener()
//    {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//        {
//            //            if (!isEnabled)
//            //            {
//            //                addSet();
//            //            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent)
//        {
//            addSet();
//        }
//    };

    @Override
    public void onSetAdded()
    {
//        setListViewHeight();
    }

    @Override
    public void onSetClicked()
    {
        addSet();
    }

    private void addSet()
    {
        Set set = new Set();
        set.setWeight(Constant.CODE_FAILED);
        set.setReps(Constant.CODE_FAILED);
        set.setDuration(null);
        set.setDifficulty(10);

        sets.add(set);

        entity.getAdapters().get(position).notifyDataSetChanged();

        setListViewHeight();
    }

    private void setListViewHeight()
    {
        ExerciseSetAdapter adapter = entity.getAdapters().get(position);
        View listItem = adapter.getView();
        listItem.measure(0, 0);

        int height = listItem.getMeasuredHeight();
        int count = adapter.getItemCount();

        // Set a new height for the layout.
        // If we don't do this, the height is 0 because we are inside a RecyclerView.
        setsListView.getLayoutParams().height = height * count;
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