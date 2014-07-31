package com.ros.smartrocket.db.entity;

public class Project extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private transient Integer Id;
    private String Name;
    private String Icon;

    public Project() {
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

}
