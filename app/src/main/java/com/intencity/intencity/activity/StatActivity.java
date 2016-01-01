package com.intencity.intencity.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

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

//        setListViewHeight();
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

    /**
     * Sets the ListView height. This is needed because we use a ListView inside of a
     * RecyclerView. It is not recommended to do this, but it is needed so we don't need
     * to keep track of the indexes of different fragments.
     */
//    private void setListViewHeight()
//    {
//        ExerciseSetAdapter adapter = entity.getAdapters().get(position);
//        View listItem = adapter.getView();
//        listItem.measure(0, 0);
//
//        int height = listItem.getMeasuredHeight();
//        int count = adapter.getItemCount();
//
//        // Set a new height for the layout.
//        // If we don't do this, the height is 0 because we are inside a RecyclerView.
//        setsListView.getLayoutParams().height = height * count;
//    }
}