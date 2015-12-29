package com.intencity.intencity.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

/**
 * The header for the recycler view.
 *
 * Created by Nick Piscopio on 12/28/15.
 */
public class HeaderDecoration extends RecyclerView.ItemDecoration {

    private View mLayout;

    public HeaderDecoration(final Context context, RecyclerView parent, @LayoutRes int resId)
    {
        // inflate and measure the layout
        mLayout = LayoutInflater.from(context).inflate(resId, parent, false);
        mLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        super.onDraw(c, parent, state);
        // layout basically just gets drawn on the reserved space on top of the first view
        mLayout.layout(parent.getLeft(), 0, parent.getRight(), mLayout.getMeasuredHeight());
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            View view = parent.getChildAt(i);
            if (parent.getChildAdapterPosition(view) == 0)
            {
                c.save();
                final int height = mLayout.getMeasuredHeight();
                final int top = view.getTop() - height;
                c.translate(0, top);
                mLayout.draw(c);
                c.restore();
                break;
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        if (parent.getChildAdapterPosition(view) == 0)
        {
            outRect.set(0, mLayout.getMeasuredHeight(), 0, 0);
        }
        else
        {
            outRect.setEmpty();
        }
    }
}
