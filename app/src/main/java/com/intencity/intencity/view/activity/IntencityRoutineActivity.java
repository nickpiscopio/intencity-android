package com.intencity.intencity.view.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.RoutineAdapter;
import com.intencity.intencity.model.RoutineRow;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * This is the Intencity Routine activity.
 *
 * Created by Nick Piscopio on 5/6/16.
 */
public class IntencityRoutineActivity extends AppCompatActivity
{
    private FloatingActionButton start;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intencity_routine);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        start = (FloatingActionButton) findViewById(R.id.button_next);
        start.setOnClickListener(startExerciseClickListener);

        Bundle bundle = getIntent().getExtras();

        ArrayList<RoutineRow> rows = bundle.getParcelableArrayList(Constant.BUNDLE_ROUTINE_ROWS);

        RoutineAdapter adapter = new RoutineAdapter(getApplicationContext(), R.layout.list_item_header, android.R.layout.simple_list_item_single_choice, rows);

        ListView listView = (ListView) findViewById(R.id.list_view);
//        listView.addHeaderView(getLayoutInflater().inflate(R.layout.routine_header, null), null, false);
        listView.setOnItemClickListener(routineClickListener);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * The click listener for when a routine is clicked in the ListView.
     */
    private AdapterView.OnItemClickListener routineClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            start.setVisibility(View.VISIBLE);
        }
    };

    /**
     * The start exercising click listener.
     */
    private View.OnClickListener startExerciseClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            // TODO: Start exercising.
        }
    };
}