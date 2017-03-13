package org.hisp.dhis.android.dataentry.commons.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RaisedButton extends CardView {
    private final FontTextView buttonTextView;
    private final LinearLayout linearLayout;

    public RaisedButton(Context context) {
        super(context);

        buttonTextView = new FontTextView(context);
        linearLayout = new LinearLayout(context);

        init();
    }

    public RaisedButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        buttonTextView = new FontTextView(context, attrs);
        linearLayout = new LinearLayout(context, attrs);

        init();
    }

    public RaisedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        buttonTextView = new FontTextView(context, attrs, defStyleAttr);
        linearLayout = new LinearLayout(context, attrs, defStyleAttr);

        init();
    }


    private void init() {
        setRadius(calculatePixels(2));
        setCardElevation(calculatePixels(2));
        setPreventCornerOverlap(true);
        setUseCompatPadding(true);
        setClickable(true);

        this.addView(linearLayout, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
        linearLayout.addView(buttonTextView, new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        int linearLayoutPadding = calculatePixels(8);
        linearLayout.setPadding(linearLayoutPadding, linearLayoutPadding,
                linearLayoutPadding, linearLayoutPadding);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            linearLayout.setBackgroundDrawable(getSelectableItemBackground());
        } else {
            linearLayout.setBackground(getSelectableItemBackground());
        }
    }

    private Drawable getSelectableItemBackground() {
        int[] attrs = new int[]{
                android.R.attr.selectableItemBackground
        };

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = typedArray.getDrawable(0);

        // Free resources used by TypedArray
        typedArray.recycle();

        return drawableFromTheme;
    }

    private int calculatePixels(int dps) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps,
                getResources().getDisplayMetrics());
    }

    public TextView getTextView() {
        return buttonTextView;
    }
}
