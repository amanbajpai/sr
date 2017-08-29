package com.ros.smartrocket.ui.login.terms;

import com.ros.smartrocket.App;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.base.BasePresenter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class TermsPresenter<V extends TermsMvpView> extends BasePresenter<V> implements TermsMvpPresenter<V> {

    @Override
    public void sendTermsAndConditionsViewed() {
        getMvpView().showLoading(false);
        Call<ResponseBody> call = App.getInstance().getApi().sendTandC();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    if (response.isSuccessful())
                        getMvpView().onTermsAndConditionsSent();
                    else
                        getMvpView().showNetworkError(new NetworkError(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    getMvpView().showNetworkError(new NetworkError(t));
                }
            }
        });
    }
}
