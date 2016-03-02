package com.intencity.intencity.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.ViewPagerAdapter;
import com.intencity.intencity.view.fragment.LoginFragment;
import com.intencity.intencity.view.fragment.PagerFragment;
import com.intencity.intencity.util.Constant;

/**
 * Screens to demo the application before the user logs into Intencity.
 *
 * Created by Nick Piscopio on 12/9/15.
 */
public class DemoActivity extends FragmentActivity
{
    public static final int DESCRIPTION = 0;
    public static final int FITNESS_GURU = 1;
    public static final int FITNESS_DIRECTION = 2;
    public static final int FITNESS_LOG = 3;
    public static final int RANKING = 4;
    public static final int LOG_IN = 5;

    private static final int PAGER_SELECTED_RESOURCE = R.mipmap.pager_selected;
    private static final int PAGER_UNSELECTED_RESOURCE = R.mipmap.pager_unselected;

    private ViewPager mPager;

    private RelativeLayout navigation;

    private ImageView pager0;
    private ImageView pager1;
    private ImageView pager2;
    private ImageView pager3;
    private ImageView pager4;

    private ImageButton next;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        // Instantiate a ViewPager and a PagerAdapter.
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(getNewPagerFragment(DESCRIPTION), "");
        adapter.addFrag(getNewPagerFragment(FITNESS_GURU), "");
        adapter.addFrag(getNewPagerFragment(FITNESS_DIRECTION), "");
        adapter.addFrag(getNewPagerFragment(FITNESS_LOG), "");
        adapter.addFrag(getNewPagerFragment(RANKING), "");
        adapter.addFrag(new LoginFragment(), "");

        Bundle extras = getIntent().getExtras();
        int pageToStart = extras.getInt(Constant.EXTRA_DEMO_PAGE);

        navigation = (RelativeLayout) findViewById(R.id.navigation);

        pager0 = (ImageView) findViewById(R.id.pager_0);
        pager1 = (ImageView) findViewById(R.id.pager_1);
        pager2 = (ImageView) findViewById(R.id.pager_2);
        pager3 = (ImageView) findViewById(R.id.pager_3);
        pager4 = (ImageView) findViewById(R.id.pager_4);

        next = (ImageButton) findViewById(R.id.button_next);
        next.setOnClickListener(nextListener);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(adapter);
        mPager.addOnPageChangeListener(pageChangeListener);
        mPager.setCurrentItem(pageToStart);
    }

    /**
     * Creates a new PagerFragment for the view pager.
     *
     * @param position  The position the fragment will be added.
     *
     * @return  The PagerFragment
     */
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

    /**
     * The listener for the page changing.
     */
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageSelected(int position)
        {
            switch (position)
            {
                case DESCRIPTION:
                    pager0.setImageResource(PAGER_SELECTED_RESOURCE);
                    pager1.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager2.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager3.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager4.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    break;
                case FITNESS_GURU:
                    pager0.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager1.setImageResource(PAGER_SELECTED_RESOURCE);
                    pager2.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager3.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager4.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    break;
                case FITNESS_DIRECTION:
                    pager0.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager1.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager2.setImageResource(PAGER_SELECTED_RESOURCE);
                    pager3.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager4.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    break;
                case FITNESS_LOG:
                    pager0.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager1.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager2.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager3.setImageResource(PAGER_SELECTED_RESOURCE);
                    pager4.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    break;
                case RANKING:
                    pager0.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager1.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager2.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager3.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager4.setImageResource(PAGER_SELECTED_RESOURCE);

                    navigation.setVisibility(View.VISIBLE);
                    break;
                case LOG_IN:
                    navigation.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) { }
    };

    /**
     * The listener for the next button.
     */
    private View.OnClickListener nextListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        }
    };
}