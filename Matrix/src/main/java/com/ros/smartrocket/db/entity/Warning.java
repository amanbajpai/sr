package com.ros.smartrocket.db.entity;


import com.google.gson.annotations.SerializedName;

public class Warning {

    @SerializedName("Code")
    private int code;
    @SerializedName("Params")
    private int[] params;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int[] getParams() {
        return params;
    }

    public void setParams(int[] params) {
        this.params = params;
    }
}
