package com.ros.smartrocket.presentation.question.photo;

import android.graphics.Bitmap;

import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpView;
import com.ros.smartrocket.ui.gallery.model.GalleryInfo;

import java.util.HashMap;
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

    void getSelectedImgPath(HashMap<String, GalleryInfo> selectedPath);



}
