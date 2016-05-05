package com.intencity.intencity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.model.User;
import com.intencity.intencity.util.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the ranking list.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class RankingListAdapter extends ArrayAdapter<User>
{
    private Context context;

    private int layoutResourceId;

    private ArrayList<User> objects;

    private boolean isSearch;

    private LayoutInflater inflater;

    private int position;

    static class RankingListHolder
    {
        LinearLayout badgesLayout;
        TextView rank;
        TextView name;
        TextView points;
        TextView totalBadgesTextView;
        ImageView userIndicator;
        ImageView profilePic;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     * @param layoutResourceId  The resource id of the view we are inflating.
     * @param users             The list of users to populate the list.
     * @param isSearch          A boolean value of whether or not we are searching for a user.
     */
    public RankingListAdapter(Context context, int layoutResourceId, ArrayList<User> users, boolean isSearch)
    {
        super(context, layoutResourceId, users);

        this.context = context;

        this.layoutResourceId = layoutResourceId;

        this.objects = users;

        this.isSearch = isSearch;

        position = -1;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final RankingListHolder holder = (convertView == null) ?
                                            new RankingListHolder() :
                                            (RankingListHolder)convertView.getTag();

        if (this.position != position || convertView == null)
        {
            final int index = position;
            this.position = position;

            convertView = inflater.inflate(layoutResourceId, parent, false);

            final User user = objects.get(position);

            holder.badgesLayout = (LinearLayout) convertView.findViewById(R.id.layout_badges);
            holder.name = (TextView) convertView.findViewById(R.id.text_view_name);
            holder.points = (TextView) convertView.findViewById(R.id.text_view_points);
            holder.totalBadgesTextView = (TextView) convertView.findViewById(R.id.total_badges);
            holder.userIndicator = (ImageView) convertView.findViewById(R.id.user_indicator);
            holder.profilePic = (ImageView) convertView.findViewById(R.id.profile_pic);

            holder.name.setText(user.getFullName());
            holder.points.setText(String.valueOf(user.getEarnedPoints()));

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.default_profile_picture)
                    .showImageForEmptyUri(R.mipmap.default_profile_picture)
                    .showImageOnFail(R.mipmap.default_profile_picture)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();

            ImageLoader.getInstance().displayImage(Constant.UPLOAD_FOLDER + user.getId() + Constant.USER_PROFILE_PIC_NAME, holder.profilePic, options);

            if (!isSearch)
            {
                holder.rank = (TextView) convertView.findViewById(R.id.text_view_rank);
                holder.rank.setText(String.valueOf(position + 1));
                holder.rank.setVisibility(View.VISIBLE);

                holder.userIndicator.setVisibility(user.getFollowingId() > (int)Constant.CODE_FAILED ? View.GONE : View.VISIBLE);
            }

            int totalBadges = user.getTotalBadges();
            if (totalBadges > 0)
            {
                holder.totalBadgesTextView.setText(String.valueOf(totalBadges));

                holder.badgesLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.badgesLayout.setVisibility(View.GONE);
            }

            convertView.setTag(holder);
        }

        return convertView;
    }
}