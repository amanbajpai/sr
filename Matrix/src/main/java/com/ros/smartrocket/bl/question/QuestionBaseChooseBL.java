package com.ros.smartrocket.bl.question;

import android.database.DataSetObserver;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.adapter.AnswerBaseAdapter;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;

import butterknife.BindView;

public class QuestionBaseChooseBL extends QuestionBaseBL {
    protected AnswerBaseAdapter adapter;

    @BindView(R.id.conditionText)
    TextView conditionText;
    @BindView(R.id.choiceListLayout)
    LinearLayout answerLayout;

    public void configureView(int stringId, final AnswerBaseAdapter adapter) {
        this.adapter = adapter;
        conditionText.setText(stringId);

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                answerLayout.removeAllViews();
                for (int i = 0; i < adapter.getCount(); i++) {
                    View item = adapter.getView(i, null, null);
                    item.setOnClickListener(new MyClickListener(i));
                    answerLayout.addView(item);
                }
            }
        });

        loadAnswers();
    }

    @Override
    public boolean saveQuestion() {
        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
            AnswersBL.updateAnswersToDB(handler, question.getAnswers());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            boolean selected = false;
            for (Answer answer : adapter.getData()) {
                if (answer.getChecked()) {
                    selected = true;
                    break;
                }
            }
            answerSelectedListener.onAnswerSelected(selected, question.getId());
        }

        if (answerPageLoadingFinishedListener != null) {
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
        }
    }

    @Override
    protected void fillViewWithAnswers(Answer[] answers) {
        question.setAnswers(answers);
        adapter.setData(question.getAnswers());
        refreshNextButton();
    }

    protected void handleClick(View item, int position) {
        // Do nothing
    }

    class MyClickListener implements View.OnClickListener {
        private final int position;

        public MyClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            handleClick(v, position);
        }
    }
}