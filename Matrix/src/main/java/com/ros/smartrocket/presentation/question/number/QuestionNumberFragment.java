package com.ros.smartrocket.presentation.question.number;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

import butterknife.BindView;

public class QuestionNumberFragment extends BaseQuestionFragment<NumberMvpPresenter<NumberMvpView>, NumberMvpView> {
    @BindView(R.id.numberView)
    NumberView numberView;

    @Override
    public NumberMvpPresenter<NumberMvpView> getPresenter() {
        return new NumberPresenter<>(question);
    }

    @Override
    public NumberMvpView getMvpView() {
        numberView.setPatternType(question.getPatternType());
        numberView.setPresenter(presenter);
        return numberView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_number;
    }
}