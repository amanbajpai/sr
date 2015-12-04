package com.ros.smartrocket.bl.question;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import com.ros.smartrocket.R;
import com.ros.smartrocket.adapter.AnswerRadioButtonAdapter;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.utils.UIUtils;

public final class QuestionSingleChooseBL extends QuestionBaseChooseBL {
    @Override
    public void configureView() {
        super.configureView(R.string.choose_one_answer, itemClickListener,
                new AnswerRadioButtonAdapter(activity, answerSelectedListener));
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View item, int position, long id) {
            handleClick(item, position);
        }
    };

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