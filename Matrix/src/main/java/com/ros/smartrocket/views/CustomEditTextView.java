package com.ros.smartrocket.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.EditText;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.FontUtils;


public class CustomEditTextView extends AppCompatEditText {

    public CustomEditTextView(Context context) {
        this(context, null);
    }

    public CustomEditTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText);
        int textStyle = a.getInt(R.styleable.CustomEditText_textStyle, 0);

        if (!isInEditMode()) {
            String fontAssetPath = FontUtils.getFontAssetPath(textStyle);
            Typeface t = FontUtils.loadFontFromAsset(getContext().getAssets(), fontAssetPath);
            if (t != null) {
                setTypeface(t);
            }
        }

        a.recycle();
    }
}
