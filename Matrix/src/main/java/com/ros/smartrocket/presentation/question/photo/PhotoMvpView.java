package com.ros.smartrocket.presentation.question.photo;

import android.graphics.Bitmap;

import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface PhotoMvpView extends BaseQuestionMvpView {
    void showPhotoCanNotBeAddDialog();

    void refreshPhotoGallery(List<Answer> answers);

    void selectGalleryPhoto(int position);

    void setBitmap(Bitmap bitmap);

    void setImagePath(String commaSeperator);

    int getCurrentPos();

    void setCurrentPos(int pos);

    void refreshConfirmButton(boolean isPhotoAdded);

    void refreshRePhotoButton(boolean isPhotoAdded);

    void refreshDeletePhotoButton(boolean isPhotoAdded);

    void getSelectedImgPath(ArrayList<File> selectedPath);


}
