package com.ros.smartrocket.bl.question;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;

import butterknife.ButterKnife;

public class QuestionBaseBL {
    protected OnAnswerSelectedListener answerSelectedListener;
    protected OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;
    protected AsyncQueryHandler handler;
    protected Question question;
    protected Bundle savedInstanceState;
    protected BaseActivity activity;
    protected Fragment fragment;
    protected View view;
    protected Product product;

    TextView questionText;
    TextView presetValidationComment;
    TextView validationComment;

    public final void initView(View view, Question question, Bundle savedInstanceState, BaseActivity activity,
                               Fragment fragment, Product product) {
        ButterKnife.bind(this, view);
        this.savedInstanceState = savedInstanceState;
        this.product = product;
        this.question = question;
        this.activity = activity;
        this.fragment = fragment;
        this.view = view;
        this.handler = new BaseDbHandler(activity.getContentResolver());

        configureView();
        validateView();
    }

    protected void validateView() {
        questionText = (TextView) view.findViewById(R.id.questionText);
        presetValidationComment = (TextView) view.findViewById(R.id.presetValidationComment);
        validationComment = (TextView) view.findViewById(R.id.validationComment);

        questionText.setMovementMethod(LinkMovementMethod.getInstance());
        if (!TextUtils.isEmpty(question.getSubQuestionNumber())) {
            questionText.setText(Html.fromHtml(question.getSubQuestionNumber() + this.question.getQuestion()));
        } else {
            questionText.setText(Html.fromHtml(this.question.getQuestion()));
        }
        validationComment.setMovementMethod(LinkMovementMethod.getInstance());
        presetValidationComment.setMovementMethod(LinkMovementMethod.getInstance());
        if (!TextUtils.isEmpty(this.question.getPresetValidationText())) {
            presetValidationComment.setText(Html.fromHtml(this.question.getPresetValidationText()));
            presetValidationComment.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(this.question.getValidationComment())) {
            validationComment.setText(Html.fromHtml(this.question.getValidationComment()));
            validationComment.setVisibility(View.VISIBLE);
        }
    }

    protected void configureView() {
        // Do nothing
    }

    public void destroyView() {
        handler.removeCallbacksAndMessages(null);
    }

    public void onStart() {
        // Do nothing
    }

    public void onPause() {
        // Do nothing
    }

    public void onStop() {
        // Do nothing
    }

    public void loadAnswers() {
        if (product != null) {
            AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId(),
                    product.getId());
        } else {
            AnswersBL.getAnswersListFromDB(handler, question.getTaskId(), question.getMissionId(), question.getId());
        }
    }

    protected Integer getProductId() {
        return product != null ? product.getId() : null;
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

    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            answerSelectedListener.onAnswerSelected(true, question.getId());
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
    }

    public FragmentActivity getActivity() {
        return activity;
    }

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    class BaseDbHandler extends AsyncQueryHandler {
        public BaseDbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (token == AnswerDbSchema.Query.TOKEN_QUERY) {
                Answer[] answers = AnswersBL.convertCursorToAnswersArray(cursor);
                fillViewWithAnswers(answers);
            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            if (token == AnswerDbSchema.Query.TOKEN_UPDATE) {
                answersUpdate();
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            if (token == AnswerDbSchema.Query.TOKEN_DELETE) {
                answersDeleteComplete();
            }
        }
    }

    protected void answersDeleteComplete() {
        // Nothing to do
    }

    protected void answersUpdate() {
        // Nothing to do
    }

    protected void fillViewWithAnswers(Answer[] answers) {
        // Do nothing
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}