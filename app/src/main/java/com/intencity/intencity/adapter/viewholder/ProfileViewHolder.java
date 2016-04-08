package com.intencity.intencity.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intencity.intencity.R;
import com.intencity.intencity.util.Badge;

/**
 * The ExerciseViewHolder for the exercise RecyclerView.
 *
 * Created by Nick Piscopio on 12/21/15.
 */
public class ProfileViewHolder extends RecyclerView.ViewHolder
{
    private LinearLayout headerLayout;
    private LinearLayout awardLayout;
    private LinearLayout routineLayout;
    private ImageView award;
    private TextView amount;
    private TextView header;
    private TextView routine;

    public ProfileViewHolder(View view)
    {
        super(view);

        headerLayout = (LinearLayout) view.findViewById(R.id.layout_list_item_header);
        awardLayout = (LinearLayout) view.findViewById(R.id.award_layout);
        routineLayout = (LinearLayout) view.findViewById(R.id.layout_list_item_standard);
        award = (ImageView) view.findViewById(R.id.image_view_award);
        amount = (TextView) view.findViewById(R.id.total_badges);
        header = (TextView) view.findViewById(R.id.text_view_header);
        routine = (TextView) view.findViewById(R.id.text_view);
    }

    public void setAsHeader(String title)
    {
        routineLayout.setVisibility(View.GONE);
        awardLayout.setVisibility(View.GONE);
        headerLayout.setVisibility(View.VISIBLE);

        header.setText(title);
    }

    public void setAsAwardView(String title, String amount)
    {
        headerLayout.setVisibility(View.GONE);
        routineLayout.setVisibility(View.GONE);
        awardLayout.setVisibility(View.VISIBLE);

        this.amount.setText(amount);

        int imageResId;

        switch (title)
        {
            case Badge.FINISHER:
                imageResId = R.mipmap.finisher;
                break;
            case Badge.KEPT_SWIMMING:
                imageResId = R.mipmap.kept_swimming;
                break;
            case Badge.LEFT_IT_ON_THE_FIELD:
                imageResId = R.mipmap.left_it_on_the_field;
                break;
            default:
                imageResId = R.mipmap.badge;
                break;
        }

        award.setImageResource(imageResId);
    }

    public void setAsRoutineView(String routine)
    {
        headerLayout.setVisibility(View.GONE);
        awardLayout.setVisibility(View.GONE);
        routineLayout.setVisibility(View.VISIBLE);

        this.routine.setText(routine);
    }
}