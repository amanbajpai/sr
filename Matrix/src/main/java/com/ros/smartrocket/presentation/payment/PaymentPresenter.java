package com.ros.smartrocket.presentation.payment;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.account.MyAccount;
import com.ros.smartrocket.db.entity.payment.PaymentField;
import com.ros.smartrocket.db.entity.payment.PaymentInfo;
import com.ros.smartrocket.db.entity.payment.PaymentsData;
import com.ros.smartrocket.db.entity.payment.SavePaymentInfoRequest;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class PaymentPresenter<V extends PaymentMvpView> extends BaseNetworkPresenter<V> implements PaymentMvpPresenter<V> {
    private PaymentsData paymentsData;

    @Override
    public void getPaymentFields() {
        getMvpView().showLoading(false);
        MyAccount account = App.getInstance().getMyAccount();
        addDisposable(App.getInstance().getApi().getPaymentFields(account.getCountryCode(), getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPaymentFieldsLoaded, this::showNetworkError));
    }

    @Override
    public void savePaymentsInfo(PaymentsData paymentsData) {
        this.paymentsData = paymentsData;
        if (paymentsData.getPaymentImageInfo() != null)
            savePaymentImage(paymentsData.getPaymentImageInfo());
        else if (!paymentsData.getPaymentTextInfos().isEmpty())
            saveTextPaymentsInfo(paymentsData);
    }

    private void savePaymentImage(PaymentInfo paymentImageInfo) {
        showLoading(false);
        addDisposable(getSavePaymentImageSingle(paymentImageInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> onPaymentImageSaved(s, paymentImageInfo), this::showNetworkError));
    }

    private void onPaymentImageSaved(String s, PaymentInfo paymentImageInfo) {
        paymentImageInfo.setValue(s);
        paymentsData.getPaymentTextInfos().add(paymentImageInfo);
        saveTextPaymentsInfo(paymentsData);
    }

    private void saveTextPaymentsInfo(PaymentsData paymentsData) {
        showLoading(false);
        addDisposable(getSavePaymentInfoSingle(paymentsData.getPaymentTextInfos())
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

    private Single<ResponseBody> getSavePaymentInfoSingle(List<PaymentInfo> data) {
        SavePaymentInfoRequest request = new SavePaymentInfoRequest(data);
        return App.getInstance().getApi().savePaymentInfo(request);
    }

    private Single<String> getSavePaymentImageSingle(PaymentInfo info) {
        return App.getInstance().getApi().sendPaymentFile(null, null);
    }
}
