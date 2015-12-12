package com.intencity.intencity.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.ViewPagerAdapter;
import com.intencity.intencity.fragment.LoginFragment;
import com.intencity.intencity.fragment.PagerFragment;

/**
 * Screens to demo the application before the user logs into Intencity.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class DemoActivity extends FragmentActivity
{
    // TODO: RENAME THESE LATER.
    public static final int DESCRIPTION_PAGE = 0;
    public static final int INSPIRATION_PAGE = 1;
    public static final int SHARE_PAGE = 2;

    private static final int PAGER_SELECTED_RESOURCE = R.mipmap.pager_selected;
    private static final int PAGER_UNSELECTED_RESOURCE = R.mipmap.pager_unselected;

    private ViewPager mPager;

    private ImageView pager0;
    private ImageView pager1;
    private ImageView pager2;
    private ImageView pager3;

    private ImageButton next;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        // Instantiate a ViewPager and a PagerAdapter.
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(getNewPagerFragment(DESCRIPTION_PAGE), "");
        adapter.addFrag(getNewPagerFragment(INSPIRATION_PAGE), "");
        adapter.addFrag(getNewPagerFragment(SHARE_PAGE), "");
        adapter.addFrag(new LoginFragment(), "");

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(adapter);
        mPager.addOnPageChangeListener(pageChangeListener);

        pager0 = (ImageView)findViewById(R.id.pager_0);
        pager1 = (ImageView)findViewById(R.id.pager_1);
        pager2 = (ImageView)findViewById(R.id.pager_2);
        pager3 = (ImageView)findViewById(R.id.pager_3);

        next = (ImageButton)findViewById(R.id.button_next);
        next.setOnClickListener(nextListener);
    }

    private PagerFragment getNewPagerFragment(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(PagerFragment.FRAGMENT_PAGE, position);

        PagerFragment sliderFragment = new PagerFragment();
        sliderFragment.setArguments(bundle);

        return sliderFragment;
    }

    @Override
    public void onBackPressed()
    {
        if (mPager.getCurrentItem() == 0)
        {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        }
        else
        {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageSelected(int position)
        {
            switch (position)
            {
                case 0:
                    pager0.setImageResource(PAGER_SELECTED_RESOURCE);
                    pager1.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager2.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager3.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    break;
                case 1:
                    pager0.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager1.setImageResource(PAGER_SELECTED_RESOURCE);
                    pager2.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager3.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    break;
                case 2:
                    pager1.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager2.setImageResource(PAGER_SELECTED_RESOURCE);
                    pager3.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager0.setVisibility(View.VISIBLE);
                    pager1.setVisibility(View.VISIBLE);
                    pager2.setVisibility(View.VISIBLE);
                    pager3.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    // TODO: Fix this. The pager 3 cannot be seen.
                    pager0.setVisibility(View.GONE);
                    pager1.setVisibility(View.GONE);
                    pager2.setVisibility(View.GONE);
                    pager3.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) { }
    };

    private View.OnClickListener nextListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        }
    };
}