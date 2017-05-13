package com.intencity.intencity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.SearchListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.view.activity.Direction;

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
     * @param searchExerciseResults     The list of exercises from the search.
     * @param currentExercises          The list of exercises the user has currently completed.
     * @param listener                  The SearchListener to call when we click on the add button.
     */
    public SearchExerciseListAdapter(Context context, ArrayList<Exercise> searchExerciseResults, ArrayList<Exercise> currentExercises, SearchListener listener)
    {
        super(context, 0, searchExerciseResults);

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

        boolean includedInIntencity = exercise.isIncludedInIntencity();

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
        LinearLayout layoutExerciseName = (LinearLayout) view.findViewById(R.id.layout_exercise_name);
        TextView name = (TextView) view.findViewById(R.id.text_view_name);
        TextView description = (TextView) view.findViewById(R.id.text_view_description);
        name.setText(exerciseName);

        if (includedInIntencity)
        {
            layoutExerciseName.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, Direction.class);
                    intent.putExtra(Constant.BUNDLE_EXERCISE, exercise);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            description.setVisibility(View.GONE);
            layout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        }
        else
        {
            description.setVisibility(View.VISIBLE);
            layout.setBackgroundColor(ContextCompat.getColor(context, R.color.page_background));
        }

        final ImageButton addButton = (ImageButton) view.findViewById(R.id.button_add);

        // Needed to add the click listener in getView because the id was
        // getting overwritten by the last id in the list.
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
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