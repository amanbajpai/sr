package com.ros.smartrocket.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.AssetFontsCache;


public class CustomEditTextView extends EditText {

    public CustomEditTextView(Context context) {
        this(context, null);
    }

    public CustomEditTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText);
        String fontAssetPath = a.getString(R.styleable.CustomEditText_fontAsset);
        setTypefaceFromFontAsset(fontAssetPath);
        a.recycle();
    }

    public boolean setTypefaceFromFontAsset(String fontAsset) {
        Typeface t = AssetFontsCache.loadFontFromAsset(getContext().getAssets(), fontAsset);
        if (t == null) return false;

        setTypeface(t);
        return true;
    }

    /*
     * @Override protected void onDraw(Canvas canvas) { int yOffset = getHeight() - getBaseline(); canvas.translate(0,
     * yOffset); super.onDraw(canvas); }
     */
}
