package com.ros.smartrocket.presentation.payment;

import com.ros.smartrocket.db.entity.payment.PaymentsData;
import com.ros.smartrocket.presentation.base.MvpPresenter;

public interface PaymentMvpPresenter<V extends PaymentMvpView> extends MvpPresenter<V> {
    void getPaymentFields();

    void savePaymentsInfo(PaymentsData paymentsData);
}
