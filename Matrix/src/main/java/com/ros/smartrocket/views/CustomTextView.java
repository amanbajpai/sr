package com.ros.smartrocket.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.FontUtils;


public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        int textStyle = a.getInt(R.styleable.CustomTextView_textStyle, 0);

        String fontAssetPath = FontUtils.getFontAssetPath(textStyle);

        Typeface t = FontUtils.loadFontFromAsset(getContext().getAssets(), fontAssetPath);
        if (t != null) {
            setTypeface(t);
        }

        a.recycle();
    }
}
