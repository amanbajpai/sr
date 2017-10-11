package com.ros.smartrocket.presentation.question.choose.multiple;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;
import com.ros.smartrocket.presentation.question.choose.ChoiceMvpPresenter;
import com.ros.smartrocket.presentation.question.choose.ChoiceMvpView;
import com.ros.smartrocket.presentation.question.choose.ChoicePresenter;

import butterknife.BindView;

public class QuestionMultipleChoiceFragment extends BaseQuestionFragment<ChoiceMvpPresenter<ChoiceMvpView>, ChoiceMvpView> {
    @BindView(R.id.multipleChooseView)
    MultipleChoiceView multipleChoiceView;

    @Override
    public ChoiceMvpPresenter<ChoiceMvpView> getPresenter() {
        return new ChoicePresenter<>(question);
    }

    @Override
    public ChoiceMvpView getMvpView() {
        multipleChoiceView.setPresenter(presenter);
        return multipleChoiceView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_multiple_choose;
    }
}