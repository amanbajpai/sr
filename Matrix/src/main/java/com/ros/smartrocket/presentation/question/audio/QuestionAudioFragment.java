package com.ros.smartrocket.presentation.question.audio;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

import butterknife.BindView;

public class QuestionAudioFragment extends BaseQuestionFragment<AudioMvpPresenter<AudioMvpView>, AudioMvpView> {
    @BindView(R.id.audioView)
    AudioView audioView;

    @Override
    public AudioMvpPresenter<AudioMvpView> getPresenter() {
        return new AudioPresenter<>(question);
    }

    @Override
    public AudioMvpView getMvpView() {
        audioView.setPresenter(presenter);
        return audioView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_audio;
    }
}
