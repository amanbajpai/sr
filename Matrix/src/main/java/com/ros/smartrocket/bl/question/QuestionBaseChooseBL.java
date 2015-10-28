package com.ros.smartrocket.bl.question;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.AnswerBaseAdapter;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;

public class QuestionBaseChooseBL extends QuestionBaseBL {
    protected AnswerBaseAdapter adapter;

    @Bind(R.id.conditionText)
    TextView conditionText;
    @Nullable
    @Bind(R.id.answerList)
    ListView listView;
    @Nullable
    @Bind(R.id.choiceListLayout)
    LinearLayout answerLayout;

    public void initView(View view, Question question, Bundle savedInstanceState, FragmentActivity activity,
                         int stringId, AdapterView.OnItemClickListener itemClickListener,
                         final AnswerBaseAdapter adapter) {
        super.initView(view, question, savedInstanceState, activity);

        this.adapter = adapter;
        conditionText.setText(stringId);

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if (answerLayout != null) {
                    answerLayout.removeAllViews();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        View item = adapter.getView(i, null, null);
//                        item.setOnClickListener(new MyClickListener(i));
                        answerLayout.addView(item);
                    }
                }
            }
        });

        if (listView != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(itemClickListener);
        }

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
    public void refreshNextButton() {
        if (answerSelectedListener != null) {
            boolean selected = false;
            for (Answer answer : adapter.getData()) {
                if (answer.getChecked()) {
                    selected = true;
                    break;
                }
            }
            answerSelectedListener.onAnswerSelected(selected);
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
}