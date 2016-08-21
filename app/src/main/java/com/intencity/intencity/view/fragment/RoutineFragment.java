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
import com.intencity.intencity.helper.doa.UserRoutineDao;
import com.intencity.intencity.listener.DatabaseListener;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.LoadingListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.Exercise;
import com.intencity.intencity.model.RoutineSection;
import com.intencity.intencity.model.SelectableListItem;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.notification.ToastDialog;
import com.intencity.intencity.task.GetExerciseTask;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.RoutineState;
import com.intencity.intencity.util.RoutineType;
import com.intencity.intencity.util.SecurePreferences;
import com.intencity.intencity.util.Util;
import com.intencity.intencity.view.activity.EquipmentActivity;
import com.intencity.intencity.view.activity.RoutineIntencityActivity;
import com.intencity.intencity.view.activity.RoutineSavedActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Routine Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class RoutineFragment extends android.support.v4.app.Fragment implements DatabaseListener,
                                                                                DialogListener
{
    private Context context;

    private ListView listView;

    private int routineState;
    private String routineName;
    private ArrayList<Exercise> previousExercises;
    private int index;

    private LoadingListener listener;

    private String email;

    private RoutineSectionAdapter adapter;

    private Map<Integer, RoutineSection> sectionMap;
    private ArrayList<RoutineSection> sections;

    private int sectionSelected;

    private SecurePreferences securePreferences;

    private ToastDialog toastDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);
        View header = inflater.inflate(R.layout.list_item_routine_header, null);

        listView = (ListView) view.findViewById(R.id.list_view);

        context = getContext();

        email = Util.getSecurePreferencesEmail(context);

        sectionMap = new TreeMap<>();
        sections = new ArrayList<>();

        // Gets routines from the device database if it has any.
        // This will be added to the CONTINUE Card.
        new GetExerciseTask(context, this).execute();

        adapter  = new RoutineSectionAdapter(context, R.layout.list_item_routine, sections);
        listView.setAdapter(adapter);
        listView.addHeaderView(header, null, false);
        listView.setOnItemClickListener(routineClickListener);

        initRoutineCards();

        showEquipmentToastIfNeeded();

        return view;
    }

    @Override
    public void onDestroy()
    {
        if (toastDialog != null)
        {
            toastDialog.dismiss();
        }

        super.onDestroy();
    }

    /**
     * Initializes the routine cards.
     */
    public void initRoutineCards()
    {
        listener.onStartLoading();

        // Clear the entire map because we are reinitializing it.
        sectionMap.clear();

        int size = sections.size();
        if (size > 0 && sections.get(RoutineType.CONTINUE.ordinal()).getType() == RoutineType.CONTINUE)
        {
            // Only reinsert the first section because it is the CONTINUE card.
            insertSection(RoutineType.CONTINUE, sections.get(RoutineType.CONTINUE.ordinal()), false);
        }

        // Clear the entire section array because we are reinitializing it.
        sections.clear();

        insertSection(RoutineType.CUSTOM_ROUTINE, new RoutineSection(RoutineType.CUSTOM_ROUTINE, getString(R.string.title_custom_routine), null), true);

        // Get the Featured Routines
        new ServiceTask(intencityRoutineServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                                 Constant.generateStoredProcedureParameters(
                                                                    Constant.STORED_PROCEDURE_GET_ALL_DISPLAY_MUSCLE_GROUPS,
                                                                    email));

        // Get the Saved Routines
        new ServiceTask(savedRoutineServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                                                 Constant.generateStoredProcedureParameters(
                                                                    Constant.STORED_PROCEDURE_GET_USER_ROUTINE,
                                                                    email));
    }

    /**
     * Checks if the user set his or her equipment yet. If not, we show the set equipment toast.
     */
    private void showEquipmentToastIfNeeded()
    {
        securePreferences = new SecurePreferences(context);

        boolean userHasSetEquipment = securePreferences.getBoolean(Constant.USER_SET_EQUIPMENT, false);

        if (!userHasSetEquipment)
        {
            CustomDialogContent content = new CustomDialogContent(context.getString(R.string.title_set_equipment));
            content.setPositiveButtonStringRes(R.string.title_button_set_equipment);

            toastDialog = new ToastDialog(context, content, this);
        }
    }

    /**
     * The service listener for getting the Intencity Routine names.
     */
    private ServiceListener intencityRoutineServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            try
            {
                insertSection(RoutineType.FEATURED_ROUTINE, new RoutineSection(RoutineType.FEATURED_ROUTINE, getString(R.string.title_featured_routines), new IntencityRoutineDao().parseJson(context, response)), true);

                listener.onFinishedLoading(Constant.CODE_NULL);
            }
            catch (JSONException e)
            {
                Log.e(Constant.TAG, "Error parsing muscle group data " + e.toString());

                listener.onFinishedLoading((int) Constant.CODE_FAILED);
            }
        }

        @Override
        public void onRetrievalFailed()
        {
            listener.onFinishedLoading((int) Constant.CODE_FAILED);
        }
    };

    /**
     * The service listener for getting the Intencity Routine names.
     */
    private ServiceListener savedRoutineServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            try
            {
                if (!response.equalsIgnoreCase(Constant.RETURN_NULL))
                {
                    insertSection(RoutineType.SAVED_ROUTINE, new RoutineSection(RoutineType.SAVED_ROUTINE, getString(R.string.title_saved_routines), new UserRoutineDao().parseJson(response)), true);
                }

                listener.onFinishedLoading(Constant.CODE_NULL);
            }
            catch (JSONException e)
            {
                Log.e(Constant.TAG, "Error parsing muscle group data " + e.toString());

                listener.onFinishedLoading((int) Constant.CODE_FAILED);
            }
        }

        @Override
        public void onRetrievalFailed()
        {
            listener.onFinishedLoading((int) Constant.CODE_FAILED);
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

                case FEATURED_ROUTINE:

                    routineState = RoutineState.INTENCITY;

                    startActivity(RoutineIntencityActivity.class, rows);

                    break;

                case SAVED_ROUTINE:

                    routineState = RoutineState.SAVED;

                    startActivity(RoutineSavedActivity.class, rows);

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Starts an activity.
     *
     * @param cls       The class to start.
     * @param rows      The array list of rows for the listview in the new class.
     */
    private void startActivity(Class cls, ArrayList<?> rows)
    {
        Intent intent = new Intent(context, cls);
        intent.putExtra(Constant.BUNDLE_ROUTINE_ROWS, rows);

        startActivityForResult(intent, Constant.REQUEST_ROUTINE_UPDATED);
    }

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

            insertSection(RoutineType.CONTINUE, new RoutineSection(RoutineType.CONTINUE, getString(R.string.routine_continue, routineName.toUpperCase()), null), true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constant.REQUEST_ROUTINE_UPDATED || resultCode == Constant.REQUEST_SAVED_ROUTINE_UPDATED)
        {
            ArrayList<SelectableListItem> rows = data.getParcelableArrayListExtra(Constant.BUNDLE_ROUTINE_ROWS);

            sections.remove(sectionSelected);

            if (resultCode == Constant.REQUEST_ROUTINE_UPDATED)
            {
                sections.add(sectionSelected, new RoutineSection(RoutineType.FEATURED_ROUTINE, getString(R.string.title_featured_routines), rows));
            }
            else
            {
                if (rows.size() > 0)
                {
                    sections.add(sectionSelected, new RoutineSection(RoutineType.SAVED_ROUTINE, getString(R.string.title_saved_routines), rows));
                }
            }

            adapter.notifyDataSetChanged();
        }
        else if (resultCode == Constant.REQUEST_START_EXERCISING)
        {
            index = 0;
            routineName = data.getStringExtra(Constant.BUNDLE_ROUTINE_NAME);
            previousExercises = data.getParcelableArrayListExtra(Constant.BUNDLE_EXERCISE_LIST);

            listener.onFinishedLoading(Constant.ID_FRAGMENT_EXERCISE_LIST);
        }
    }

    /**
     * Inserts a routine section into the map and then into the section ArrayList.
     * We do this so we can give an order to the sections.
     *
     * @param routineType       The RoutineType of the section. This will be it's index.
     * @param routineSection    The RoutineSection we are adding.
     * @param notifyChange      Boolean value of whether we are notifying the adapter that we've added an item.
     */
    private void insertSection(RoutineType routineType, RoutineSection routineSection, boolean notifyChange)
    {
        sectionMap.put(routineType.ordinal(), routineSection);

        // sort and add to sections.
        sections.clear();
        sections.addAll(new ArrayList<>(sectionMap.values()));

        if (notifyChange)
        {
            adapter.notifyDataSetChanged();
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

    @Override
    public void onButtonPressed(int which)
    {
        switch (which)
        {
            case Constant.POSITIVE_BUTTON:
                startActivity(new Intent(context, EquipmentActivity.class));
                break;
            default:
                break;
        }
    }
}