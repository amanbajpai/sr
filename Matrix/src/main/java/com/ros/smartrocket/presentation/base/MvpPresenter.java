package com.ros.smartrocket.presentation.base;

public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();

    boolean isViewAttached();
}
