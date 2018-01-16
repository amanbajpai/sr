package com.ros.smartrocket.db.entity.payment;

public enum PaymentFieldType {
    NA("na"), TEXT("Text"), PHOTO("Photo");

    private final String name;

    PaymentFieldType(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }

    public static PaymentFieldType fromName(String typeName) {
        for (PaymentFieldType paymentFieldType : values()) {
            if (typeName.equals(paymentFieldType.getName())) {
                return paymentFieldType;
            }
        }
        return NA;
    }
}
