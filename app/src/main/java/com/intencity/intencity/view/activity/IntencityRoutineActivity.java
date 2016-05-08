package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.RoutineAdapter;
import com.intencity.intencity.helper.doa.IntencityRoutineDao;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.RoutineRow;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.Util;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * This is the Intencity Routine activity.
 *
 * Created by Nick Piscopio on 5/6/16.
 */
public class IntencityRoutineActivity extends AppCompatActivity implements ServiceListener
{
    private Context context;

    private ListView listView;

    private FloatingActionButton start;

    private String email;

    private RoutineAdapter adapter;

    private ArrayList<RoutineRow> rows;

    private int routineSelected;

    private boolean hasMoreExercises = false;

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

        context = getApplicationContext();

        start = (FloatingActionButton) findViewById(R.id.button_next);
        start.setOnClickListener(startExerciseClickListener);

        Bundle bundle = getIntent().getExtras();

        rows = bundle.getParcelableArrayList(Constant.BUNDLE_ROUTINE_ROWS);

        email = Util.getSecurePreferencesEmail(context);

        View header = getLayoutInflater().inflate(R.layout.header_title_description, null);

        TextView title = (TextView) header.findViewById(R.id.title);
        TextView description = (TextView) header.findViewById(R.id.description);

        title.setText(context.getString(R.string.title_routine));
        description.setText(context.getString(R.string.intencity_routine_description));

        listView = (ListView) findViewById(R.id.list_view);
        listView.addHeaderView(header, null, false);
        listView.setOnItemClickListener(routineClickListener);
        listView.setHeaderDividersEnabled(false);

        populateRoutineList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routine_menu, menu);

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
            case R.id.edit:
                Intent intent = new Intent(context, EditIntencityRoutineActivity.class);
                startActivityForResult(intent, Constant.REQUEST_ROUTINE_UPDATED);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * Gets the routines from the server.
     */
    private void getRoutines()
    {
        new ServiceTask(this).execute(Constant.SERVICE_STORED_PROCEDURE, Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_ALL_DISPLAY_MUSCLE_GROUPS, email));
    }

    /**
     * Populates the routine list.
     */
    private void populateRoutineList()
    {
        adapter = new RoutineAdapter(context, R.layout.list_item_header, android.R.layout.simple_list_item_single_choice, rows);

        listView.setAdapter(adapter);
    }

    /**
     * Displays a generic error to the user stating Intencity couldn't connect to the server.
     */
    private void showConnectionIssue()
    {
        CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), context.getString(R.string.intencity_communication_error), false);

        new CustomDialog(IntencityRoutineActivity.this, dialogListener, dialog, false);
    }

    /**
     * The click listener for when a routine is clicked in the ListView.
     */
    private AdapterView.OnItemClickListener routineClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            routineSelected = position - 1;

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
            RoutineRow row = rows.get(routineSelected);

            Intent intent = new Intent();
            intent.putExtra(Constant.BUNDLE_ROUTINE_NAME, row.getTitle());
            intent.putExtra(Constant.BUNDLE_POSITION, row.getRowNumber());

            setResult(Constant.REQUEST_START_EXERCISING_INTENCITY_ROUTINE, intent);
            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constant.REQUEST_ROUTINE_UPDATED)
        {
            getRoutines();
        }
    }

    /**
     * The dialog listener for when the connection to Intencity fails.
     */
    private DialogListener dialogListener = new DialogListener()
    {
        @Override
        public void onButtonPressed(int which)
        {
            // There is only 1 button that can be pressed, so we aren't going to switch on it.
            finish();
        }
    };

    @Override
    public void onBackPressed()
    {
        if (hasMoreExercises)
        {
            Intent intent = new Intent();
            intent.putExtra(Constant.BUNDLE_ROUTINE_ROWS, rows);

            setResult(Constant.REQUEST_ROUTINE_UPDATED, intent);
        }

        super.onBackPressed();
    }

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            rows.clear();
            rows.addAll(new IntencityRoutineDao().parseJson(context, response));

            adapter.notifyDataSetChanged();

            hasMoreExercises = true;
        }
        catch (JSONException e)
        {
            showConnectionIssue();
        }
    }

    @Override
    public void onRetrievalFailed()
    {
        showConnectionIssue();
    }
}