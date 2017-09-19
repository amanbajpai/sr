package com.ros.smartrocket.presentation.login.promo;

import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface PromoMvpView extends NetworkMvpView {
    void paramsNotValid();
    void onPromoCodeSent();
}
