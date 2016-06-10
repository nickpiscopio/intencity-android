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
import com.intencity.intencity.notification.AwardDialogContent;
import com.intencity.intencity.util.Constant;
import com.intencity.intencity.util.NotificationUtil;

import java.util.ArrayList;

/**
 * The custom ArrayAdapter for the notification list.
 *
 * Created by Nick Piscopio on 1/26/16.
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
        LinearLayout badgeAmountLayout;
        TextView title;
        TextView description;
        TextView badgeAmount;
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

        position = (int) Constant.CODE_FAILED;

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

            new NotificationUtil(objects.get(position),
                                 (ImageView) convertView.findViewById(R.id.image_view_award),
                                 (LinearLayout) convertView.findViewById(R.id.layout_badge_amount),
                                 (TextView) convertView.findViewById(R.id.text_view_title),
                                 (TextView) convertView.findViewById(R.id.text_view_description),
                                 (TextView) convertView.findViewById(R.id.amount));

            convertView.setTag(holder);
        }

        return convertView;
    }
}