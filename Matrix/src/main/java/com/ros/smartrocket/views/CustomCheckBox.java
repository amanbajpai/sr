package com.ros.smartrocket.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.FontUtils;


public class CustomCheckBox extends android.support.v7.widget.AppCompatCheckBox {

    public CustomCheckBox(Context context) {
        this(context, null);
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomCheckBox);
        int textStyle = a.getInt(R.styleable.CustomCheckBox_textStyle, 0);

        String fontAssetPath = FontUtils.getFontAssetPath(textStyle);

        Typeface t = FontUtils.loadFontFromAsset(getContext().getAssets(), fontAssetPath);
        if (t != null) {
            setTypeface(t);
        }

        a.recycle();
    }
}
