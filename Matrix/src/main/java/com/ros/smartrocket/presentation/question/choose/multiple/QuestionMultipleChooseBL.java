package com.ros.smartrocket.presentation.question.choose.multiple;

import android.view.View;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.presentation.question.choose.QuestionBaseChooseBL;
import com.ros.smartrocket.ui.adapter.AnswerCheckBoxAdapter;

public final class QuestionMultipleChooseBL extends QuestionBaseChooseBL {
    @Override
    public void configureView() {
        super.configureView(R.string.choose_one_or_more_answers,
                new AnswerCheckBoxAdapter(activity, answerSelectedListener));
    }

    @Override
    protected void handleClick(View item, int position) {
        Answer answer = adapter.getItem(position);
        answer.toggleChecked();

        AnswerCheckBoxAdapter.ViewHolder viewHolder = (AnswerCheckBoxAdapter.ViewHolder) item.getTag();
        viewHolder.getCheckBox().setChecked(answer.getChecked());

        refreshNextButton();
    }
}