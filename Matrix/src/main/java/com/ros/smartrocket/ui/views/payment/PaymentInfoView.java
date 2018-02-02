package com.ros.smartrocket.ui.views.payment;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.payment.PaymentField;
import com.ros.smartrocket.db.entity.payment.PaymentFieldType;
import com.ros.smartrocket.db.entity.payment.PaymentInfo;
import com.ros.smartrocket.db.entity.payment.PaymentsData;
import com.ros.smartrocket.interfaces.PhotoActionsListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class PaymentInfoView extends LinearLayout {
    private List<PaymentInfoTextView> paymentInfoTextViews = new ArrayList<>();
    private List<PaymentInfoImageView> paymentInfoImageViews = new ArrayList<>();
    private PhotoActionsListener listener;


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
        setOrientation(VERTICAL);
        ButterKnife.bind(this);
    }

    public void fillPaymentInfoViews(List<PaymentField> paymentFields) {
        removeAllViews();
        paymentInfoTextViews.clear();
        paymentInfoImageViews.clear();
            for (PaymentField f : paymentFields) {
                switch (PaymentFieldType.fromName(f.getType())) {
                    case TEXT:
                        PaymentInfoTextView paymentInfoTextView = getPaymentInfoTextView(f);
                        paymentInfoTextViews.add(paymentInfoTextView);
                        addView(paymentInfoTextView);
                        break;
                    case PHOTO:
                        PaymentInfoImageView paymentInfoImageView = getPaymentInfoImageView(f);
                        paymentInfoImageViews.add(paymentInfoImageView);
                        addView(paymentInfoImageView);
                        break;
                }
            }
        invalidate();
    }

    private PaymentInfoImageView getPaymentInfoImageView(PaymentField f) {
        return new PaymentInfoImageView(getContext(), f, listener);
    }

    private PaymentInfoTextView getPaymentInfoTextView(PaymentField f) {
        return new PaymentInfoTextView(getContext(), f);
    }

    public PaymentsData getPaymentsData() {
        PaymentsData pd = new PaymentsData();
        pd.setPaymentTextInfos(getPaymentTextInfos());
        pd.setPaymentImageInfos(getPaymentImageInfos());
        return pd;
    }

    @NonNull
    private List<PaymentInfo> getPaymentTextInfos() {
        List<PaymentInfo> paymentTextInfoList = new ArrayList<>();
        for (PaymentInfoTextView tv : paymentInfoTextViews) {
            PaymentInfo pi = tv.getPaymentInfo();
            if (pi != null)
                paymentTextInfoList.add(pi);
        }
        return paymentTextInfoList;
    }

    @NonNull
    private List<PaymentInfo> getPaymentImageInfos() {
        List<PaymentInfo> paymentImageInfoList = new ArrayList<>();
        for (PaymentInfoImageView tv : paymentInfoImageViews) {
            PaymentInfo pi = tv.getPaymentInfo();
            if (pi != null)
                paymentImageInfoList.add(pi);
        }
        return paymentImageInfoList;
    }

    public void setPhotoActionsListener(PhotoActionsListener listener) {
        this.listener = listener;
    }

    public void setPhoto(Bitmap photo, int fieldId) {
        for (PaymentInfoImageView piv : paymentInfoImageViews) {
            if (piv.getFieldId() == fieldId)
                piv.setPhoto(photo);
        }
    }
}
