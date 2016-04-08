package com.intencity.intencity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intencity.intencity.R;
import com.intencity.intencity.adapter.viewholder.ProfileViewHolder;
import com.intencity.intencity.model.ProfileRow;
import com.intencity.intencity.util.TwoWayView.SpannableGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    // Going to need this for the math.
    private final Context context;
    private final List<ProfileRow> rows;

    public ProfileAdapter(Context context, ArrayList<ProfileRow> rows)
    {
        this.context = context;
        this.rows = rows;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_profile, parent, false);
        return new ProfileViewHolder(v);
    }

    @Override
    // Documentation for fixing layout params:
    // http://stackoverflow.com/questions/28748905/twoway-view-spannable-grid-layout-parameters-not-working
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        View itemView = holder.itemView;

        SpannableGridLayoutManager.LayoutParams lp = (SpannableGridLayoutManager.LayoutParams)itemView.getLayoutParams();

        ProfileRow row = rows.get(position);

        // TODO: Do math to get the real column span needed for the awards.
        int colSpan = (row.getAmount() != "" ? 1 : 5);
        // This should probably be the same value as the column span.
        // It appears if it is larger than the default span, then the view wraps content properly.
        int rowSpan = 5;

        if (lp.rowSpan != rowSpan || lp.colSpan != colSpan)
        {
            lp.rowSpan = rowSpan;
            lp.colSpan = colSpan;

            itemView.setLayoutParams(lp);
        }

        ProfileViewHolder profileHolder = (ProfileViewHolder)holder;

        String title = row.getTitle();

        if (row.isSectionHeader())
        {
            profileHolder.setAsHeader(row.getTitle());
        }
        else
        {
            String amount = row.getAmount();

            if (amount != "")
            {
                //set as award item

                profileHolder.setAsAwardView(title, amount);
            }
            else
            {
                //set as routine list item

                profileHolder.setAsRoutineView(row.getTitle());

            }
        }
    }

    @Override
    public int getItemCount()
    {
        return rows.size();
    }
}
