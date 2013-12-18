package com.ros.smartrocket.db.entity;

import android.location.Location;

public class MatrixLocation extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private Location location;

    public MatrixLocation() {
    }

    public MatrixLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
