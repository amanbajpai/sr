package com.ros.smartrocket.db.entity.payment;

import java.util.List;

public class PaymentsData {
    private List<PaymentInfo> paymentTextInfos;
    private List<PaymentInfo> paymentImageInfos;

    public List<PaymentInfo> getPaymentTextInfos() {
        return paymentTextInfos;
    }

    public void setPaymentTextInfos(List<PaymentInfo> paymentTextInfos) {
        this.paymentTextInfos = paymentTextInfos;
    }

    public List<PaymentInfo> getPaymentImageInfos() {
        return paymentImageInfos;
    }

    public void setPaymentImageInfos(List<PaymentInfo> paymentImageInfos) {
        this.paymentImageInfos = paymentImageInfos;
    }

    public int getFieldsCount() {
        return (paymentImageInfos != null ? paymentImageInfos.size() : 0)
                + (paymentTextInfos != null ? paymentTextInfos.size() : 0);
    }
}
