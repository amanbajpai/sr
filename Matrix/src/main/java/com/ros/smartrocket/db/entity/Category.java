package com.ros.smartrocket.db.entity;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Category extends BaseEntity {
    private static final long serialVersionUID = -8864614582101500226L;

    @SerializedName("CategoryId")
    private Integer categoryId;
    @SerializedName("CategoryName")
    private String categoryName;
    @SerializedName("Products")
    private Product[] products;

    public static Category[] getCategoryArray(String jsonArrayString) {
        Category[] categories = new Category[]{};
        if (!TextUtils.isEmpty(jsonArrayString)) {
            categories = new Gson().fromJson(jsonArrayString, Category[].class);
        }
        return categories;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }
}