package com.ros.smartrocket.presentation.payment;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.entity.payment.PaymentField;
import com.ros.smartrocket.db.entity.payment.PaymentInfo;
import com.ros.smartrocket.db.entity.payment.PaymentsData;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.PreferencesManager;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PaymentPresenter<V extends PaymentMvpView> extends BaseNetworkPresenter<V> implements PaymentMvpPresenter<V> {
    private String imagePath;
    private PaymentsData paymentsData;

    @Override
    public void getPaymentFields() {
        getMvpView().showLoading(false);
        int countryId = PreferencesManager.getInstance().getInt(Keys.COUNTRY_ID, 0);
        addDisposable(App.getInstance().getApi().getPaymentFields(countryId, getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPaymentFieldsLoaded, this::showNetworkError));
    }

    @Override
    public void savePaymentsInfo(PaymentsData paymentsData) {
        this.paymentsData = paymentsData;
        if (!TextUtils.isEmpty(imagePath))
            savePaymentImage();
        else if (!paymentsData.getPaymentTextInfos().isEmpty())
            saveAllPaymentsInfo(paymentsData.getPaymentTextInfos());
        else
            getMvpView().onPaymentFieldsEmpty();
    }

    private void savePaymentImage() {
        showLoading(false);
        addDisposable(getSavePaymentImageSingle(imagePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPaymentImageSaved, this::showNetworkError));
    }

    private void onPaymentImageSaved(String s) {
        PaymentInfo paymentImageInfo = paymentsData.getPaymentImageInfo();
        paymentImageInfo.setValue(s);
        List<PaymentInfo> paymentInfoList = paymentsData.getPaymentTextInfos();
        paymentInfoList.add(paymentImageInfo);
        saveAllPaymentsInfo(paymentInfoList);
    }

    private void saveAllPaymentsInfo(List<PaymentInfo> paymentInfoList) {
        showLoading(false);
        addDisposable(App.getInstance().getApi().savePaymentInfo(paymentInfoList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rb -> onAllPaymentsSaved(), this::showNetworkError));
    }

    private void onAllPaymentsSaved() {
        if (isViewAttached()) {
            hideLoading();
            getMvpView().onPaymentsSaved();
        }
    }

    private void onPaymentFieldsLoaded(List<PaymentField> pf) {
        if (isViewAttached()) {
            hideLoading();
            getMvpView().onPaymentFieldsLoaded(pf);
        }
    }

    private Single<String> getSavePaymentImageSingle(String imgPath) {
        return App.getInstance().getApi().sendPaymentFile(null, null);
    }
}
