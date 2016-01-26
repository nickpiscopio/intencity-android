package com.intencity.intencity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.dialog.AwardDialogContent;
import com.intencity.intencity.util.Constant;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the ranking list.
 *
 * Created by Nick Piscopio on 12/18/15.
 */
public class NotificationAdapter extends ArrayAdapter<AwardDialogContent>
{
    private Context context;

    private int layoutResourceId;

    private ArrayList<AwardDialogContent> objects;

    private LayoutInflater inflater;

    private int position;

    static class RankingListHolder
    {
        ImageView awardImage;
        TextView title;
        TextView description;
    }

    /**
     * The constructor.
     *
     * @param context           The application context.
     * @param layoutResourceId  The resource id of the view we are inflating.
     * @param awards            The list of awards teh user received.
     */
    public NotificationAdapter(Context context, int layoutResourceId, ArrayList<AwardDialogContent> awards)
    {
        super(context, layoutResourceId, awards);

        this.context = context;

        this.layoutResourceId = layoutResourceId;

        this.objects = awards;

        position = Constant.CODE_FAILED;

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final RankingListHolder holder = (convertView == null) ?
                                            new RankingListHolder() :
                                            (RankingListHolder)convertView.getTag();

        if (this.position != position || convertView == null)
        {
            this.position = position;

            convertView = inflater.inflate(layoutResourceId, parent, false);

            final AwardDialogContent award = objects.get(position);

            holder.awardImage = (ImageView) convertView.findViewById(R.id.image_view_award);
            holder.title = (TextView) convertView.findViewById(R.id.text_view_title);
            holder.description = (TextView) convertView.findViewById(R.id.text_view_description);

            int awardImgRes = award.getImgRes();
            if (awardImgRes > 0)
            {
                holder.awardImage.setImageResource(awardImgRes);
                holder.awardImage.setVisibility(View.VISIBLE);

                holder.title.setVisibility(View.GONE);
            }
            else
            {
                holder.title.setText(award.getTitle());
                holder.title.setVisibility(View.VISIBLE);

                holder.awardImage.setVisibility(View.GONE);
            }

            holder.description.setText(award.getDescription());

            convertView.setTag(holder);
        }

        return convertView;
    }
}