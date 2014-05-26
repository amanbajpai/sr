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
import com.ros.smartrocket.utils.L;

/**
 * Fragment for display About information
 */
public class QuestionType6Fragment extends BaseQuestionFragment {
    //private static final String TAG = QuestionType6Fragment.class.getSimpleName();
    private ViewGroup view;
    private TextView questionText;
    private TextView writeNumberFromTillText;
    private EditText answerEditText;
    private Question question;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;

    private AsyncQueryHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        view = (ViewGroup) localInflater.inflate(R.layout.fragment_question_type_6, null);

        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Keys.QUESTION);
        }

        handler = new DbHandler(getActivity().getContentResolver());

        questionText = (TextView) view.findViewById(R.id.questionText);
        if (!TextUtils.isEmpty(question.getValidationComment())) {
            TextView validationComment = (TextView) view.findViewById(R.id.validationComment);
            validationComment.setText(question.getValidationComment());
            validationComment.setVisibility(View.VISIBLE);
        }

        writeNumberFromTillText = (TextView) view.findViewById(R.id.writeNumberFromTillText);
        answerEditText = (EditText) view.findViewById(R.id.answerEditText);

        if (question.getPatternType() != 1) {
            answerEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        setEditTextWatcher(answerEditText);

        questionText.setText(question.getQuestion());
        writeNumberFromTillText.setText(getString(R.string.write_your_number, question.getMinValue(),
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
                    if (answers[0].isChecked()) {
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

        AnswersBL.setAnswersToDB(handler, question.getAnswers());
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
