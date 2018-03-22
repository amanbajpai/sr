package com.ros.smartrocket.db.entity.payment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentField implements Serializable {
    @SerializedName("Id")
    private int id;
    @SerializedName("Icon")
    private String icon;
    @SerializedName("Name")
    private String name;
    @SerializedName("Instructions")
    private String instructions;
    @SerializedName("Type")
    private String type;
    @SerializedName("Value")
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions == null ? "" : instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
