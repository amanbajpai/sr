package com.ros.smartrocket.net;


public interface NetworkOperationListenerInterface {
    void onNetworkOperationSuccess(BaseOperation operation);
    void onNetworkOperationFailed(BaseOperation operation);
}
