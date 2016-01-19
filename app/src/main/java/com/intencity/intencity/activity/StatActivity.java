package com.intencity.intencity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.ExerciseSetAdapter;
import com.intencity.intencity.dialog.CustomDialog;
import com.intencity.intencity.dialog.Dialog;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.Set;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.Util;

import java.util.ArrayList;

/**
 * This is the set activity for an Exercise.
 *
 * Created by Nick Piscopio on 12/31/15.
 */
public class StatActivity extends AppCompatActivity implements DialogListener
{
    private final int REPS = 0;
    private final int TIME = 1;

    private int durationType;

    private TextView directions;
    private EditText notes;
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

        directions = (TextView) findViewById(R.id.text_view_directions);
        notes = (EditText) findViewById(R.id.edit_text_notes);
        setsListView = (ListView) findViewById(R.id.list_view);
        Spinner durationSpinner = (Spinner) findViewById(R.id.spinner_duration);

        directions.setOnClickListener(directionListener);
        notes.addTextChangedListener(textChangeListener);

        ActionBar actionBar = getSupportActionBar();

        Bundle bundle = getIntent().getExtras();

        exercise = bundle.getParcelable(Constant.BUNDLE_EXERCISE);
        position = bundle.getInt(Constant.BUNDLE_EXERCISE_POSITION);

        if(exercise != null)
        {
            exerciseName = exercise.getName();
            sets = exercise.getSets();

            // Set the notes to that of the first set.
            // All of them should be the same anyway.
            notes.setText(sets.get(0).getNotes());

            if (actionBar != null)
            {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(exerciseName);
            }
        }

        context = getApplicationContext();

        // Initialize the set adapter with the sets.
        initializeExerciseSetAdapter();

        // Populate the spinner.
        String[] spinnerValues = new String[] { context.getString(
                R.string.title_reps), context.getString(R.string.title_time) };
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(context, R.layout.spinner_duration, spinnerValues);
        durationAdapter.setDropDownViewResource(R.layout.spinner_item);

        durationSpinner.setAdapter(durationAdapter);

        scrollToBottom();

        durationType = sets.get(0).getDuration().contains(":") ? TIME : REPS;
        durationSpinner.setSelection(durationType, false);
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
            case R.id.add:
                // We only add a set if the user has set a duration.
                // This is because we don't want to add tons of sets that couldn't have happened.
                // If there isn't a duration, then the user didn't do the exercise.
                if (hasDuration())
                {
                    addSet();
                }
                else
                {
                    notifyToAddDuration(false);
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * The listener for the directions EditText.
     */
    private View.OnClickListener directionListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(context, Direction.class);
            intent.putExtra(Constant.BUNDLE_EXERCISE_NAME, exerciseName);
            startActivity(intent);
        }
    };

    /**
     * The text watcher for the notes.
     */
    private TextWatcher textChangeListener = new TextWatcher()
    {
        String beforeText = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            beforeText = String.valueOf(s);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s)
        {
            String value = String.valueOf(s);

            if (!beforeText.equals(value))
            {
                for (Set set : sets)
                {
                    set.setNotes(value);
                }
            }
        }
    };

    /**
     * The selection listener for the duration menu item.
     */
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
        public void onNothingSelected(AdapterView<?> parent) { }
    };

    /**
     * Initializes the set adapter for the ListView.
     */
    private void initializeExerciseSetAdapter()
    {
        adapter = new ExerciseSetAdapter(context, R.layout.fragment_exercise_set, sets);
        setsListView.setAdapter(adapter);
    }

    /**
     * Converts the sets to rep format.
     */
    private void setRepFormat()
    {
        durationType = REPS;

        if (sets.size() == 1)
        {
            initializeExerciseSetAdapter();
        }

        for (Set set : sets)
        {
            String duration = set.getDuration();

            int reps = Integer.parseInt(Util.getRepFormat(duration));
            set.setReps(reps);
            set.setDuration(Constant.RETURN_NULL);
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Converts the sets to a time format.
     */
    private void setTimeFormat()
    {
        durationType = TIME;

        if (sets.size() == 1)
        {
            initializeExerciseSetAdapter();
        }

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
     * Checks if the user ahs added a duration.
     * If he or she has, then the user can add another set.
     */
    private boolean hasDuration()
    {
        Set set = sets.get(sets.size() - 1);
        return set.getReps() > 0 || Integer.parseInt(Util.getRepFormat(set.getDuration())) > 0;
    }

    /**
     * Adds a set to the exercise and the ArrayList.
     */
    private void addSet()
    {
        Set set = new Set();
        set.setWeight(Constant.CODE_FAILED);

        switch (durationType)
        {
            case REPS: //Reps selected.
                set.setReps(0);
                set.setDuration(null);
                break;
            case TIME: // Time selected.
                set.setReps(Constant.CODE_FAILED);
                set.setDuration(Constant.DURATION_0);
                break;
            default:
                break;
        }

        set.setDifficulty(10);
        set.setNotes(notes.getText().toString());

        sets.add(set);

        adapter.notifyDataSetChanged();

        scrollToBottom();
    }

    /**
     * Scrolls to the bottom of the set list.
     */
    private void scrollToBottom()
    {
        setsListView.setSelection(adapter.getCount() - 1);
    }

    /**
     * Displays a dialog to tell the user to add a duration to the last set.
     */
    private void notifyToAddDuration(boolean notifyToRemoveLastSet)
    {
        if (notifyToRemoveLastSet)
        {
            new CustomDialog(StatActivity.this, StatActivity.this,
                             new Dialog(getString(R.string.title_add_set_error),
                                        new String[] { getString(R.string.button_remove_set), getString(android.R.string.cancel) }));
        }
        else
        {
            new CustomDialog(StatActivity.this, StatActivity.this,
                             new Dialog(getString(R.string.title_add_set_error),
                                        getString(R.string.message_add_set_error), false));
        }

    }

    /**
     * Dismisses the stat activity.
     */
    private void dismissActivity()
    {
        // Send the new sets back to the exercise listener so we can use them later.
        Intent intent = new Intent();
        intent.putExtra(Constant.BUNDLE_EXERCISE_POSITION, position);
        intent.putExtra(Constant.BUNDLE_EXERCISE_SETS, sets);
        setResult(Constant.REQUEST_CODE_STAT, intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        if (hasDuration())
        {
            dismissActivity();

            super.onBackPressed();
        }
        else
        {
            notifyToAddDuration(true);
        }
    }

    @Override
    public void onButtonPressed(int which)
    {
        // If removing the last set was clicked.
        if (which == 0)
        {
            if (sets.size() > 1)
            {
                // Remove the last set.
                sets.remove(sets.size() - 1);
            }

            dismissActivity();
        }
    }
}