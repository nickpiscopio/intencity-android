package com.intencity.intencity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.intencity.intencity.R;
import com.intencity.intencity.activity.AboutActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Menu Fragment for Intencity.
 *
 * Created by Nick Piscopio on 12/12/15.
 */
public class MenuFragment extends android.support.v4.app.Fragment
{
    private HashMap<String, Intent> menuMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_intencity_menu, container, false);

        ListView listView = (ListView) view.findViewById(R.id.list_view_menu);

        Context context = getContext();

        menuMap = new HashMap<>();
        menuMap.put(getString(R.string.title_about), new Intent(context, AboutActivity.class));

        ArrayList<String> menuTitles = new ArrayList<>();
        menuTitles.addAll(menuMap.keySet());

        ArrayAdapter<String> menuAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, menuTitles);
        listView.setAdapter(menuAdapter);
        listView.setOnItemClickListener(itemClickListener);

        return view;
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            String title = parent.getItemAtPosition(position).toString();

            // Sets the intent from the hashmap.
            Intent intent = menuMap.get(title);
            startActivity(intent);
        }
    };
}