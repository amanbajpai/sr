package com.ros.smartrocket.presentation.login.promo;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface PromoMvpPresenter<V extends PromoMvpView> extends MvpPresenter<V> {
    void sendPromoCode(String code);
}
