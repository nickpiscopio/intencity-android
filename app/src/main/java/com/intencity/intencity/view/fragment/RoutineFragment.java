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
        View header = inflater.inflate(R.layout.routine_header, null);
        View footer = inflater.inflate(R.layout.routine_footer, null);

        listView = (ListView) view.findViewById(R.id.list_view);

//        Bundle bundle = getArguments();
//
//        if (bundle != null)
//        {
//            sections = bundle.getParcelableArrayList(Constant.BUNDLE_ROUTINE_SECTIONS);
//            routineName = bundle.getString(Constant.BUNDLE_ROUTINE_NAME);
//            previousExercises = bundle.getParcelableArrayList(Constant.BUNDLE_EXERCISE_LIST);
//            index = bundle.getInt(Constant.BUNDLE_EXERCISE_LIST_INDEX);
//            recommendedRoutine = bundle.getInt(Constant.BUNDLE_RECOMMENDED_ROUTINE);
//        }

        context = getContext();

        email = Util.getSecurePreferencesEmail(context);

        initRoutineCards();

        getMuscleGroups();

        // Gets routines from the device database if it has any.
        // This will be added to the CONTINUE Card.
        new GetExerciseTask(context, this).execute();

        adapter  = new RoutineSectionAdapter(context, R.layout.list_item_routine_continue, R.layout.list_item_routine, sections);
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
     * The service listener for setting the routine.
     */
    public ServiceListener routineServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            new ServiceTask(exerciseServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                             Constant.generateStoredProcedureParameters(
                                                                     Constant.STORED_PROCEDURE_GET_EXERCISES_FOR_TODAY,
                                                                     email));
        }

        @Override
        public void onRetrievalFailed()
        {
            listener.onFinishedLoading(Constant.CODE_FAILED_REPOPULATE);
        }
    };

    /**
     * The service listener for getting the exercise list.
     */
    public ServiceListener exerciseServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            ExerciseDao dao = new ExerciseDao();
            ArrayList<Exercise> exercises = new ArrayList<>();

            try
            {
                previousExercises = exercises;
                index = 0;

                // We are adding a stretch to the exercise list.
                Exercise stretch = dao.getNewExercise(context.getString(R.string.stretch),
                                                      Constant.RETURN_NULL, Constant.RETURN_NULL,
                                                      Constant.RETURN_NULL, Constant.RETURN_NULL, true);
                stretch.setDescription(context.getString(R.string.stretch_description));

                // We are adding a warm-up to the exercise list.
                exercises.add(getWarmUp(dao));
                exercises.addAll(dao.parseJson(response, ""));
                exercises.add(stretch);

                listener.onFinishedLoading(Constant.ID_FRAGMENT_EXERCISE_LIST);
            }
            catch (JSONException e)
            {
                e.printStackTrace();

                listener.onFinishedLoading(Constant.CODE_FAILED_REPOPULATE);
            }
        }

        @Override
        public void onRetrievalFailed()
        {
            listener.onFinishedLoading(Constant.CODE_FAILED_REPOPULATE);
        }
    };

    /**
     * Gets a warm up exercise to add to the exercise list.
     *
     * @param dao   An instance of the ExerciseDao.
     *
     * @return  The warm up exercise.
     */
    private Exercise getWarmUp(ExerciseDao dao)
    {
        Exercise warmUp = dao.getNewExercise(context.getString(R.string.warm_up),
                                             Constant.RETURN_NULL,
                                             Constant.RETURN_NULL,
                                             Constant.RETURN_NULL,
                                             Constant.RETURN_NULL,
                                             true);

        warmUp.setDescription(context.getString(R.string.warm_up_description));

        return warmUp;
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

            Intent intent = null;

            switch (sectionType)
            {
                case CONTINUE:

                    listener.onFinishedLoading(Constant.ID_FRAGMENT_EXERCISE_LIST);

                    break;

                case CUSTOM_ROUTINE:

                    routineName = getString(R.string.title_custom_routine);
                    previousExercises = new ArrayList<>();
                    previousExercises.add(getWarmUp(new ExerciseDao()));

                    index = 1;

                    routineState = RoutineState.CUSTOM;

                    listener.onFinishedLoading(Constant.ID_FRAGMENT_EXERCISE_LIST);

                    break;

                case INTENCITY_ROUTINE:

                    intent = new Intent(context, IntencityRoutineActivity.class);
                    intent.putExtra(Constant.BUNDLE_ROUTINE_ROWS, rows);

                    routineState = RoutineState.INTENCITY;

                    break;

                case SAVED_ROUTINE:
                    break;
                default:
                    break;
            }

            if (intent != null)
            {
                startActivityForResult(intent, Constant.REQUEST_ROUTINE_UPDATED);
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
            sections.add(new RoutineSection(RoutineType.INTENCITY_ROUTINE, getString(R.string.title_intencity_routines), new int[] { RoutineKey.USER_SELECTED, RoutineKey.RANDOM }, rows));

            adapter.notifyDataSetChanged();
        }
        else if (resultCode == Constant.REQUEST_START_EXERCISING_INTENCITY_ROUTINE)
        {
            routineName = data.getStringExtra(Constant.BUNDLE_ROUTINE_NAME);
            int position = data.getIntExtra(Constant.BUNDLE_POSITION, (int)Constant.CODE_FAILED);

            String routine = String.valueOf(position);
            String storedProcedureParameters = Constant.generateStoredProcedureParameters(
                    Constant.STORED_PROCEDURE_SET_CURRENT_MUSCLE_GROUP, email, routine);

            listener.onStartLoading();

            new ServiceTask(routineServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                            storedProcedureParameters);
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

    public void setRoutineName(String routineName)
    {
        this.routineName = routineName;
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

    public void setRecommendedRoutine(int recommendedRoutine)
    {
        this.recommendedRoutine = recommendedRoutine;
    }
}