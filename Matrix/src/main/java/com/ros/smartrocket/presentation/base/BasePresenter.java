package com.ros.smartrocket.presentation.base;

import com.ros.smartrocket.utils.PreferencesManager;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {
    private V mvpView;
    private CompositeDisposable compositeDisposable;

    @Override
    public void attachView(V mvpView) {
        this.mvpView = mvpView;
    }

    @Override
    public void detachView() {
        mvpView = null;
        unDispose();
    }

    @Override
    public boolean isViewAttached() {
        return mvpView != null;
    }

    public V getMvpView() {
        return mvpView;
    }

    protected String getLanguageCode() {
        String code = PreferencesManager.getInstance().getLanguageCode();
        return "in".equals(code) ? "id" : code;
    }

    protected void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    private void unDispose() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }
}
