package com.ros.smartrocket.bl.question;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

public class QuestionTypeBaseBL {
    protected OnAnswerSelectedListener answerSelectedListener;
    protected OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;
    protected Question question;

    protected QuestionTypeBaseBL(View view, Question question) {
        ButterKnife.bind(this, view);
        this.question = question;

        TextView questionText = (TextView) view.findViewById(R.id.questionText);
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
        questionText.setText(question.getQuestion());
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
}