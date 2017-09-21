package com.ros.smartrocket.presentation.question.choose.single;

import android.view.View;
import android.widget.EditText;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.presentation.question.choose.QuestionBaseChooseBL;
import com.ros.smartrocket.ui.adapter.AnswerRadioButtonAdapter;
import com.ros.smartrocket.utils.UIUtils;

public final class QuestionSingleChooseBL extends QuestionBaseChooseBL {
    @Override
    public void configureView() {
        super.configureView(R.string.choose_one_answer,
                new AnswerRadioButtonAdapter(activity, answerSelectedListener));
    }

    @Override
    protected void handleClick(View item, int position) {
        for (Answer answer : adapter.getData()) {
            answer.setChecked(false);
        }

        Answer answer = adapter.getItem(position);
        answer.toggleChecked();
        adapter.notifyDataSetChanged();

        if (Integer.valueOf(answer.getValue()) < 1000) {
            EditText editText = (EditText) item.findViewById(R.id.otherAnswerEditText);
            UIUtils.hideSoftKeyboard(item.getContext(), editText);
        }

        refreshNextButton();
    }
}