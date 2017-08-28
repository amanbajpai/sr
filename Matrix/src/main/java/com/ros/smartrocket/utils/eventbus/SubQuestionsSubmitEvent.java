package com.ros.smartrocket.utils.eventbus;

public final class SubQuestionsSubmitEvent {
    public final Integer productId;

    public SubQuestionsSubmitEvent(Integer productId) {
        this.productId = productId;
    }
}
