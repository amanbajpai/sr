package com.ros.smartrocket.presentation.question.photo;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;
import com.ros.smartrocket.utils.helpers.photo.PhotoManager;

import butterknife.BindView;

public class QuestionPhotoFragment extends BaseQuestionFragment<PhotoMvpPresenter<PhotoMvpView>, PhotoMvpView> {

    @BindView(R.id.photoView)
    PhotoView photoView;

    @Override
    public PhotoMvpPresenter<PhotoMvpView> getPresenter() {
        return new PhotoPresenter<>(question, new PhotoManager(this));
    }

    @Override
    public PhotoMvpView getMvpView() {
        photoView.setPresenter(presenter);
        return photoView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_photo;
    }
}
