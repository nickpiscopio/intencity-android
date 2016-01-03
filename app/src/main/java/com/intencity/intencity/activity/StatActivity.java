package com.intencity.intencity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.ExerciseSetAdapter;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * This is the set activity for an Exercise.
 *
 * Created by Nick Piscopio on 12/31/15.
 */
public class StatActivity extends AppCompatActivity
{
    private final int REPS = 0;
    private final int TIME = 1;

    private ListView setsListView;

    private ExerciseSetAdapter adapter;
    private ArrayList<Set> sets;

    private Exercise exercise;
    private int position;

    private Context context;

    private String exerciseName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        setsListView = (ListView) findViewById(R.id.list_view);
        Spinner durationSpinner = (Spinner) findViewById(R.id.spinner_duration);

        ActionBar actionBar = getSupportActionBar();

        Bundle bundle = getIntent().getExtras();

        exercise = bundle.getParcelable(Constant.BUNDLE_EXERCISE);
        position = bundle.getInt(Constant.BUNDLE_EXERCISE_POSITION);

        if(exercise != null)
        {
            exerciseName = exercise.getName();
            sets = exercise.getSets();

            if (actionBar != null)
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(exerciseName);
            }
        }

        context = getApplicationContext();

        // Initialize the set adapter with the sets.
        adapter = new ExerciseSetAdapter(context, R.layout.fragment_exercise_set, sets);
        setsListView.setAdapter(adapter);

        // Populate the spinner.
        String[] spinnerValues = new String[] { context.getString(
                R.string.title_reps), context.getString(R.string.title_time) };
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(context, R.layout.spinner_duration, spinnerValues);
        durationAdapter.setDropDownViewResource(R.layout.spinner_item);

        durationSpinner.setAdapter(durationAdapter);
        durationSpinner.setSelection(sets.get(0).getDuration().contains(":") ? TIME : REPS, false);
        durationSpinner.setOnItemSelectedListener(durationTypeSelected);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exercise_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.info:
                Intent intent = new Intent(context, Direction.class);
                intent.putExtra(Constant.BUNDLE_EXERCISE_NAME, exerciseName);
                startActivity(intent);
                return true;
            case R.id.add:
                addSet();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private AdapterView.OnItemSelectedListener durationTypeSelected = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            switch (position)
            {
                case REPS: //Reps selected.
                    setRepFormat();
                    break;
                case TIME: // Time selected.
                    setTimeFormat();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    };

    private void setRepFormat()
    {
        for (Set set : sets)
        {
            String duration = set.getDuration();

            // Make the input 0 if it doesn't have a value.
            duration = duration == null ||
                       duration.equals(Constant.RETURN_NULL) ||
                       duration.length() < 1 ? "0": duration;

            String formatted = duration.replaceAll(":", "");

            int reps = Integer.parseInt(formatted.replaceFirst("^0+(?!$)", ""));
            set.setReps(reps);
            set.setDuration(Constant.RETURN_NULL);
        }

        adapter.notifyDataSetChanged();
    }

    private void setTimeFormat()
    {
        for (Set set : sets)
        {
            int reps = set.getReps();

            int timeLength = 6;

            String padded = String.format("%0" + timeLength + "d", reps);
            String duration = padded.replaceAll("..(?!$)", "$0:");
            set.setDuration(duration);
            set.setReps(Constant.CODE_FAILED);
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Adds a set to the exercise and the ArrayList.
     */
    private void addSet()
    {
        Set set = new Set();
        set.setWeight(Constant.CODE_FAILED);
        set.setReps(Constant.CODE_FAILED);
        set.setDuration(null);
        set.setDifficulty(10);

        sets.add(set);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed()
    {
        // Send the new sets back to the exercise listener so we can use them later.
        Intent intent = new Intent();
        intent.putExtra(Constant.BUNDLE_EXERCISE_POSITION, position);
        intent.putExtra(Constant.BUNDLE_EXERCISE_SETS, sets);
        setResult(Constant.REQUEST_CODE_STAT, intent);
        finish();

        super.onBackPressed();
    }
}