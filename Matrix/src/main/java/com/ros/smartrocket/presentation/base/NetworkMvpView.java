package com.ros.smartrocket.presentation.base;

import com.ros.smartrocket.interfaces.BaseNetworkError;

public interface NetworkMvpView extends MvpView {
    void showNetworkError(BaseNetworkError networkError);
}
