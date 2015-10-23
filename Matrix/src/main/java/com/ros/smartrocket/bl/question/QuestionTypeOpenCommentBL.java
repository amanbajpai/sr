package com.ros.smartrocket.bl.question;

import android.content.AsyncQueryHandler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;

public final class QuestionTypeOpenCommentBL extends QuestionTypeBaseBL {
    @Bind(R.id.answerEditText)
    EditText answerEditText;

    public QuestionTypeOpenCommentBL(View view, Question question) {
        super(view, question);
        setEditTextWatcher(answerEditText);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(question.getMaximumCharacters());
        answerEditText.setFilters(filterArray);
    }

    public void setEditTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshNextButton();
            }
        });
    }

    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            boolean selected = !TextUtils.isEmpty(answerEditText.getText().toString().trim());

            answerSelectedListener.onAnswerSelected(selected);
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
    }

    public boolean saveQuestion(AsyncQueryHandler handler) {
        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
            Answer answer = question.getAnswers()[0];
            answer.setValue(answerEditText.getText().toString());
            answer.setChecked(true);

            AnswersBL.updateAnswersToDB(handler, question.getAnswers());
            return true;
        } else {
            return false;
        }
    }

    public void clearAnswer(AsyncQueryHandler handler) {
        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
            Answer[] answers = question.getAnswers();
            for (Answer answer : answers) {
                answer.setChecked(false);
            }

            AnswersBL.updateAnswersToDB(handler, answers);
        }
    }

    public void fillViewWithAnswers(Answer[] answers) {
        question.setAnswers(answers);
        if (answers[0].getChecked()) {
            answerEditText.setText(answers[0].getValue());
        }

        refreshNextButton();
    }
}
