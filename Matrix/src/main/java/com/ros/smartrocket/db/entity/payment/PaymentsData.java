package com.ros.smartrocket.db.entity.payment;

import java.util.List;

public class PaymentsData {
    private List<PaymentInfo> paymentTextInfos;
    private PaymentInfo paymentImageInfo;

    public List<PaymentInfo> getPaymentTextInfos() {
        return paymentTextInfos;
    }

    public void setPaymentTextInfos(List<PaymentInfo> paymentTextInfos) {
        this.paymentTextInfos = paymentTextInfos;
    }

    public PaymentInfo getPaymentImageInfo() {
        return paymentImageInfo;
    }

    public void setPaymentImage(PaymentInfo paymentImageInfo) {
        this.paymentImageInfo = paymentImageInfo;
    }
}
