package com.intencity.intencity.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.intencity.intencity.R;

import java.util.List;

/**
 * Created by nickpiscopio on 12/13/15.
 */
public class FragmentHandler
{
    public void pushFragment(Fragment fragment, int parent, Fragment fragmentToAdd, Bundle bundle, boolean replace)
    {
        FragmentManager fragmentManager = fragment.getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_slide_in_up, R.anim.anim_slide_out_up);

        if (replace)
        {
            List<Fragment> fragments = fragmentManager.getFragments();

            Fragment last = fragments.get(fragments.size() - 1);

            transaction.remove(last);
        }

        if (bundle != null)
        {
            fragmentToAdd.setArguments(bundle);
        }

        transaction.add(parent, fragmentToAdd);
        transaction.commit();
    }
}
