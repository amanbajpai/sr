package com.ros.smartrocket.db.entity;

public class Country extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private Integer Id;
    private String Name;

    public Country() {
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

}
