package com.ros.smartrocket.presentation.question.choose.multiple;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;
import com.ros.smartrocket.presentation.question.choose.ChooseMvpPresenter;
import com.ros.smartrocket.presentation.question.choose.ChooseMvpView;
import com.ros.smartrocket.presentation.question.choose.ChoosePresenter;

import butterknife.BindView;

public class QuestionMultipleChooseFragment extends BaseQuestionFragment<ChooseMvpPresenter<ChooseMvpView>, ChooseMvpView> {
    @BindView(R.id.multipleChooseView)
    MultipleChooseView multipleChooseView;

    @Override
    public ChooseMvpPresenter<ChooseMvpView> getPresenter() {
        return new ChoosePresenter<>(question);
    }

    @Override
    public ChooseMvpView getMvpView() {
        multipleChooseView.setPresenter(presenter);
        return multipleChooseView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_multiple_choose;
    }
}