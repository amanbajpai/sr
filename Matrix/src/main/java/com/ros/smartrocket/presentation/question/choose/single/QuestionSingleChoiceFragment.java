package com.ros.smartrocket.presentation.question.choose.single;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;
import com.ros.smartrocket.presentation.question.choose.ChoiceMvpPresenter;
import com.ros.smartrocket.presentation.question.choose.ChoiceMvpView;
import com.ros.smartrocket.presentation.question.choose.ChoicePresenter;

import butterknife.BindView;

public class QuestionSingleChoiceFragment extends BaseQuestionFragment<ChoiceMvpPresenter<ChoiceMvpView>, ChoiceMvpView> {

    @BindView(R.id.singleChooseView)
    SingleChoiceView singleChoiceView;

    @Override
    public ChoiceMvpPresenter<ChoiceMvpView> getPresenter() {
        return new ChoicePresenter<>(question);
    }

    @Override
    public ChoiceMvpView getMvpView() {
        singleChoiceView.setPresenter(presenter);
        return singleChoiceView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_single_choose;
    }
}