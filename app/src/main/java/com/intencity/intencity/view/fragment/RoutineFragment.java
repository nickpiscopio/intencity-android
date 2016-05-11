package com.intencity.intencity.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.RoutineSectionAdapter;
import com.intencity.intencity.helper.doa.ExerciseDao;
import com.intencity.intencity.helper.doa.IntencityRoutineDao;
import com.intencity.intencity.listener.DatabaseListener;
import com.intencity.intencity.listener.LoadingListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.RoutineRow;
import com.intencity.intencity.model.RoutineSection;
import com.intencity.intencity.task.GetExerciseTask;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.RoutineKey;
import com.intencity.intencity.util.RoutineState;
import com.intencity.intencity.util.RoutineType;
import com.intencity.intencity.util.Util;
import com.intencity.intencity.view.activity.IntencityRoutineActivity;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * The Routine Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class RoutineFragment extends android.support.v4.app.Fragment implements DatabaseListener
{
    private Context context;

    private ListView listView;

    private int routineState;
    private String routineName;
    private ArrayList<Exercise> previousExercises;
    private int index;
    private int recommendedRoutine;

    private LoadingListener listener;

    private String email;

    private RoutineSectionAdapter adapter;

    private ArrayList<RoutineSection> sections;

    private int sectionSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);
        View header = inflater.inflate(R.layout.list_item_routine_header, null);
        View footer = inflater.inflate(R.layout.list_item_routine_footer, null);

        listView = (ListView) view.findViewById(R.id.list_view);

        context = getContext();

        email = Util.getSecurePreferencesEmail(context);

        initRoutineCards();

        getMuscleGroups();

        // Gets routines from the device database if it has any.
        // This will be added to the CONTINUE Card.
        new GetExerciseTask(context, this).execute();

        adapter  = new RoutineSectionAdapter(context, R.layout.list_item_routine, sections);
        listView.setAdapter(adapter);
        listView.addHeaderView(header, null, false);
        listView.addFooterView(footer, null, false);
        listView.setOnItemClickListener(routineClickListener);

        return view;
    }

    private void initRoutineCards()
    {
        sections = new ArrayList<>();
        sections.add(new RoutineSection(RoutineType.CUSTOM_ROUTINE, getString(R.string.title_custom_routine), new int[] { RoutineKey.USER_SELECTED }, null));
        sections.add(new RoutineSection(RoutineType.SAVED_ROUTINE, getString(R.string.title_saved_routines), new int[] { RoutineKey.USER_SELECTED, RoutineKey.CONSECUTIVE }, null));
    }

    /**
     * The click listener for the start button.
     */
    private View.OnClickListener startClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
//            int spinnerPosition = spinner.getSelectedItemPosition();
//
//            String routineSelection = spinner.getItemAtPosition(spinnerPosition).toString();

//            // If the user selects to continue from the last routine he or she chose.
//            if (routineSelection.equals(getString(R.string.routine_continue)))
//            {
//                listener.onFinishedLoading(Constant.ID_FRAGMENT_EXERCISE_LIST);
//            }
//            else
//            {
//                routineName = routineSelection;
//
//                String routine = String.valueOf(spinnerPosition + 1);
//                String storedProcedureParameters = Constant.generateStoredProcedureParameters(
//                        Constant.STORED_PROCEDURE_SET_CURRENT_MUSCLE_GROUP, email, routine);
//
//                listener.onStartLoading();
//
//                new ServiceTask(routineServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
//                                                                storedProcedureParameters);
//            }
        }
    };

    /**
     * Starts the service to get the muscle groups from the web server.
     */
    private void getMuscleGroups()
    {
        new ServiceTask(muscleGroupServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                            Constant.generateStoredProcedureParameters(
                                                                    Constant.STORED_PROCEDURE_GET_ALL_DISPLAY_MUSCLE_GROUPS,
                                                                    email));
    }

    /**
     * The service listener for populating the spinner with routine names.
     *
     * The routine name that gets set is the recommended routine name.
     */
    public ServiceListener muscleGroupServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            try
            {
                sections.add(new RoutineSection(RoutineType.INTENCITY_ROUTINE, getString(R.string.title_intencity_routines), new int[] { RoutineKey.USER_SELECTED, RoutineKey.RANDOM }, new IntencityRoutineDao().parseJson(context, response)));

//                if (pushedTryAgain)
//                {
//                    // Repopulate the spinner if the user gets their connection back
//                    try
//                    {
//                        //                        repopulateSpinner(sections);
//                    }
//                    catch (Exception e)
//                    {
//                        // Only add the saved exercises to the spinner because of the network issue.
//                        pushRoutineFragment(sections);
//                    }
//
//                    removeConnectionIssueMessage();
//                    stopLoading();
//                }
//                else
//                {
                    adapter.notifyDataSetChanged();
//                    pushRoutineFragment(sections);
//                }
            }
            catch (JSONException e)
            {
                Log.e(Constant.TAG, "Error parsing muscle group data " + e.toString());

//                onFinishedLoading(pushedTryAgain ? Constant.CODE_FAILED_REPOPULATE : (int) Constant.CODE_FAILED);
            }
        }

        @Override
        public void onRetrievalFailed()
        {
//            onFinishedLoading(pushedTryAgain ? Constant.CODE_FAILED_REPOPULATE : (int) Constant.CODE_FAILED);
        }
    };

    /**
     * The click listener for each item clicked in the settings list.
     */
    private AdapterView.OnItemClickListener routineClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            sectionSelected = position - 1;

            RoutineSection section = sections.get(sectionSelected);
            RoutineType sectionType = section.getType();
            ArrayList<?> rows = section.getRoutineRows();

            switch (sectionType)
            {
                case CONTINUE:

                    listener.onFinishedLoading(Constant.ID_FRAGMENT_EXERCISE_LIST);

                    break;

                case CUSTOM_ROUTINE:

                    ExerciseDao dao = new ExerciseDao(context);

                    routineName = getString(R.string.title_custom_routine);
                    previousExercises = new ArrayList<>();
                    previousExercises.add(dao.getInjuryPreventionExercise(ExerciseDao.ExerciseType.WARM_UP));

                    index = 0;

                    routineState = RoutineState.CUSTOM;

                    listener.onFinishedLoading(Constant.ID_FRAGMENT_EXERCISE_LIST);

                    break;

                case INTENCITY_ROUTINE:

                    Intent intent = new Intent(context, IntencityRoutineActivity.class);
                    intent.putExtra(Constant.BUNDLE_ROUTINE_ROWS, rows);

                    routineState = RoutineState.INTENCITY;

                    startActivityForResult(intent, Constant.REQUEST_ROUTINE_UPDATED);

                    break;

                case SAVED_ROUTINE:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    public void onRetrievalSuccessful(int routineState, String routineName, ArrayList<?> results, int index)
    {
        if (routineName != null)
        {
            this.index = index;
            this.routineState = routineState;
            this.routineName = routineName;
            this.previousExercises = (ArrayList<Exercise>)results;

            sections.add(0, new RoutineSection(RoutineType.CONTINUE, getString(R.string.routine_continue, routineName.toUpperCase()), null, null));

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constant.REQUEST_ROUTINE_UPDATED)
        {
            ArrayList<RoutineRow> rows = data.getParcelableArrayListExtra(Constant.BUNDLE_ROUTINE_ROWS);

            sections.remove(sectionSelected);
            sections.add(new RoutineSection(RoutineType.INTENCITY_ROUTINE, getString(R.string.title_intencity_routines),
                                            new int[] { RoutineKey.USER_SELECTED, RoutineKey.RANDOM }, rows));

            adapter.notifyDataSetChanged();
        }
        else if (resultCode == Constant.REQUEST_START_EXERCISING_INTENCITY_ROUTINE)
        {
            index = 0;
            routineName = data.getStringExtra(Constant.BUNDLE_ROUTINE_NAME);
            previousExercises = data.getParcelableArrayListExtra(Constant.BUNDLE_EXERCISE_LIST);

            listener.onFinishedLoading(Constant.ID_FRAGMENT_EXERCISE_LIST);
        }
    }

    /**
     * Setters and getters for the RoutineFragment.
     */
    public void setListener(LoadingListener listener)
    {
        this.listener = listener;
    }

    public String getRoutineName()
    {
        return routineName;
    }

    public ArrayList<Exercise> getPreviousExercises()
    {
        return previousExercises;
    }

    public int getIndex()
    {
        return index;
    }

    public int getRoutineState()
    {
        return routineState;
    }
}