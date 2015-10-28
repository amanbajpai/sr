package com.ros.smartrocket.bl.question;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.AnswerCheckBoxAdapter;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;

public final class QuestionMultipleChooseBL extends QuestionBaseChooseBL {

    @Override
    public void initView(View view, Question question, Bundle savedInstanceState, FragmentActivity activity) {
        super.initView(view, question, savedInstanceState, activity,
                R.string.choose_one_or_more_answers, itemClickListener,
                new AnswerCheckBoxAdapter(view.getContext(), answerSelectedListener));
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View item, int position, long id) {
            Answer answer = adapter.getItem(position);
            answer.toggleChecked();

            AnswerCheckBoxAdapter.ViewHolder viewHolder = (AnswerCheckBoxAdapter.ViewHolder) item.getTag();
            viewHolder.getCheckBox().setChecked(answer.getChecked());

            refreshNextButton();
        }
    };
}