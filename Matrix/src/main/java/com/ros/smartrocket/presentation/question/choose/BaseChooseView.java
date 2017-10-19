package com.ros.smartrocket.presentation.question.choose;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;

import java.util.List;

import butterknife.BindView;

public abstract class BaseChooseView extends BaseQuestionView<ChooseMvpPresenter<ChooseMvpView>> implements ChooseMvpView {
    @BindView(R.id.conditionText)
    TextView conditionText;
    @BindView(R.id.choiceListLayout)
    LinearLayout answerLayout;
    protected AnswerChooseBaseAdapter adapter;

    public BaseChooseView(Context context) {
        super(context);
    }

    public BaseChooseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_question_choose;
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
        adapter.setData(answers);
        presenter.refreshNextButton(answers);
    }

    @Override
    public void configureView(Question question) {
        this.adapter = getAdapter();
        conditionText.setText(getTitleResId());
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                answerLayout.removeAllViews();
                for (int i = 0; i < adapter.getCount(); i++) {
                    View item = adapter.getView(i, null, null);
                    item.setOnClickListener(new ChooseClickListener(i));
                    answerLayout.addView(item);
                }
            }
        });
        presenter.loadAnswers();
    }

    protected abstract AnswerChooseBaseAdapter getAdapter();

    protected abstract void handleClick(View item, int position);

    protected abstract int getTitleResId();

    private class ChooseClickListener implements View.OnClickListener {
        private final int position;

        ChooseClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            handleClick(v, position);
        }
    }
}
