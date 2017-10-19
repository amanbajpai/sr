package com.ros.smartrocket.presentation.question.choose.single;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.presentation.question.choose.AnswerChooseBaseAdapter;
import com.ros.smartrocket.presentation.question.choose.BaseChooseView;
import com.ros.smartrocket.utils.UIUtils;

public class SingleChooseView extends BaseChooseView {
    public SingleChooseView(Context context) {
        super(context);
    }

    public SingleChooseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected AnswerChooseBaseAdapter getAdapter() {
        return new SingleChooseAdapter(getContext(), presenter);
    }

    @Override
    protected int getTitleResId() {
        return R.string.choose_one_answer;
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

        presenter.refreshNextButton(adapter.getData());
    }
}
