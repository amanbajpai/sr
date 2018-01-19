package com.ros.smartrocket.db.entity.payment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentInfo implements Serializable {
    @SerializedName("PaymentFieldId")
    private int paymentFieldId;
    @SerializedName("Value")
    private String value;

    public PaymentInfo(int paymentFieldId, String value) {
        this.paymentFieldId = paymentFieldId;
        this.value = value;
    }

    public PaymentInfo(PaymentField paymentField) {
        this(paymentField.getId(), paymentField.getValue());
    }

    public int getPaymentFieldId() {
        return paymentFieldId;
    }

    public void setPaymentFieldId(int paymentFieldId) {
        this.paymentFieldId = paymentFieldId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
