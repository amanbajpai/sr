package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

/**
 * Numeric question type
 */
public class QuestionType6Fragment extends BaseQuestionFragment {
    private EditText answerEditText;
    private Question question;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;

    private AsyncQueryHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_6, null);

        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
        }

        handler = new DbHandler(getActivity().getContentResolver());

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

        TextView conditionText = (TextView) view.findViewById(R.id.conditionText);
        answerEditText = (EditText) view.findViewById(R.id.answerEditText);

        if (question.getPatternType() != 1) {
            answerEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        setEditTextWatcher(answerEditText);

        questionText.setText(question.getQuestion());
        conditionText.setText(getString(R.string.write_your_number, question.getMinValue(),
                question.getMaxValue()));
        AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getId());

        return view;
    }

    class DbHandler extends AsyncQueryHandler {

        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case AnswerDbSchema.Query.TOKEN_QUERY:
                    Answer[] answers = AnswersBL.convertCursorToAnswersArray(cursor);

                    QuestionType6Fragment.this.question.setAnswers(answers);
                    if (answers[0].getChecked()) {
                        answerEditText.setText(answers[0].getValue());
                    }

                    refreshNextButton();
                    break;
                default:
                    break;
            }
        }
    }

    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            String answerText = answerEditText.getText().toString().trim();
            double answerNumber = -1;

            if (!TextUtils.isEmpty(answerText)) {
                answerNumber = Double.valueOf(answerText);
            }

            boolean selected = !TextUtils.isEmpty(answerText) && answerNumber >= question.getMinValue()
                    && answerNumber <= question.getMaxValue();

            answerSelectedListener.onAnswerSelected(selected);
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
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

    @Override
    public void saveQuestion() {
        Answer answer = question.getAnswers()[0];
        answer.setValue(answerEditText.getText().toString());
        answer.setChecked(true);

        AnswersBL.updateAnswersToDB(handler, question.getAnswers());
    }

    @Override
    public void clearAnswer() {
        Answer[] answers = question.getAnswers();
        for (Answer answer: answers){
            answer.setChecked(false);
        }

        AnswersBL.updateAnswersToDB(handler, answers);
    }

    @Override
    public Question getQuestion() {
        return question;
    }


    @Override
    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        this.answerSelectedListener = answerSelectedListener;
    }

    @Override
    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener
                                                             answerPageLoadingFinishedListener) {
        this.answerPageLoadingFinishedListener = answerPageLoadingFinishedListener;
    }
}
