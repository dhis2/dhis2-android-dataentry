package org.hisp.dhis.android.dataentry.commons.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.utils.TypefaceManager;

import static org.hisp.dhis.android.dataentry.utils.Preconditions.isNull;

public class FontTextInputEditText extends TextInputEditText {

    public FontTextInputEditText(Context context) {
        super(context);
    }

    public FontTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FontTextInputEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (!isInEditMode()) {
            TypedArray attrs = context.obtainStyledAttributes(attributeSet, R.styleable.ViewFont);
            setFont(attrs.getString(R.styleable.ViewFont_font));
            attrs.recycle();
        }
    }

    public final void setFont(@StringRes int resId) {
        String name = getResources().getString(resId);
        setFont(name);
    }

    private void setFont(final String fontName) {
        isNull(fontName, "fontName must not be null");

        if (getContext() != null && getContext().getAssets() != null) {
            Typeface typeface = TypefaceManager.getTypeface(getContext().getAssets(), fontName);
            if (typeface != null) {
                setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
                setTypeface(typeface);
            }
        }
    }
}
