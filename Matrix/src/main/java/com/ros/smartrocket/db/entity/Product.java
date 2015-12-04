package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class Product extends BaseEntity {
    private static final long serialVersionUID = 4845715664252477541L;

    @SerializedName("SkuId")
    private String skuId;
    @SerializedName("Name")
    private String name;
    @SerializedName("Image")
    private String image;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}