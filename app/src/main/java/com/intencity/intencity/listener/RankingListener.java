package com.intencity.intencity.listener;

import android.view.View;

/**
 * A listener to for the ranking list..
 *
 * Created by Nick Piscopio on 1/27/16.
 */
public interface RankingListener
{
    void onRemoveUser(View view, int position, int webServerId);
}