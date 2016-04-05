package com.intencity.intencity.view.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.User;
import com.intencity.intencity.util.Constant;

/**
 * This is the profile activity for a user.
 *
 * Created by Nick Piscopio on 4/5/15.
 */
public class ProfileActivity extends AppCompatActivity implements ServiceListener
{
    private enum FollowState
    {
        FOLLOWING,
        NOT_FOLLOWING
    }

    private FollowState followState;

    private ImageButton addRemoveButton;

    private User user;

    private int originalFollowId;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Add the back button to the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        context = getApplicationContext();

        Bundle bundle = getIntent().getExtras();
        user = bundle.getParcelable(Constant.BUNDLE_USER);

        ((TextView)findViewById(R.id.text_view_name)).setText(user.getFullName());
        ((TextView)findViewById(R.id.text_view_points)).setText(String.valueOf(
                user.getEarnedPoints()));

        addRemoveButton = (ImageButton) findViewById(R.id.button_add_remove);
        addRemoveButton.setOnClickListener(addRemoveClickListener);

        originalFollowId = user.getFollowingId();

        followState = originalFollowId > 0 ? FollowState.FOLLOWING : FollowState.NOT_FOLLOWING;

        setAddRemoveButtonImage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    /**
     * Sets the image for the add/remove button.
     */
    private void setAddRemoveButtonImage()
    {
        Drawable drawable = ContextCompat.getDrawable(context, followState == FollowState.FOLLOWING ?
                R.mipmap.ic_account_minus : R.mipmap.ic_account_plus_white);

        addRemoveButton.setImageDrawable(drawable);
    }

    private View.OnClickListener addRemoveClickListener = new View.OnClickListener()
    {
        @Override public void onClick(View v)
        {
            if (followState == FollowState.FOLLOWING)
            {
                followState = FollowState.NOT_FOLLOWING;

                user.setFollowingId((int)Constant.CODE_FAILED);
            }
            else
            {
                followState = FollowState.FOLLOWING;

                user.setFollowingId(originalFollowId);
            }

            setAddRemoveButtonImage();
        }
    };

    @Override
    public void onRetrievalSuccessful(String response)
    {
//        response = response.replaceAll("\"", "");
    }

    @Override
    public void onRetrievalFailed() { }
}