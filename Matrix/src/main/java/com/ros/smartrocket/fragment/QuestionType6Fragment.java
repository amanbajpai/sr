package com.ros.smartrocket.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.utils.L;

/**
 * Numeric question type
 */
public class QuestionType6Fragment extends BaseQuestionFragment {
//    private EditText answerEditText;
    private Question question;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;
    private AsyncQueryHandler handler;

    @Bind(R.id.answerTextView)
    TextView answerTextView;
    @Bind(R.id.keyDotBtn)
    Button dotButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_6, null);
        ButterKnife.bind(this, view);

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
//        answerEditText = (EditText) view.findViewById(R.id.answerEditText);

        if (question.getPatternType() == 1) {
            // Decimal question
            dotButton.setText(R.string.key_dot);
        } else {
            // Numeric question without dot
            dotButton.setText("");
        }

        questionText.setText(question.getQuestion());
        conditionText.setText(getString(R.string.write_your_number, question.getMinValue(),
                question.getMaxValue()));
        AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId());

        return view;
    }

    @SuppressWarnings("unused")
    @OnClick({R.id.keyOneBtn, R.id.keyTwoBtn, R.id.keyThreeBtn, R.id.keyFourBtn, R.id.keyFiveBtn, R.id.keySixBtn,
            R.id.keySevenBtn, R.id.keyEightBtn, R.id.keyNineBtn, R.id.keyZeroBtn})
    public void onCipherClick(View v) {
        Button view = (Button) v;
        String text = view.getText().toString();
        L.v("QuestionType6Fragment.onClick", text);

        answerTextView.setText(answerTextView.getText() + text);
        refreshNextButton();
    }

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
                        answerTextView.setText(answers[0].getValue());
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
    public boolean saveQuestion() {
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

    @Override
    public void clearAnswer() {
        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
            Answer[] answers = question.getAnswers();
            for (Answer answer : answers) {
                answer.setChecked(false);
            }

            AnswersBL.updateAnswersToDB(handler, answers);
        }
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
