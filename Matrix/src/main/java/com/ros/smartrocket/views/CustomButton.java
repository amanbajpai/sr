package com.ros.smartrocket.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.AssetFontsCache;

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
        String fontAssetPath = a.getString(R.styleable.CustomButton_fontAsset);
        setTypefaceFromFontAsset(fontAssetPath);
        a.recycle();
    }

    public boolean setTypefaceFromFontAsset(String fontAsset) {
        Typeface t = AssetFontsCache.loadFontFromAsset(getContext().getAssets(), fontAsset);
        if (t == null) return false;

        setTypeface(t);
        return true;
    }

}
