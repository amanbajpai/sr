package com.ros.smartrocket.presentation.question.choose.multiple;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.presentation.question.choose.AnswerChooseBaseAdapter;
import com.ros.smartrocket.presentation.question.choose.BaseChooseView;

public class MultipleChooseView extends BaseChooseView {
    public MultipleChooseView(Context context) {
        super(context);
    }

    public MultipleChooseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected AnswerChooseBaseAdapter getAdapter() {
        return new MultipleChooseAdapter(getContext(), presenter);
    }

    @Override
    protected int getTitleResId() {
        return R.string.choose_one_or_more_answers;
    }

    @Override
    protected void handleClick(View item, int position) {
        Answer answer = adapter.getItem(position);
        answer.toggleChecked();
        MultipleChooseAdapter.ViewHolder viewHolder = (MultipleChooseAdapter.ViewHolder) item.getTag();
        viewHolder.getCheckBox().setChecked(answer.getChecked());
        presenter.refreshNextButton(adapter.getData());
    }
}
