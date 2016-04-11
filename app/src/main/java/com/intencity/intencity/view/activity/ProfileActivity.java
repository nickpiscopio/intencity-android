package com.intencity.intencity.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.ProfileAdapter;
import com.intencity.intencity.listener.DialogListener;
import com.intencity.intencity.listener.ImageListener;
import com.intencity.intencity.listener.ServiceListener;
import com.intencity.intencity.model.ProfileRow;
import com.intencity.intencity.model.User;
import com.intencity.intencity.notification.CustomDialog;
import com.intencity.intencity.notification.CustomDialogContent;
import com.intencity.intencity.task.ServiceTask;
import com.intencity.intencity.task.UploadImageTask;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.TwoWayView.TwoWayView;
import com.intencity.intencity.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the profile activity for a user.
 *
 * Created by Nick Piscopio on 4/5/15.
 */
public class ProfileActivity extends AppCompatActivity implements DialogListener, ImageListener
{
    private enum FollowState
    {
        FOLLOWING,
        NOT_FOLLOWING
    }

    private FollowState followState;

    private ImageView profilePic;

    private FloatingActionButton camera;
    private FloatingActionButton addRemoveButton;

    private TwoWayView recyclerView;

    private TextView emptyListTextView;

    private boolean fromSearch;
    private boolean profileIsCurrentUser;
    private boolean updatedProfilePic = false;

    private User user;

    private int index;
    private int originalFollowId;
    private int userId;

    private String email;

    private Context context;

    private ProfileAdapter adapter;

    private ArrayList<ProfileRow> profileSections = new ArrayList<>();

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
        fromSearch = bundle.getBoolean(Constant.BUNDLE_FROM_SEARCH, false);
        index = bundle.getInt(Constant.BUNDLE_POSITION, (int)Constant.CODE_FAILED);
        user = bundle.getParcelable(Constant.BUNDLE_USER);
        profileIsCurrentUser = bundle.getBoolean(Constant.BUNDLE_PROFILE_IS_USER, false);

        ((TextView)findViewById(R.id.text_view_name)).setText(user.getFullName());
        ((TextView)findViewById(R.id.text_view_points)).setText(
                String.valueOf(user.getEarnedPoints()));

        profilePic = (ImageView) findViewById(R.id.profile_pic);

        emptyListTextView = (TextView) findViewById(R.id.empty_list);

        camera = (FloatingActionButton) findViewById(R.id.camera);
        camera.setOnClickListener(cameraClickListener);

        addRemoveButton = (FloatingActionButton) findViewById(R.id.button_add_remove);
        addRemoveButton.setOnClickListener(addRemoveClickListener);

        originalFollowId = user.getFollowingId();
        userId = user.getId();

        if (!profileIsCurrentUser)
        {
            followState = originalFollowId > 0 ? FollowState.FOLLOWING : FollowState.NOT_FOLLOWING;
        }
        else
        {
            camera.setVisibility(View.VISIBLE);

            // Remove add/remove button because a user cannot follow or unfollow him or herself.
            addRemoveButton.setVisibility(View.GONE);
        }

        setAddRemoveButtonImage();

        email = Util.getSecurePreferencesEmail(context);

        String userId = String.valueOf(user.getId());

        new ServiceTask(badgeRetrievalListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(
                                              Constant.STORED_PROCEDURE_GET_BADGES, userId));

        new ServiceTask(last7DayRoutineServiceListener).execute(Constant.SERVICE_STORED_PROCEDURE,
                                      Constant.generateStoredProcedureParameters(
                                              Constant.STORED_PROCEDURE_GET_LAST_WEEK_ROUTINES, userId));

        recyclerView = (TwoWayView) findViewById(R.id.list);

        adapter = new ProfileAdapter(context, profileSections);

        recyclerView.setAdapter(adapter);

        File savedProfilePic = ImageLoader.getInstance().getDiskCache().get(Constant.UPLOAD_FOLDER + user.getId() + Constant.USER_PROFILE_PIC_NAME);

        Bitmap bitmap = BitmapFactory.decodeFile(savedProfilePic.getAbsolutePath());

        if (bitmap != null)
        {
            profilePic.setImageBitmap(bitmap);
        }
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

    /**
     * Updates the activity list view.
     */
    private void updateListView()
    {
        if (profileSections.isEmpty())
        {
            emptyListTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);

            adapter.notifyDataSetChanged();
        }
    }

    private View.OnClickListener cameraClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            PackageManager pm = context.getPackageManager();

            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                CustomDialogContent content = new CustomDialogContent(context.getString(R.string.profile_pic_dialog_title), context.getString(R.string.profile_pic_dialog_message), true);
                content.setNegativeButtonStringRes(R.string.profile_pic_dialog_pictures_button);
                content.setPositiveButtonStringRes(R.string.profile_pic_dialog_camera_button);

                new CustomDialog(ProfileActivity.this, ProfileActivity.this, content, true);
            }
            else
            {
                // We are opening the photos because the user doesn't have a camera.
                onButtonPressed(Constant.NEGATIVE_BUTTON);
            }
        }
    };

    private View.OnClickListener addRemoveClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
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

    private ServiceListener badgeRetrievalListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            try
            {
                ArrayList<ProfileRow> awardSection = new ArrayList<>();
                awardSection.add(new ProfileRow(true, context.getString(R.string.awards_title), ""));

                JSONArray array = new JSONArray(response);

                int length = array.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject object = array.getJSONObject(i);

                    String badgeName = object.getString(Constant.COLUMN_BADGE_NAME);
                    String amount = object.getString(Constant.COLUMN_TOTAL_BADGES);

                    awardSection.add(new ProfileRow(false, badgeName, amount));
                }

                profileSections.addAll(0, awardSection);
            }
            catch (Exception e) { }

            updateListView();
        }

        @Override
        public void onRetrievalFailed() { }
    };

    private ServiceListener last7DayRoutineServiceListener = new ServiceListener()
    {
        @Override
        public void onRetrievalSuccessful(String response)
        {
            try
            {
                ArrayList<ProfileRow> lastWeekRoutines = new ArrayList<>();
                lastWeekRoutines.add(new ProfileRow(true, context.getString(R.string.profile_routines_title), ""));

                JSONArray array = new JSONArray(response);

                int length = array.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject object = array.getJSONObject(i);

                    String title = object.getString(Constant.COLUMN_DISPLAY_NAME);
                    lastWeekRoutines.add(new ProfileRow(false, title, ""));
                }

                profileSections.addAll(lastWeekRoutines);
            }
            catch (Exception e) { }

            updateListView();
        }

        @Override
        public void onRetrievalFailed() { }
    };

    @Override
    public void onImageRetrieved(Bitmap bmp)
    {
        profilePic.setImageBitmap(bmp);

        updatedProfilePic = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            Bitmap bitmap = null;

            if (requestCode == Constant.REQUEST_CODE_OPEN_IMAGE)
            {
                Uri imageUri = data.getData();

                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                    Log.i(Constant.TAG, "Opened image: " + bitmap);
                }
                catch (IOException e)
                {
                    Log.e(Constant.TAG, "Couldn't retrieve image.");
                }
            }
            else if (requestCode == Constant.REQUEST_CODE_CAPTURE_IMAGE)
            {
                Bundle extras = data.getExtras();
                // This bundle seems to be set by the Android camera.
                bitmap = (Bitmap) extras.get(Constant.BUNDLE_DATA);

                Log.i(Constant.TAG, "Captured image: " + bitmap);
            }

            // Start the task to save the image to the web server.
            new UploadImageTask(this, bitmap, user.getId()).execute();
        }
    }

    @Override
    public void onButtonPressed(int which)
    {
        Intent intent = new Intent();
        int result = 0;

        switch (which)
        {
            // Opens the photos.
            case Constant.NEGATIVE_BUTTON:
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                result = Constant.REQUEST_CODE_OPEN_IMAGE;
                break;

            // Opens the camera
            case Constant.POSITIVE_BUTTON:
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                result = Constant.REQUEST_CODE_CAPTURE_IMAGE;
                break;

            default:
                break;
        }

        startActivityForResult(intent, result);
    }

    @Override
    public void onBackPressed()
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

            if (fromSearch)
            {
                // Send the followId back to the search,
                // so the add/remove button isn't reset if the user clicks on the same user.
                intent = new Intent();
                intent.putExtra(Constant.BUNDLE_POSITION, index);
                intent.putExtra(Constant.BUNDLE_FOLLOW_ID, user.getFollowingId());
            }

            setResult(Constant.REQUEST_CODE_PROFILE, intent);
        }
        else if (updatedProfilePic)
        {
            Intent intent = new Intent();
            intent.putExtra(Constant.BUNDLE_POSITION, index);

            setResult(Constant.REQUEST_CODE_PROFILE, intent);
        }

        super.onBackPressed();
    }
}