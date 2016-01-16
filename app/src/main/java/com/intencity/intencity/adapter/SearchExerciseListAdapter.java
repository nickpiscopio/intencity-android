package com.intencity.intencity.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.Direction;
import com.intencity.intencity.listener.SearchListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the ranking list.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class SearchExerciseListAdapter extends ArrayAdapter<Exercise>
{
    private Context context;

    private ArrayList<Exercise> searchExerciseResults;
    private ArrayList<Exercise> currentExercises;

    private SearchListener listener;

    /**
     * The constructor.
     *
     * @param context                   The application context.
     * @param resId                     The layout resource id for the list item.
     * @param searchExerciseResults     The list of exercises from the search.
     * @param currentExercises          The list of exercises the user has currently completed.
     * @param listener                  The SearchListener to call when we click on the add button.
     */
    public SearchExerciseListAdapter(Context context, int resId, ArrayList<Exercise> searchExerciseResults, ArrayList<Exercise> currentExercises, SearchListener listener)
    {
        super(context, resId, searchExerciseResults);

        this.context = context;

        this.searchExerciseResults = searchExerciseResults;
        this.currentExercises = currentExercises;

        this.listener = listener;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_search_exercise, null);
        }

        final Exercise exercise = searchExerciseResults.get(position);

        final String exerciseName = exercise.getName();

        TextView name = (TextView) view.findViewById(R.id.text_view_name);
        name.setText(exerciseName);
        name.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, Direction.class);
                intent.putExtra(Constant.BUNDLE_EXERCISE_NAME, exerciseName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        final ImageButton addButton = (ImageButton) view.findViewById(R.id.button_add);

        // Needed to add the click listener in getView because the id was
        // getting overwritten by the last id in the list.
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                listener.onExerciseAdded(exercise);
            }
        });

        if (currentExercises == null)
        {
            addButton.setVisibility(View.GONE);
        }
        else
        {
            // TODO: This search will need to be updated better in case we have a lot of results.
            // TODO: (continued) Either that, or we should only get so many results at once.
            // Adds the add button to the UI if the user has not completed the exercise yet.
            for (Exercise searchedExercise : currentExercises)
            {
                if (searchedExercise.getName().equalsIgnoreCase(exerciseName))
                {
                    addButton.setVisibility(View.GONE);
                    break;
                }
                else
                {
                    addButton.setVisibility(View.VISIBLE);
                }
            }
        }


        return view;
    }
}