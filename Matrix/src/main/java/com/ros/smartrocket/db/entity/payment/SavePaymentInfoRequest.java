package com.ros.smartrocket.db.entity.payment;

import java.io.Serializable;
import java.util.List;

public final class SavePaymentInfoRequest implements Serializable {
    private List<PaymentInfo> paymentInfoList;

    public SavePaymentInfoRequest(List<PaymentInfo> paymentInfoList) {
        this.paymentInfoList = paymentInfoList;
    }
}
