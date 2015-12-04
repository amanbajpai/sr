package com.ros.smartrocket.bl.question;

import android.view.View;
import android.widget.AdapterView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.AnswerCheckBoxAdapter;
import com.ros.smartrocket.db.entity.Answer;

public final class QuestionMultipleChooseBL extends QuestionBaseChooseBL {
    @Override
    public void configureView() {
        super.configureView(R.string.choose_one_or_more_answers, itemClickListener,
                new AnswerCheckBoxAdapter(activity, answerSelectedListener));
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View item, int position, long id) {
            handleClick(item, position);
        }
    };

    @Override
    protected void handleClick(View item, int position) {
        Answer answer = adapter.getItem(position);
        answer.toggleChecked();

        AnswerCheckBoxAdapter.ViewHolder viewHolder = (AnswerCheckBoxAdapter.ViewHolder) item.getTag();
        viewHolder.getCheckBox().setChecked(answer.getChecked());

        refreshNextButton();
    }
}