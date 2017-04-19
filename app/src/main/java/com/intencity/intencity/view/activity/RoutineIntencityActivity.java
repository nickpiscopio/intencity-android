package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.intencity.intencity.R;
import com.intencity.intencity.adapter.RoutineAdapter;
import com.intencity.intencity.helper.GoogleGeocode;
import com.intencity.intencity.helper.doa.ExerciseDao;
import com.intencity.intencity.helper.doa.FitnessLocationDao;
import com.intencity.intencity.helper.doa.IntencityRoutineDao;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.GeocodeListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.SelectableListItem;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.SelectionType;
import com.intencity.intencity.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is the Intencity Routine activity.
 *
 * Created by Nick Piscopio on 5/6/16.
 */
public class RoutineIntencityActivity extends AppCompatActivity implements ServiceListener,
                                                                           GeocodeListener
{
    private final int REQUEST_CODE_ADDRESS = 20;

    private GoogleGeocode googleGeocode;

    private Context context;

    private ProgressBar progressBar;

    private LinearLayout connectionIssueLayout;

    private ListView listView;

    private FloatingActionButton start;

    private String email;
    private String currentUserLocation;

    private RoutineAdapter adapter;

    private ArrayList<SelectableListItem> rows;

    private int routineSelected;

    private boolean hasMoreExercises = false;

    private FitnessLocationDao fitnessLocationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_intencity);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        context = getApplicationContext();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_loading);
        connectionIssueLayout = (LinearLayout) findViewById(R.id.layout_connection_issue);
        start = (FloatingActionButton) findViewById(R.id.button_add);
        start.setOnClickListener(startExerciseClickListener);

        Bundle bundle = getIntent().getExtras();

        rows = bundle.getParcelableArrayList(Constant.BUNDLE_ROUTINE_ROWS);

        email = Util.getSecurePreferencesUserId(context);

        View header = getLayoutInflater().inflate(R.layout.list_item_header_title_description, null);
        header.findViewById(R.id.divider).setVisibility(View.GONE);

        TextView title = (TextView) header.findViewById(R.id.title);
        TextView description = (TextView) header.findViewById(R.id.description);

        title.setText(context.getString(R.string.title_routine));
        description.setText(context.getString(R.string.intencity_routine_description));

        listView = (ListView) findViewById(R.id.list_view);
        listView.addHeaderView(header, null, false);
        listView.setOnItemClickListener(routineClickListener);

        fitnessLocationDao = new FitnessLocationDao(fitnessLocationServiceListener, email);

        populateRoutineList();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        googleGeocode = new GoogleGeocode(this, this);
    }

    @Override
    protected void onPause()
    {
        googleGeocode.onDestroy();

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        googleGeocode.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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
                Intent intent = new Intent(context, RoutineIntencityEditActivity.class);
                startActivityForResult(intent, Constant.REQUEST_CODE_ROUTINE_UPDATED);
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
        showLoading();

        new ServiceTask(this).execute(Constant.SERVICE_EXECUTE_STORED_PROCEDURE, Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_ALL_DISPLAY_MUSCLE_GROUPS, email));
    }

    /**
     * Populates the routine list.
     */
    private void populateRoutineList()
    {
        adapter = new RoutineAdapter(context, R.layout.list_item_header, R.layout.list_item_standard_radio_button, rows);

        listView.setAdapter(adapter);
    }

    /**
     * Shows the loading view.
     */
    private void showLoading()
    {
        progressBar.setVisibility(View.VISIBLE);
        connectionIssueLayout.setVisibility(View.GONE);

        start.setEnabled(false);
    }

    /**
     * Hides the loading view.
     */
    private void hideLoading()
    {
        progressBar.setVisibility(View.GONE);

        start.setEnabled(true);
    }

    /**
     * Shows the connection issue view.
     */
    private void showConnectionIssue()
    {
        hideLoading();

        connectionIssueLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Displays a generic error to the user stating Intencity couldn't connect to the server.
     */
    private void showConnectionIssueDialog()
    {
        CustomDialogContent dialog = new CustomDialogContent(context.getString(R.string.generic_error), context.getString(R.string.intencity_communication_error), false);

        new CustomDialog(RoutineIntencityActivity.this, dialogListener, dialog, false);

        hideLoading();
    }

    /**
     * Sets the flag to select or deselect a row.
     *
     * @param type      The type of selection we want to do.
     * @param item      The list item to select or deselect.
     */
    private void setSelection(SelectionType type, SelectableListItem item)
    {
        item.setSelected(type == SelectionType.SELECT);
    }

    /**
     * Opens the fitness location screen so the user can select his or her fitness location.
     * We do this because the location we found didn't match what the user had.
     *
     * @param locations     The user's fitness locations to send to the fitness location screen so we don't need to get them again.
     */
    private void openFitnessLocation(ArrayList<SelectableListItem> locations)
    {
        Intent intent = new Intent(context, FitnessLocationActivity.class);
        intent.putExtra(Constant.BUNDLE_FITNESS_LOCATION_SELECT, true);
        intent.putExtra(Constant.BUNDLE_FITNESS_LOCATIONS, locations);
        RoutineIntencityActivity.this.startActivityForResult(intent, Constant.REQUEST_CODE_FITNESS_LOCATION);
    }

    /**
     * Starts the service to start exercising.
     */
    private void startServiceToStartExercising()
    {
        SelectableListItem row = rows.get(routineSelected);

        String routine = String.valueOf(row.getRowNumber());
        String storedProcedureParameters = Constant.generateStoredProcedureParameters(Constant.STORED_PROCEDURE_GET_ROUTINE_EXERCISES,
                                                                                      email, currentUserLocation, routine);

        new ServiceTask(exerciseServiceListener).execute(Constant.SERVICE_EXECUTE_STORED_PROCEDURE,
                                                         storedProcedureParameters);
    }

    /**
     * The click listener for when a routine is clicked in the ListView.
     */
    private AdapterView.OnItemClickListener routineClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            int clickedPosition = position - 1;

            SelectableListItem selectedItem = rows.get(clickedPosition);
            if (selectedItem.getRowNumber() > Constant.CODE_FAILED)
            {
                // We deselect the old row.
                setSelection(SelectionType.DESELECT, rows.get(routineSelected));

                // We select the new row.
                routineSelected = clickedPosition;
                setSelection(SelectionType.SELECT, selectedItem);

                adapter.notifyDataSetChanged();

                start.setVisibility(View.VISIBLE);
            }
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
            showLoading();

            // Get location of user since there wasn't one already.
            googleGeocode.checkLocationPermission(REQUEST_CODE_ADDRESS);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constant.REQUEST_CODE_ROUTINE_UPDATED)
        {
            getRoutines();
        }
        else if (resultCode == Constant.REQUEST_CODE_FITNESS_LOCATION)
        {
            showLoading();

            currentUserLocation = data.getStringExtra(Constant.BUNDLE_FITNESS_LOCATION);

            startServiceToStartExercising();
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

            setResult(Constant.REQUEST_CODE_ROUTINE_UPDATED, intent);
        }

        super.onBackPressed();
    }

    /**
     * The service listener for getting the exercise list.
     */
    public ServiceListener exerciseServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            ExerciseDao dao = new ExerciseDao(context);
            ArrayList<Exercise> exercises = new ArrayList<>();

            try
            {
                // We are adding a warm-up to the exercise list.
                exercises.add(dao.getInjuryPreventionExercise(ExerciseDao.ExerciseType.WARM_UP));
                exercises.addAll(dao.parseJson(response, ""));
                exercises.add(dao.getInjuryPreventionExercise(ExerciseDao.ExerciseType.STRETCH));

                SelectableListItem row = rows.get(routineSelected);

                Intent intent = new Intent();
                intent.putExtra(Constant.BUNDLE_ROUTINE_NAME, row.getTitle());
                intent.putExtra(Constant.BUNDLE_EXERCISE_LIST, exercises);

                setResult(Constant.REQUEST_CODE_START_EXERCISING, intent);
                finish();
            }
            catch (JSONException e)
            {
                Log.e(Constant.TAG, e.getMessage());

                showConnectionIssue();
            }
        }

        @Override
        public void onRetrievalSuccessful(int statusCode, String response)
        {

        }

        @Override
        public void onRetrievalFailed(int statusCode)
        {
            showConnectionIssue();
        }
    };

    /**
     * The service listener for getting the user's fitness locations.
     */
    public ServiceListener fitnessLocationServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            ArrayList<SelectableListItem> locations = new ArrayList<>();

            try
            {
                locations.addAll(fitnessLocationDao.parseJson(response, currentUserLocation,
                                                              SelectableListItem.ListItemType.TYPE_RADIO_BUTTON));

                if (fitnessLocationDao.hasValidFitnessLocation())
                {
                    startServiceToStartExercising();
                }
                else
                {
                    // The location wasn't valid, so we need to notify the user.
                    openFitnessLocation(locations);

                    hideLoading();
                }
            }
            catch (JSONException e)
            {
                Log.e(Constant.TAG, "Couldn't parse user locations. " + e.toString());

                openFitnessLocation(null);

                hideLoading();
            }
        }

        @Override
        public void onRetrievalSuccessful(int statusCode, String response)
        {

        }

        @Override
        public void onRetrievalFailed(int statusCode)
        {
            showConnectionIssue();
        }
    };

    @Override
    public void onRetrievalSuccessful(String response)
    {
        try
        {
            rows.clear();
            rows.addAll(new IntencityRoutineDao().parseJson(context, response));

            adapter.notifyDataSetChanged();

            hasMoreExercises = true;

            hideLoading();
        }
        catch (JSONException e)
        {
            showConnectionIssueDialog();
        }
    }

    @Override
    public void onRetrievalSuccessful(int statusCode, String response)
    {

    }

    @Override
    public void onRetrievalFailed(int statusCode)
    {
        showConnectionIssueDialog();
    }

    @Override
    public void onGoogleApiClientConnected(int requestCode, GoogleApiClient googleApiClient, Location location)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_ADDRESS:
                googleGeocode.getLastLocationAddress(REQUEST_CODE_ADDRESS, location);
                break;
            default:
                break;
        }
    }

    @Override
    public void onGeocodeRetrievalSuccessful(int requestCode, Object obj)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_ADDRESS:
                currentUserLocation = (String)obj;

                // See if the currentUserLocation is in our list.
                fitnessLocationDao.getFitnessLocations();

                break;

            default:
                break;
        }
    }

    @Override
    public void onGeocodeRetrievalFailed(int requestCode)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_ADDRESS:

                // Failed to retrieve the fitness location, so open the fitness location screen for the user to select it.
                openFitnessLocation(null);

            default:
                hideLoading();
                break;
        }
    }

    /**
     * This gets called by GoogleGeocode when we have determined that the location services have been enabled.
     */
    @Override
    public void onLocationServiceEnabled()
    {
        showLoading();
    }

    /**
     * This gets called by GoogleGeocode when we have determined that the location services have not been enabled.
     */
    @Override
    public void onLocationServiceNotEnabled(int requestCode)
    {
        switch (requestCode)
        {
            case GoogleGeocode.REQUEST_CODE_CANCELED:

                // The user decided he or she didn't want to set location services.
                // Just open the fitness location for the user to select one manually.
                openFitnessLocation(null);

                // Fall through to stop the loading.

            case GoogleGeocode.LOCATION_NOT_AVAILABLE:
            case GoogleGeocode.REQUEST_CODE_PERMISSION_NEEDED:
            default:
                hideLoading();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, FragmentActivity activity)
    {
        onLocationServiceNotEnabled(GoogleGeocode.REQUEST_CODE_CANCELED);
    }
}