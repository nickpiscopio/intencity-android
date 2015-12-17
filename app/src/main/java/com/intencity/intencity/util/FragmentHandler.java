package com.intencity.intencity.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.intencity.intencity.R;

import java.util.List;

/**
 * Handles the fragments for Intencity.
 *
 * Created by Nick Piscopio on 12/13/15.
 */
public class FragmentHandler
{
    /**
     * Animates a fragment to be visible on the screen.
     *
     * @param manager           The FragmentManager.
     * @param parent            The parent to push the fragment to.
     * @param fragmentToAdd     The fragment to add to the parent.
     * @param bundle            The information to send to the next fragment.
     * @param replace           Boolean on whether to replace the previous fragment.
     */
    public void pushFragment(FragmentManager manager, int parent, Fragment fragmentToAdd, Bundle bundle, String tag, boolean replace)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_slide_in_up, R.anim.anim_slide_out_up);

        if (replace)
        {
            List<Fragment> fragments = manager.getFragments();

            int fragmentsSize = fragments.size() - 1;

            for (int i = fragmentsSize; i > 0 ; i--)
            {
                if (!fragments.get(i).getTag().equals(Constant.TAG_SET))
                {
                    Fragment last = fragments.get(i);

                    transaction.remove(last);

                    break;
                }
            }
        }

        if (bundle != null)
        {
            fragmentToAdd.setArguments(bundle);
        }

        transaction.add(parent, fragmentToAdd, tag);
        transaction.commit();
    }
}