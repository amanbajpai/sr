package com.ros.smartrocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.MassAuditExpandableListAdapter;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

/**
 * Mass Audit question type
 */
public class QuestionTypeMassAuditFragment extends BaseQuestionFragment {
    private Question question;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_question_type_mass_audit, null);

        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.massAuditExpandableListView);
        TextView questionTextView = (TextView) view.findViewById(R.id.massAuditQuestionText);

        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
        }

        if (question != null) {
            listView.setAdapter(new MassAuditExpandableListAdapter(getActivity(), question.getCategoriesArray()));
            questionTextView.setText(question.getQuestion());

            if (!TextUtils.isEmpty(question.getPresetValidationText())) {
                TextView presetValidationComment = (TextView) view.findViewById(R.id.presetValidationComment);
                presetValidationComment.setText(question.getPresetValidationText());
                presetValidationComment.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(question.getValidationComment())) {
                TextView validationComment = (TextView) view.findViewById(R.id.validationComment);
                validationComment.setText(question.getValidationComment());
                validationComment.setVisibility(View.VISIBLE);
            }
        }

        refreshNextButton();

        return view;
    }

    @Override
    public boolean saveQuestion() {
        return true;
    }

    @Override
    public Question getQuestion() {
        return question;
    }

    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            answerSelectedListener.onAnswerSelected(true);
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
    }

    @Override
    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        this.answerSelectedListener = answerSelectedListener;
    }

    @Override
    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener
                                                             answerPageLoadingFinishedListener) {
        this.answerPageLoadingFinishedListener = answerPageLoadingFinishedListener;
    }}