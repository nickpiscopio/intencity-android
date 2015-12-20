package com.intencity.intencity.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.intencity.intencity.R;
import com.intencity.intencity.fragment.CardFragmentExercise;
import com.intencity.intencity.fragment.CardFragmentExerciseStat;
import com.intencity.intencity.listener.FragmentListener;

import java.util.List;

/**
 * Handles the fragments for Intencity.
 *
 * Created by Nick Piscopio on 12/13/15.
 */
public class FragmentHandler
{
    public static FragmentHandler fragmentHandler = null;

    private FragmentListener fragmentListener;

    /**
     * Gets the instance of the singleton for the FragmentHandler.
     *
     * @return  The singleton instance of the FragmentHandler.
     */
    public static FragmentHandler getInstance()
    {
        if (fragmentHandler == null)
        {
            fragmentHandler = new FragmentHandler();
        }

        return fragmentHandler;
    }

    /**
     * Animates a fragment to be visible on the screen.
     *
     * @param manager           The FragmentManager.
     * @param parent            The parent to push the fragment to.
     * @param fragmentToAdd     The fragment to add to the parent.
     * @param invert            Inverts the way the view is added. True will make the view come from the top.
     * @param bundle            The information to send to the next fragment.
     * @param replace           Boolean on whether to replace the previous fragment.
     */
    public void pushFragment(FragmentManager manager, int parent, Fragment fragmentToAdd, String tag, boolean invert, Bundle bundle, boolean replace)
    {
        FragmentTransaction transaction = manager.beginTransaction();

        if (invert)
        {
            transaction.setCustomAnimations(R.anim.anim_slide_in_down, R.anim.anim_slide_out_up);
        }
        else
        {
            transaction.setCustomAnimations(R.anim.anim_slide_in_up, R.anim.anim_slide_out_up);
        }

        if (replace)
        {
            List<Fragment> fragments = manager.getFragments();

            Fragment last = fragments.get(fragments.size() - 1);

            transaction.remove(last);
        }

        if (bundle != null)
        {
            fragmentToAdd.setArguments(bundle);
        }

        transaction.add(parent, fragmentToAdd, tag);
        transaction.commit();

        if (fragmentToAdd instanceof CardFragmentExercise ||
            fragmentToAdd instanceof CardFragmentExerciseStat)
        {
            fragmentListener.onFragmentAdded();
        }
    }

    /**
     * Remove a fragment from the fragment manager.
     *
     * @param manager   The FragmentManager.
     * @param tag       The tag of the fragment to remove.
     */
    public void removeFragment(FragmentManager manager, String tag)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(tag);
        transaction.remove(fragment);
        transaction.commit();
    }

    /**
     * The fragmentListener setter.
     *
     * @param fragmentListener  The fragmentListener to set.
     */
    public void setFragmentListener(FragmentListener fragmentListener)
    {
        this.fragmentListener = fragmentListener;
    }
}