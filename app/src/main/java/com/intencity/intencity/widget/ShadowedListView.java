package com.intencity.intencity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * This class is a custom ListView class that has a shadow on the top of it when scrolling.
 *
 * Created by Nick Piscopio on 12/26/16.
 */
public class ShadowedListView extends ListView
{
    public ShadowedListView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return 0;
    }
}
