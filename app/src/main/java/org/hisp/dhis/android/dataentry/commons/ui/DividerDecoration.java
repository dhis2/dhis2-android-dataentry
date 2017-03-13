package org.hisp.dhis.android.dataentry.commons.ui;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * ItemDecoration implementation that applies and inset margin
 * around each child of the RecyclerView. It also draws item dividers
 * that are expected from a vertical list implementation, such as
 * ListView.
 */
public class DividerDecoration extends RecyclerView.ItemDecoration {
    private final Drawable divider;

    public DividerDecoration(Drawable drawable) {
        divider = drawable;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        drawVertical(canvas, parent);
    }

    /**
     * Draw dividers underneath each child view
     */
    public void drawVertical(Canvas canvas, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }
}