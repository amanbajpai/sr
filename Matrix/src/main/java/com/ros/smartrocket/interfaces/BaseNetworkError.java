package com.ros.smartrocket.interfaces;

import com.ros.smartrocket.db.entity.error.ErrorResponse;

public interface BaseNetworkError {

    int getErrorMessageRes();

    int getErrorCode();

    ErrorResponse getErrorResponse();

}
