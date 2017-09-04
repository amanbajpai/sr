package com.ros.smartrocket.flow.login.promo;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface PromoMvpPresenter<V extends PromoMvpView> extends MvpPresenter<V> {
    void sendPromoCode(String code);
}
