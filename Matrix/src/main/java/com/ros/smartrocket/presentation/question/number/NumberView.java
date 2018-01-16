package com.ros.smartrocket.presentation.question.number;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.ui.views.CustomTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class NumberView extends BaseQuestionView<NumberMvpPresenter<NumberMvpView>> implements NumberMvpView {
    public static final String EXTRA_TEXT_VIEW_NUMBER = "com.ros.smartrocket.EXTRA_TEXT_VIEW_NUMBER";
    public static final int DECIMAL_PATTERN = 1;
    @BindView(R.id.answerTextView)
    TextView answerTextView;
    @BindView(R.id.keyDotBtn)
    Button dotButton;
    @BindView(R.id.conditionText)
    CustomTextView conditionText;
    private int patternType;

    public NumberView(Context context) {
        super(context);
    }

    public NumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_question_number;
    }

    @Override
    public void configureView(Question question) {
        if (state != null && state.containsKey(EXTRA_TEXT_VIEW_NUMBER))
            answerTextView.setText(state.getString(EXTRA_TEXT_VIEW_NUMBER));

        if (question.getPatternType() == DECIMAL_PATTERN)
            dotButton.setText(R.string.key_dot);
        else
            dotButton.setText("");

        if (question.getPatternType() == DECIMAL_PATTERN)
            conditionText.setText(getContext().getString(R.string.write_your_number,
                    question.getMinValue(), question.getMaxValue()));
        else
            conditionText.setText(getContext().getString(R.string.write_your_number_int,
                    question.getMinValue().intValue(), question.getMaxValue().intValue()));
        presenter.loadAnswers();
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
        if (answers.get(0).getChecked()) answerTextView.setText(answers.get(0).getValue());
        presenter.onNumberEntered(answerTextView.getText().toString());
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("unused")
    @OnClick({R.id.keyOneBtn, R.id.keyTwoBtn, R.id.keyThreeBtn, R.id.keyFourBtn, R.id.keyFiveBtn, R.id.keySixBtn,
            R.id.keySevenBtn, R.id.keyEightBtn, R.id.keyNineBtn, R.id.keyZeroBtn})
    public void onCipherClick(View v) {
        Button view = (Button) v;
        String text = view.getText().toString();
        answerTextView.setText(answerTextView.getText() + text);
        presenter.onNumberEntered(answerTextView.getText().toString());
    }

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("unused")
    @OnClick(R.id.keyDotBtn)
    void onDotClick() {
        String text = answerTextView.getText().toString();
        if (!text.contains(".") && patternType == DECIMAL_PATTERN) {
            answerTextView.setText(text + ".");
            presenter.onNumberEntered(answerTextView.getText().toString());
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.keyBackspaceBtn)
    void onBackspaceClick() {
        String text = answerTextView.getText().toString();
        if (text.length() > 0) {
            answerTextView.setText(text.substring(0, text.length() - 1));
            presenter.onNumberEntered(answerTextView.getText().toString());
        }
    }

    @Override
    public String getAnswerValue() {
        return answerTextView.getText().toString();
    }

    @Override
    public void setPatternType(int patternType) {
        this.patternType = patternType;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_TEXT_VIEW_NUMBER, answerTextView.getText().toString());
    }

}
