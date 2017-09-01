package com.ros.smartrocket.net.retrofit;

import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.net.NetworkError;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableSingleObserver;
import retrofit2.Response;

public abstract class ResponseWrapper<T extends Response<R>, R> extends DisposableSingleObserver<T> {

    @Override
    public void onSuccess(@NonNull T t) {
        if (t.isSuccessful())
            onSuccessResponse(t.body());
        else
            onFailedResponse(new NetworkError(t.errorBody()));
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onFailedResponse(new NetworkError(e));
    }


    public abstract void onSuccessResponse(R r);

    public abstract void onFailedResponse(BaseNetworkError e);


}
