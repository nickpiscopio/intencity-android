package com.intencity.intencity.handler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.intencity.intencity.R;
import com.intencity.intencity.util.Constant;

/**
 * Handles the fragments for Intencity.
 *
 * Created by Nick Piscopio on 12/13/15.
 */
public class FragmentHandler
{
    public static FragmentHandler fragmentHandler = null;

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

        if (bundle != null)
        {
            fragmentToAdd.setArguments(bundle);
        }

        // Remove the routine fragment if it already exists.
        if (tag.equalsIgnoreCase(Constant.FRAGMENT_ROUTINE))
        {
            Fragment fragment = manager.findFragmentByTag(tag);
            if (fragment != null)
            {
                transaction.remove(fragment);
            }
        }

        if (replace)
        {
            transaction.replace(parent, fragmentToAdd, tag);
        }
        else
        {
            transaction.add(parent, fragmentToAdd, tag);
        }

        // Changing the way this is committed as per the descriptions from the below URL:
        // http://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-wit
        transaction.commitAllowingStateLoss();
    }
}