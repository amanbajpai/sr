package com.ros.smartrocket.presentation.question.audit;

import android.os.Bundle;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.audit.additional.MassAuditNavigator;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

import butterknife.BindView;

public class QuestionMassAuditFragment extends BaseQuestionFragment<MassAuditMvpPresenter<MassAuditMvpView>, MassAuditMvpView> {
    @BindView(R.id.massAuditView)
    MassAuditView massAuditView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.getChildQuestionsListFromDB();
    }

    @Override
    public MassAuditMvpPresenter<MassAuditMvpView> getPresenter() {
        return new MassAuditPresenter<>(question, new MassAuditNavigator(getActivity()));
    }

    @Override
    public MassAuditMvpView getMvpView() {
        massAuditView.setPresenter(presenter);
        return massAuditView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_mass_audit;
    }

}