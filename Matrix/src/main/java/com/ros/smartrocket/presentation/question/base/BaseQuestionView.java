package com.ros.smartrocket.presentation.question.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.ui.dialog.CustomProgressDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseQuestionView<P extends BaseQuestionMvpPresenter> extends LinearLayout implements BaseQuestionMvpView {
    @BindView(R.id.presetValidationComment)
    TextView presetValidationComment;
    @BindView(R.id.validationComment)
    TextView validationComment;
    @BindView(R.id.questionText)
    TextView questionText;
    private CustomProgressDialog progressDialog;
    protected P presenter;

    public BaseQuestionView(Context context) {
        super(context);
        init();
    }

    public BaseQuestionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseQuestionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(getLayoutResId(), this, true);
        ButterKnife.bind(this);
    }

    public abstract int getLayoutResId();

    @Override
    public void validateView(Question question) {
        questionText.setMovementMethod(LinkMovementMethod.getInstance());
        if (!TextUtils.isEmpty(question.getSubQuestionNumber()))
            questionText.setText(Html.fromHtml(question.getSubQuestionNumber() + question.getQuestion()));
        else
            questionText.setText(Html.fromHtml(question.getQuestion()));

        validationComment.setMovementMethod(LinkMovementMethod.getInstance());
        presetValidationComment.setMovementMethod(LinkMovementMethod.getInstance());
        if (!TextUtils.isEmpty(question.getPresetValidationText())) {
            presetValidationComment.setText(Html.fromHtml(question.getPresetValidationText()));
            presetValidationComment.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(question.getValidationComment())) {
            validationComment.setText(Html.fromHtml(question.getValidationComment()));
            validationComment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public abstract void configureView(Question question);

    @Override
    public void answersDeleteComplete() {

    }

    @Override
    public void answersUpdate() {

    }

    @Override
    public abstract void fillViewWithAnswers(List<Answer> answers);

    @Override
    public void hideLoading() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    @Override
    public void showLoading(boolean isCancelable) {
        hideLoading();
        progressDialog = CustomProgressDialog.show(getContext());
        progressDialog.setCancelable(isCancelable);
    }

    @Override
    public void onDestroy() {
        hideLoading();
    }

    @Override
    public String getAnswerValue() {
        return "";
    }


    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }
}
