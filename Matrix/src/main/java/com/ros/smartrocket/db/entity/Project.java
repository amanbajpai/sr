package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class Project extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private transient Integer id;
    @SerializedName("Name")
    private String name;
    @SerializedName("Icon")
    private String icon;

    public Project() {
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
