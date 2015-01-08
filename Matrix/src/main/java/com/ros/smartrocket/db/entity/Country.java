package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class Country extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    @SerializedName("Id")
    private transient Integer id;
    @SerializedName("Name")
    private String name;

    public Country() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
