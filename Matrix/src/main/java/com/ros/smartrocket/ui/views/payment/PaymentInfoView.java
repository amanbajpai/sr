package com.ros.smartrocket.ui.views.payment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.payment.PaymentField;
import com.ros.smartrocket.db.entity.payment.PaymentFieldType;
import com.ros.smartrocket.db.entity.payment.PaymentsData;

import java.util.ArrayList;
import java.util.List;

public class PaymentInfoView extends LinearLayout {
    private List<PaymentInfoTextView> paymentInfoTextViews = new ArrayList<>();
    private PaymentInfoImageView paymentInfoImageView;

    public PaymentInfoView(Context context) {
        super(context);
        init(context);
    }

    public PaymentInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaymentInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context c) {
        LayoutInflater inflater = LayoutInflater.from(c);
        inflater.inflate(R.layout.view_payment, this, true);
    }

    public void fillPaymentInfoViews(List<PaymentField> paymentFields) {
        removeAllViews();
        paymentInfoTextViews.clear();
        for (PaymentField f : paymentFields) {
            switch (PaymentFieldType.fromName(f.getType())) {
                case TEXT:
                    PaymentInfoTextView pitv = getPaymentInfoTextView(f);
                    paymentInfoTextViews.add(pitv);
                    addView(pitv);
                    break;
                case PHOTO:
                    paymentInfoImageView = getPaymentInfoImageView(f);
                    addView(paymentInfoImageView);
                    break;
            }
        }
    }

    private PaymentInfoImageView getPaymentInfoImageView(PaymentField f) {
        return null;
    }

    private PaymentInfoTextView getPaymentInfoTextView(PaymentField f) {
        return null;
    }

    public PaymentsData getPaymentsData() {
        return null;
    }
}
