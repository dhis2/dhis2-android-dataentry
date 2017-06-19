package org.hisp.dhis.android.dataentry.commons.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.utils.TypefaceManager;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

public class FontCheckBox extends AppCompatCheckBox {

    public FontCheckBox(@NonNull Context context) {
        super(context);
    }

    public FontCheckBox(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FontCheckBox(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @NonNull AttributeSet attributeSet) {
        if (!isInEditMode()) {
            TypedArray attrs = context.obtainStyledAttributes(
                    attributeSet, R.styleable.ViewFont);
            setFont(attrs.getString(R.styleable.ViewFont_font));
            attrs.recycle();
        }
    }

    public void setFont(@StringRes int resId) {
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
