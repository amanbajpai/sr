package com.ros.smartrocket.presentation.question.audit.additional;

import com.ros.smartrocket.db.entity.question.Category;
import com.ros.smartrocket.db.entity.question.Product;

public  class CategoryProductPair {
    public final Category category;
    public final Product product;
    final int productPosition;

    public CategoryProductPair(Category category, Product product, int productPos) {
        this.category = category;
        this.product = product;
        this.productPosition = productPos;
    }
}
