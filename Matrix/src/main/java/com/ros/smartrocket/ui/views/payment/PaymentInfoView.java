package com.ros.smartrocket.ui.views.payment;

import android.content.Context;
import android.graphics.Bitmap;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentInfoView extends LinearLayout {
    @BindView(R.id.container)
    LinearLayout container;
    private List<PaymentInfoTextView> paymentInfoTextViews = new ArrayList<>();
    private PaymentInfoImageView paymentInfoImageView;
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
        ButterKnife.bind(this);
    }

    public void fillPaymentInfoViews(List<PaymentField> paymentFields) {
        container.removeAllViews();
        paymentInfoTextViews.clear();
        for (PaymentField f : paymentFields) {
            switch (PaymentFieldType.fromName(f.getType())) {
                case TEXT:
                    PaymentInfoTextView pitv = getPaymentInfoTextView(f);
                    paymentInfoTextViews.add(pitv);
                    container.addView(pitv);
                    break;
                case PHOTO:
                    paymentInfoImageView = getPaymentInfoImageView(f);
                    paymentInfoImageView.setListener(listener);
                    container.addView(paymentInfoImageView);
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
        List<PaymentInfo> paymentInfoList = new ArrayList<>();
        for (PaymentInfoTextView tv : paymentInfoTextViews) {
            PaymentInfo pi = tv.getPaymentInfo();
            if (pi != null)
                paymentInfoList.add(pi);
        }
        pd.setPaymentTextInfos(paymentInfoList);
        return pd;
    }

    public void setPhotoActionsListener(PhotoActionsListener listener) {
        this.listener = listener;
    }

    public void setPhoto(Bitmap photo) {
        if (paymentInfoImageView != null) paymentInfoImageView.setPhoto(photo);
    }
}
