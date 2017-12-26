package com.ros.smartrocket.db.entity.task;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class Waves extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    @SerializedName("Waves")
    private Wave[] waves;

    public Waves() {
    }

    public Wave[] getWaves() {
        return waves;
    }
}
