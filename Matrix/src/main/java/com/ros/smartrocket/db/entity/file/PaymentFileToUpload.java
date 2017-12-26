package com.ros.smartrocket.db.entity.file;

import com.google.gson.annotations.SerializedName;

public class PaymentFileToUpload extends FileToUpload {
    @SerializedName("PaymentFieldId")
    private int paymentFieldId;

    public int getPaymentFieldId() {
        return paymentFieldId;
    }

    public void setPaymentFieldId(int paymentFieldId) {
        this.paymentFieldId = paymentFieldId;
    }
}
