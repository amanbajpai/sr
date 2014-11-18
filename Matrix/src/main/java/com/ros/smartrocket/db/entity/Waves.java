package com.ros.smartrocket.db.entity;

public class Waves extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;
    private Wave[] Waves;

    public Waves() {
    }

    public Wave[] getWaves() {
        return Waves;
    }
}
