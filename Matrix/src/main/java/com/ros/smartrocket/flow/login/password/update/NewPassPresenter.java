package com.ros.smartrocket.flow.login.password.update;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.SetPassword;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.UIUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class NewPassPresenter<V extends NewPassMvpView> extends BaseNetworkPresenter<V> implements NewPassMvpPresenter<V> {

    @Override
    public void changePassword(String email, String token, String newPassword) {
        if (UIUtils.isPasswordValid(newPassword))
            setNewPassword(email, token, newPassword);
        else
            getMvpView().passwordNotValid();
    }

    private void setNewPassword(String email, String token, String newPassword) {
        getMvpView().showLoading(false);
        addDisposable(App.getInstance().getApi()
                .setPassword(new SetPassword(email, token, newPassword))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> handleSuccess(), this::showNetworkError)
        );
    }

    private void handleSuccess() {
        hideLoading();
        getMvpView().onPasswordChangeSuccess();
    }
}
