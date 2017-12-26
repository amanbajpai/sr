package com.ros.smartrocket.db.entity.question;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class Product extends BaseEntity {
    @SkipFieldInContentValues
    private static final long serialVersionUID = 4845715664252477541L;

    @SerializedName("SkuId")
    private String skuId;
    @SerializedName("Name")
    private String name;
    @SerializedName("Image")
    private String image;
    private String cachedImage;

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

    public String getCachedImage() {
        return cachedImage;
    }

    public void setCachedImage(String cachedImage) {
        this.cachedImage = cachedImage;
    }
}