package com.ros.smartrocket.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.FontUtils;

public class CustomButton extends android.support.v7.widget.AppCompatButton {

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

        if (!isInEditMode()) {
            setFont(textStyle);
        }
        a.recycle();
    }

    public void setFont(int textStyle) {
        String fontAssetPath = FontUtils.getFontAssetPath(textStyle);
        Typeface t = FontUtils.loadFontFromAsset(getContext().getAssets(), fontAssetPath);
        if (t != null) {
            setTypeface(t);
        }
    }


}
