package com.ros.smartrocket.presentation.question.choose.single;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;
import com.ros.smartrocket.presentation.question.choose.ChooseMvpPresenter;
import com.ros.smartrocket.presentation.question.choose.ChooseMvpView;
import com.ros.smartrocket.presentation.question.choose.ChoosePresenter;

import butterknife.BindView;

public class QuestionSingleChooseFragment extends BaseQuestionFragment<ChooseMvpPresenter<ChooseMvpView>, ChooseMvpView> {

    @BindView(R.id.singleChooseView)
    SingleChooseView singleChooseView;

    @Override
    public ChooseMvpPresenter<ChooseMvpView> getPresenter() {
        return new ChoosePresenter<>(question);
    }

    @Override
    public ChooseMvpView getMvpView() {
        singleChooseView.setPresenter(presenter);
        return singleChooseView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_single_choose;
    }
}