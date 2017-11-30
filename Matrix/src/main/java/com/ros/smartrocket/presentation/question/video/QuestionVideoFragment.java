package com.ros.smartrocket.presentation.question.video;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;
import com.ros.smartrocket.presentation.question.video.helper.VideoQuestionHelper;

import butterknife.BindView;

public class QuestionVideoFragment extends BaseQuestionFragment<VideoMvpPresenter<VideoMvpView>, VideoMvpView> {
    @BindView(R.id.videoView)
    VideoQuestionView videoQuestionView;

    @Override
    public VideoMvpPresenter<VideoMvpView> getPresenter() {
        return new VideoPresenter<>(question, new VideoQuestionHelper(this));
    }

    @Override
    public VideoMvpView getMvpView() {
        videoQuestionView.setPresenter(presenter);
        return videoQuestionView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_video;
    }
}
