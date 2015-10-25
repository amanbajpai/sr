package com.ros.smartrocket.bl.question;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

public class QuestionBaseBL {
    protected OnAnswerSelectedListener answerSelectedListener;
    protected OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;
    protected AsyncQueryHandler handler;
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
        this.handler = new BaseDbHandler(view.getContext().getContentResolver());

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

    public void destroyView() {
        handler.removeCallbacksAndMessages(null);
    }

    public void loadAnswers() {
        AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId());
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
        // Do nothing
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        return false;
    }

    public boolean saveQuestion() {
        return true;
    }

    public void clearAnswer() {
        // Do nothing
    }

    protected void fillViewWithAnswers(Answer[] answers) {
        // Do nothing
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

    class BaseDbHandler extends AsyncQueryHandler {
        public BaseDbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case AnswerDbSchema.Query.TOKEN_QUERY:
                    Answer[] answers = AnswersBL.convertCursorToAnswersArray(cursor);
                    fillViewWithAnswers(answers);
                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            switch (token) {
                case AnswerDbSchema.Query.TOKEN_UPDATE:
                    answersUpdate();
                    break;
                default:
                    break;
            }
        }

    }

    protected void answersUpdate() {
    }
}