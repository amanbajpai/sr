package com.ros.smartrocket.interfaces;

import com.ros.smartrocket.db.entity.ErrorResponse;

public interface BaseNetworkError {

    int getErrorMessageRes();

    int getErrorCode();

    ErrorResponse getErrorResponse();

}
