package com.ros.smartrocket.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.FontUtils;

public class CustomButton extends Button {

    public CustomButton(Context context) {
        this(context, null);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);
        int textStyle = a.getInt(R.styleable.CustomButton_textStyle, 0);

        String fontAssetPath = FontUtils.getFontAssetPath(textStyle);

        Typeface t = FontUtils.loadFontFromAsset(getContext().getAssets(), fontAssetPath);
        if (t != null) {
            setTypeface(t);
        }

        a.recycle();
    }
}
