package com.ros.smartrocket.eventbus;

public final class SubQuestionsSubmitEvent {
    public final Integer productId;

    public SubQuestionsSubmitEvent(Integer productId) {
        this.productId = productId;
    }
}
