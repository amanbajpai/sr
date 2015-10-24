package com.ros.smartrocket.bl.question;

import android.content.AsyncQueryHandler;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.AnswerRadioButtonAdapter;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.utils.UIUtils;

public final class QuestionSingleChooseBL extends QuestionBaseBL {
    private AnswerRadioButtonAdapter adapter;

    @Bind(R.id.conditionText)
    TextView conditionText;
    @Bind(R.id.answerList)
    ListView listView;

    @Override
    public void initView(View view, Question question, Bundle savedInstanceState) {
        super.initView(view, question, savedInstanceState);

        conditionText.setText(R.string.choose_one_answer);

        adapter = new AnswerRadioButtonAdapter(view.getContext(), answerSelectedListener);
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
                for (Answer answer : adapter.getData()) {
                    answer.setChecked(false);
                }

                Answer answer = adapter.getItem(position - 1);
                answer.toggleChecked();
                adapter.notifyDataSetChanged();

                if (Integer.valueOf(answer.getValue()) < 1000) {
                    EditText editText = (EditText) item.findViewById(R.id.otherAnswerEditText);
                    UIUtils.hideSoftKeyboard(arg0.getContext(), editText);
                }

                refreshNextButton();
            }
        }
    };
}