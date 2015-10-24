package com.ros.smartrocket.bl.question;

import android.content.AsyncQueryHandler;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.AnswerCheckBoxAdapter;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;

public final class QuestionMultipleChooseBL extends QuestionBaseBL {
    private AnswerCheckBoxAdapter adapter;

    @Bind(R.id.conditionText)
    TextView conditionText;
    @Bind(R.id.answerList)
    ListView listView;

    @Override
    public void initView(View view, Question question, Bundle savedInstanceState) {
        super.initView(view, question, savedInstanceState);

        conditionText.setText(R.string.choose_one_or_more_answers);

        adapter = new AnswerCheckBoxAdapter(view.getContext(), answerSelectedListener);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public void fillViewWithAnswers(Answer[] answers) {
        question.setAnswers(answers);
        adapter.setData(question.getAnswers());
        refreshNextButton();
    }

    @Override
    public boolean saveQuestion(AsyncQueryHandler handler) {
        if (question != null && question.getAnswers() != null && question.getAnswers().length > 0) {
            AnswersBL.updateAnswersToDB(handler, question.getAnswers());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clearAnswer(AsyncQueryHandler handler) {
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

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View item, int position, long id) {
            if (position > 0) {
                Answer answer = adapter.getItem(position - 1);
                answer.toggleChecked();

                AnswerCheckBoxAdapter.ViewHolder viewHolder = (AnswerCheckBoxAdapter.ViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(answer.getChecked());

                refreshNextButton();
            }
        }
    };
}