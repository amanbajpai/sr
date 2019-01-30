package com.ros.smartrocket.presentation.question.comment;

import android.graphics.Bitmap;

import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;

import java.util.ArrayList;

public interface CommentMvpPresenter<V extends CommentMvpView> extends BaseQuestionMvpPresenter<V> {
    void onCommentEntered(String s);

    ArrayList<String> getDialogGalleryImages();

}

