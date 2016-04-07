package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.User;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.Util;

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

    private boolean profileIsCurrentUser;
    private User user;

    private int index;
    private int originalFollowId;
    private int userId;

    private String email;

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
        index = bundle.getInt(Constant.BUNDLE_POSITION, (int)Constant.CODE_FAILED);
        user = bundle.getParcelable(Constant.BUNDLE_USER);
        profileIsCurrentUser = bundle.getBoolean(Constant.BUNDLE_PROFILE_IS_USER, false);

        ((TextView)findViewById(R.id.text_view_name)).setText(user.getFullName());
        ((TextView)findViewById(R.id.text_view_points)).setText(String.valueOf(
                user.getEarnedPoints()));

        ImageView profilePic = (ImageView) findViewById(R.id.profile_pic);

        addRemoveButton = (ImageButton) findViewById(R.id.button_add_remove);
        addRemoveButton.setOnClickListener(addRemoveClickListener);

        originalFollowId = user.getFollowingId();
        userId = user.getId();

        Bitmap savedProfilePic = user.getBmp();

        if (savedProfilePic != null)
        {
            profilePic.setImageBitmap(savedProfilePic);
        }

        if (!profileIsCurrentUser)
        {
            followState = originalFollowId > 0 ? FollowState.FOLLOWING : FollowState.NOT_FOLLOWING;
        }
        else
        {
            //TODO: NEED TO ADD CAMERA HERE.

            // Remove add/remove button because a user cannot follow or unfollow him or herself.
            addRemoveButton.setVisibility(View.GONE);
        }

        setAddRemoveButtonImage();

        email = Util.getSecurePreferencesEmail(context);
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
        Drawable drawable;

        if (followState == FollowState.FOLLOWING)
        {
            drawable = ContextCompat.getDrawable(context, R.mipmap.ic_account_minus);

            user.setFollowingId(originalFollowId > (int)Constant.CODE_FAILED ? originalFollowId : 1);
        }
        else
        {
            drawable = ContextCompat.getDrawable(context,R.mipmap.ic_account_plus_white);

            user.setFollowingId((int)Constant.CODE_FAILED);
        }

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

    @Override public void onBackPressed()
    {
        int followId = user.getFollowingId();

        if (originalFollowId != followId)
        {
            if (followId < 0)
            {
                new ServiceTask(null).execute(Constant.SERVICE_STORED_PROCEDURE,
                                              Constant.generateStoredProcedureParameters(
                                                      Constant.STORED_PROCEDURE_REMOVE_FROM_FOLLOWING, String.valueOf(originalFollowId)));
            }
            else
            {
                // Follow the user.
                new ServiceTask(null).execute(Constant.SERVICE_STORED_PROCEDURE,
                           Constant.generateStoredProcedureParameters(
                                   Constant.STORED_PROCEDURE_FOLLOW_USER, email, String.valueOf(userId)));
            }

            // We want this to be null if the index is less than 0.
            // This means that we are coming from the ranking list,
            // which we don't need to reset the followId because we reset the followers on back press.
            Intent intent = null;

            if (index >= 0)
            {
                // Send the followId back to the search,
                // so the add/remove button isn't reset if the user clicks on the same user.
                intent = new Intent();
                intent.putExtra(Constant.BUNDLE_POSITION, index);
                intent.putExtra(Constant.BUNDLE_FOLLOW_ID, user.getFollowingId());
            }

            setResult(Constant.REQUEST_CODE_PROFILE, intent);
        }

        super.onBackPressed();
    }
}