package com.ros.smartrocket.presentation.payment;

import com.ros.smartrocket.db.entity.payment.PaymentField;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

import java.util.List;

public interface PaymentMvpView extends NetworkMvpView {
    void onPaymentFieldsLoaded(List<PaymentField> fields);

    void onPaymentsSaved();

    void onPaymentsSavedError();

    void onPaymentFieldsEmpty();
}
