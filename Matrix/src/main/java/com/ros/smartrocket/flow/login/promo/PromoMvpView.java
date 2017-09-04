package com.ros.smartrocket.flow.login.promo;

import com.ros.smartrocket.flow.base.NetworkMvpView;

interface PromoMvpView extends NetworkMvpView {
    void paramsNotValid();
    void onPromoCodeSent();
}
