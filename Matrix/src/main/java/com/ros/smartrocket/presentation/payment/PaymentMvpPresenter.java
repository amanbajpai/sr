package com.ros.smartrocket.presentation.payment;

import android.content.Intent;

import com.ros.smartrocket.db.entity.payment.PaymentsData;
import com.ros.smartrocket.presentation.base.MvpPresenter;
import com.ros.smartrocket.utils.eventbus.PhotoEvent;

public interface PaymentMvpPresenter<V extends PaymentMvpView> extends MvpPresenter<V> {
    void getPaymentFields();

    void savePaymentsInfo(PaymentsData paymentsData);

    void onPhotoEvent(PhotoEvent event);

    void onPhotoClicked(String path, int fieldId);

    void onPhotoRequested(int fieldId);

    boolean onActivityResult(int requestCode, int resultCode, Intent intent);
}
