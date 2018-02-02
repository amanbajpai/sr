package com.ros.smartrocket.ui.views.payment;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.payment.PaymentField;
import com.ros.smartrocket.db.entity.payment.PaymentInfo;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentInfoTextView extends RelativeLayout {
    @BindView(R.id.icon)
    AppCompatImageView icon;
    @BindView(R.id.value)
    CustomEditTextView value;
    @BindView(R.id.description)
    CustomTextView description;
    private PaymentField paymentField;

    public PaymentInfoTextView(Context context) {
        super(context);
    }

    public PaymentInfoTextView(Context context, PaymentField paymentField) {
        super(context);
        this.paymentField = paymentField;
        init(context);
    }

    private void init(Context c) {
        inflate(c, R.layout.view_payment_field_text, this);
        setBackgroundResource(R.color.grey_light);
        ButterKnife.bind(this);
        fillData();
    }

    private void fillData() {
        setIcon(paymentField);
        if (!TextUtils.isEmpty(paymentField.getValue()))
            value.setText(paymentField.getValue());
        value.setHint(paymentField.getName());
        description.setText(paymentField.getInstructions());
    }

    private void setIcon(PaymentField paymentField) {
        if (!TextUtils.isEmpty(paymentField.getIcon()))
            Picasso.with(getContext())
                    .load(paymentField.getIcon())
                    .error(R.drawable.cam)
                    .into(icon);
        else
            icon.setVisibility(GONE);
    }

    public PaymentInfo getPaymentInfo() {
        if (TextUtils.isEmpty(value.getText().toString().trim()))
            return null;
        else
            return new PaymentInfo(paymentField.getId(), value.getText().toString().trim());
    }
}
