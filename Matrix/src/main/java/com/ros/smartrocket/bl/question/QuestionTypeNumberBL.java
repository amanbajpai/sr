package com.ros.smartrocket.bl.question;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.utils.L;

public final class QuestionTypeNumberBL extends QuestionTypeBaseBL {
    public static final String EXTRA_TEXT_VIEW_NUMBER = "com.ros.smartrocket.EXTRA_TEXT_VIEW_NUMBER";

    @Bind(R.id.answerTextView)
    TextView answerTextView;
    @Bind(R.id.keyDotBtn)
    Button dotButton;

    public QuestionTypeNumberBL(View view, Question question, Bundle savedInstanceState) {
        super(view, question);
        Context context = view.getContext();

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TEXT_VIEW_NUMBER)) {
            answerTextView.setText(savedInstanceState.getString(EXTRA_TEXT_VIEW_NUMBER));
        }

        if (question.getPatternType() == 1) {
            // Decimal question
            dotButton.setText(R.string.key_dot);
        } else {
            // Numeric question without dot
            dotButton.setText("");
        }

        TextView conditionText = (TextView) view.findViewById(R.id.conditionText);
        conditionText.setText(context.getString(R.string.write_your_number,
                question.getMinValue(), question.getMaxValue()));
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_TEXT_VIEW_NUMBER, answerTextView.getText().toString());
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("unused")
    @OnClick({R.id.keyOneBtn, R.id.keyTwoBtn, R.id.keyThreeBtn, R.id.keyFourBtn, R.id.keyFiveBtn, R.id.keySixBtn,
            R.id.keySevenBtn, R.id.keyEightBtn, R.id.keyNineBtn, R.id.keyZeroBtn})
    public void onCipherClick(View v) {
        Button view = (Button) v;
        String text = view.getText().toString();

        answerTextView.setText(answerTextView.getText() + text);
        refreshNextButton();
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("unused")
    @OnClick(R.id.keyDotBtn)
    void onDotClick() {
        String text = answerTextView.getText().toString();
        if (!text.contains(".") && question.getPatternType() == 1) {
            answerTextView.setText(text + ".");
            refreshNextButton();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.keyBackspaceBtn)
    void onBackspaceClick() {
        String text = answerTextView.getText().toString();
        if (text.length() > 0) {
            answerTextView.setText(text.substring(0, text.length() - 1));
            refreshNextButton();
        }
    }

    public boolean saveQuestion(AsyncQueryHandler handler) {
        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
            Answer answer = question.getAnswers()[0];
            answer.setValue(answerTextView.getText().toString());
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
            answerTextView.setText(answers[0].getValue());
        }

        refreshNextButton();
    }

    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            String answerText = answerTextView.getText().toString().trim();
            Double answerNumber = null;

            try {
                answerNumber = Double.valueOf(answerText);
            } catch (NumberFormatException e) {
                L.d("Parse", "Not a Double " + answerText);
            }

            boolean selected = answerNumber != null
                    && answerNumber >= question.getMinValue() && answerNumber <= question.getMaxValue();

            answerSelectedListener.onAnswerSelected(selected);
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
    }
}
