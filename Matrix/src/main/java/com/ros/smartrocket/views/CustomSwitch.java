package com.ros.smartrocket.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.interfaces.SwitchCheckedChangeListener;
import com.ros.smartrocket.utils.LocaleUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomSwitch extends LinearLayout {
    @BindView(R.id.no_btn)
    CustomTextView noBtn;
    @BindView(R.id.yes_btn)
    CustomTextView yesBtn;

    private boolean isChecked = false;
    private SwitchCheckedChangeListener onCheckedChangeListener;

    public CustomSwitch(Context context) {
        super(context);
        init(context);
    }

    public CustomSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context c) {
        inflate(c, R.layout.view_custom_switch, this);
        ButterKnife.bind(this);
        updateButtons();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = !isChecked;
                updateButtons();
                onCheckedChange();
            }
        });
    }

    private void onCheckedChange() {
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChange(this, isChecked);
        }
    }

    private void updateButtons() {
        yesBtn.setEnabled(isChecked);
        noBtn.setEnabled(!isChecked);
    }

    public void setOnCheckedChangeListener(SwitchCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        updateButtons();
    }
}
