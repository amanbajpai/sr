package com.ros.smartrocket.flow.base;

import com.ros.smartrocket.interfaces.BaseNetworkError;

public interface NetworkMvpView extends MvpView {
    void showNetworkError(BaseNetworkError networkError);
}
