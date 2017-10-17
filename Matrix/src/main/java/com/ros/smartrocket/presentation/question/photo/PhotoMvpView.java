package com.ros.smartrocket.presentation.question.photo;

import android.graphics.Bitmap;

import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpView;

import java.util.List;

public interface PhotoMvpView extends BaseQuestionMvpView {
    void showPhotoCanNotBeAddDialog();

    void refreshPhotoGallery(List<Answer> answers);

    void selectGalleryPhoto(int position);

    void setBitmap(Bitmap bitmap);

    int getCurrentPos();

    void setCurrentPos(int pos);

    void refreshConfirmButton(boolean isPhotoAdded);

    void refreshRePhotoButton(boolean isPhotoAdded);

    void refreshDeletePhotoButton(boolean isPhotoAdded);
}
