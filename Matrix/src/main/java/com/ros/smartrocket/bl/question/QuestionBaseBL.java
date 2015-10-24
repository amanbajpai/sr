package com.ros.smartrocket.bl.question;

import android.content.AsyncQueryHandler;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

public class QuestionBaseBL {
    protected OnAnswerSelectedListener answerSelectedListener;
    protected OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;
    protected Question question;
    private FragmentActivity activity;

    @Bind(R.id.questionText)
    TextView questionText;
    @Bind(R.id.presetValidationComment)
    TextView presetValidationComment;
    @Bind(R.id.validationComment)
    TextView validationComment;

    public void initView(View view, Question question, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        this.question = question;

        questionText.setText(question.getQuestion());

        if (!TextUtils.isEmpty(question.getPresetValidationText())) {
            presetValidationComment.setText(question.getPresetValidationText());
            presetValidationComment.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(question.getValidationComment())) {
            validationComment.setText(question.getValidationComment());
            validationComment.setVisibility(View.VISIBLE);
        }
    }

    public Question getQuestion() {
        return question;
    }

    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        this.answerSelectedListener = answerSelectedListener;
    }

    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener listener) {
        this.answerPageLoadingFinishedListener = listener;
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public boolean saveQuestion(AsyncQueryHandler handler) {
        return true;
    }

    public void clearAnswer(AsyncQueryHandler handler) {
    }

    public void fillViewWithAnswers(Answer[] answers) {
    }

    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            answerSelectedListener.onAnswerSelected(true);
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
    }

    public FragmentActivity getActivity() {
        return activity;
    }

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }
}