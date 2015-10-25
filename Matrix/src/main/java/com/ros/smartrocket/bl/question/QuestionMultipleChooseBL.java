package com.ros.smartrocket.bl.question;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.AnswerCheckBoxAdapter;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;

public final class QuestionMultipleChooseBL extends QuestionBaseChooseBL {

    @Override
    public void initView(View view, Question question, Bundle savedInstanceState) {
        super.initView(view, question, savedInstanceState, R.string.choose_one_or_more_answers, itemClickListener,
                new AnswerCheckBoxAdapter(view.getContext(), answerSelectedListener));
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