package com.ros.smartrocket.presentation.question.photo;

import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;
import com.ros.smartrocket.utils.eventbus.PhotoEvent;

public interface PhotoMvpPresenter<V extends PhotoMvpView> extends BaseQuestionMvpPresenter<V> {

    void onPhotoEvent(PhotoEvent event);

    void selectGalleryPhoto(int position);

    void onPhotoConfirmed(int photoPos , int type); // type 1: Camera , 2 : Gallery

    void onPhotoDeleted(int photoPos);

    void onPhotoClicked(int photoPos);

    void onPhotoRequested(int photoPos);

    void onQuestionCount(int aCount);


    void onGalleryPhotoRequested(int photoPos, int size);
}
