package com.ros.smartrocket.ui.views;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.FontUtils;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TutorialView extends LinearLayout {

    @BindView(R.id.title)
    CustomTextView title;
    @BindView(R.id.text)
    CustomTextView text;
    @BindView(R.id.image)
    ImageView image;

    public TutorialView(Context context) {
        super(context);
        initViews(context);
    }

    private void initViews(Context c) {
        inflate(c, R.layout.view_tutorial, this);
        setOrientation(VERTICAL);
        ButterKnife.bind(this);
        title.setTypeface(FontUtils.loadFontFromAsset(c.getAssets(), FontUtils.getFontAssetPath(0)));
        text.setTypeface(FontUtils.loadFontFromAsset(c.getAssets(), FontUtils.getFontAssetPath(0)));
    }

    public void setTextAndImages(int titleRes, int textRes, int imgRes) {
        title.setText(titleRes);
        text.setText(textRes);
        image.setImageResource(imgRes);
    }

    public void setLastSlide(OnClickListener listener) {
        title.setOnClickListener(listener);
        title.setBackgroundResource(R.drawable.tut_button_selector);
        title.setTextColor(getResources().getColor(R.color.white));
        int padding = UIUtils.getPxFromDp(getContext(), 10);
        title.setPadding(padding, padding, padding, padding);
    }
}
